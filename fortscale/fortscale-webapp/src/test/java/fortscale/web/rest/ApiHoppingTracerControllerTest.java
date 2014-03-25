package fortscale.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import fortscale.domain.tracer.Connection;
import fortscale.domain.tracer.FilterSettings;
import fortscale.domain.tracer.ListMode;
import fortscale.services.tracer.HoppingTracerService;

public class ApiHoppingTracerControllerTest {

	private static final double DELTA = 1e-15;
	
	@Mock
	private HoppingTracerService hoppingTracerService;
	@InjectMocks
	private ApiHoppingTracerController controller;
	
	private MockMvc mockMvc;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(hoppingTracerService.expandConnections(anyString(), anyBoolean(), any(FilterSettings.class)))
		.thenReturn(new LinkedList<Connection>());
		
		List<String> machines = new ArrayList<String>(1);
		machines.add("myhost");
		when(hoppingTracerService.lookupMachines(anyString(), anyInt(), anyInt())).thenReturn(machines);
		
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}
	
	@Test	
	public void expand_should_pass_defualt_values_to_repository_when_called_with_no_params() throws Exception {
		
		// perform rest call to the controller
		mockMvc.perform(get("/api/tracer/xxx/expand").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		// verify default parameters were passed to repository
		ArgumentCaptor<FilterSettings> filterCaptor = ArgumentCaptor.forClass(FilterSettings.class);
		ArgumentCaptor<String> machineCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> sourceCaptor = ArgumentCaptor.forClass(Boolean.class);
		verify(hoppingTracerService).expandConnections(machineCaptor.capture(), sourceCaptor.capture(), filterCaptor.capture());
		assertTrue(machineCaptor.getValue()!=null);
		assertTrue(machineCaptor.getValue().equals("xxx"));
		assertTrue(sourceCaptor.getValue()==true);
		assertTrue(filterCaptor.getValue()!=null);
		assertTrue(filterCaptor.getValue().getMachines().isEmpty());
		assertTrue(filterCaptor.getValue().getAccounts().isEmpty());
		assertTrue(filterCaptor.getValue().getScoreRange().isEmpty());
		assertTrue(filterCaptor.getValue().getStart()==0L);
		assertTrue(filterCaptor.getValue().getEnd()==0L);
		assertTrue(filterCaptor.getValue().getSources().isEmpty());
	}
	
	@Test
	public void expand_should_fail_when_start_is_after_finish() throws Exception {
		// perform rest call to the controller
		mockMvc.perform(get("/api/tracer/xxx/expand")
				.param("start", "5000")
				.param("end", "300")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}
	
	
	@Test
	public void expand_should_pass_all_parameters_values_to_repostiroy() throws Exception {
		// perform rest call to the controller
		mockMvc.perform(get("/api/tracer/xxx/expand")
				.param("treatAsSource", "false")
				.param("start", "1394521686")
				.param("end", "1394521686")
				.param("accounts", "a,b,c")
				.param("excludeAccounts", "true")
				.param("machines", "box1,box2,box3")
				.param("excludeMachines", "true")
				.param("sources", "vpn,ssh")
				.param("excludeSources", "true")
				.param("minScore", "90.0d")
				.param("maxScore", "100.0d")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"));
		
		// verify default parameters were passed to repository
		ArgumentCaptor<FilterSettings> filterCaptor = ArgumentCaptor.forClass(FilterSettings.class);
		ArgumentCaptor<String> machineCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> sourceCaptor = ArgumentCaptor.forClass(Boolean.class);
		verify(hoppingTracerService).expandConnections(machineCaptor.capture(), sourceCaptor.capture(), filterCaptor.capture());
		assertEquals("xxx",machineCaptor.getValue());
		assertTrue(sourceCaptor.getValue()==false);
		assertTrue(filterCaptor.getValue()!=null);
		FilterSettings filter = filterCaptor.getValue();
		assertTrue(CollectionUtils.isEqualCollection(filter.getMachines(), Arrays.asList("box1", "box2", "box3")));
		assertTrue(CollectionUtils.isEqualCollection(filter.getAccounts(), Arrays.asList("a", "b", "c")));
		assertTrue(CollectionUtils.isEqualCollection(filter.getSources(), Arrays.asList("vpn", "ssh")));
		assertTrue(filter.getAccountsListMode()==ListMode.Exclude);
		assertTrue(filter.getMachinesListMode()==ListMode.Exclude);
		assertTrue(filter.getSourcesListMode()==ListMode.Exclude);
		assertEquals(100.0d,filter.getScoreRange().getMaxScore(), DELTA);
		assertEquals(90.0d,filter.getScoreRange().getMinScore(), DELTA);
		assertEquals(1394521686L,filter.getStart());
		assertEquals(1394521686L,filter.getEnd());
	}
	
	@Test
	public void lookup_should_fail_when_machine_name_empty() throws Exception {
		// perform rest call to controller
		mockMvc.perform(get("/api/tracer//lookup")).andExpect(status().is(404));
	}
	
	@Test
	public void lookup_should_pass_machine_name_to_tracer_service() throws Exception {
		// perform rest call to controller
		mockMvc.perform(get("/api/tracer/myhost/lookup"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"));
	
		// verify parameters passed to tracer service
		verify(hoppingTracerService).lookupMachines("myhost", 0, 10);
	}
	
	@Test
	public void lookup_should_pass_count_to_tracer_service() throws Exception {
		// perform rest call to controller
		mockMvc.perform(get("/api/tracer/myhost/lookup").param("count", "20"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"));
	
		// verify parameters passed to tracer service
		verify(hoppingTracerService).lookupMachines("myhost", 0, 20);
	}
	
	
	
}
