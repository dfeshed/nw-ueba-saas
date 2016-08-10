package fortscale.web.rest;

import fortscale.domain.core.*;
import fortscale.domain.core.activities.UserActivityDeviceDocument;
import fortscale.domain.core.activities.UserActivitySourceMachineDocument;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.rest.UserRestFilter;
import fortscale.services.*;
import fortscale.web.rest.Utils.UserDeviceUtils;
import fortscale.web.rest.entities.activity.UserActivityData;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Rans at 7/5/2014
 */
public class ApiUserControllerTest {

	@Mock
	private UserScoreService userScoreService;

	@Mock
	private UserService userService;

	@Mock
	private UserServiceFacade userServiceFacade;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private ApiUserController controller;

	@Mock
	private AlertsService alertsService;

	@Mock
	private UserActivityService userActivityService;

	@Mock
	private UserDeviceUtils userDeviceUtils;

	@Mock
	private UserWithAlertService userWithAlertService;

	private MockMvc mockMvc;

	private static String UID = "123";
	private static String USER_NAME = "John Dow";
	private static String USER_NORMALIZEDNAME = "jdow@somebigcompany.com";
	private static String USER_DN = "CN=John Dow,DC=somebigcompany,DC=com";
	private static final String DIRECT_REPORT_DN = "CM=Direct report,OU=employees,DC=somebigcompany,DC=com";
	private static final String DIRECT_REPORT_NAME = "Directreport";
	private static final String MANAGER_DN = "CM=Manager,OU=employees,DC=somebigcompany,DC=com";
	private static final String MANAGER_NAME = "Manager";

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

		Mockito.when(userScoreService.getUserSeverityForScore(Mockito.anyDouble())).thenReturn(Severity.Critical);
	}

	/**
	 * retrieve user details
	 * /api/user/{uid}/details
	 * @throws Exception
	 */
	@Test
	public void testDetails() throws Exception {

		User user = new User();
		user.setUsername(USER_NAME);
		user.setAdDn(USER_DN);
		UserAdInfo adInfo = new UserAdInfo();
		AdUserDirectReport directReport = new AdUserDirectReport(DIRECT_REPORT_DN, "drepoert");
		adInfo.setAdDirectReports(new HashSet<AdUserDirectReport>(Arrays.asList(directReport)));
		adInfo.setManagerDN(MANAGER_DN);
		user.setAdInfo(adInfo);
		User employee = new User();
		employee.setUsername(DIRECT_REPORT_NAME);
		employee.setAdDn(DIRECT_REPORT_DN);
		List<User> users = new ArrayList<>();
		users.add(user);
		user.setScore(90.0);
		when(userRepository.findByIds(new ArrayList<>(Arrays.asList(new String[]{UID})))).thenReturn(users);
		when(userRepository.findByDNs(any(Collection.class))).thenReturn(new ArrayList(Arrays.asList(employee)));
		MvcResult result = mockMvc.perform(get("/api/user/123/details")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn();
		List<String> uids = new ArrayList<>(Arrays.asList(new String[]{UID}));
		verify(userRepository, times(1)).findByIds(eq(uids));
		JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(1, jsonObject.get("total"));
		assertEquals(USER_NAME, ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("username"));
		assertEquals(USER_NAME, ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("username"));
		assertEquals(Severity.Critical.name(), ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("scoreSeverity"));
		assertEquals(90.0, ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("score"));
	}

	@Test
	public void testDetailsByUserName() throws Exception {

		User user = new User();
		user.setUsername(USER_NAME);
		user.setAdDn(USER_DN);
		UserAdInfo adInfo = new UserAdInfo();
		AdUserDirectReport directReport = new AdUserDirectReport(DIRECT_REPORT_DN, "drepoert");
		adInfo.setAdDirectReports(new HashSet<AdUserDirectReport>(Arrays.asList(directReport)));
		adInfo.setManagerDN(MANAGER_DN);
		user.setAdInfo(adInfo);
		User employee = new User();
		employee.setUsername(DIRECT_REPORT_NAME);
		user.setScore(90.0);
		employee.setAdDn(DIRECT_REPORT_DN);
		when(userRepository.findByUsername(USER_NORMALIZEDNAME)).thenReturn(user);
		when(userRepository.findByDNs(any(Collection.class))).thenReturn(new ArrayList(Arrays.asList(employee)));
		MvcResult result = mockMvc.perform(get("/api/user/jdow@somebigcompany.com/userdata")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn();
		verify(userRepository, times(1)).findByUsername(eq(USER_NORMALIZEDNAME));
		JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(1, jsonObject.get("total"));
		assertEquals(USER_NAME, ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("username"));
		assertEquals(USER_NAME, ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("username"));
		assertEquals(Severity.Critical.name(), ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("scoreSeverity"));
		assertEquals(90.0, ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("score"));
	}

	/**
	 * retrieve followed user details
	 * /api/user/{uid}/followedUsersDetails
	 * @throws Exception
	 */
	@Test
	public void testFollowedUserDetails() throws Exception {

		User user = new User();
		user.setUsername(USER_NAME);
		user.setAdDn(USER_DN);
		UserAdInfo adInfo = new UserAdInfo();
		AdUserDirectReport directReport = new AdUserDirectReport(DIRECT_REPORT_DN, "drepoert");
		adInfo.setAdDirectReports(new HashSet<AdUserDirectReport>(Arrays.asList(directReport)));
		adInfo.setManagerDN(MANAGER_DN);
		user.setScore(90.0);
		user.setAdInfo(adInfo);
		User manager = new User();
		manager.setUsername(MANAGER_NAME);
		manager.setAdDn(MANAGER_DN);
		adInfo.setAdDirectReports(new HashSet<AdUserDirectReport>(Arrays.asList(directReport)));
		adInfo.setDn(DIRECT_REPORT_DN);
		manager.setAdInfo(adInfo);
		when(userRepository.findByFollowed(true)).thenReturn(new ArrayList(Arrays.asList(user)));
		when(userRepository.findByDNs(any(Collection.class))).thenReturn(new ArrayList(Arrays.asList(manager)));
		MvcResult result = mockMvc.perform(get("/api/user/followedUsersDetails")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn();

		JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(1, jsonObject.get("total"));
		assertEquals(USER_NAME, ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("username"));
		assertEquals(USER_NAME, ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("username"));
		assertEquals(Severity.Critical.name(), ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("scoreSeverity"));
		assertEquals(90.0, ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("score"));
	}

	@Test
	public void testGetUsers_NoAdditionalInfo() throws Exception {
		User user = new User();
		user.setUsername(USER_NAME);
		user.setAdDn(USER_DN);
		UserAdInfo adInfo = new UserAdInfo();
		AdUserDirectReport directReport = new AdUserDirectReport(DIRECT_REPORT_DN, "drepoert");
		adInfo.setAdDirectReports(new HashSet<AdUserDirectReport>(Arrays.asList(directReport)));
		adInfo.setManagerDN(MANAGER_DN);
		user.setAdInfo(adInfo);
		User employee = new User();
		employee.setUsername(DIRECT_REPORT_NAME);
		employee.setAdDn(DIRECT_REPORT_DN);
		List<User> users = new ArrayList<>();
		users.add(user);
		user.setScore(90.0);
		when(userWithAlertService.findUsersByFilter(any(UserRestFilter.class), any(PageRequest.class))).thenReturn(users);
		when(userWithAlertService.countUsersByFilter(any(UserRestFilter.class))).thenReturn(1);
		MvcResult result = mockMvc.perform(get("/api/user")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn();

		JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(1, jsonObject.get("total"));
		assertEquals(USER_NAME, ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("username"));
		assertEquals(Severity.Critical.name(), ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("scoreSeverity"));
		assertEquals(90.0, ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("score"));
		assertEquals("null", ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("devices").toString());
		assertEquals("null",((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("alerts").toString());
	}

	@Test
	public void testGetUsers_WithAdditionalInfo() throws Exception {
		TestUser user = new TestUser();
		user.setUsername(USER_NAME);
		user.setAdDn(USER_DN);
		user.setId("1");
		UserAdInfo adInfo = new UserAdInfo();
		AdUserDirectReport directReport = new AdUserDirectReport(DIRECT_REPORT_DN, "drepoert");
		adInfo.setAdDirectReports(new HashSet<AdUserDirectReport>(Arrays.asList(directReport)));
		adInfo.setManagerDN(MANAGER_DN);
		user.setAdInfo(adInfo);
		User employee = new User();
		employee.setUsername(DIRECT_REPORT_NAME);
		employee.setAdDn(DIRECT_REPORT_DN);
		List<User> users = new ArrayList<>();
		users.add(user);
		user.setScore(90.0);
		when(userWithAlertService.findUsersByFilter(any(UserRestFilter.class), any(PageRequest.class))).thenReturn(users);
		when(userWithAlertService.countUsersByFilter(any(UserRestFilter.class))).thenReturn(1);
		Set<Alert> alerts = new HashSet<>();
		Alert alert = new Alert("Alert", 1, 2, EntityType.User, USER_NAME, null, 0, 100, Severity.Critical,
				AlertStatus.Open, AlertFeedback.None, "1", AlertTimeframe.Daily, 1, true);
		alerts.add(alert);
		when(alertsService.getOpenAlertsByUsername(anyString())).thenReturn(alerts);
		List<UserActivitySourceMachineDocument> machines = new ArrayList<>();
		UserActivitySourceMachineDocument machineDocument = new UserActivitySourceMachineDocument();
		UserActivitySourceMachineDocument.Machines machine = new UserActivitySourceMachineDocument.Machines();
		Map<String, Double> machineHistogram = new HashMap<>();
		machineHistogram.put("1", 1d);
		machine.setMachinesHistogram(machineHistogram);
		machineDocument.setMachines(machine);
		machines.add(machineDocument);
		when(userActivityService.getUserActivitySourceMachineEntries(anyString(), anyInt())).thenReturn(machines);
		List<UserActivityData.DeviceEntry> deviceList = new ArrayList<>();
		String deviceName = "comp";
		int deviceCount = 1;
		UserActivityData.DeviceEntry device = new UserActivityData.DeviceEntry(deviceName, deviceCount, UserActivityData.DeviceType.Desktop);
		deviceList.add(device);
		when(userDeviceUtils.convertDeviceDocumentsResponse(anyListOf(UserActivityDeviceDocument.class), anyInt())).thenReturn(deviceList);
		MvcResult result = mockMvc.perform(get("/api/user?addAlertsAndDevices=true")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(1, jsonObject.get("total"));
		assertEquals(USER_NAME, ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("username"));
		assertEquals(Severity.Critical.name(), ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("scoreSeverity"));
		assertEquals(90.0, ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("score"));
		JSONArray devicesArray = (JSONArray) ((JSONObject) ((JSONArray) jsonObject.get("data")).get(0)).get("devices");
		assertEquals(deviceCount, devicesArray.length());
		assertEquals(deviceName, ((JSONObject)devicesArray.get(0)).get("deviceName"));
		JSONArray alertsArray = (JSONArray) ((JSONObject) ((JSONArray) jsonObject.get("data")).get(0)).get("alerts");
		assertEquals(1, alertsArray.length());
		assertEquals(USER_NAME, ((JSONObject)alertsArray.get(0)).get("entityName"));
	}

	public static class TestUser extends User{
		private static final long serialVersionUID = 1L;

		public void setId(String id) {
			super.setId(id);
		}
	}
}
