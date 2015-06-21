package fortscale.web.rest;

import fortscale.domain.core.Alert;
import fortscale.domain.core.AlertSeverity;
import fortscale.domain.core.EntityType;
import fortscale.domain.core.dao.AlertsRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/alerts-context-test.xml"})
public class ApiAlertsControllerTest {


	@Mock
	private AlertsRepository alertsDao;
	@InjectMocks
	private ApiAlertController subject;
	
	private MockMvc mockMvc;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);


		this.mockMvc = MockMvcBuilders.standaloneSetup(subject).build();
	}
	

	@Test
	public void list_all_alerts() throws Exception {
		// set up alerts repository mocked behavior
		List<Alert> alerts = new ArrayList<Alert>();
		alerts.add(new Alert("1", 1, 2, EntityType.USER, "user1", "rule1", null, "a", 90, AlertSeverity.CRITICAL, "a", "a"));
		alerts.add(new Alert("2", 1, 2, EntityType.USER, "user1", "rule1", null, "a", 90, AlertSeverity.CRITICAL, "a", "a"));

		when(alertsDao.findAll(any(PageRequest.class), anyInt())).thenReturn(alerts);

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(get("/api/alerts").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andReturn();

		//validate
		assertTrue( result.getResponse().getContentAsString().contains("{\"id\":null,\"ts_start\":1,\"ts_end\":2,\"entity_type\":\"USER\",\"entity_name\":\"user1\",\"rule\":\"rule1\",\"evidences\":null,\"cause\":\"a\",\"score\":90,\"severity\":\"CRITICAL\",\"status\":\"a\",\"comment\":\"a\"}"));
		verify(alertsDao).findAll(any(PageRequest.class), anyInt());
	}
	
	@Test
	public void add_alert() throws Exception {

		String sAlert = "{\n" +
				"    \"ts_start\": 1,\n" +
				"    \"ts_end\": 2,\n" +
				"    \"entity_type\": \"USER\",\n" +
				"    \"entity_name\": \"user1\",\n" +
				"    \"rule\": \"rule1\",\n" +
				"    \"evidences\": [],\n" +
				"    \"cause\": \"a\",\n" +
				"    \"score\": 90,\n" +
				"    \"severity\": \"CRITICAL\",\n" +
				"    \"status\": \"ok\",\n" +
				"    \"comment\": \"a\"\n" +
				"}";

		String sAlertLittle = "{\"entity_name\": \"user1\",\"rule\": \"rule1\"}";

		Alert alert = new Alert("1", 1, 2, EntityType.USER, "user1", "rule1", null, "a", 90, AlertSeverity.CRITICAL, "a", "a");
//		Gson gson = new Gson();
//		String json = gson.toJson(alert);

		// perform rest call to the controller
		MvcResult result =
				mockMvc.perform(post("/api/alerts")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(sAlertLittle)
						)
						.andDo(print())
						.andExpect(status().isOk())
			.andReturn();

		//validate
		assertNotNull(result.getResponse().getContentAsString());
//		verify(alertsDao).add(any(Alert.class));
	}


}
