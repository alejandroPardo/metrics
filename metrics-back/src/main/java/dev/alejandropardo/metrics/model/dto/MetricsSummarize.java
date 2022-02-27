package dev.alejandropardo.metrics.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetricsSummarize {
	private String name;
	private Long count;
	private Double averageTime;
}
