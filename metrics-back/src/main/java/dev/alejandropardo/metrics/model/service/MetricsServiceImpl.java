package dev.alejandropardo.metrics.model.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

@Repository
public class MetricsServiceImpl implements MetricsService {

	public MetricsServiceImpl(NamedParameterJdbcTemplate template) {
		this.template = template;
	}

	NamedParameterJdbcTemplate template;

	private final String summarizedMetricsSQL = "select name, count(name) as count, avg(duration_ms) as averageTime from metrics group by name order by averageTime desc;";
	private final String summarizedTransactionsSQL = "select * from metrics order by metric_timestamp;";

	@Override
	public List<Metric> findAll() {
		return template.query("select * from metrics group by metric_timestamp, name, description, duration_ms, metric_uuid order by metric_timestamp", new MetricsRowMapper());
	}
	
	@Override
	public ResponseObject findForChart(TimelineValues timeline) {
		Instant startTime = Instant.now();

		SqlParameterSource param = new MapSqlParameterSource();
		var rows = template.queryForList(timeline.getSqlQuery(), param);/*, new ResultSetExtractor<List<Object>>() {

			@Override
			public List<Object> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<Object> result = new ArrayList<>();
				while(rs.next()) {
					result.add(List.of(rs.getTimestamp("hour").toInstant().toEpochMilli(), rs.getLong("average")));
		        }
		        return result;
			}
		});*/

		Metadata metadata = new Metadata("self", "1.0", startTime.toString(), Instant.now().toString(), null, rows.size());
		return new ResponseObject(rows, metadata);
	}

	@Override
	public ResponseObject findSummarizedMetrics(LocalDateTime timestampFrom, LocalDateTime timestampTo) {
		Instant startTime = Instant.now();

		SqlParameterSource param = new MapSqlParameterSource();
		var rows = template.queryForList(summarizedMetricsSQL, param);

		Metadata metadata = new Metadata("self", "1.0", startTime.toString(), Instant.now().toString(), null, rows.size());
		return new ResponseObject(rows, metadata);
	}
	
	@Override
	public ResponseObject findSummarizedTransactions(LocalDateTime timestampFrom, LocalDateTime timestampTo) {
		Instant startTime = Instant.now();

		SqlParameterSource param = new MapSqlParameterSource();
		var rows = template.queryForList(summarizedTransactionsSQL, param);

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
		final String sql = "INSERT INTO TRANSACTIONS(TRANSACTION_UUID, METRIC_UUID, TRANSACTION_TIMESTAMP, TYPE, TRANSACTION_LEVEL, TRANSACTION_CODE) values(:transactionUuid, :metricUuid, :transactionTimestamp, :type, :transactionLevel, :transactionCode)";
		String type = (transaction.getType() != null) ? transaction.getType().toString() : LogType.EMPTY.toString();
		String level = (transaction.getTransactionLevel() != null) ? transaction.getTransactionLevel().toString() : LogLevel.EMPTY.toString();
		SqlParameterSource param = new MapSqlParameterSource().addValue("transactionUuid", transaction.getTransactionUuid()).addValue("metricUuid", transaction.getMetricUuid()).addValue("transactionTimestamp", transaction.getTransactionTimestamp()).addValue("type", type)
				.addValue("transactionLevel", level).addValue("transactionCode", transaction.getCode());
		template.update(sql, param);
	}

}