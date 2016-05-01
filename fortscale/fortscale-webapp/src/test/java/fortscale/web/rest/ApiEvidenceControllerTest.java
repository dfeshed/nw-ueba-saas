package fortscale.web.rest;

import fortscale.aggregation.feature.services.historicaldata.SupportingInformationGenericData;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationService;
import fortscale.common.dataqueries.querydto.DataQueryDTO;
import fortscale.common.dataqueries.querydto.DataQueryDTOImpl;
import fortscale.common.dataqueries.querydto.DataQueryHelper;
import fortscale.common.dataqueries.querygenerators.DataQueryRunner;
import fortscale.common.dataqueries.querygenerators.DataQueryRunnerFactory;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.VpnOverlappingSupportingInformation;
import fortscale.domain.core.VpnSessionOverlap;
import fortscale.domain.historical.data.SupportingInformationDualKey;
import fortscale.domain.historical.data.SupportingInformationKey;
import fortscale.domain.historical.data.SupportingInformationSingleKey;
import fortscale.services.EvidencesService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.skyscreamer.jsonassert.JSONAssert;
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
		when(dataQueryHelper.createDataQuery(anyString(), anyString(), anyList(), anyList(), anyInt(), any(Class.class))).
				thenReturn(new DataQueryDTOImpl());
		mockMvc.perform(get("/api/evidences/" + EVIDENCE_ID + "/events")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"));

		verify(repository, times(1)).findById(EVIDENCE_ID);

	}

	@Test
	public void testGetTop3EventsForExistingSupportingInfo() throws Exception {

		TestEvidence evidence = new TestEvidence();
		evidence.setId("123");
		VpnOverlappingSupportingInformation vpnOverlappingSupportingInformation = new VpnOverlappingSupportingInformation();
		VpnSessionOverlap vpnSessionOverlap1 = new VpnSessionOverlap();
		VpnSessionOverlap vpnSessionOverlap2 = new VpnSessionOverlap();
		vpnSessionOverlap1.setCountry("Oz");
		vpnSessionOverlap1.setDatabucket(5314);
		vpnSessionOverlap1.setDate_time_unix(6);
		vpnSessionOverlap1.setDuration(27524);
		vpnSessionOverlap1.setHostname("Bla1");
		vpnSessionOverlap1.setLocal_ip("1.2.3.4");
		vpnSessionOverlap1.setReadbytes(152656342);
		vpnSessionOverlap1.setSource_ip(".1.5.6.7");
		vpnSessionOverlap1.setTotalbytes(1000);
		vpnSessionOverlap1.setCity("Dimona");
		vpnSessionOverlap1.setUsername("Idan");
		vpnSessionOverlap1.setWritebytes(2);
		vpnSessionOverlap1.setEventscore(92);

		vpnSessionOverlap2.setCountry("Oz");
		vpnSessionOverlap2.setDatabucket(1234);
		vpnSessionOverlap2.setDate_time_unix(2);
		vpnSessionOverlap2.setDuration(5678);
		vpnSessionOverlap2.setHostname("Bla1");
		vpnSessionOverlap2.setLocal_ip("1.2.3.5");
		vpnSessionOverlap2.setReadbytes(152652);
		vpnSessionOverlap2.setSource_ip(".1.5.6.8");
		vpnSessionOverlap2.setTotalbytes(1001);
		vpnSessionOverlap2.setCity("Dimona");
		vpnSessionOverlap2.setUsername("Idan");
		vpnSessionOverlap2.setWritebytes(2);
		vpnSessionOverlap2.setEventscore(92);

		List<VpnSessionOverlap> vpnSessionOverlapList = new ArrayList<>();
		vpnSessionOverlapList.add(vpnSessionOverlap1);
		vpnSessionOverlapList.add(vpnSessionOverlap2);
		vpnOverlappingSupportingInformation.setRawEvents(vpnSessionOverlapList);
		evidence.setSupportingInformation(vpnOverlappingSupportingInformation);




		List<String> dataEntitiesIds = new ArrayList<>();
		dataEntitiesIds.add("vpn");
		evidence.setDataEntitiesIds(dataEntitiesIds);
		evidence.setTop3eventsJsonStr(SOME_EVENT_VALUE);
		evidence.setStartDate(System.currentTimeMillis());
		evidence.setEndDate(System.currentTimeMillis());
		when(repository.findById(EVIDENCE_ID)).thenReturn(evidence);
		when(dataQueryHelper.createDataQuery(anyString(), anyString(), anyList(), anyList(), anyInt(), any(Class.class))).
				thenReturn(new DataQueryDTOImpl());

		MvcResult result = mockMvc.perform(get("/api/evidences/" + EVIDENCE_ID + "/events")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();


		String contentAsString = result.getResponse().getContentAsString();
		assertTrue(contentAsString.contains("{\"data\":[{\"duration\":5678,\"local_ip\":\"1.2.3.5\",\"start_time\":-5676000,\"country\":\"Oz\",\"hostname\":\"Bla1\",\"session_score\":92,\"write_bytes\":2,\"data_bucket\":1234,\"end_time\":2000,\"read_bytes\":152652,\"username\":\"Idan\",\"source_ip\":\".1.5.6.8\"},{\"duration\":27524,\"local_ip\":\"1.2.3.4\",\"start_time\":-27518000,\"country\":\"Oz\",\"hostname\":\"Bla1\",\"session_score\":92,\"write_bytes\":2,\"data_bucket\":5314,\"end_time\":6000,\"read_bytes\":152656342,\"username\":\"Idan\",\"source_ip\":\".1.5.6.7\"}],\"total\":2,\"offset\":0,\"warning\":null,\"info\":null}"));





	}

	@Test
	public void testGetSupportingInfoEventsPage2Size3() throws Exception {

		TestEvidence evidence = new TestEvidence();
		evidence.setId("123");
		VpnOverlappingSupportingInformation vpnOverlappingSupportingInformation = new VpnOverlappingSupportingInformation();
		VpnSessionOverlap vpnSessionOverlap1 = new VpnSessionOverlap();
		VpnSessionOverlap vpnSessionOverlap2 = new VpnSessionOverlap();
		VpnSessionOverlap vpnSessionOverlap3 = new VpnSessionOverlap();
		VpnSessionOverlap vpnSessionOverlap4 = new VpnSessionOverlap();
		VpnSessionOverlap vpnSessionOverlap5 = new VpnSessionOverlap();
		VpnSessionOverlap vpnSessionOverlap6 = new VpnSessionOverlap();
		VpnSessionOverlap vpnSessionOverlap7 = new VpnSessionOverlap();
		VpnSessionOverlap vpnSessionOverlap8 = new VpnSessionOverlap();
		VpnSessionOverlap vpnSessionOverlap9 = new VpnSessionOverlap();
		VpnSessionOverlap vpnSessionOverlap10 = new VpnSessionOverlap();

		vpnSessionOverlap1.setCountry("Oz");
		vpnSessionOverlap1.setDatabucket(5314);
		vpnSessionOverlap1.setDate_time_unix(10);
		vpnSessionOverlap1.setDuration(27524);
		vpnSessionOverlap1.setHostname("Bla1");
		vpnSessionOverlap1.setLocal_ip("1.2.3.4");
		vpnSessionOverlap1.setReadbytes(152656342);
		vpnSessionOverlap1.setSource_ip(".1.5.6.7");
		vpnSessionOverlap1.setTotalbytes(1000);
		vpnSessionOverlap1.setCity("Dimona");
		vpnSessionOverlap1.setUsername("Idan");
		vpnSessionOverlap1.setWritebytes(2);
		vpnSessionOverlap1.setEventscore(92);

		vpnSessionOverlap2.setCountry("Oz");
		vpnSessionOverlap2.setDatabucket(1234);
		vpnSessionOverlap2.setDate_time_unix(9);
		vpnSessionOverlap2.setDuration(5678);
		vpnSessionOverlap2.setHostname("Bla1");
		vpnSessionOverlap2.setLocal_ip("1.2.3.5");
		vpnSessionOverlap2.setReadbytes(152652);
		vpnSessionOverlap2.setSource_ip(".1.5.6.8");
		vpnSessionOverlap2.setTotalbytes(1001);
		vpnSessionOverlap2.setCity("Dimona");
		vpnSessionOverlap2.setUsername("Idan");
		vpnSessionOverlap2.setWritebytes(2);
		vpnSessionOverlap2.setEventscore(92);

		vpnSessionOverlap3.setCountry("Oz");
		vpnSessionOverlap3.setDatabucket(5314);
		vpnSessionOverlap3.setDate_time_unix(8);
		vpnSessionOverlap3.setDuration(27524);
		vpnSessionOverlap3.setHostname("Bla1");
		vpnSessionOverlap3.setLocal_ip("1.2.3.4");
		vpnSessionOverlap3.setReadbytes(152656342);
		vpnSessionOverlap3.setSource_ip(".1.5.6.7");
		vpnSessionOverlap3.setTotalbytes(1000);
		vpnSessionOverlap3.setCity("Dimona");
		vpnSessionOverlap3.setUsername("Idan");
		vpnSessionOverlap3.setWritebytes(2);
		vpnSessionOverlap3.setEventscore(92);

		vpnSessionOverlap4.setCountry("Oz");
		vpnSessionOverlap4.setDatabucket(5314);
		vpnSessionOverlap4.setDate_time_unix(7);
		vpnSessionOverlap4.setDuration(27524);
		vpnSessionOverlap4.setHostname("Bla1");
		vpnSessionOverlap4.setLocal_ip("1.2.3.4");
		vpnSessionOverlap4.setReadbytes(152656342);
		vpnSessionOverlap4.setSource_ip(".1.5.6.7");
		vpnSessionOverlap4.setTotalbytes(1000);
		vpnSessionOverlap4.setCity("Dimona");
		vpnSessionOverlap4.setUsername("Idan");
		vpnSessionOverlap4.setWritebytes(2);
		vpnSessionOverlap4.setEventscore(92);

		vpnSessionOverlap5.setCountry("Oz");
		vpnSessionOverlap5.setDatabucket(5314);
		vpnSessionOverlap5.setDate_time_unix(6);
		vpnSessionOverlap5.setDuration(27524);
		vpnSessionOverlap5.setHostname("Bla1");
		vpnSessionOverlap5.setLocal_ip("1.2.3.4");
		vpnSessionOverlap5.setReadbytes(152656342);
		vpnSessionOverlap5.setSource_ip(".1.5.6.7");
		vpnSessionOverlap5.setTotalbytes(1000);
		vpnSessionOverlap5.setCity("Dimona");
		vpnSessionOverlap5.setUsername("Idan");
		vpnSessionOverlap5.setWritebytes(2);
		vpnSessionOverlap5.setEventscore(92);

		vpnSessionOverlap6.setCountry("Oz");
		vpnSessionOverlap6.setDatabucket(5314);
		vpnSessionOverlap6.setDate_time_unix(5);
		vpnSessionOverlap6.setDuration(27524);
		vpnSessionOverlap6.setHostname("Bla1");
		vpnSessionOverlap6.setLocal_ip("1.2.3.4");
		vpnSessionOverlap6.setReadbytes(152656342);
		vpnSessionOverlap6.setSource_ip(".1.5.6.7");
		vpnSessionOverlap6.setTotalbytes(1000);
		vpnSessionOverlap6.setCity("Dimona");
		vpnSessionOverlap6.setUsername("Idan");
		vpnSessionOverlap6.setWritebytes(2);
		vpnSessionOverlap6.setEventscore(92);

		vpnSessionOverlap7.setCountry("Oz");
		vpnSessionOverlap7.setDatabucket(5314);
		vpnSessionOverlap7.setDate_time_unix(4);
		vpnSessionOverlap7.setDuration(27524);
		vpnSessionOverlap7.setHostname("Bla1");
		vpnSessionOverlap7.setLocal_ip("1.2.3.4");
		vpnSessionOverlap7.setReadbytes(152656342);
		vpnSessionOverlap7.setSource_ip(".1.5.6.7");
		vpnSessionOverlap7.setTotalbytes(1000);
		vpnSessionOverlap7.setCity("Dimona");
		vpnSessionOverlap7.setUsername("Idan");
		vpnSessionOverlap7.setWritebytes(2);
		vpnSessionOverlap7.setEventscore(92);

		vpnSessionOverlap8.setCountry("Oz");
		vpnSessionOverlap8.setDatabucket(5314);
		vpnSessionOverlap8.setDate_time_unix(3);
		vpnSessionOverlap8.setDuration(27524);
		vpnSessionOverlap8.setHostname("Bla1");
		vpnSessionOverlap8.setLocal_ip("1.2.3.4");
		vpnSessionOverlap8.setReadbytes(152656342);
		vpnSessionOverlap8.setSource_ip(".1.5.6.7");
		vpnSessionOverlap8.setTotalbytes(1000);
		vpnSessionOverlap8.setCity("Dimona");
		vpnSessionOverlap8.setUsername("Idan");
		vpnSessionOverlap8.setWritebytes(2);
		vpnSessionOverlap8.setEventscore(92);

		vpnSessionOverlap9.setCountry("Oz");
		vpnSessionOverlap9.setDatabucket(5314);
		vpnSessionOverlap9.setDate_time_unix(2);
		vpnSessionOverlap9.setDuration(27524);
		vpnSessionOverlap9.setHostname("Bla1");
		vpnSessionOverlap9.setLocal_ip("1.2.3.4");
		vpnSessionOverlap9.setReadbytes(152656342);
		vpnSessionOverlap9.setSource_ip(".1.5.6.7");
		vpnSessionOverlap9.setTotalbytes(1000);
		vpnSessionOverlap9.setCity("Dimona");
		vpnSessionOverlap9.setUsername("Idan");
		vpnSessionOverlap9.setWritebytes(2);
		vpnSessionOverlap9.setEventscore(92);

		vpnSessionOverlap10.setCountry("Oz");
		vpnSessionOverlap10.setDatabucket(5314);
		vpnSessionOverlap10.setDate_time_unix(1);
		vpnSessionOverlap10.setDuration(27524);
		vpnSessionOverlap10.setHostname("Bla1");
		vpnSessionOverlap10.setLocal_ip("1.2.3.4");
		vpnSessionOverlap10.setReadbytes(152656342);
		vpnSessionOverlap10.setSource_ip(".1.5.6.7");
		vpnSessionOverlap10.setTotalbytes(1000);
		vpnSessionOverlap10.setCity("Dimona");
		vpnSessionOverlap10.setUsername("Idan");
		vpnSessionOverlap10.setWritebytes(2);
		vpnSessionOverlap10.setEventscore(92);

		List<VpnSessionOverlap> vpnSessionOverlapList = new ArrayList<>();
		vpnSessionOverlapList.add(vpnSessionOverlap1);
		vpnSessionOverlapList.add(vpnSessionOverlap2);
		vpnSessionOverlapList.add(vpnSessionOverlap3);
		vpnSessionOverlapList.add(vpnSessionOverlap4);
		vpnSessionOverlapList.add(vpnSessionOverlap5);
		vpnSessionOverlapList.add(vpnSessionOverlap6);
		vpnSessionOverlapList.add(vpnSessionOverlap7);
		vpnSessionOverlapList.add(vpnSessionOverlap8);
		vpnSessionOverlapList.add(vpnSessionOverlap9);
		vpnSessionOverlapList.add(vpnSessionOverlap10);
		vpnOverlappingSupportingInformation.setRawEvents(vpnSessionOverlapList);
		evidence.setSupportingInformation(vpnOverlappingSupportingInformation);

		List<String> dataEntitiesIds = new ArrayList<>();
		dataEntitiesIds.add("vpn");
		evidence.setDataEntitiesIds(dataEntitiesIds);
		evidence.setTop3eventsJsonStr(SOME_EVENT_VALUE);
		evidence.setStartDate(System.currentTimeMillis());
		evidence.setEndDate(System.currentTimeMillis());
		when(repository.findById(EVIDENCE_ID)).thenReturn(evidence);
		when(dataQueryHelper.createDataQuery(anyString(), anyString(), anyList(), anyList(), anyInt(), any(Class.class))).
				thenReturn(new DataQueryDTOImpl());
		MvcResult result = mockMvc.perform(get("/api/evidences/" + EVIDENCE_ID + "/events?page=2&size=3")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();

		String contentAsString = result.getResponse().getContentAsString();

		assertTrue(contentAsString.contains("{\"data\":[{\"duration\":27524,\"local_ip\":\"1.2.3.4\",\"start_time\":-27520000,\"country\":\"Oz\",\"hostname\":\"Bla1\",\"session_score\":92,\"write_bytes\":2,\"data_bucket\":5314,\"end_time\":4000,\"read_bytes\":152656342,\"username\":\"Idan\",\"source_ip\":\".1.5.6.7\"},{\"duration\":27524,\"local_ip\":\"1.2.3.4\",\"start_time\":-27519000,\"country\":\"Oz\",\"hostname\":\"Bla1\",\"session_score\":92,\"write_bytes\":2,\"data_bucket\":5314,\"end_time\":5000,\"read_bytes\":152656342,\"username\":\"Idan\",\"source_ip\":\".1.5.6.7\"},{\"duration\":27524,\"local_ip\":\"1.2.3.4\",\"start_time\":-27518000,\"country\":\"Oz\",\"hostname\":\"Bla1\",\"session_score\":92,\"write_bytes\":2,\"data_bucket\":5314,\"end_time\":6000,\"read_bytes\":152656342,\"username\":\"Idan\",\"source_ip\":\".1.5.6.7\"}],\"total\":10,\"offset\":0,\"warning\":null,\"info\":null}"));

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
