package dev.alejandropardo.metrics.batch;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dev.alejandropardo.metrics.controller.requests.MetricRequest;
import dev.alejandropardo.metrics.controller.requests.TransactionRequest;
import dev.alejandropardo.metrics.model.dao.LogLevel;
import dev.alejandropardo.metrics.model.dao.LogType;
import dev.alejandropardo.metrics.model.service.MetricsService;

@Component
public class GenerateMetrics {
		
	@Autowired
	private MetricsService metricsService;
	
	Random rand = new Random();
	
	/*requests/exceptions*/
	private List<String> urls = List.of("v1/api/sales", "/v1/api/units", "/v1/api/returns", "/v1/api/companies", "v1/api/stock", "v1/api/employees", "v1/api/metrics", "v1/api/reports", "v1/api/tasks", "v1/api/schedule");
	private List<String> methods = List.of("GET", "POST", "PUT", "DELETE");
	private List<String> responseCodesKO = List.of("400", "502", "401", "403");
	
	private Integer lowResponseTime = 100;
	private Integer highResponseTime = 20000;
	
	private List<LogType> types = List.of(LogType.TRACE, LogType.DEPENDENCY);
	private List<LogLevel> levels = List.of(LogLevel.DEBUG, LogLevel.WARN, LogLevel.INFO);
	/*traces*/
	private List<String> okLogs = List.of("Retrieved 1000 results from database", "Successfully created", "Successfully updated", "Retrieved all rows from database");
	private List<String> koLogs = List.of("Bad request on endpoint", "Cannot create record in database", "Cannot update record in database", "Parameters does not match the api");
	private List<String> forbiddenLogs = List.of("Invalid username/password in request", "The token received was not valid");
	private List<String> unauthorizedLogs = List.of("The user has no permissions for the endpoint", "The token is expired");
	

	/*dependencies*/
	private List<String> dependencies = List.of("Snowflake", "Oracle", "PostgreSQL", "MongoDB", "Couchbase", "External API", "Oauth token", "JWT Token");
	private List<String> tables = List.of("sales", "units", "returns", "companies", "stock", "employees", "metrics", "reports", "tasks", "schedule");
	
	@Scheduled(fixedDelayString = "#{new Double((T(java.lang.Math).random() + 1) * 1000).intValue()}")
	public void generateOKRequests() {
		Integer duration = rand.nextInt(highResponseTime-lowResponseTime) + lowResponseTime;
		String requestURL = methods.get(rand.nextInt(methods.size())) + " " + urls.get(rand.nextInt(urls.size()));
		LocalDateTime requestTime = LocalDateTime.now();
		List<TransactionRequest> transactions = new ArrayList<>();
		Integer durationAux = duration;
		LocalDateTime requestTimeAux = requestTime;
		Integer transactionNumber = rand.nextInt(5);
		for(int i = 0; i < transactionNumber-1; i++) {
			Integer durationStep = rand.nextInt(durationAux);
			durationAux -= durationStep;
			requestTimeAux = requestTimeAux.plus(durationStep, ChronoField.MILLI_OF_DAY.getBaseUnit());
			transactions.add(generateTransaction(false, durationStep, requestURL, requestTimeAux));
		}
		transactions.add(generateTransaction(true, durationAux, requestURL, requestTimeAux));
		
		MetricRequest req = new MetricRequest(UUID.randomUUID().toString(), requestURL, requestTime, "Response 200 OK", duration, transactions);
		metricsService.insertMetric(req);
		
	}
	
	private TransactionRequest generateTransaction(boolean isRequest, Integer duration, String url, LocalDateTime requestTime) {
		if(isRequest) {
			return new TransactionRequest(LogType.REQUEST.toString(), null, url, requestTime, null, 200);
		} else {
			LogType type = types.get(rand.nextInt(types.size()));
			TransactionRequest transaction = null;
			switch(type) {
			case DEPENDENCY:
				transaction = new TransactionRequest(LogType.DEPENDENCY.toString(), null, dependencies.get(rand.nextInt(dependencies.size())), requestTime, "select " + tables.get(rand.nextInt(tables.size())) + " " + tables.get(rand.nextInt(tables.size())) + " from schema."+tables.get(rand.nextInt(tables.size())), null);
				break;
			case TRACE:
				String level = levels.get(rand.nextInt(levels.size())).toString();
				transaction = new TransactionRequest(LogType.TRACE.toString(), level, "LOG " + level, requestTime, okLogs.get(rand.nextInt(okLogs.size())), null);
				break;
			default:
				break;
			}
			return transaction;
			
		}
	}

	@Scheduled(fixedDelayString = "#{new Double((T(java.lang.Math).random() + 1) * 4000).intValue()}")
	public void generateKORequests() {
		String koValue = responseCodesKO.get(rand.nextInt(responseCodesKO.size()));
		String requestURL = methods.get(rand.nextInt(methods.size())) + " " + urls.get(rand.nextInt(urls.size()));
		LocalDateTime requestTime = LocalDateTime.now();
		TransactionRequest request = new TransactionRequest(LogType.REQUEST.toString(), null, requestURL, requestTime, null, Integer.parseInt(koValue));
		List<TransactionRequest> transactions = new ArrayList<>();
		
		Integer duration = lowResponseTime;
		switch(koValue) {
		case "502":
			duration = highResponseTime + 1;
			transactions.add(new TransactionRequest(LogType.EXCEPTION.toString(), null, dependencies.get(rand.nextInt(dependencies.size())), requestTime, "Timeout retrieving data from: select " + tables.get(rand.nextInt(tables.size())) + " " + tables.get(rand.nextInt(tables.size())) + " from schema."+tables.get(rand.nextInt(tables.size())), null));
			transactions.add(request);
			break;
		case "400":
			duration = rand.nextInt(highResponseTime-lowResponseTime) + lowResponseTime;
			transactions.add(new TransactionRequest(LogType.TRACE.toString(), LogLevel.ERROR.toString(), "LOG ERROR", requestTime, koLogs.get(rand.nextInt(koLogs.size())), null));
			transactions.add(request);
			break;
		case "401":
			transactions.add(new TransactionRequest(LogType.TRACE.toString(), LogLevel.ERROR.toString(), "LOG ERROR", requestTime, unauthorizedLogs.get(rand.nextInt(unauthorizedLogs.size())), null));
			transactions.add(request);
			break;
		case "403":
			transactions.add(new TransactionRequest(LogType.TRACE.toString(), LogLevel.ERROR.toString(), "LOG ERROR", requestTime, forbiddenLogs.get(rand.nextInt(forbiddenLogs.size())), null));
			transactions.add(request);
			break;
		default:
			break;
		}
		MetricRequest metric = new MetricRequest(UUID.randomUUID().toString(), requestURL, requestTime, "RESPONSE "+koValue, duration, transactions);
		metricsService.insertMetric(metric);
	}
	
}