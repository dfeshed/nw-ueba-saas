package fortscale.web.rest;

import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.EventScore;
import fortscale.services.fe.ClassifierService;
import fortscale.web.beans.DataBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
	@InjectMocks
	private ApiController controller;

	private MockMvc mockMvc;


	private static final String QUERY_1 = "select foo";
	private static final String QUERY_2 = "select bar";
	private static final String QUERY_3 = "select a from b limit 45";
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




		// known bug in UI: sending "page" for graphs (with limit)

		mockMvc.perform(get("/api/investigate")
										.param("pageSize", PAGE_SIZE.toString())
										.param("page", "1") // page 1
										.param("query", QUERY_3)
										.param("useCache", "true") // use cache
										.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType("application/json;charset=UTF-8"));

		verify(impalaJdbcTemplate, times(1)).query(eq(
										QUERY_3), any(ColumnMapRowMapper.class)); // query should be sent as-is to impala

		mockMvc.perform(get("/api/investigate")
										.param("pageSize", PAGE_SIZE.toString())
										.param("page", "1") // page 1
										.param("query", QUERY_3)
										.param("useCache", "true") // use cache
										.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType("application/json;charset=UTF-8"));

		verify(impalaJdbcTemplate, times(1)).query(eq(
										QUERY_3), any(ColumnMapRowMapper.class)); // should use cache




	}


}
