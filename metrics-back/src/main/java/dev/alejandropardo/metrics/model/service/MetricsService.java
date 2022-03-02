package dev.alejandropardo.metrics.model.service;

import java.util.List;

import dev.alejandropardo.metrics.controller.requests.MetricRequest;
import dev.alejandropardo.metrics.controller.requests.TimelineValues;
import dev.alejandropardo.metrics.controller.response.ResponseObject;
import dev.alejandropardo.metrics.model.dao.Metric;
import dev.alejandropardo.metrics.model.dao.Transaction;

public interface MetricsService {

	List<Metric> findAll();
	
	List<Transaction> findAllByMetricId();

	void insertTransaction(Transaction emp);

	void insertMetric(MetricRequest metric);

	ResponseObject findOperations(TimelineValues timeline, boolean isFailure);

	ResponseObject findForChart(TimelineValues timeline, boolean isFailure);

	ResponseObject findTransactions(TimelineValues timeline);

	ResponseObject findTransactionsList(TimelineValues timeline);

}