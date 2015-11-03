package fortscale.web.rest;

import fortscale.aggregation.feature.services.historicaldata.SupportingInformationGenericData;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationService;
import fortscale.domain.core.Evidence;
import fortscale.domain.historical.data.SupportingInformationDualKey;
import fortscale.domain.historical.data.SupportingInformationKey;
import fortscale.domain.historical.data.SupportingInformationSingleKey;
import fortscale.services.EvidencesService;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryHelper;
import fortscale.services.dataqueries.querygenerators.DataQueryRunner;
import fortscale.services.dataqueries.querygenerators.DataQueryRunnerFactory;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.skyscreamer.jsonassert.JSONAssert;


public class ApiEvidenceControllerTest {

	public static final String EVIDENCE_ID = "test1";
	public static final String MOCK_EVIDENCE_ID = "test2";
	private static final String SOME_EVENT_VALUE = "{some event value}";


	@Mock
	private EvidencesService repository;

	@Mock
	private DataQueryHelper dataQueryHelper;

	@Mock
	Evidence mockEvidence;

	@InjectMocks
	private ApiEvidenceController controller;

	private MockMvc mockMvc;

	@Mock
	DataQueryRunnerFactory dataQueryRunnerFactory;
	@Mock
	DataQueryRunner dataQueryRunner;

	@Mock SupportingInformationService supportingInformationService;

	Map<SupportingInformationKey,Double> countries;
	SupportingInformationKey anomalyCountry;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		Evidence evidence = new Evidence();
		when(repository.findById(EVIDENCE_ID)).thenReturn(evidence);
		when(repository.findById(MOCK_EVIDENCE_ID)).thenReturn(mockEvidence);
		when(dataQueryRunnerFactory.getDataQueryRunner(any(DataQueryDTO.class))).thenReturn(dataQueryRunner);

			this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

		//inits for historical data tests

		countries = new HashMap<>();
		countries.put(new SupportingInformationSingleKey("Israel"),10.0);
		countries.put(new SupportingInformationSingleKey("USA"),7.0);
		anomalyCountry = new SupportingInformationSingleKey("Afghanistan");
		countries.put(anomalyCountry,1.0);

		List<String> dataEntities = new ArrayList<>();
		dataEntities.add("vpn");

		when(mockEvidence.getId()).thenReturn(MOCK_EVIDENCE_ID);
		when(mockEvidence.getAnomalyValue()).thenReturn("Afghanistan");
		when(mockEvidence.getDataEntitiesIds()).thenReturn(dataEntities);
		when(supportingInformationService.getEvidenceSupportingInformationData(eq(mockEvidence), anyString(), anyString(), anyString(), anyInt(), eq("Count"))).thenReturn(new SupportingInformationGenericData<Double>(countries, anomalyCountry));


	}


	@Test
	public void testGetEvidence() throws Exception {

		mockMvc.perform(get("/api/evidences/" + EVIDENCE_ID)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"));

		verify(repository, times(1)).findById(EVIDENCE_ID);

	}

	@Test
	public void testGetTop3Events() throws Exception {

		TestEvidence evidence = new TestEvidence();
		evidence.setId("123");
		List<String> dataEntitiesIds = new ArrayList<>();
		dataEntitiesIds.add("vpn");
		evidence.setDataEntitiesIds(dataEntitiesIds);
		evidence.setTop3eventsJsonStr(SOME_EVENT_VALUE);
		evidence.setStartDate(System.currentTimeMillis());
		evidence.setEndDate(System.currentTimeMillis());
		when(repository.findById(EVIDENCE_ID)).thenReturn(evidence);
		when(dataQueryHelper.createDataQuery(anyString(), anyString(), anyList(), anyList(), anyInt())).
				thenReturn(new DataQueryDTO());
		mockMvc.perform(get("/api/evidences/" + EVIDENCE_ID + "/events")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"));

		verify(repository, times(1)).findById(EVIDENCE_ID);

	}

	@Test(expected = AssertionError.class)
	public void testGetTop3EventsWithWrongId() throws Exception {

		mockMvc.perform(get("/api/evidences/" + EVIDENCE_ID + "/events")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"));

		verify(repository, times(1)).findById(EVIDENCE_ID);

	}

	/**
	 * mocks the anomaly finding, test only the API
	 * @throws Exception
	 */
	@Test
	public void testGetHistoricalDataSingleKey() throws Exception {

		MvcResult result =   mockMvc.perform(get("/api/evidences/" + MOCK_EVIDENCE_ID + "/historical-data?context_type=someCT&context_value=someCV&feature=someFeature&function=Count").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("{\"data\":[{\"keys\":[\"Israel\"],\"additionalInformation\":null,\"value\":10.0,\"anomaly\":false},{\"keys\":[\"USA\"],\"additionalInformation\":null,\"value\":7.0,\"anomaly\":false},{\"keys\":[\"Afghanistan\"],\"additionalInformation\":null,\"value\":1.0,\"anomaly\":true}],\"total\":1,\"offset\":0,\"warning\":null,\"info\":null}"));
	}

	/**
	 * assert the sort direction parameter of API works
	 * @throws Exception
	 */
	@Test
	public void testHistoricalDataSortDirection() throws Exception{

		MvcResult result =   mockMvc.perform(get("/api/evidences/" + MOCK_EVIDENCE_ID + "/historical-data?context_type=someCT&context_value=someCV&feature=someFeature&function=Count&sort_direction=DESC").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("{\"data\":[{\"keys\":[\"Israel\"],\"additionalInformation\":null,\"value\":10.0,\"anomaly\":false},{\"keys\":[\"USA\"],\"additionalInformation\":null,\"value\":7.0,\"anomaly\":false},{\"keys\":[\"Afghanistan\"],\"additionalInformation\":null,\"value\":1.0,\"anomaly\":true}],\"total\":1,\"offset\":0,\"warning\":null,\"info\":null}"));
	}

	/**
	 * tests the separation to 'others' columns according to num_columns parameter:
	 * if the number of columns requested is smaller than the service response, set all the
	 * rest of the columns into one column named 'Others'.
	 * the columns that will be combined into 'Others' are the smallest.
	 * @throws Exception
	 */
	@Test
	public void testNumColumns() throws Exception{

		MvcResult result =   mockMvc.perform(get("/api/evidences/" + MOCK_EVIDENCE_ID + "/historical-data?context_type=someCT&context_value=someCV&feature=someFeature&function=Count&num_columns=0&sort_direction=DESC").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("{\"data\":[{\"keys\":[\"Afghanistan\"],\"additionalInformation\":null,\"value\":1.0,\"anomaly\":true},{\"keys\":[\"Others\"],\"additionalInformation\":null,\"value\":17.0,\"anomaly\":false}],\"total\":1,\"offset\":0,\"warning\":null,\"info\":null}"));

		 result =   mockMvc.perform(get("/api/evidences/" + MOCK_EVIDENCE_ID + "/historical-data?context_type=someCT&context_value=someCV&feature=someFeature&function=Count&num_columns=1&sort_direction=DESC").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("{\"data\":[{\"keys\":[\"Afghanistan\"],\"additionalInformation\":null,\"value\":1.0,\"anomaly\":true},{\"keys\":[\"Israel\"],\"additionalInformation\":null,\"value\":10.0,\"anomaly\":false},{\"keys\":[\"Others\"],\"additionalInformation\":null,\"value\":7.0,\"anomaly\":false}],\"total\":1,\"offset\":0,\"warning\":null,\"info\":null}"));
	}

	/**
	 * mocks the anomaly finding, test only the API
	 * @throws Exception
	 */
	@Test
	public void testHistoricalDataDualKey() throws Exception{

		Map<SupportingInformationKey,Double> heatmap = new HashMap<>();
		heatmap.put(new SupportingInformationDualKey("Sunday","13:00"),9.0);
		heatmap.put(new SupportingInformationDualKey("Sunday","07:00"),8.0);
		heatmap.put(new SupportingInformationDualKey("Monday","13:00"),2.0);

		SupportingInformationKey anomalyTime = new SupportingInformationDualKey("Tuesday","16:00");
		heatmap.put(anomalyTime, 7.0);

		List<String> dataEntities = new ArrayList<>();
		dataEntities.add("vpn");

		when(mockEvidence.getAnomalyValue()).thenReturn("2015-08-05 02:05:53");
		when(mockEvidence.getDataEntitiesIds()).thenReturn(dataEntities);

		when(supportingInformationService.getEvidenceSupportingInformationData(eq(mockEvidence), anyString(), anyString(), anyString(), anyInt(), eq("hourlyCountGroupByDayOfWeek"))).thenReturn(new SupportingInformationGenericData<Double>(heatmap, anomalyTime));

		MvcResult result =   mockMvc.perform(get("/api/evidences/" + MOCK_EVIDENCE_ID + "/historical-data?context_type=someCT&context_value=someCV&feature=someFeature&function=hourlyCountGroupByDayOfWeek").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn();

		String res = result.getResponse().getContentAsString();
		String expected = "{\"data\":[{\"keys\":[\"Sunday\",\"07:00\"],\"additionalInformation\":null,\"value\":8.0,\"anomaly\":false},{\"keys\":[\"Monday\",\"13:00\"],\"additionalInformation\":null,\"value\":2.0,\"anomaly\":false},{\"keys\":[\"Tuesday\",\"16:00\"],\"additionalInformation\":null,\"value\":7.0,\"anomaly\":true},{\"keys\":[\"Sunday\",\"13:00\"],\"additionalInformation\":null,\"value\":9.0,\"anomaly\":false}],\"total\":1,\"offset\":0,\"warning\":null,\"info\":null}";
		JSONAssert.assertEquals(expected, res, false);
	}


	public static class TestEvidence extends Evidence{
		private static final long serialVersionUID = 1L;


		public void setId(String evidenceIdString) {
			super.setId(evidenceIdString);
		}
	}
}