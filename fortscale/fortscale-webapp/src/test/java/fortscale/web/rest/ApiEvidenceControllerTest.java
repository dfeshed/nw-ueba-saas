package fortscale.web.rest;

import fortscale.aggregation.feature.services.historicaldata.SupportingInformationService;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.SupportingInformationData;
import fortscale.domain.core.dao.EvidencesRepository;
import fortscale.domain.histogram.HistogramKey;
import fortscale.domain.histogram.HistogramSingleKey;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryHelper;
import fortscale.services.dataqueries.querygenerators.DataQueryRunner;
import fortscale.services.dataqueries.querygenerators.DataQueryRunnerFactory;
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
	private static final String SOME_EVENT_VALUE = "{some event value}";

	private Map<HistogramKey,Double> countries;

	@Mock
	private EvidencesRepository repository;

	@Mock
	private DataQueryHelper dataQueryHelper;

	@Mock
	Evidence evidence;

	@InjectMocks
	private ApiEvidenceController controller;

	private MockMvc mockMvc;

	@Mock
	DataQueryRunnerFactory dataQueryRunnerFactory;
	@Mock
	DataQueryRunner dataQueryRunner;

	@Mock SupportingInformationService supportingInformationService;


	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(repository.findById(EVIDENCE_ID)).thenReturn(evidence);
		when(evidence.getId()).thenReturn(EVIDENCE_ID);
		when(dataQueryRunnerFactory.getDataQueryRunner(any(DataQueryDTO.class))).thenReturn(dataQueryRunner);

		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
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

	@Test(expected = NestedServletException.class)
	public void testGetTop3EventsWithWrongId() throws Exception {

		mockMvc.perform(get("/api/evidences/" + EVIDENCE_ID + "/events")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"));

		verify(repository, times(1)).findById(EVIDENCE_ID);

	}

	@Test
	public void testGetHistoricalData() throws Exception {

		countries = new HashMap<>();
		countries.put(new HistogramSingleKey("Israel"),10.0);
		countries.put(new HistogramSingleKey("USA"),7.0);
		HistogramKey anomalyCountry = new HistogramSingleKey("Afghanistan");
		countries.put(anomalyCountry,1.0);

		List<String> dataEntities = new ArrayList<>();
		dataEntities.add("vpn");

		when(evidence.getAnomalyValue()).thenReturn("Afghanistan");
		when(evidence.getDataEntitiesIds()).thenReturn(dataEntities);

		when(supportingInformationService.getEvidenceSupportingInformationData(anyString(), anyString(), anyList(), anyString(), anyString(), anyLong(), anyInt(), eq("Count"))).thenReturn(new SupportingInformationData(countries, anomalyCountry));

		MvcResult result =   mockMvc.perform(get("/api/evidences/" + EVIDENCE_ID + "/historical-data?context_type=someCT&context_value=someCV&feature=someFeature&function=Count").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("{\"data\":[{\"keys\":[\"Afghanistan\"],\"value\":1.0,\"anomaly\":true},{\"keys\":[\"USA\"],\"value\":7.0,\"anomaly\":false},{\"keys\":[\"Israel\"],\"value\":10.0,\"anomaly\":false}],\"total\":1,\"offset\":0,\"warning\":null,\"info\":null}"));

	}



	public static class TestEvidence extends Evidence{
		private static final long serialVersionUID = 1L;


		public void setId(String evidenceIdString) {
			super.setId(evidenceIdString);
		}
	}
}