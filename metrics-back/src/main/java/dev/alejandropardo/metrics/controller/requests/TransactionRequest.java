package dev.alejandropardo.metrics.controller.requests;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionRequest {
	private String type;
	private String level;
	private String name;
	private LocalDateTime transactionTimestamp;
	private String value;
	private Integer code;
}