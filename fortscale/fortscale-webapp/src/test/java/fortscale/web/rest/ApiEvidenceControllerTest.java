package fortscale.web.rest;

import fortscale.domain.core.Evidence;
import fortscale.domain.core.dao.EvidencesRepository;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querygenerators.DataQueryRunner;
import fortscale.services.dataqueries.querygenerators.DataQueryRunnerFactory;
import fortscale.services.exceptions.InvalidValueException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiEvidenceControllerTest {

	public static final String EVIDENCE_ID = "test1";
	private static final String SOME_EVENT_VALUE = "{some event value}";

	@Mock
	private EvidencesRepository repository;

	@InjectMocks
	private ApiEvidenceController controller;

	private MockMvc mockMvc;

	@Mock
	DataQueryRunnerFactory dataQueryRunnerFactory;
	@Mock
	DataQueryRunner dataQueryRunner;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		Evidence evidence = new Evidence();
		when(repository.findById(EVIDENCE_ID)).thenReturn(evidence);
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
		evidence.setDataEntityId("vpn");
		evidence.setTop3eventsJsonStr(SOME_EVENT_VALUE);
		evidence.setStartDate(System.currentTimeMillis());
		evidence.setEndDate(System.currentTimeMillis());
		when(repository.findById(EVIDENCE_ID)).thenReturn(evidence);
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

	public static class TestEvidence extends Evidence{
		private static final long serialVersionUID = 1L;


		public void setId(String evidenceIdString) {
			super.setId(evidenceIdString);
		}
	}
}