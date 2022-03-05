package dev.alejandropardo.metrics.model.service;

import dev.alejandropardo.metrics.controller.requests.MetricRequest;
import dev.alejandropardo.metrics.controller.requests.TimelineValues;
import dev.alejandropardo.metrics.controller.response.ResponseObject;

public interface MetricsService {
	
	void insertMetric(MetricRequest metric);

	ResponseObject findOperations(TimelineValues timeline, boolean isFailure);

	ResponseObject findForChart(TimelineValues timeline, boolean isFailure);

	ResponseObject findTransactions(TimelineValues timeline);

	ResponseObject findTransactionsList(TimelineValues timeline);

	ResponseObject findByMetricId(String uuid);

}