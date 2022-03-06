package dev.alejandropardo.metrics.model.service;

public final class Queries {
	/* MODALS */
	public static final String METRIC_BY_UUID = "select name, metric_timestamp as timestamp, duration_ms as duration, description from metrics where metric_uuid = :uuid";
	public static final String TRANSACTION_BY_METRIC_UUID = "select name, transaction_timestamp as timestamp, type, transaction_level as level, transaction_value as value, transaction_code as code from transactions where metric_uuid = :metric_uuid";
	
	/* WITH CLAUSES */
	public static final String GENERATE_SERIES = "select generate_series(date_trunc(':truncate', now()) - '1 :timeline'::interval, date_trunc(':truncate', now()), '1 :truncate'::interval) as hour";
	public static final String WITH_FAILURES_TIMELINE = ", metrics as (select duration_ms, metric_timestamp from metrics inner join transactions on transactions.metric_uuid = metrics.metric_uuid where transaction_code >= 400) ";
	public static final String WITH_OPERATIONS = "with metrics as (select metrics.name, metrics.duration_ms, metric_timestamp, transactions.transaction_code as code from metrics inner join transactions on transactions.metric_uuid = metrics.metric_uuid where transaction_code < 400) ";
	public static final String WITH_FAILURES_OPERATIONS = "with metrics as (select metrics.name, metrics.duration_ms, metric_timestamp, transactions.transaction_code as code from metrics inner join transactions on transactions.metric_uuid = metrics.metric_uuid where transaction_code >= 400) ";
	
	/* SUMMARIZE */
	public static final String SUMMARIZE_CHART_BY_TIMELINE = "select hours.hour, coalesce(avg(duration_ms),0) as average, count(metric_timestamp) from hours left join metrics on date_trunc(':truncate', metric_timestamp) = hours.hour group by hours.hour order by hours.hour";
	public static final String SUMMARIZED_METRICS_BY_TIMELINE = "select metrics.name as name, avg(metrics.duration_ms) as averageTime, count(metrics.name) as count, code from metrics where metric_timestamp >= (now() - interval '1 :timeline') group by metrics.name, code order by count desc;";
	
	/* TRANSACTIONS */
	public static final String TRANSACTION_CHART_BY_TIMELINE = "select hours.hour, count(transaction_timestamp), transactions.type as transaction_type from hours left join transactions on date_trunc(':truncate', transaction_timestamp) = hours.hour group by hours.hour, transactions.type order by hours.hour";
	public static final String TRANSACTION_METRICS_BY_TIMELINE = "select transaction_uuid, metric_uuid, name, transaction_timestamp, type, transaction_level, transaction_value, transaction_code from transactions where transaction_timestamp >= (now() - interval '1 :timeline') order by transaction_timestamp desc;";
	
}