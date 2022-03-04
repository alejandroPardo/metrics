package dev.alejandropardo.metrics.controller.requests;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MetricRequest {
	private String uuid;
	private String name;
	private LocalDateTime metricTimestamp;
	private String description;
	private Integer duration;
	private List<TransactionRequest> transactions;
}