package dev.alejandropardo.metrics;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import dev.alejandropardo.metrics.controller.requests.MetricRequest;
import dev.alejandropardo.metrics.controller.requests.TransactionRequest;
import dev.alejandropardo.metrics.controller.response.ResponseObject;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { MetricsApplication.class })
@ActiveProfiles({ "standalone", "test" })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class EndpointTests {

	@Autowired
	private TestRestTemplate testRestTemplate;
	
	private final String url = "/v1/metrics/api";
	private final String getUrl = "/v1/metrics/api?_timeline=:timeline&_transaction=:transaction";
	private final String uuid = UUID.randomUUID().toString();
	
	private void makeGETRequest(String timeline, String transaction) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json; charset=utf-8");
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		String useCase = getUrl.replace(":timeline", timeline).replace(":transaction", transaction);
		
		ResponseEntity<ResponseObject> exchange = this.testRestTemplate.exchange(URI.create(useCase), HttpMethod.GET, entity, ResponseObject.class);
		
		Assertions.assertEquals(HttpStatus.OK.value(), exchange.getStatusCodeValue());
		Assertions.assertNotNull(exchange.getBody());
		if (exchange.getStatusCodeValue() == HttpStatus.OK.value()) {
			ResponseObject object = exchange.getBody();
			Assertions.assertNotNull(object);
		}
	}

	@Test
	@Order(1)
	void getEmpty() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json; charset=utf-8");
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ResponseEntity<ResponseObject> exchange = this.testRestTemplate.exchange(URI.create(url), HttpMethod.GET, entity, ResponseObject.class);

		Assertions.assertEquals(HttpStatus.OK.value(), exchange.getStatusCodeValue());
		Assertions.assertNotNull(exchange.getBody());
		if (exchange.getStatusCodeValue() == HttpStatus.OK.value()) {
			ResponseObject object = (ResponseObject) exchange.getBody();
			Assertions.assertNotNull(object);
		}
	}

	@Test
	@Order(2)
	void postMetrics() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json; charset=utf-8");
		List<TransactionRequest> transactions = List.of(
			new TransactionRequest("REQUEST", null, "GET /api/metrics", LocalDateTime.now(), null, 200),
			new TransactionRequest("DEPENDENCY", null, "org.postgresql", LocalDateTime.now().plusSeconds(1), "select * from metrics", null),
			new TransactionRequest("TRACE", "INFO", null, LocalDateTime.now().plusSeconds(2), "All Metrics requested", null)
		);
		
		MetricRequest body = new MetricRequest(this.uuid, "GET /api/metrics", LocalDateTime.now(), null, 2010, transactions);
		
		HttpEntity<MetricRequest> entity = new HttpEntity<>(body, headers);

		ResponseEntity<ResponseObject> exchange = this.testRestTemplate.exchange(URI.create(url), HttpMethod.POST, entity, ResponseObject.class);

		Assertions.assertEquals(HttpStatus.OK.value(), exchange.getStatusCodeValue());
	}

	@Test
	@Order(3)
	void getMetricsSummarize() {
		makeGETRequest("WEEK", "AVERAGE");
		makeGETRequest("DAY", "AVERAGE");
		makeGETRequest("HOUR", "AVERAGE");
		makeGETRequest("MINUTE", "AVERAGE");
		
		makeGETRequest("WEEK", "OPERATIONS");
		makeGETRequest("DAY", "OPERATIONS");
		makeGETRequest("HOUR", "OPERATIONS");
		makeGETRequest("MINUTE", "OPERATIONS");
	}
	
	@Test
	@Order(4)
	void getMetricsFailures() {
		makeGETRequest("WEEK", "FAILURE_AVERAGE");
		makeGETRequest("DAY", "FAILURE_AVERAGE");
		makeGETRequest("HOUR", "FAILURE_AVERAGE");
		makeGETRequest("MINUTE", "FAILURE_AVERAGE");
		
		makeGETRequest("WEEK", "FAILURE_OPERATIONS");
		makeGETRequest("DAY", "FAILURE_OPERATIONS");
		makeGETRequest("HOUR", "FAILURE_OPERATIONS");
		makeGETRequest("MINUTE", "FAILURE_OPERATIONS");
	}
	
	@Test
	@Order(5)
	void getTransactions() {
		makeGETRequest("DAY", "TRANSACTIONS_AVERAGE");
		makeGETRequest("HOUR", "TRANSACTIONS_AVERAGE");
		makeGETRequest("MINUTE", "TRANSACTIONS_AVERAGE");
		
		makeGETRequest("WEEK", "TRANSACTIONS");
		makeGETRequest("DAY", "TRANSACTIONS");
		makeGETRequest("HOUR", "TRANSACTIONS");
		makeGETRequest("MINUTE", "TRANSACTIONS");
	}
	
	@Test
	@Order(6)
	void getDetails() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json; charset=utf-8");
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		String useCase = url + "/" + uuid;
		
		ResponseEntity<ResponseObject> exchange = this.testRestTemplate.exchange(URI.create(useCase), HttpMethod.GET, entity, ResponseObject.class);
		
		Assertions.assertEquals(HttpStatus.OK.value(), exchange.getStatusCodeValue());
		Assertions.assertNotNull(exchange.getBody());
		if (exchange.getStatusCodeValue() == HttpStatus.OK.value()) {
			ResponseObject object = exchange.getBody();
			Assertions.assertNotNull(object);
		}
	}

}
