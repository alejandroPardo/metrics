package dev.alejandropardo.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import dev.alejandropardo.metrics.controller.requests.TimelineValues;
import dev.alejandropardo.metrics.controller.response.ResponseObject;
import dev.alejandropardo.metrics.model.dao.Transaction;
import dev.alejandropardo.metrics.model.dto.MetricDetails;
import dev.alejandropardo.metrics.model.dto.TransactionDetails;
import dev.alejandropardo.metrics.model.dto.TransactionsAverage;
import dev.alejandropardo.metrics.model.service.MetricsService;
import dev.alejandropardo.metrics.model.service.MetricsServiceImpl;

@TestInstance(Lifecycle.PER_CLASS)
@SuppressWarnings("unchecked")
public class ServiceTests {
	
	NamedParameterJdbcTemplate entityManager;
	MetricsService service;
	
	@BeforeAll
	public void setUp() {
		entityManager = mock(NamedParameterJdbcTemplate.class);
		service = new MetricsServiceImpl(entityManager);
	}
	
	@Test
	public void findForChart() {
		TimelineValues timeline = TimelineValues.WEEK;
		
		List<Map<String, Object>> rows = List.of(Map.of("data", "data"));
		Mockito.when(entityManager.queryForList(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class))).thenReturn(rows);
		
		ResponseObject findWeek = service.findForChart(timeline, false);
		assertEquals(findWeek.getMetadata().getRows(), 1);
		
		ResponseObject findWeekFailure = service.findForChart(timeline, true);
		assertEquals(findWeekFailure.getMetadata().getRows(), 1);
	}
	
	@Test
	public void findOperations() {
		TimelineValues timeline = TimelineValues.WEEK;
		
		List<Map<String, Object>> rows = List.of(Map.of("data", "data"));
		Mockito.when(entityManager.queryForList(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class))).thenReturn(rows);
		
		ResponseObject findWeek = service.findOperations(timeline, false);
		assertEquals(findWeek.getMetadata().getRows(), 1);
		
		ResponseObject findWeekFailure = service.findOperations(timeline, true);
		assertEquals(findWeekFailure.getMetadata().getRows(), 1);
	}
	
	
	@Test
	public void findTransactions() {
		TimelineValues timeline = TimelineValues.DAY;
		
		List<TransactionsAverage> rows = List.of(new TransactionsAverage(LocalDateTime.now().toString(), new HashMap<>()));
		Mockito.when(entityManager.query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),  Mockito.any(ResultSetExtractor.class))).thenReturn(rows);
		
		ResponseObject findWeek = service.findTransactions(timeline);
		assertEquals(findWeek.getMetadata().getRows(), 1);
	}
	
	@Test
	public void findTransactionsList() {
		TimelineValues timeline = TimelineValues.DAY;
		
		List<Transaction> rows = List.of(new Transaction());
		Mockito.when(entityManager.query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),  Mockito.any(ResultSetExtractor.class))).thenReturn(rows);
		
		ResponseObject findWeek = service.findTransactionsList(timeline);
		assertEquals(findWeek.getMetadata().getRows(), 1);
	}
	
	@Test
	public void findByMetricId() {		
		MetricDetails rows = new MetricDetails();
		Mockito.when(entityManager.queryForObject(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),  Mockito.any(RowMapper.class))).thenReturn(rows);
		List<TransactionDetails> details = new ArrayList<>();
		Mockito.when(entityManager.query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),  Mockito.any(ResultSetExtractor.class))).thenReturn(details);
		ResponseObject findWeek = service.findByMetricId(UUID.randomUUID().toString());
		assertEquals(findWeek.getMetadata().getRows(), 0);
	}

}
