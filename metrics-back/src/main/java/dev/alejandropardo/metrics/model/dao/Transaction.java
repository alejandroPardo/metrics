package dev.alejandropardo.metrics.model.dao;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Transaction {
	private String transactionUuid;
	private String metricUuid;
	private String type;
	private String name;
	private LocalDateTime transactionTimestamp;
	private String transactionLevel;
	private String transactionValue;
	private Integer code;
}