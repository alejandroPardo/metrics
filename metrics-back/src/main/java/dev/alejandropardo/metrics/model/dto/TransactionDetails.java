package dev.alejandropardo.metrics.model.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import dev.alejandropardo.metrics.model.dao.LogType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDetails {
	private String name;
	private LocalDateTime timestamp;
	private Long duration;
	private LogType type;
	private String value;
	
}
