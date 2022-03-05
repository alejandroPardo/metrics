package dev.alejandropardo.metrics.model.service;

public final class Queries {
	//select generate_series(date_trunc('hour', now()) - '1 week'::interval, date_trunc('hour', now()), '1 hour'::interval) as hour
	//"select hours.hour, count(metric_timestamp), coalesce(avg(duration_ms),0) as average from hours left join metrics on date_trunc('hour', metric_timestamp) = hours.hour :joins :where group by hours.hour order by hours.hour"
	
	
	//with hours as (select generate_series(date_trunc('hour', now()) - '1 week'::interval, date_trunc('hour', now()), '1 hour'::interval) as hour) 
	//select metrics.name as name, count(metrics.name) as count, avg(metrics.duration_ms) as averageTime from metrics  where metric_timestamp >= (now() - interval '1 week')  group by metrics.name order by averageTime desc;
	/* MODALS */
	public static final String METRIC_BY_UUID = "select name, metric_timestamp as timestamp, duration_ms as duration, description from metrics where metric_uuid = :uuid";
	public static final String TRANSACTION_BY_METRIC_UUID = "select name, transaction_timestamp as timestamp, type, transaction_level as level, transaction_value as value, transaction_code as code from transactions where metric_uuid = :metric_uuid";
	
	/* WITH CLAUSES */
	public static final String GENERATE_SERIES = "select generate_series(date_trunc(':truncate', now()) - '1 :timeline'::interval, date_trunc(':truncate', now()), '1 :truncate'::interval) as hour";
	//public static final String 
	
	/* SUMMARIZE */
	public static final String SUMMARIZE_CHART_BY_TIMELINE = "select hours.hour, coalesce(avg(duration_ms),0) as average, count(metric_timestamp) from hours left join metrics on date_trunc(':truncate', metric_timestamp) = hours.hour :joins :where group by hours.hour order by hours.hour";
	public static final String SUMMARIZED_METRICS_BY_TIMELINE = "select metrics.name as name, avg(metrics.duration_ms) as averageTime, count(metrics.name) as count from metrics :joins where metric_timestamp >= (now() - interval '1 :timeline') :where group by metrics.name order by averageTime desc;";
	
	/* TRANSACTIONS */
	public static final String TRANSACTION_CHART_BY_TIMELINE = "select hours.hour, count(transaction_timestamp), transactions.type as transaction_type from hours left join transactions on date_trunc(':timeline', transaction_timestamp) = hours.hour group by hours.hour, transactions.type order by hours.hour";
	
}