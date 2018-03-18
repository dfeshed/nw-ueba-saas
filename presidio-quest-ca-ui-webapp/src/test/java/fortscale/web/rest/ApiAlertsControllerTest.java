package fortscale.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.analyst.AnalystAuth;
import fortscale.domain.core.*;
import fortscale.domain.core.alert.analystfeedback.AnalystCommentFeedback;
import fortscale.domain.core.alert.analystfeedback.AnalystFeedback;
import fortscale.domain.core.alert.analystfeedback.AnalystRiskFeedback;
import fortscale.services.AlertCommentsService;
import fortscale.services.AlertsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(MockitoJUnitRunner.class)
public class ApiAlertsControllerTest {

	@Mock private AlertsService alertsDao;
	@Mock private AlertsService alertsService;
	@Mock private AlertCommentsService alertCommentsService;

	@InjectMocks private ApiAlertController controller;

	private MockMvc mockMvc;

	@Before public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test public void nothing() {
		when(alertCommentsService.addComment(any(AnalystFeedback.class))).thenAnswer(new Answer<AnalystFeedback>() {
			@Override
			public AnalystFeedback answer(InvocationOnMock invocationOnMock) throws Throwable {
				final AnalystFeedback analystFeedback= (AnalystFeedback) invocationOnMock.getArguments()[0];
				return analystFeedback;
			}
		});
	}


	@Test public void updateStatus_valid() throws Exception {
		// set up alerts repository mocked behavior
		Alert alert = new Alert("Alert1", 1, 2, EntityType.User, "user1", null, 0, 90, Severity.Critical,
				AlertStatus.Open, AlertFeedback.None, "a", AlertTimeframe.Daily, 0.0, true);

		when(alertsService.getAlertById(anyString())).thenReturn(alert);
		when(alertsService.updateAlertStatus(alert, AlertStatus.Closed, AlertFeedback.Approved, "admin")).thenReturn(new AnalystRiskFeedback());

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(patch("/api/alerts/{id}", "2222")
				.sessionAttr("SPRING_SECURITY_CONTEXT",getSecurityContextForSessionWithAnalyst("admin"))
				.content("{\"status\": \"Closed\", \"feedback\": \"Rejected\", \"analystUserName\":\"admin\"}")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		//validate
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		verify(alertsService).updateAlertStatus(any(), any(), any(), any());
	}

	@Test public void updateStatus_NoAlert() throws Exception {

		when(alertsService.getAlertById(anyString())).thenReturn(null);

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(patch("/api/alerts/{id}", "2222")
				.sessionAttr("SPRING_SECURITY_CONTEXT",getSecurityContextForSessionWithAnalyst("admin"))
				.content("{\"status\": \"Closed\", \"feedback\": \"Rejected\", \"analystUserName\":\"admin\"}")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		//validate
		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
	}

	@Test public void addComment_valid() throws Exception {
		// set up alerts repository mocked behavior
		Alert alert = new Alert("Alert1", 1, 2, EntityType.User, "user1", null, 0, 90, Severity.Critical,
				AlertStatus.Open, AlertFeedback.None, "a", AlertTimeframe.Daily, 0.0, true);

		when(alertsService.getAlertById(anyString())).thenReturn(alert);


		// perform rest call to the controller
		MvcResult result = mockMvc.perform(post("/api/alerts/{id}/comments", "2222").sessionAttr("SPRING_SECURITY_CONTEXT",getSecurityContextForSessionWithAnalyst("admin"))
				.content("{\"analystUserName\": \"admin\", \"commentText\":\"hhhh\"}")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		//validate
		assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
		verify(alertCommentsService).addComment(any());

		ObjectMapper objectMapper = new ObjectMapper();
		AnalystFeedback comment = objectMapper.readValue(result.getResponse().getContentAsString(),AnalystFeedback.class);
		assertEquals("Unauthenticated User",comment.getAnalystUserName());
		assertEquals("2222",comment.getAlertId());


	}

	@Test public void addComment_valid_with_user_on_header() throws Exception {
		// set up alerts repository mocked behavior
		Alert alert = new Alert("Alert1", 1, 2, EntityType.User, "user1", null, 0, 90, Severity.Critical,
				AlertStatus.Open, AlertFeedback.None, "a", AlertTimeframe.Daily, 0.0, true);

		when(alertsService.getAlertById(anyString())).thenReturn(alert);


		// perform rest call to the controller
		MvcResult result = mockMvc.perform(post("/api/alerts/{id}/comments", "2222").sessionAttr("SPRING_SECURITY_CONTEXT",getSecurityContextForSessionWithAnalyst("admin"))
				.content("{\"analystUserName\": \"admin\", \"commentText\":\"hhhh\"}")
				.header("Authenticated_User","Shay")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		//validate
		assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
		verify(alertCommentsService).addComment(any());

		ObjectMapper objectMapper = new ObjectMapper();
		AnalystFeedback comment = objectMapper.readValue(result.getResponse().getContentAsString(),AnalystFeedback.class);
		assertEquals("Shay",comment.getAnalystUserName());
		assertEquals("2222",comment.getAlertId());


	}

	@Test public void addComment_NoAlert() throws Exception {

		when(alertsService.getAlertById(anyString())).thenReturn(null);

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(post("/api/alerts/{id}/comments", "2222")
				.sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContextForSessionWithAnalyst("admin"))
				.content("{\"analystUserName\": \"admin\", \"commentText\":\"hhhh\"}")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		//validate
		assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
	}

	@Test public void addComment_emptyComment() throws Exception {
		// set up alerts repository mocked behavior
		Alert alert = new Alert("Alert1", 1, 2, EntityType.User, "user1", null, 0, 90, Severity.Critical,
				AlertStatus.Open, AlertFeedback.None, "a", AlertTimeframe.Daily, 0.0, true);

		when(alertsService.getAlertById(anyString())).thenReturn(alert);

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(post("/api/alerts/{id}/comments", "2222")
				.sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContextForSessionWithAnalyst("admin"))
				.content("{\"analystUserName\": \"admin\", \"commentText\":\"\"}")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException);
		assertTrue(result.getResolvedException().getMessage().contains("commentText"));
		assertTrue(result.getResolvedException().getMessage().contains("NotEmpty"));
	}

	@Test public void addComment_nullComment() throws Exception {
		// set up alerts repository mocked behavior
		Alert alert = new Alert("Alert1", 1, 2, EntityType.User, "user1", null, 0, 90, Severity.Critical,
				AlertStatus.Open, AlertFeedback.None, "a", AlertTimeframe.Daily, 0.0, true);

		when(alertsService.getAlertById(anyString())).thenReturn(alert);

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(post("/api/alerts/{id}/comments", "2222")
				.sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContextForSessionWithAnalyst("admin"))
				.content("{\"analystUserName\": \"admin\", \"commentText\":null}")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException);
		assertTrue(result.getResolvedException().getMessage().contains("commentText"));
		assertTrue(result.getResolvedException().getMessage().contains("NotNull"));
	}

	@Test public void updateComment_valid() throws Exception {
		// set up alerts repository mocked behavior
		List<AnalystFeedback> comments = new ArrayList<>();
		String commentId = "1";
		AnalystFeedback comment = new AnalystCommentFeedback("Alex", "AnalystCommentFeedback", commentId, System.currentTimeMillis(),null);
		comments.add(comment);

		Alert alert = new Alert("Alert1", 1, 2, EntityType.User, "user1", null, 0, 90, Severity.Critical,
				AlertStatus.Open, AlertFeedback.None, "a", AlertTimeframe.Daily, 0.0, true);

		comment.setAlertId(alert.getId());
		alert.setAnalystFeedback(comments);

		when(alertsService.getAlertById(anyString())).thenReturn(alert);

		when(alertCommentsService.getCommentById("1")).thenReturn(comment);

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(patch("/api/alerts/{id}/comments/{commentId}", "2222", commentId)
				.sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContextForSessionWithAnalyst("admin"))
				.content("{\"analystUserName\": \"admin\", \"commentText\":\"hhhh\"}")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		//validate
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		verify(alertCommentsService).updateComment(any());
	}

	@Test public void updateComment_NoAlert() throws Exception {
		when(alertsService.getAlertById(anyString())).thenReturn(null);

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(patch("/api/alerts/{id}/comments/{commentId}", "2222", "111")
				.sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContextForSessionWithAnalyst("admin"))
				.content("{\"analystUserName\": \"admin\", \"commentText\":\"hhhh\"}")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		//validate
		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
	}

	@Test public void updateComment_NoComment() throws Exception {
		// set up alerts repository mocked behavior
		Alert alert = new Alert("Alert1", 1, 2, EntityType.User, "user1", null, 0, 90, Severity.Critical,
				AlertStatus.Open, AlertFeedback.None, "a", AlertTimeframe.Daily, 0.0, true);

		when(alertsService.getAlertById(anyString())).thenReturn(alert);

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(patch("/api/alerts/{id}/comments/{commentId}", "2222", "1")
				.sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContextForSessionWithAnalyst("admin"))
				.content("{\"analystUserName\": \"admin\", \"commentText\":\"hhhh\"}")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		//validate
		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
	}

	@Test public void updateComment_emptyComment() throws Exception {
		List<AnalystFeedback> comments = new ArrayList<>();
		String commentId = "1";
		AnalystCommentFeedback comment = new AnalystCommentFeedback("Alex", "AnalystCommentFeedback", commentId, System.currentTimeMillis(),null);
		comments.add(comment);

		// set up alerts repository mocked behavior
		Alert alert = new Alert("Alert1", 1, 2, EntityType.User, "user1", null, 0, 90, Severity.Critical,
				AlertStatus.Open, AlertFeedback.None, "a", AlertTimeframe.Daily, 0.0, true);
		alert.setAnalystFeedback(comments);
		comment.setAlertId(alert.getId());

		when(alertsService.getAlertById(anyString())).thenReturn(alert);

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(patch("/api/alerts/{id}/comments/{commentId}", "2222", commentId)
				.sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContextForSessionWithAnalyst("admin"))
				.content("{\"analystUserName\": \"admin\", \"commentText\":\"\"}")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException);
		assertTrue(result.getResolvedException().getMessage().contains("commentText"));
		assertTrue(result.getResolvedException().getMessage().contains("NotEmpty"));
	}

	@Test public void updateComment_nullComment() throws Exception {
		List<AnalystFeedback> comments = new ArrayList<>();
		String commentId = "1";
		AnalystCommentFeedback comment = new AnalystCommentFeedback("Alex",  "AnalystCommentFeedback", commentId, System.currentTimeMillis(),null);

		// set up alerts repository mocked behavior
		Alert alert = new Alert("Alert1", 1, 2, EntityType.User, "user1", null, 0, 90, Severity.Critical,
				AlertStatus.Open, AlertFeedback.None, "a", AlertTimeframe.Daily, 0.0, true);
		comment.setAlertId(alert.getId());
		comments.add(comment);
		alert.setAnalystFeedback(comments);

		when(alertsService.getAlertById(anyString())).thenReturn(alert);

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(patch("/api/alerts/{id}/comments/{commentId}", "2222", commentId)
				.sessionAttr("SPRING_SECURITY_CONTEXT", getSecurityContextForSessionWithAnalyst("admin"))
				.content("{\"analystUserName\": \"admin\", \"commentText\":null}")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException);
		assertTrue(result.getResolvedException().getMessage().contains("commentText"));
		assertTrue(result.getResolvedException().getMessage().contains("NotNull"));
	}

	@Test public void deleteComment_valid() throws Exception {
		List<AnalystFeedback> comments = new ArrayList<>();
		String commentId = "1";
		AnalystCommentFeedback comment = new AnalystCommentFeedback("Alex",  "AnalystCommentFeedback", commentId, System.currentTimeMillis(),"2222");
		comments.add(comment);


		// set up alerts repository mocked behavior
		Alert alert = new Alert("Alert1", 1, 2, EntityType.User, "user1", null, 0, 90, Severity.Critical,
				AlertStatus.Open, AlertFeedback.None, "a", AlertTimeframe.Daily, 0.0, true);

		alert.setAnalystFeedback(comments);

		when(alertsService.getAlertById(anyString())).thenReturn(alert);
		when(alertCommentsService.getCommentById(anyString())).thenReturn(comment);

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(delete("/api/alerts/{id}/comments/{commentId}", "2222", commentId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		//validate
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		verify(alertCommentsService).deleteComment(any());
	}

	@Test public void deleteComment_NoAlert() throws Exception {

		when(alertsService.getAlertById(anyString())).thenReturn(null);

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(delete("/api/alerts/{id}/comments/{commentId}", "2222", "111")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		//validate
		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
	}

	@Test public void deleteComment_noComment() throws Exception {
		List<AnalystFeedback> comments = new ArrayList<>();

		// set up alerts repository mocked behavior
		Alert alert = new Alert("Alert1", 1, 2, EntityType.User, "user1", null, 0, 90, Severity.Critical,
				AlertStatus.Open, AlertFeedback.None, "a", AlertTimeframe.Daily, 0.0, true);
		alert.setAnalystFeedback(comments);


		when(alertsService.getAlertById(anyString())).thenReturn(alert);

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(delete("/api/alerts/{id}/comments/{commentId}", "2222", "1")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		//validate
		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
	}

	/*@Test
	public void list_all_alerts() throws Exception {
		// set up alerts repository mocked behavior
		List<Alert> alertsList = new ArrayList<Alert>();
		alertsList.add(new Alert("Alert1", 1, 2, EntityType.User, "user1", null, 1, 90, Severity.Critical, AlertStatus.Open, AlertFeedback.None, "a", "12345", AlertTimeframe.Daily,0.0,true));
		alertsList.add(new Alert("Alert2", 1, 2, EntityType.User, "user1", null, 1, 90, Severity.Critical, AlertStatus.Open, AlertFeedback.None, "a", "12345", AlertTimeframe.Daily,0.0,true));
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
		assertTrue(result.getResponse().getContentAsString().contains("\"startDate\":1,\"endDate\":2,\"entityType\":\"User\",\"entityName\":\"user1\",\"entityId\":\"12345\",\"evidences\":null,\"evidenceSize\":1,\"score\":90,\"severityCode\":0,\"severity\":\"Critical\",\"status\":\"Open\",\"feedback\":\"None\",\"comment\":\"a\",\"userScoreContribution\":0.0,\"userScoreContributionFlag\":true,\"timeframe\":\"Daily\",\"dataSourceAnomalyTypePair\":[]}"));
		verify(alertsDao).findAll(any(PageRequest.class));
	}

	@Test
	public void list_alerts_by_severity_filter() throws Exception {
		// set up alerts repository mocked behavior
		List<Alert> alertsList = new ArrayList<Alert>();
		alertsList.add(new Alert("Alert1", 1, 2, EntityType.User, "user1", null, 1, 90, Severity.Critical, AlertStatus.Open, AlertFeedback.None, "a", "12345", AlertTimeframe.Daily,0.0,true));
		alertsList.add(new Alert("Alert2", 1, 2, EntityType.User, "user1", null, 1, 90, Severity.Critical, AlertStatus.Open, AlertFeedback.None, "a", "12345", AlertTimeframe.Daily,0.0,true));
		Alerts alerts = new Alerts();
		alerts.setAlerts(alertsList);

		when(alertsDao.findAlertsByFilters(any(PageRequest.class), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any())).thenReturn(alerts);

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(get("/api/alerts?severity=high,MEDIUM&sort_field=startTime&sort_direction=DESC&page=1&size=20").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andReturn();

		//validate
		assertTrue( result.getResponse().getContentAsString().contains("\"startDate\":1,\"endDate\":2,\"entityType\":\"User\",\"entityName\":\"user1\",\"entityId\":\"12345\",\"evidences\":null,\"evidenceSize\":1,\"score\":90,\"severityCode\":0,\"severity\":\"Critical\",\"status\":\"Open\",\"feedback\":\"None\",\"comment\":\"a\",\"userScoreContribution\":0.0,\"userScoreContributionFlag\":true,\"timeframe\":\"Daily\",\"dataSourceAnomalyTypePair\":[]}"));
		verify(alertsDao).findAlertsByFilters(any(PageRequest.class), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any());
		assertTrue( result.getResponse().getContentAsString().contains("\"startDate\":1,\"endDate\":2,\"entityType\":\"User\",\"entityName\":\"user1\",\"entityId\":\"12345\",\"evidences\":null,\"evidenceSize\":1,\"score\":90,\"severityCode\":0,\"severity\":\"Critical\",\"status\":\"Open\",\"feedback\":\"None\",\"comment\":\"a\",\"userScoreContribution\":0.0,\"userScoreContributionFlag\":true,\"timeframe\":\"Daily\",\"dataSourceAnomalyTypePair\":[]}"));
		verify(alertsDao).findAlertsByFilters(any(PageRequest.class), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any());
	}

	@Test
	public void list_alerts_by_entity_name_filter() throws Exception {
		// set up alerts repository mocked behavior
		List<Alert> alertsList = new ArrayList<Alert>();
		alertsList.add(new Alert("Alert1", 1, 2, EntityType.User, "user1", null, 1, 90, Severity.Critical, AlertStatus.Open, AlertFeedback.None, "a", "12345", AlertTimeframe.Daily,0.0,true));
		alertsList.add(new Alert("Alert2", 1, 2, EntityType.User, "user2", null, 1, 90, Severity.Critical, AlertStatus.Open, AlertFeedback.None, "a", "12345", AlertTimeframe.Daily,0.0,true));
		Alerts alerts = new Alerts();
		alerts.setAlerts(alertsList);

		when(alertsDao.findAlertsByFilters(any(PageRequest.class), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any())).thenReturn(alerts);

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(get("/api/alerts?entity_name=user1&sort_field=startTime&sort_direction=DESC&page=1&size=20").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn();

		//validate
		assertTrue( result.getResponse().getContentAsString().contains("\"startDate\":1,\"endDate\":2,\"entityType\":\"User\",\"entityName\":\"user1\",\"entityId\":\"12345\",\"evidences\":null,\"evidenceSize\":1,\"score\":90,\"severityCode\":0,\"severity\":\"Critical\",\"status\":\"Open\",\"feedback\":\"None\",\"comment\":\"a\",\"userSocreContribution\":0.0,\"userSocreContributionFlag\":true,\"timeframe\":\"Daily\",\"dataSourceAnomalyTypePair\":[]}"));
		verify(alertsDao).findAlertsByFilters(any(PageRequest.class), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any());
		assertTrue( result.getResponse().getContentAsString().contains("\"startDate\":1,\"endDate\":2,\"entityType\":\"User\",\"entityName\":\"user1\",\"entityId\":\"12345\",\"evidences\":null,\"evidenceSize\":1,\"score\":90,\"severityCode\":0,\"severity\":\"Critical\",\"status\":\"Open\",\"feedback\":\"None\",\"comment\":\"a\",\"userSocreContribution\":0.0,\"userSocreContributionFlag\":true,\"timeframe\":\"Daily\",\"dataSourceAnomalyTypePair\":[]}"));
		verify(alertsDao).findAlertsByFilters(any(PageRequest.class), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any());
	}

	@Test
	public void list_all_alerts_without_request_params() throws Exception {
		// set up alerts repository mocked behavior
		List<Alert> alertsList = new ArrayList<Alert>();
		alertsList.add(new Alert("Alert1", 1, 2, EntityType.User, "user1", null, 1, 90, Severity.Critical, AlertStatus.Open, AlertFeedback.None, "a", "12345", AlertTimeframe.Daily,0.0,true));
		alertsList.add(new Alert("Alert2", 1, 2, EntityType.User, "user1", null, 1, 90, Severity.Critical, AlertStatus.Open, AlertFeedback.None, "a", "12345", AlertTimeframe.Daily,0.0,true));
		Alerts alerts = new Alerts();
		alerts.setAlerts(alertsList);

		when(alertsDao.findAll(any(PageRequest.class))).thenReturn(alerts);

		// perform rest call to the controller
		MvcResult result = mockMvc.perform(get("/api/alerts").accept(MediaType.APPLICATION_JSON))
	//		.andExpect(status().isOk())
	//		.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andReturn();

		//validate
		assertTrue( result.getResponse().getContentAsString().contains("\"startDate\":1,\"endDate\":2,\"entityType\":\"User\",\"entityName\":\"user1\",\"entityId\":\"12345\",\"evidences\":null,\"evidenceSize\":1,\"score\":90,\"severityCode\":0,\"severity\":\"Critical\",\"status\":\"Open\",\"feedback\":\"None\",\"comment\":\"a\",\"userScoreContribution\":0.0,\"userScoreContributionFlag\":true,\"timeframe\":\"Daily\",\"dataSourceAnomalyTypePair\":[]}"));
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


*/
	private SecurityContextImpl getSecurityContextForSessionWithAnalyst(String username){
		SecurityContextImpl securityContext = new SecurityContextImpl();

		Collection<? extends GrantedAuthority> roles = Collections.emptySet();
		AnalystAuth analystAuth = new AnalystAuth(username,"a",roles);
		Authentication a = new TestingAuthenticationToken(analystAuth,roles);
		securityContext.setAuthentication(a);

		return  securityContext;

	}
}