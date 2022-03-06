package dev.alejandropardo.metrics.model.dao;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
	private String transactionUuid;
	private String metricUuid;
	private String type;
	private String name;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime transactionTimestamp;
	private String transactionLevel;
	private String transactionValue;
	private Integer code;
}