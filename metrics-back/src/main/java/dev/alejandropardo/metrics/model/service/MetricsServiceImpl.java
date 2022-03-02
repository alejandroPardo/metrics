package dev.alejandropardo.metrics.model.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import dev.alejandropardo.metrics.controller.requests.MetricRequest;
import dev.alejandropardo.metrics.controller.requests.TimelineValues;
import dev.alejandropardo.metrics.controller.response.Metadata;
import dev.alejandropardo.metrics.controller.response.ResponseObject;
import dev.alejandropardo.metrics.model.dao.LogLevel;
import dev.alejandropardo.metrics.model.dao.LogType;
import dev.alejandropardo.metrics.model.dao.Metric;
import dev.alejandropardo.metrics.model.dao.Transaction;
import dev.alejandropardo.metrics.model.dto.TransactionsAverage;

@Repository
public class MetricsServiceImpl implements MetricsService {

	public MetricsServiceImpl(NamedParameterJdbcTemplate template) {
		this.template = template;
	}

	NamedParameterJdbcTemplate template;

	
	private final String summarizedMetricsSQL = "select metrics.name as name, count(metrics.name) as count, avg(metrics.duration_ms) as averageTime from metrics :joins where metric_timestamp >= (now() - interval '1 :timeline') :where group by metrics.name order by averageTime desc;";
	private final String transactionsSQL = "select * from transactions where transaction_timestamp >= (now() - interval '1 :timeline') order by transaction_timestamp desc;";
	private final String transactionChartSQL = "select hours.hour, count(transaction_timestamp), transactions.type as transaction_type from hours left join transactions on date_trunc('hour', transaction_timestamp) = hours.hour group by hours.hour, transactions.type order by hours.hour";

	private final String hoursWith = "with hours as (select generate_series(date_trunc('hour', now()) - '1 week'::interval, date_trunc('hour', now()), '1 hour'::interval) as hour) ";
	private final String transactionsJoin = " inner join transactions on transactions.metric_uuid = metrics.metric_uuid ";
	private final String failuresWhere = "  transaction_code >= 400 ";
	
	@Override
	public List<Metric> findAll() {
		return template.query("select * from metrics group by metric_timestamp, name, description, duration_ms, metric_uuid order by metric_timestamp", new MetricsRowMapper());
	}
	
	@Override
	public ResponseObject findForChart(TimelineValues timeline, boolean isFailure) {
		Instant startTime = Instant.now();
		List<Map<String, Object>> rows = null;
		if(isFailure) {
			rows = template.queryForList(timeline.getSqlQuery().replace(":joins", transactionsJoin).replace(":where", " where " + failuresWhere), new MapSqlParameterSource());
		} else {
			rows = template.queryForList(timeline.getSqlQuery().replace(":joins", "").replace(":where", ""), new MapSqlParameterSource());
		}

		Metadata metadata = new Metadata("self", "1.0", startTime.toString(), Instant.now().toString(), null, rows.size());
		return new ResponseObject(rows, metadata);
	}

	@Override
	public ResponseObject findOperations(TimelineValues timeline, boolean isFailure) {
		Instant startTime = Instant.now();

		List<Map<String, Object>> rows = null;
		if(isFailure) {
			rows = template.queryForList(summarizedMetricsSQL.replace(":timeline", timeline.name()).replace(":joins", transactionsJoin).replace(":where", " and " + failuresWhere), new MapSqlParameterSource());
		} else {
			rows = template.queryForList(summarizedMetricsSQL.replace(":timeline", timeline.name()).replace(":joins", "").replace(":where", ""), new MapSqlParameterSource());
		}
		
		Metadata metadata = new Metadata("self", "1.0", startTime.toString(), Instant.now().toString(), null, rows.size());
		return new ResponseObject(rows, metadata);
	}
	
	@Override
	public ResponseObject findTransactions(TimelineValues timeline) {
		Instant startTime = Instant.now();
		var rows = template.query(hoursWith + transactionChartSQL.replace(":timeline", timeline.name()), new MapSqlParameterSource(), new ResultSetExtractor<List<TransactionsAverage>>(){
		    @Override
		    public List<TransactionsAverage> extractData(ResultSet rs) throws SQLException,DataAccessException {
		    	List<TransactionsAverage> results = new ArrayList<>();
		        while(rs.next()){
		        	String key = rs.getString("hour");
		        	String transactionType = rs.getString("transaction_type");
		        	TransactionsAverage transaction = results.stream().filter(r -> r.getTimestamp().equalsIgnoreCase(key)).findFirst().orElse(null);
		        	if(transaction!=null) {
		        		if(transactionType != null) {
		        			transaction.getValues().put(transactionType, rs.getInt("count"));
		        		} 
		        	} else {
		        		transaction = new TransactionsAverage(key, new HashMap<>()); 
		        		results.add(transaction);
		        	}
		        	if(transactionType != null) {
	        			transaction.getValues().put(transactionType, rs.getInt("count"));
	        		} 
		        }
		        return results;
		    }
		});
		
		
		
		Set<String> values = rows.stream().flatMap(r -> r.getValues().keySet().stream()).filter(r -> r!= null).collect(Collectors.toSet());
		
		Map<String, Object> response = Map.of("keys", values, "values", rows);

		Metadata metadata = new Metadata("self", "1.0", startTime.toString(), Instant.now().toString(), null, rows.size());
		return new ResponseObject(response, metadata);
	}
	
	@Override
	public ResponseObject findTransactionsList(TimelineValues timeline) {
		Instant startTime = Instant.now();

		var rows = template.queryForList(transactionsSQL.replace(":timeline", timeline.name()), new MapSqlParameterSource());
		
		Metadata metadata = new Metadata("self", "1.0", startTime.toString(), Instant.now().toString(), null, rows.size());
		return new ResponseObject(rows, metadata);
	}

	@Override
	public List<Transaction> findAllByMetricId() {
		return null;
	}

	@Override
	@Transactional(rollbackFor = { SQLException.class, DuplicateKeyException.class })
	public void insertMetric(MetricRequest metric) {
		final String sql = "INSERT INTO METRICS(METRIC_UUID, NAME, METRIC_TIMESTAMP, DURATION_MS, DESCRIPTION) values (:metricUuid, :name, :metricTimestamp, :durationMS, :description)";
		SqlParameterSource param = new MapSqlParameterSource().addValue("metricUuid", metric.getUuid()).addValue("name", metric.getName()).addValue("metricTimestamp", metric.getMetricTimestamp()).addValue("durationMS", metric.getDuration()).addValue("description", metric.getDescription());
		template.update(sql, param);
		metric.getTransactions().stream().map(t -> new Transaction(UUID.randomUUID().toString(), metric.getUuid(), t.getType(), t.getName(), t.getTransactionTimestamp(), t.getLevel(), t.getValue(), t.getCode())).forEach(t -> insertTransaction(t));
	}

	@Override
	public void insertTransaction(Transaction transaction) {
		final String sql = "INSERT INTO TRANSACTIONS(TRANSACTION_UUID, NAME, METRIC_UUID, TRANSACTION_TIMESTAMP, TYPE, TRANSACTION_LEVEL, TRANSACTION_VALUE, TRANSACTION_CODE) values(:transactionUuid, :name, :metricUuid, :transactionTimestamp, :type, :transactionLevel, :transactionValue, :transactionCode)";
		String type = (transaction.getType() != null) ? transaction.getType().toString() : LogType.EMPTY.toString();
		String level = (transaction.getTransactionLevel() != null) ? transaction.getTransactionLevel().toString() : LogLevel.EMPTY.toString();
		SqlParameterSource param = new MapSqlParameterSource().addValue("transactionUuid", transaction.getTransactionUuid()).addValue("name", transaction.getName()).addValue("metricUuid", transaction.getMetricUuid()).addValue("transactionTimestamp", transaction.getTransactionTimestamp()).addValue("type", type)
				.addValue("transactionLevel", level).addValue("transactionValue", transaction.getTransactionValue()).addValue("transactionCode", transaction.getCode());
		template.update(sql, param);
	}

}