package dev.alejandropardo.metrics.model.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import dev.alejandropardo.metrics.model.dao.Metric;

public class MetricsRowMapper implements RowMapper<Metric> {

	@Override
	public Metric mapRow(ResultSet rs, int arg1) throws SQLException {
		Metric met = new Metric();
		met.setMetricUuid(rs.getString("METRIC_UUID"));
		met.setName(rs.getString("NAME"));
		met.setMetricTimestamp(rs.getTimestamp("METRIC_TIMESTAMP").toLocalDateTime());
		met.setDescription(rs.getString("DESCRIPTION"));
		met.setDuration(rs.getInt("DURATION_MS"));

		return met;
	}

}