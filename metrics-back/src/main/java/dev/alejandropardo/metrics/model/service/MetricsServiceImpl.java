package dev.alejandropardo.metrics.model.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import dev.alejandropardo.metrics.controller.requests.MetricRequest;
import dev.alejandropardo.metrics.controller.requests.TimelineValues;
import dev.alejandropardo.metrics.controller.response.Metadata;
import dev.alejandropardo.metrics.controller.response.ResponseObject;
import dev.alejandropardo.metrics.model.dao.LogType;
import dev.alejandropardo.metrics.model.dao.Transaction;
import dev.alejandropardo.metrics.model.dto.MetricDetails;
import dev.alejandropardo.metrics.model.dto.TransactionDetails;
import dev.alejandropardo.metrics.model.dto.TransactionsAverage;

@Repository
public class MetricsServiceImpl implements MetricsService {

	public MetricsServiceImpl(NamedParameterJdbcTemplate template) {
		this.template = template;
	}

	NamedParameterJdbcTemplate template;

	private MapSqlParameterSource parameterSource = new MapSqlParameterSource();

	@Override
	public ResponseObject findForChart(TimelineValues timeline, boolean isFailure) {
		Instant startTime = Instant.now();
		List<Map<String, Object>> rows = null;
		if (isFailure) {
			String query = getSeriesSqlQuery(timeline, Queries.WITH_FAILURES_TIMELINE + Queries.SUMMARIZE_CHART_BY_TIMELINE);
			rows = template.queryForList(query, parameterSource);
		} else {
			String query = getSeriesSqlQuery(timeline, Queries.SUMMARIZE_CHART_BY_TIMELINE);
			rows = template.queryForList(query, parameterSource);
		}

		Metadata metadata = new Metadata("self", "1.0", startTime.toString(), Instant.now().toString(), null, rows.size());
		return new ResponseObject(rows, metadata);
	}

	private String getSeriesSqlQuery(TimelineValues timeline, String query) {
		return ("with hours as (" + Queries.GENERATE_SERIES + ") " + query).replace(":timeline", timeline.name().toLowerCase()).replace(":truncate", timeline.getTruncate());
	}

	@Override
	public ResponseObject findOperations(TimelineValues timeline, boolean isFailure) {
		Instant startTime = Instant.now();

		List<Map<String, Object>> rows = null;
		if (isFailure) {
			String query = (Queries.WITH_FAILURES_OPERATIONS + Queries.SUMMARIZED_METRICS_BY_TIMELINE).replace(":timeline", timeline.name());
			rows = template.queryForList(query, parameterSource);
		} else {
			String query = (Queries.WITH_OPERATIONS + Queries.SUMMARIZED_METRICS_BY_TIMELINE).replace(":timeline", timeline.name());
			rows = template.queryForList(query, parameterSource);
		}

		Metadata metadata = new Metadata("self", "1.0", startTime.toString(), Instant.now().toString(), null, rows.size());
		return new ResponseObject(rows, metadata);
	}

	@Override
	public ResponseObject findTransactions(TimelineValues timeline) {
		Instant startTime = Instant.now();
		var rows = template.query(getSeriesSqlQuery(timeline, Queries.TRANSACTION_CHART_BY_TIMELINE), new MapSqlParameterSource(), new ResultSetExtractor<List<TransactionsAverage>>() {
			@Override
			public List<TransactionsAverage> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<TransactionsAverage> results = new ArrayList<>();
				while (rs.next()) {
					String key = rs.getString("hour");
					String transactionType = rs.getString("transaction_type");
					TransactionsAverage transaction = results.stream().filter(r -> r.getTimestamp().equalsIgnoreCase(key)).findFirst().orElse(null);
					if (transaction != null) {
						if (transactionType != null) {
							transaction.getValues().put(transactionType, rs.getInt("count"));
						}
					} else {
						transaction = new TransactionsAverage(key, new HashMap<>());
						results.add(transaction);
					}
					if (transactionType != null) {
						transaction.getValues().put(transactionType, rs.getInt("count"));
					}
				}
				return results;
			}
		});

		Set<String> values = rows.stream().flatMap(r -> r.getValues().keySet().stream()).filter(r -> r != null).collect(Collectors.toSet());

		Map<String, Object> response = Map.of("keys", values, "values", rows);

		Metadata metadata = new Metadata("self", "1.0", startTime.toString(), Instant.now().toString(), null, rows.size());
		return new ResponseObject(response, metadata);
	}

	@Override
	public ResponseObject findTransactionsList(TimelineValues timeline) {
		Instant startTime = Instant.now();

		var rows = template.query(Queries.TRANSACTION_METRICS_BY_TIMELINE.replace(":timeline", timeline.name()), new MapSqlParameterSource(), new ResultSetExtractor<List<Transaction>>() {
			@Override
			public List<Transaction> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<Transaction> results = new ArrayList<>();
				while (rs.next()) {
					String level = rs.getString("transaction_level");
					String transactionValue = rs.getString("transaction_value");
					String code = rs.getString("transaction_code");

					String value = transactionValue;

					if (level == null && transactionValue == null) {
						value = "Response Code " + code;
					} else if (level != null) {
						value = level + " " + transactionValue;
					}

					Transaction transaction = new Transaction(rs.getString("transaction_uuid"), rs.getString("metric_uuid"), rs.getString("type"), rs.getString("name"), rs.getTimestamp("transaction_timestamp").toLocalDateTime(), level, value, code == null ? null : Integer.parseInt(code));
					results.add(transaction);
				}
				return results;
			}
		});

		Metadata metadata = new Metadata("self", "1.0", startTime.toString(), Instant.now().toString(), null, rows.size());
		return new ResponseObject(rows, metadata);
	}

	@Override
	public ResponseObject findByMetricId(String uuid) {
		var metric = template.queryForObject(Queries.METRIC_BY_UUID, new MapSqlParameterSource().addValue("uuid", uuid), new RowMapper<MetricDetails>() {
			@Override
			public MetricDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new MetricDetails(rs.getString("name"), rs.getTimestamp("timestamp").toLocalDateTime(), rs.getInt("duration"), rs.getString("description"), null);
			}
		});
		metric.setTransactions(template.query(Queries.TRANSACTION_BY_METRIC_UUID, new MapSqlParameterSource().addValue("metric_uuid", uuid), new ResultSetExtractor<List<TransactionDetails>>() {
			@Override
			public List<TransactionDetails> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<TransactionDetails> results = new ArrayList<>();
				while (rs.next()) {
					String level = rs.getString("level");
					String transactionValue = rs.getString("value");
					String code = rs.getString("code");
					String value = transactionValue;

					if (level == null && transactionValue == null) {
						value = "Response Code " + code;
					} else if (level != null) {
						value = level + " " + transactionValue;
					}

					TransactionDetails transaction = new TransactionDetails(rs.getString("name"), rs.getTimestamp("timestamp").toLocalDateTime(), 0L, LogType.valueOf(rs.getString("type")), value);
					results.add(transaction);
				}
				var iter = results.listIterator();
				while (iter.hasNext()) {
					if (iter.hasPrevious()) {
						var prev = iter.previous();
						iter.next();
						var next = iter.next();
						prev.setDuration(Duration.between(prev.getTimestamp(), next.getTimestamp()).toMillis());
					} else {
						iter.next();
					}
				}
				return results;
			}
		}));
		if(!metric.getTransactions().isEmpty()) {
			Long finalTime = metric.getTimestamp().toInstant(ZoneOffset.UTC).toEpochMilli() + metric.getDuration();
			TransactionDetails detail = metric.getTransactions().get(metric.getTransactions().size() - 1);
			Long lastTransactionTime = detail.getTimestamp().toInstant(ZoneOffset.UTC).toEpochMilli();
			detail.setDuration(finalTime - lastTransactionTime);
		}
		

		Instant startTime = Instant.now();
		Metadata metadata = new Metadata("self", "1.0", startTime.toString(), Instant.now().toString(), null, 0);
		return new ResponseObject(metric, metadata);
	}

	@Override
	@Transactional(rollbackFor = { SQLException.class, DuplicateKeyException.class })
	public void insertMetric(MetricRequest metric) {
		final String sql = "INSERT INTO METRICS(METRIC_UUID, NAME, METRIC_TIMESTAMP, DURATION_MS, DESCRIPTION) values (:metricUuid, :name, :metricTimestamp, :durationMS, :description)";
		SqlParameterSource param = new MapSqlParameterSource().addValue("metricUuid", metric.getUuid()).addValue("name", metric.getName()).addValue("metricTimestamp", metric.getMetricTimestamp()).addValue("durationMS", metric.getDuration()).addValue("description", metric.getDescription());
		template.update(sql, param);
		metric.getTransactions().stream().map(t -> new Transaction(UUID.randomUUID().toString(), metric.getUuid(), t.getType(), t.getName(), t.getTransactionTimestamp(), t.getLevel(), t.getValue(), t.getCode())).forEach(t -> insertTransaction(t));
	}

	private void insertTransaction(Transaction transaction) {
		final String sql = "INSERT INTO TRANSACTIONS(TRANSACTION_UUID, NAME, METRIC_UUID, TRANSACTION_TIMESTAMP, TYPE, TRANSACTION_LEVEL, TRANSACTION_VALUE, TRANSACTION_CODE) values(:transactionUuid, :name, :metricUuid, :transactionTimestamp, :type, :transactionLevel, :transactionValue, :transactionCode)";
		SqlParameterSource param = new MapSqlParameterSource().addValue("transactionUuid", transaction.getTransactionUuid()).addValue("name", transaction.getName()).addValue("metricUuid", transaction.getMetricUuid()).addValue("transactionTimestamp", transaction.getTransactionTimestamp())
				.addValue("type", transaction.getType()).addValue("transactionLevel", transaction.getTransactionLevel()).addValue("transactionValue", transaction.getTransactionValue()).addValue("transactionCode", transaction.getCode());
		template.update(sql, param);
	}

}