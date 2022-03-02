package dev.alejandropardo.metrics.controller.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Transactions {
	
	AVERAGE, OPERATIONS, FAILURE_AVERAGE, FAILURE_OPERATIONS, TRANSACTIONS, TRANSACTIONS_AVERAGE;
	
}