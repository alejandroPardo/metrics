package dev.alejandropardo.metrics.model.dao;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Metric {
	private String metricUuid;
	private String name;
	private LocalDateTime metricTimestamp;
	private String description;
	private Integer duration;
}