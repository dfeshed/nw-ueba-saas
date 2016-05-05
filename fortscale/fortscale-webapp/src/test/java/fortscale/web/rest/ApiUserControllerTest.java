package fortscale.web.rest;

import fortscale.domain.core.AdUserDirectReport;
import fortscale.domain.core.User;
import fortscale.domain.core.UserAdInfo;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.IUserScoreHistoryElement;
import fortscale.services.UserService;
import fortscale.services.UserServiceFacade;
import fortscale.services.impl.UserScoreHistoryElement;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.*;

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
	private UserService userService;

	@Mock
	private UserServiceFacade userServiceFacade;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private ApiUserController controller;

	private MockMvc mockMvc;

	private static String UID = "123";
	private static String USER_NAME = "John Dow";
	private static String USER_NORMALIZEDNAME = "jdow@somebigcompany.com";
	private static String USER_DN = "CN=John Dow,DC=somebigcompany,DC=com";
	private static final String DIRECT_REPORT_DN = "CM=Direct report,OU=employees,DC=somebigcompany,DC=com";
	private static final String DIRECT_REPORT_NAME = "Directreport";
	private static final String MANAGER_DN = "CM=Manager,OU=employees,DC=somebigcompany,DC=com";
	private static final String MANAGER_NAME = "Manager";

	private Date date = new Date();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
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
	}

	/**
	 * Test the apy for totalScoreHistory
	 * /api/user/{uid}/classifier/{classifierId}/scorehistory
	 * @throws Exception
	 */
	@Test
	public void testRegualRequestwithDateRange() throws Exception {

		// first request - simple regular request
		List<IUserScoreHistoryElement> userScoreHistoryElements = new ArrayList<>();
		IUserScoreHistoryElement element = new UserScoreHistoryElement(date, 90, 90);
		userScoreHistoryElements.add(element);
		element = new UserScoreHistoryElement(new Date(), 80, 80);
		userScoreHistoryElements.add(element);

		when(userServiceFacade.getUserScoresHistory(anyString(), anyString(), anyLong(), anyLong(), anyInt())).thenReturn(userScoreHistoryElements);

		DateTime dateFrom = new DateTime(2015, 4, 23, 0, 0, 0, 0);
		DateTime dateTo =   new DateTime(2015, 4, 30, 0, 0, 0, 0);
		String dateRange = String.valueOf(dateFrom.getMillis()) + "," + dateTo.getMillis();

		MvcResult result = mockMvc.perform(get("/api/user/123/classifier/total/scorehistory")
				.param("dateRange", dateRange)
				.param("tzShift", "0")
				.param("limit", "7")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn();
		verify(userServiceFacade, times(1)).getUserScoresHistory(eq(UID), eq("total"), eq(dateFrom.getMillis()), eq(dateTo.getMillis()), eq(0));
		JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(2, jsonObject.get("total"));
		assertEquals(80, ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("score"));

	}

	/**
	 * Test the apy for totalScoreHistory without DateRange and Offset only
	 * /api/user/{uid}/classifier/{classifierId}/scorehistory
	 * @throws Exception
	 */
	@Test
	public void testRegualRequestWithOffsetOnly() throws Exception {


		List<IUserScoreHistoryElement> userScoreHistoryElements = new ArrayList<>();
		IUserScoreHistoryElement element = new UserScoreHistoryElement(date, 90, 90);
		userScoreHistoryElements.add(element);
		element = new UserScoreHistoryElement(new Date(), 80, 80);
		userScoreHistoryElements.add(element);
		Long currentStartOfDay = new DateTime(DateTimeZone.forID("UTC")).withTimeAtStartOfDay().plusDays(1).getMillis();
		Long LastWeekStartOfDay = new DateTime(DateTimeZone.forID("UTC")).withTimeAtStartOfDay().minusDays(6).getMillis();

		when(userServiceFacade.getUserScoresHistory(anyString(), anyString(), anyLong(), anyLong(), anyInt())).thenReturn(userScoreHistoryElements);

		MvcResult result = mockMvc.perform(get("/api/user/123/classifier/total/scorehistory")
				.param("tzShift", "0")
				.param("limit", "7")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn();
		verify(userServiceFacade, times(1)).getUserScoresHistory(eq(UID), eq("total"), eq(LastWeekStartOfDay), eq(currentStartOfDay), eq(0));
		JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(2, jsonObject.get("total"));
		assertEquals(80, ((JSONObject)((JSONArray)jsonObject.get("data")).get(0)).get("score"));


	}

	/**
	 * Test the apy for totalScoreHistory. When the dateRange comes in reverse order we expect an exception
	 * /api/user/{uid}/classifier/{classifierId}/scorehistory
	 * @throws Exception
	 */
	@Test (expected = NestedServletException.class)
	public void testReversedRange() throws Exception {

		// order of dates is reversed:
		List<IUserScoreHistoryElement> userScoreHistoryElements = new ArrayList<>();
		IUserScoreHistoryElement element = new UserScoreHistoryElement(new Date(), 90, 90);
		userScoreHistoryElements.add(element);
		element = new UserScoreHistoryElement(new Date(), 80, 80);
		userScoreHistoryElements.add(element);

		when(userServiceFacade.getUserScoresHistory(anyString(), anyString(), anyLong(), anyLong(), anyInt())).thenReturn(userScoreHistoryElements);

		DateTime dateFrom = new DateTime(2015, 4, 30, 0, 0);
		DateTime dateTo =   new DateTime(2015, 4, 23, 0, 0);
		String dateRange = String.valueOf(dateFrom.getMillis()) + "," + dateTo.getMillis();

		mockMvc.perform(get("/api/user/123/classifier/total/scorehistory")
				.param("dateRange", dateRange)
				.param("tzShift", "0")
				.param("limit", "7")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"));

	}

}
