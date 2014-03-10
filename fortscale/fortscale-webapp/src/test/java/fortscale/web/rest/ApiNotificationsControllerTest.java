package fortscale.web.rest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

import org.mockito.*;
import org.mockito.internal.matchers.Any;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import fortscale.domain.core.Notification;
import fortscale.domain.core.dao.NotificationResourcesRepository;
import fortscale.domain.core.dao.NotificationsRepository;
import fortscale.domain.core.NotificationResource;
import fortscale.web.beans.DataBean;

public class ApiNotificationsControllerTest {

	@Mock
	private NotificationsRepository notificationRepository;
	@Mock
	private NotificationResourcesRepository notificationResourcesRepository;
	@InjectMocks
	private ApiNotificationsController controller;
	
	private MockMvc mockMvc;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		// set up notification repository mocked behavior
		List<Notification> notifications = new ArrayList<Notification>();
		notifications.add(new Notification(1, "a", "a", "a", "a", "a", "a", "a", false));
		notifications.add(new Notification(2, "b", "b", "b", "b", "b", "b", "b", false));
		notifications.add(new Notification(3, "c", "c", "c", "c", "c", "c", "c", false));
		notifications.add(new Notification(4, "d", "d", "d", "d", "d", "d", "d", false));
		
		when(notificationRepository.findByTsGreaterThan(anyInt(), any(Sort.class))).thenReturn(notifications);
		
		// set up notification resource repository mocked behavior
		NotificationResource res = new NotificationResource("x", "x", "x");
		when(notificationResourcesRepository.findByMsg_name(anyString())).thenReturn(res);
		
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}
	

	@Test
	public void list_with_no_parameters_should_pass_default_settings_to_repository() throws Exception {

		// mock repository to return empty results so the controller could continue and not fail
		when(notificationRepository.findByPredicates(anySetOf(String.class), anySetOf(String.class), eq(true), anySetOf(String.class), 
				anySetOf(String.class), any(Date.class), any(Date.class), any(PageRequest.class)))
			.thenReturn(new PageImpl<Notification>(new LinkedList<Notification>()));
		
		// perform rest call to the controller
		mockMvc.perform(get("/api/notifications").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"));

		// verify interaction with repository
		verify(notificationRepository).findByPredicates(new HashSet<String>(), new HashSet<String>(), true, 
				new HashSet<String>(), new HashSet<String>(), null, null, new PageRequest(0, 20, Direction.DESC, "ts"));
	}
	
	@Test
	public void list_with_includeDissmissed_false_should_filter_out_dismissed_notifications() throws Exception {
		// mock repository to return empty results so the controller could continue and not fail
		when(notificationRepository.findByPredicates(anySetOf(String.class), anySetOf(String.class), eq(false), anySetOf(String.class), 
				anySetOf(String.class), any(Date.class), any(Date.class), any(PageRequest.class)))
			.thenReturn(new PageImpl<Notification>(new LinkedList<Notification>()));
		
		// perform rest call to the controller
		mockMvc.perform(get("/api/notifications?includeDissmissed=false").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"));

		// verify interaction with repository
		verify(notificationRepository).findByPredicates(new HashSet<String>(), new HashSet<String>(), false, 
				new HashSet<String>(), new HashSet<String>(), null, null, new PageRequest(0, 20, Direction.DESC, "ts"));
	}
	
	
	@Test
	public void list_with_paging_parameters_should_pass_it_to_repository() throws Exception {
		// mock repository to return empty results so the controller could continue and not fail
		when(notificationRepository.findByPredicates(anySetOf(String.class), anySetOf(String.class), eq(true), anySetOf(String.class), 
				anySetOf(String.class), any(Date.class), any(Date.class), any(PageRequest.class)))
			.thenReturn(new PageImpl<Notification>(new LinkedList<Notification>()));
		
		// perform rest call to the controller
		mockMvc.perform(get("/api/notifications?page=2&size=100").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"));

		// verify interaction with repository
		verify(notificationRepository).findByPredicates(new HashSet<String>(), new HashSet<String>(), true, 
				new HashSet<String>(), new HashSet<String>(), null, null, new PageRequest(2, 100, Direction.DESC, "ts"));
	}
	
	@Test
	public void list_with_include_users_should_pass_collection_to_repository() throws Exception {
		// mock repository to return empty results so the controller could continue and not fail
		when(notificationRepository.findByPredicates(anySetOf(String.class), anySetOf(String.class), eq(true), anySetOf(String.class), 
				anySetOf(String.class), any(Date.class), any(Date.class), any(PageRequest.class)))
			.thenReturn(new PageImpl<Notification>(new LinkedList<Notification>()));
		
		// perform rest call to the controller
		mockMvc.perform(get("/api/notifications?includeUsers=xxx,yyy").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"));

		// verify interaction with repository
		Set<String> users = new HashSet<String>();
		users.add("xxx");
		users.add("yyy");
		verify(notificationRepository).findByPredicates(users, new HashSet<String>(), true, 
				new HashSet<String>(), new HashSet<String>(), null, null, new PageRequest(0, 20, Direction.DESC, "ts"));
		
	}
	
	
	@Test
	public void after_ZeroTimeStamp_ShouldReturnAllNotification() {
		// given
		long ts = 0;
		
		// when
		DataBean<List<Notification>> result = controller.after(ts);
		
		//then
		assertTrue(result.getTotal() == 4);
	}
	
	
	@Test
	public void after_ResultsShouldBeSortedByTSAcs() {
		// given
		long ts = 0;
		
		// when
		DataBean<List<Notification>> result = controller.after(ts);
		
		//then
		assertTrue(result.getTotal() == 4);
		assertTrue(result.getData().get(0).getTs() == 1);
		assertTrue(result.getData().get(1).getTs() == 2);
		assertTrue(result.getData().get(2).getTs() == 3);
		assertTrue(result.getData().get(3).getTs() == 4);
	}

}
