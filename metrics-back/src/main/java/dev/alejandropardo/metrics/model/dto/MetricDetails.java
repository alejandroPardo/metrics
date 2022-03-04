package dev.alejandropardo.metrics.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetricDetails {
	private String name;
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime timestamp;
	private Integer duration;
	private String description;
	
	private List<Transaction> transactions;
	
}
