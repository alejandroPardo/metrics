package dev.alejandropardo.metrics.controller;

import java.time.LocalDateTime;
import java.util.List;

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
import dev.alejandropardo.metrics.controller.response.ResponseObject;
import dev.alejandropardo.metrics.model.dao.Metric;
import dev.alejandropardo.metrics.model.service.MetricsService;

@RestController
@RequestMapping("/v1/metrics/api")
public class MetricsController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Resource
	MetricsService metricsService;

	@GetMapping
	public List<Metric> getMetrics() {
		LOGGER.info("All Metrics requested");
		return metricsService.findAll();
	}
	
	@GetMapping(path = "/summarize")
	public ResponseObject getSummarizedMetrics(
			@RequestParam(required = false, name = "_timestampFrom") LocalDateTime timestampFrom,
			@RequestParam(required = false, name = "_timestampTo") LocalDateTime timestampTo,
			@RequestParam(required = false, name = "_transactions") boolean transactions,
			@RequestParam(required = false, name = "_timeline") TimelineValues timeline) {
		LOGGER.info("All Metrics requested");
		if(transactions) {
			return metricsService.findForChart(timeline);
		} else {
			return metricsService.findSummarizedMetrics(timestampFrom, timestampTo);
		}
	}

	@PostMapping
	public void createMetric(@RequestBody MetricRequest met) {
		metricsService.insertMetric(met);
	}

}