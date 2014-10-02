package fortscale.web.rest;

import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.DataQueryRunner;
import fortscale.dataqueries.querygenerators.DataQueryRunnerFactory;
import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.EventScore;
import fortscale.services.fe.ClassifierService;
import fortscale.web.beans.DataBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by rotemn on 8/24/2014.
 */
public class ApiControllerTest {


	@Mock
	private JdbcOperations impalaJdbcTemplate;

	@Mock
	private DataQueryRunnerFactory dataQueryRunnerFactory;

	@InjectMocks
	private ApiController controller;

	private MockMvc mockMvc;


	private static final String QUERY_1 = "select foo";
	private static final String QUERY_2 = "select bar";
	private static final String QUERY_LIMIT = " LIMIT " + ApiController.CACHE_LIMIT + " OFFSET ";
	public static final String QUERY_1_OFFSET_200 = QUERY_1 + QUERY_LIMIT + "200";
	public static final String QUERY_2_OFFSET_0 = QUERY_2 + QUERY_LIMIT + "0";
	public static final String QUERY_1_OFFSET_0 = QUERY_1 + QUERY_LIMIT + "0";
	public static final Integer PAGE_SIZE = 20;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		// create 190 results - less than complete bulk
		List<Map<String, Object>> resultsMap = new LinkedList<>();
		for (int i=0; i < ApiController.CACHE_LIMIT - 10; i++) {
			resultsMap.add(new HashMap<String, Object>());
		}
		when(impalaJdbcTemplate.query(eq(QUERY_1_OFFSET_0), any(ColumnMapRowMapper.class))).thenReturn(resultsMap);
		when(impalaJdbcTemplate.query(eq(QUERY_1_OFFSET_200), any(ColumnMapRowMapper.class))).thenReturn(resultsMap);
		when(impalaJdbcTemplate.query(eq(QUERY_2_OFFSET_0), any(ColumnMapRowMapper.class))).thenReturn(resultsMap);


		// create mock for queries factory
		DataQueryRunner dataQueryRunner = Mockito.mock(DataQueryRunner.class);
		when(dataQueryRunner.generateQuery(any(DataQueryDTO.class))).thenReturn("select A from B");
		DataBean<List<Map<String, Object>>> dataBean = new DataBean<List<Map<String, Object>>>();
		dataBean.setData(new ArrayList<Map<String, Object>>());
		when(dataQueryRunner.executeQuery(any(String.class))).thenReturn(dataBean);
		when(dataQueryRunnerFactory.getDataQueryRunner(any(DataQueryDTO.class))).thenReturn(dataQueryRunner);


		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}


	@Test
	public void testInvestigate() throws Exception {

		// first request - page 1 from block not in cache

		mockMvc.perform(get("/api/investigate")
										.param("pageSize", PAGE_SIZE.toString())
										.param("page", "1") // page 1
										.param("query", QUERY_1)
										.param("useCache", "true") // use cache
										.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType("application/json;charset=UTF-8"));

		verify(impalaJdbcTemplate, times(1)).query(
						eq(QUERY_1_OFFSET_0), any(ColumnMapRowMapper.class)); // no data in cache, 1 call to impala service


		// second request - page 2 from block in cache

		mockMvc.perform(get("/api/investigate")
										.param("pageSize", PAGE_SIZE.toString())
										.param("page", "2")// page 2
										.param("query", QUERY_1)
										.param("useCache", "true")// use cache
										.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType("application/json;charset=UTF-8"));

		verify(impalaJdbcTemplate, times(1)).query(
						eq(QUERY_1_OFFSET_0), any(ColumnMapRowMapper.class)); // Data in cache, no more calls to impala


		// third request - not using cache false

		mockMvc.perform(get("/api/investigate")
										.param("pageSize", PAGE_SIZE.toString())
										.param("page", "2")// page 2
										.param("query", QUERY_1)
										.param("useCache", "false")// don't use cache
										.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType("application/json;charset=UTF-8"));

		verify(impalaJdbcTemplate, times(2)).query(
						eq(QUERY_1_OFFSET_0), any(ColumnMapRowMapper.class)); // Don't use cache, call impala

		// forth request - last page (not a full page)

		mockMvc.perform(get("/api/investigate")
										.param("pageSize", PAGE_SIZE.toString())
										.param("page", "10")// last page
										.param("query", QUERY_1)
										.param("useCache", "true")// use cache
										.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType("application/json;charset=UTF-8"));

		verify(impalaJdbcTemplate, times(2)).query(
						eq(QUERY_1_OFFSET_0), any(ColumnMapRowMapper.class)); // Data in cache, no more calls to impala

		// fifth request - different query

		verify(impalaJdbcTemplate, times(0)).query(
						eq(QUERY_2_OFFSET_0), any(ColumnMapRowMapper.class));

		mockMvc.perform(get("/api/investigate")
										.param("pageSize", PAGE_SIZE.toString())
										.param("page", "1")// page
										.param("query", QUERY_2) // different query
										.param("useCache", "true")// use cache
										.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType("application/json;charset=UTF-8"));

		verify(impalaJdbcTemplate, times(1)).query(
						eq(QUERY_2_OFFSET_0), any(ColumnMapRowMapper.class));

		// next offset

		mockMvc.perform(get("/api/investigate")
										.param("pageSize", PAGE_SIZE.toString())
										.param("page", "12") // page 12
										.param("query", QUERY_1)
										.param("useCache", "true") // use cache
										.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType("application/json;charset=UTF-8"));

		verify(impalaJdbcTemplate, times(1)).query(eq(
										QUERY_1_OFFSET_200), any(ColumnMapRowMapper.class)); // no data in cache, 1 call to impala service




	}

	@Test
	public void testInvestigateObject() throws Exception {

		String jsonQuery = "{\"fields\":[{\"id\":\"source_ip\"},{\"id\":\"destination_machine\"},{\"id\":\"source_machine\"},{\"id\":\"is_sensitive_machine\"},{\"id\":\"destination_machine_score\"},{\"id\":\"source_machine_score\"},{\"id\":\"event_time_score\"},{\"alias\":\"type\",\"value\":\"AD\"},{\"id\":\"event_time\",\"alias\":\"time\"},{\"id\":\"normalized_username\",\"alias\":\"username\"},{\"alias\":\"country\",\"value\":null}],\"conditions\":[{\"type\":\"term\",\"operator\":\"AND\",\"terms\":[{\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"id\":\"event_score\",\"value\":50},{\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"id\":\"event_time\",\"value\":\"1411592400\"},{\"type\":\"term\",\"operator\":\"OR\",\"terms\":[{\"operator\":\"lesserThanOrEquals\",\"type\":\"field\",\"id\":\"event_time\",\"value\":\"1412283599\"},{\"operator\":\"equals\",\"type\":\"field\",\"id\":\"normalized_username\",\"value\":\"omrik@fortscale.dom\"}]}]}],\"entities\":[\"ad_logins\"],\"sort\":[{\"field\":\"event_score\",\"direction\":\"DESC\"},{\"field\":\"event_time\",\"direction\":\"DESC\"}],\"limit\":20,\"offset\":0}";

		mockMvc.perform(get("/api/dataQuery")
										.param("pageSize", PAGE_SIZE.toString())
										.param("page", "1") // page 1
										.param("dataQuery", jsonQuery)
										.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType("application/json;charset=UTF-8"));

		// TODO verify real DataQueryDTO and not any(DataQueryDTO.class)
		verify(dataQueryRunnerFactory, times(1)).getDataQueryRunner(any(DataQueryDTO.class)); // no data in cache, 1 call to impala service

	}


}
