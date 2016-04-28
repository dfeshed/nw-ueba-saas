package fortscale.web.rest;

import fortscale.domain.core.*;
import fortscale.domain.core.dao.rest.Alerts;
import fortscale.services.AlertsService;
import fortscale.services.EvidencesService;
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
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/alerts-context-test.xml"})
public class ApiAlertsControllerTest {


	@Mock
	private EvidencesService evidencesDao;
	@Mock
	private AlertsService alertsDao;
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
		List<Alert> alertsList = new ArrayList<Alert>();
		alertsList.add(new Alert("Alert1", 1, 2, EntityType.User, "user1", null, 1, 90, Severity.Critical, AlertStatus.Open, AlertFeedback.None, "a", "12345", AlertTimeframe.Daily));
		alertsList.add(new Alert("Alert2", 1, 2, EntityType.User, "user1", null, 1, 90, Severity.Critical, AlertStatus.Open, AlertFeedback.None, "a", "12345", AlertTimeframe.Daily));
		Alerts alerts = new Alerts();
		alerts.setAlerts(alertsList);

		when(evidencesDao.findByEvidenceTypeAndAnomalyValueIn(any(EvidenceType.class), any(String[].class))).
				thenReturn(null);
		when(alertsDao.findAll(any(PageRequest.class))).thenReturn(alerts);

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(get("/api/alerts?sortField=startTime&sortDirection=DESC&page=1&size=20").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andReturn();

		//validate
		assertTrue(result.getResponse().getContentAsString().contains("\"startDate\":1,\"endDate\":2,\"entityType\":\"User\",\"entityName\":\"user1\",\"entityId\":\"12345\",\"evidences\":null,\"evidenceSize\":1,\"score\":90,\"severityCode\":0,\"severity\":\"Critical\",\"status\":\"Open\",\"feedback\":\"None\",\"comment\":\"a\",\"timeframe\":\"Daily\"}"));
		verify(alertsDao).findAll(any(PageRequest.class));
	}

	@Test
	public void list_alerts_by_severity_filter() throws Exception {
		// set up alerts repository mocked behavior
		List<Alert> alertsList = new ArrayList<Alert>();
		alertsList.add(new Alert("Alert1", 1, 2, EntityType.User, "user1", null, 1, 90, Severity.Critical, AlertStatus.Open, AlertFeedback.None, "a", "12345", AlertTimeframe.Daily));
		alertsList.add(new Alert("Alert2", 1, 2, EntityType.User, "user1", null, 1, 90, Severity.Critical, AlertStatus.Open, AlertFeedback.None, "a", "12345", AlertTimeframe.Daily));
		Alerts alerts = new Alerts();
		alerts.setAlerts(alertsList);

		when(alertsDao.findAlertsByFilters(any(PageRequest.class), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyList())).thenReturn(alerts);

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(get("/api/alerts?severity=high,MEDIUM&sort_field=startTime&sort_direction=DESC&page=1&size=20").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andReturn();

		//validate
		assertTrue( result.getResponse().getContentAsString().contains("\"startDate\":1,\"endDate\":2,\"entityType\":\"User\",\"entityName\":\"user1\",\"entityId\":\"12345\",\"evidences\":null,\"evidenceSize\":1,\"score\":90,\"severityCode\":0,\"severity\":\"Critical\",\"status\":\"Open\",\"feedback\":\"None\",\"comment\":\"a\",\"timeframe\":\"Daily\"}"));
		verify(alertsDao).findAlertsByFilters(any(PageRequest.class), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyList());
		assertTrue( result.getResponse().getContentAsString().contains("\"startDate\":1,\"endDate\":2,\"entityType\":\"User\",\"entityName\":\"user1\",\"entityId\":\"12345\",\"evidences\":null,\"evidenceSize\":1,\"score\":90,\"severityCode\":0,\"severity\":\"Critical\",\"status\":\"Open\",\"feedback\":\"None\",\"comment\":\"a\",\"timeframe\":\"Daily\"}"));
		verify(alertsDao).findAlertsByFilters(any(PageRequest.class), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyList());
	}

	@Test
	public void list_alerts_by_entity_name_filter() throws Exception {
		// set up alerts repository mocked behavior
		List<Alert> alertsList = new ArrayList<Alert>();
		alertsList.add(new Alert("Alert1", 1, 2, EntityType.User, "user1", null, 1, 90, Severity.Critical, AlertStatus.Open, AlertFeedback.None, "a", "12345", AlertTimeframe.Daily));
		alertsList.add(new Alert("Alert2", 1, 2, EntityType.User, "user2", null, 1, 90, Severity.Critical, AlertStatus.Open, AlertFeedback.None, "a", "12345", AlertTimeframe.Daily));
		Alerts alerts = new Alerts();
		alerts.setAlerts(alertsList);

		when(alertsDao.findAlertsByFilters(any(PageRequest.class), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyList())).thenReturn(alerts);

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(get("/api/alerts?entity_name=user1&sort_field=startTime&sort_direction=DESC&page=1&size=20").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn();

		//validate
		assertTrue( result.getResponse().getContentAsString().contains("\"startDate\":1,\"endDate\":2,\"entityType\":\"User\",\"entityName\":\"user1\",\"entityId\":\"12345\",\"evidences\":null,\"evidenceSize\":1,\"score\":90,\"severityCode\":0,\"severity\":\"Critical\",\"status\":\"Open\",\"feedback\":\"None\",\"comment\":\"a\",\"timeframe\":\"Daily\"}"));
		verify(alertsDao).findAlertsByFilters(any(PageRequest.class), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyList());
		assertTrue( result.getResponse().getContentAsString().contains("\"startDate\":1,\"endDate\":2,\"entityType\":\"User\",\"entityName\":\"user1\",\"entityId\":\"12345\",\"evidences\":null,\"evidenceSize\":1,\"score\":90,\"severityCode\":0,\"severity\":\"Critical\",\"status\":\"Open\",\"feedback\":\"None\",\"comment\":\"a\",\"timeframe\":\"Daily\"}"));
		verify(alertsDao).findAlertsByFilters(any(PageRequest.class), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyList());
	}

	@Test
	public void list_all_alerts_without_request_params() throws Exception {
		// set up alerts repository mocked behavior
		List<Alert> alertsList = new ArrayList<Alert>();
		alertsList.add(new Alert("Alert1", 1, 2, EntityType.User, "user1", null, 1, 90, Severity.Critical, AlertStatus.Open, AlertFeedback.None, "a", "12345", AlertTimeframe.Daily));
		alertsList.add(new Alert("Alert2", 1, 2, EntityType.User, "user1", null, 1, 90, Severity.Critical, AlertStatus.Open, AlertFeedback.None, "a", "12345", AlertTimeframe.Daily));
		Alerts alerts = new Alerts();
		alerts.setAlerts(alertsList);

		when(alertsDao.findAll(any(PageRequest.class))).thenReturn(alerts);

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(get("/api/alerts").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andReturn();

		//validate
		assertTrue( result.getResponse().getContentAsString().contains("\"startDate\":1,\"endDate\":2,\"entityType\":\"User\",\"entityName\":\"user1\",\"entityId\":\"12345\",\"evidences\":null,\"evidenceSize\":1,\"score\":90,\"severityCode\":0,\"severity\":\"Critical\",\"status\":\"Open\",\"feedback\":\"None\",\"comment\":\"a\",\"timeframe\":\"Daily\"}"));
		verify(alertsDao).findAll(any(PageRequest.class));
	}

	@Test(expected = NestedServletException.class)
	public void add_alert() throws Exception {

		String sAlert = "{\n" +
				"        \"id\": \"5586a7479f6fe4e3c1e39231\",\n" +
				"        \"startDate\": 1,\n" +
				"        \"endDate\": 2,\n" +
				"        \"entityType\": \"User\",\n" +
				"        \"entityName\": \"user11\",\n" +
				"        \"evidences\": null,\n" +
				"		 \"evidenceSize\": 1, \n" +
				"        \"score\": 90,\n" +
				"        \"severity\": \"Critical\",\n" +
				"        \"status\": \"Open\",\n" +
				"        \"comment\": \"a\"\n" +
				"    }";

		// perform rest call to the controller
		MvcResult result =
				mockMvc.perform(post("/api/alerts")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(sAlert)
						)
						.andDo(print())
						.andExpect(status().isOk())
			.andReturn();

	}

	@Test(expected = org.springframework.web.util.NestedServletException.class)
	public void add_alert_with_duplication() throws Exception {

		String sAlert = "{\n" +
				"        \"id\": \"5586a7479f6fe4e3c1e39231\",\n" +
				"        \"startDate\": 1,\n" +
				"        \"endDate\": 2,\n" +
				"        \"entityType\": \"User\",\n" +
				"        \"entityName\": \"user11\",\n" +
				"        \"evidences\": null,\n" +
				"		 \"evidenceSize\": 1, \n" +
				"        \"score\": 90,\n" +
				"        \"severity\": \"Critical\",\n" +
				"        \"status\": \"Open\",\n" +
				"        \"comment\": \"a\"\n" +
				"    }";
		doThrow(new RuntimeException()).when(alertsDao).add(any(Alert.class));

		// perform rest call to the controller
		MvcResult result =
				mockMvc.perform(post("/api/alerts")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(sAlert)
						)
						.andDo(print())
						.andExpect(status().isOk())
			.andReturn();
	}

}
