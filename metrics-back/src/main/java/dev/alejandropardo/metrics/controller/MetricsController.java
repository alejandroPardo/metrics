package dev.alejandropardo.metrics.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.alejandropardo.metrics.controller.requests.MetricRequest;
import dev.alejandropardo.metrics.controller.requests.TimelineValues;
import dev.alejandropardo.metrics.controller.requests.Transactions;
import dev.alejandropardo.metrics.controller.response.ResponseObject;
import dev.alejandropardo.metrics.model.service.MetricsService;

@RestController
@RequestMapping("/v1/metrics/api")
public class MetricsController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Resource
	MetricsService metricsService;

	/*@GetMapping
	public List<Metric> getMetrics() {
		LOGGER.info("All Metrics requested");
		return metricsService.findAll();
	}*/
	
	@GetMapping()
	public ResponseObject getSummarizedMetrics(
			@RequestParam(name = "_transaction", defaultValue = "AVERAGE") Transactions transaction,
			@RequestParam(name = "_timeline", defaultValue = "WEEK") TimelineValues timeline) {
		LOGGER.info("All Metrics requested");
		ResponseObject response = null;
		switch(transaction) {
		case AVERAGE:
			response = metricsService.findForChart(timeline, false);
			break;
		case FAILURE_AVERAGE:
			response = metricsService.findForChart(timeline, true);
			break;
		case FAILURE_OPERATIONS:
			response = metricsService.findOperations(timeline, true);
			break;
		case OPERATIONS:
			response = metricsService.findOperations(timeline, false); 
			break;
		case TRANSACTIONS_AVERAGE:
			response = metricsService.findTransactions(timeline); 
			break;
		case TRANSACTIONS:
			response = metricsService.findTransactionsList(timeline); 
			break;
		default:
			break;
		}
		return response;
	}

	@PostMapping
	public void createMetric(@RequestBody MetricRequest met) {
		metricsService.insertMetric(met);
	}

}