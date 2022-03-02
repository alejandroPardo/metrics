package dev.alejandropardo.metrics.controller.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TimelineValues {

	WEEK	("select generate_series(date_trunc('hour', now()) - '1 week'::interval, date_trunc('hour', now()), '1 hour'::interval) as hour",
			"select hours.hour, count(metric_timestamp), coalesce(avg(duration_ms),0) as average from hours left join metrics on date_trunc('hour', metric_timestamp) = hours.hour :joins :where group by hours.hour order by hours.hour"),
	DAY		("select generate_series(date_trunc('hour', now()) - '1 day'::interval, date_trunc('hour', now()), '1 hour'::interval) as hour",
			"select hours.hour, count(metric_timestamp), coalesce(avg(duration_ms),0) as average from hours left join metrics on date_trunc('hour', metric_timestamp) = hours.hour :joins :where group by hours.hour order by hours.hour"),
	HOUR	("select generate_series(date_trunc('minute', now()) - '1 hour'::interval, date_trunc('minute', now()), '1 minute'::interval) as hour",
			"select hours.hour, count(metric_timestamp), coalesce(avg(duration_ms),0) as average from hours left join metrics on date_trunc('minute', metric_timestamp) = hours.hour :joins :where group by hours.hour order by hours.hour"),
	MINUTE	("select generate_series(date_trunc('second', now()) - '1 minute'::interval, date_trunc('second', now()), '1 second'::interval) as hour",
			"select hours.hour, count(metric_timestamp), coalesce(avg(duration_ms),0) as average from hours left join metrics on date_trunc('second', metric_timestamp) = hours.hour :joins :where group by hours.hour order by hours.hour");

	/** The name. */
	private final String sqlWith;
	
	private final String sql;

	public String getSqlQuery() {
		return "with hours as (" + this.sqlWith + ") " + this.sql;
	}
	
}