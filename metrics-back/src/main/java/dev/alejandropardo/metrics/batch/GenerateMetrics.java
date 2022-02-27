package dev.alejandropardo.metrics.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GenerateMetrics {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	/*requests/exceptions*/
	private List<String> urls = List.of("v1/api/sales", "/v1/api/units", "/v1/api/returns", "/v1/api/companies", "v1/api/stock", "v1/api/employees", "v1/api/metrics", "v1/api/reports", "v1/api/tasks", "v1/api/schedule");
	private List<String> methods = List.of("GET", "POST", "PUT", "DELETE");
	private List<String> responseCodesKO = List.of("400", "500", "401", "403");
	
	/*traces*/
	private List<String> okLogs = List.of("Retrieved 1000 results from database", "Successfully created", "Successfully updated", "Retrieved all rows from database");
	private List<String> koLogs = List.of("Bad request on endpoint", "Cannot create record in database", "Cannot update record in database", "Parameters does not match the api");
	private List<String> errorLogs = List.of("Timeout retrieving data", "Unexpected error in server");
	private List<String> forbiddenLogs = List.of("Invalid username/password in request", "The token received was not valid");
	private List<String> unauthorizedLogs = List.of("The user has no permissions for the endpoint", "The token is expired");
	

	/*dependencies*/
	private List<String> dependencies = List.of("Snowflake", "Oracle", "PostgreSQL", "MongoDB", "Couchbase", "External API", "Oauth token", "JWT Token");
	private List<String> tables = List.of("sales", "units", "returns", "companies", "stock", "employees", "metrics", "reports", "tasks", "schedule");
	
	//@Scheduled(fixedDelay = 500)
	public void generateOKRequests() {
		//LOGGER.info("Creating OK metric");
	}
	
	//@Scheduled(fixedDelay = 5000)
	public void generateKORequests() {
		//LOGGER.info("Creating KO metric");
	}
	
}