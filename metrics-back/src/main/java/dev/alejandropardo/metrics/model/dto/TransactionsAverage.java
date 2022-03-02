package dev.alejandropardo.metrics.model.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionsAverage {
	private String timestamp;
	private Map<String, Object> values = new HashMap<>();
}
