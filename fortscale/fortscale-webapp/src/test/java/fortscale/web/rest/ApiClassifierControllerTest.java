package fortscale.web.rest;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.EventScore;
import fortscale.services.fe.ClassifierService;

public class ApiClassifierControllerTest {

	@Mock
	private ClassifierService classifierService;
	@InjectMocks
	private ApiClassifierController controller;
	
	private MockMvc mockMvc;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(classifierService.getEventScores(anyListOf(LogEventsEnum.class), anyString(), anyInt(), anyInt())).thenReturn(new LinkedList<EventScore>());
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}
	
	
	@Test
	public void eventsTimeline_should_pass_default_parameters_to_service() throws Exception {
		mockMvc.perform(get("/api/classifier/eventsTimeline?username=moshe")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"));
		
		List<LogEventsEnum> enums = new ArrayList<LogEventsEnum>();
		enums.add(LogEventsEnum.vpn);
		enums.add(LogEventsEnum.login);
		enums.add(LogEventsEnum.ssh);
		verify(classifierService).getEventScores(enums, "moshe", 31, 200);
	}
	
}
