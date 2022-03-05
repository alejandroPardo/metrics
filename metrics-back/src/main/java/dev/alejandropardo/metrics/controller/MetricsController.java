package dev.alejandropardo.metrics.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	@Resource
	MetricsService metricsService;

	@GetMapping(path = "/{uuid}")
	public ResponseObject getMetricByID(@PathVariable("uuid") String uuid) {
		return metricsService.findByMetricId(uuid);
	}
	
	@GetMapping()
	public ResponseObject getMetrics(
			@RequestParam(name = "_transaction", defaultValue = "AVERAGE") Transactions transaction,
			@RequestParam(name = "_timeline", defaultValue = "WEEK") TimelineValues timeline) {
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