package fortscale.web.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import fortscale.domain.core.NotificationFlag;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import fortscale.domain.core.Notification;
import fortscale.domain.core.NotificationResource;
import fortscale.domain.core.dao.NotificationResourcesRepository;
import fortscale.domain.core.dao.NotificationsRepository;
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
		notifications.add(new Notification("1", 1, "a", "a", "a", "a", "a", "a", "a", "a", false, 0));
		notifications.add(new Notification("2", 2, "b", "b", "b", "b", "b", "b", "b", "b", false, 0));
		notifications.add(new Notification("3", 3, "c", "c", "c", "c", "c", "c", "c", "c", false, 0));
		notifications.add(new Notification("4", 4, "d", "d", "d", "d", "d", "d", "d", "d", false, 0));
		
		when(notificationRepository.findByTsGreaterThanExcludeComments(anyInt(), any(Sort.class))).thenReturn(notifications);
		
		// set up notification resource repository mocked behavior
		NotificationResource res = new NotificationResource("x", "x", "x");
		when(notificationResourcesRepository.findByMsg_name(anyString())).thenReturn(res);
		
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}
	

	@Test
	public void list_with_no_parameters_should_pass_default_settings_to_repository() throws Exception {

		// mock repository to return empty results so the controller could continue and not fail
		when(notificationRepository.findByPredicates(anyListOf(String.class), anyListOf(String.class), eq(true), anyListOf(String.class), 
				anyListOf(String.class), anyLong(), anyLong(), any(PageRequest.class)))
			.thenReturn(new PageImpl<Notification>(new LinkedList<Notification>()));
		
		// perform rest call to the controller
		mockMvc.perform(get("/api/notifications/list").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"));

		// verify interaction with repository
		verify(notificationRepository).findByPredicates(null, null, true, 
				null, null, 0, 0, new PageRequest(0, 20, Direction.DESC, "ts"));
	}
	
	@Test
	public void list_with_includeDissmissed_false_should_filter_out_dismissed_notifications() throws Exception {
		// mock repository to return empty results so the controller could continue and not fail
		when(notificationRepository.findByPredicates(anyListOf(String.class), anyListOf(String.class), eq(false), anyListOf(String.class), 
				anyListOf(String.class), anyLong(), anyLong(), any(PageRequest.class)))
			.thenReturn(new PageImpl<Notification>(new LinkedList<Notification>()));
		
		// perform rest call to the controller
		mockMvc.perform(get("/api/notifications/list?includeDissmissed=false").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"));

		// verify interaction with repository
		verify(notificationRepository).findByPredicates(null, null, false, 
				null, null, 0, 0, new PageRequest(0, 20, Direction.DESC, "ts"));
	}
	
	
	@Test
	public void list_with_paging_parameters_should_pass_it_to_repository() throws Exception {
		// mock repository to return empty results so the controller could continue and not fail
		when(notificationRepository.findByPredicates(anyListOf(String.class), anyListOf(String.class), eq(true), anyListOf(String.class), 
				anyListOf(String.class), anyLong(), anyLong(), any(PageRequest.class)))
			.thenReturn(new PageImpl<Notification>(new LinkedList<Notification>()));
		
		// perform rest call to the controller
		mockMvc.perform(get("/api/notifications/list?page=2&size=100").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"));

		// verify interaction with repository
		verify(notificationRepository).findByPredicates(null, null, true, 
				null, null, 0, 0, new PageRequest(2, 100, Direction.DESC, "ts"));
	}
	
	@Test
	public void list_with_include_fsids_should_pass_collection_to_repository() throws Exception {
		// mock repository to return empty results so the controller could continue and not fail
		when(notificationRepository.findByPredicates(anyListOf(String.class), anyListOf(String.class), eq(true), anyListOf(String.class), 
				anyListOf(String.class), anyLong(), anyLong(), any(PageRequest.class)))
			.thenReturn(new PageImpl<Notification>(new LinkedList<Notification>()));
		
		// perform rest call to the controller
		mockMvc.perform(get("/api/notifications/list?includeFsIds=xxx,yyy").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"));

		// verify interaction with repository
		LinkedList<String> users = new LinkedList<String>();
		users.add("xxx");
		users.add("yyy");
		verify(notificationRepository).findByPredicates(users, null, true, 
				null, null, 0, 0, new PageRequest(0, 20, Direction.DESC, "ts"));
		
	}
	
	@Test
	public void list_with_both_include_and_exclude_fsids_should_return_error() throws Exception {
		// perform rest call to the controller
		mockMvc.perform(get("/api/notifications/list?includeFsIds=xxx,yyy&excludeFsIds=fff").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}
		
	@Test
	public void dismiss_should_succeed_with_valid_notification_id() throws Exception {
		// mock repository to return notification
		Notification notification = new Notification("1", 1L, "my-index", "my-generator", "name", "cause", "displayName", "uuid", "fsId", "type", false, 0);
		when(notificationRepository.findOne("1")).thenReturn(notification);
		
		// perform rest call to the controller
		mockMvc.perform(get("/api/notifications/dismiss/1").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		
		// verify interactions with the repository
		ArgumentCaptor<Notification> notificationCapture = ArgumentCaptor.forClass(Notification.class);
		verify(notificationRepository).save(notificationCapture.capture());
		assertTrue(notificationCapture.getValue().isDismissed());
	}
	
	@Test
	public void commentOnNotification_should_increment_comments_count() throws Exception {
		// mock repository to return notification
		Notification notification = new Notification("1", 1L, "my-index", "my-generator", "name", "cause", "displayName", "uuid", "fsId", "type", false, 0);
		when(notificationRepository.findOne("1")).thenReturn(notification);
		when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
		
		// perform rest call to the controller
		mockMvc.perform(get("/api/notifications/comment/1?message=hello world").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"));
		
		// verify interactions with the repository
		ArgumentCaptor<Notification> notificationCapture = ArgumentCaptor.forClass(Notification.class);
		verify(notificationRepository).save(notificationCapture.capture());
		assertTrue(notificationCapture.getValue().getCommentsCount()==1);
		assertTrue(notificationCapture.getValue().getComments().get(0).getMessage().equals("hello world"));
		assertTrue(notificationCapture.getValue().getComments().get(0).getBasedOn()==null);
	}
	
	@Test
	public void dismiss_should_not_save_already_dismissed_notification() throws Exception {
		// mock repository to return notification
		Notification notification = new Notification("1", 1L, "my-index", "my-generator", "name", "cause", "displayName", "uuid", "fsId", "type", true, 0);
		when(notificationRepository.findOne("1")).thenReturn(notification);
		
		// perform rest call to the controller
		mockMvc.perform(get("/api/notifications/dismiss/1").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		
		// verify interactions with the repository
		verify(notificationRepository, times(0)).save(any(Notification.class));		
	}
	
	
	@Test
	public void undismiss_should_succeed_with_valid_notification_id() throws Exception {
		// mock repository to return notification
		Notification notification = new Notification("1", 1L, "my-index", "my-generator", "name", "cause", "displayName", "uuid", "fsId", "type", true, 0);
		when(notificationRepository.findOne("1")).thenReturn(notification);
		
		// perform rest call to the controller
		mockMvc.perform(get("/api/notifications/undismiss/1").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		
		// verify interactions with the repository
		ArgumentCaptor<Notification> notificationCapture = ArgumentCaptor.forClass(Notification.class);
		verify(notificationRepository).save(notificationCapture.capture());
		assertTrue(!notificationCapture.getValue().isDismissed());
	}

	@Test
	public void flag_should_succeed_adding_flag_with_valid_notification_id() throws Exception {
		// mock repository to return notification
		Notification notification = new Notification("1", 1L, "my-index", "my-generator", "name", "cause", "displayName", "uuid", "fsId", "type", true, 0);
		when(notificationRepository.findOne("1")).thenReturn(notification);

		// perform rest call to the controller
		mockMvc.perform(get("/api/notifications/flag/1?flag=FP").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// verify interactions with the repository
		ArgumentCaptor<Notification> notificationCapture = ArgumentCaptor.forClass(Notification.class);
		verify(notificationRepository).save(notificationCapture.capture());
		assertEquals(NotificationFlag.FP,notificationCapture.getValue().getFlag());
	}

	@Test
	 public void flag_should_succeed_removing_flag_with_valid_notification_id() throws Exception {
		// mock repository to return notification
		Notification notification = new Notification("1", 1L, "my-index", "my-generator", "name", "cause", "displayName", "uuid", "fsId", "type", true, 0);
		notification.setFlag(NotificationFlag.FN);
		when(notificationRepository.findOne("1")).thenReturn(notification);

		// perform rest call to the controller
		mockMvc.perform(get("/api/notifications/flag/1").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// verify interactions with the repository
		ArgumentCaptor<Notification> notificationCapture = ArgumentCaptor.forClass(Notification.class);
		verify(notificationRepository).save(notificationCapture.capture());
		assertNull(notificationCapture.getValue().getFlag());
	}

	@Test
	public void flag_should_succeed_updating_flag_with_valid_notification_id() throws Exception {
		// mock repository to return notification
		Notification notification = new Notification("1", 1L, "my-index", "my-generator", "name", "cause", "displayName", "uuid", "fsId", "type", true, 0);
		notification.setFlag(NotificationFlag.TN);
		when(notificationRepository.findOne("1")).thenReturn(notification);

		// perform rest call to the controller
		mockMvc.perform(get("/api/notifications/flag/1?flag=TP").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// verify interactions with the repository
		ArgumentCaptor<Notification> notificationCapture = ArgumentCaptor.forClass(Notification.class);
		verify(notificationRepository).save(notificationCapture.capture());
		assertEquals(NotificationFlag.TP,notificationCapture.getValue().getFlag());
	}
	
	
	@Test
	public void dismiss_should_not_save_notification_that_does_not_exists() throws Exception {
		// mock repository to return notification
		when(notificationRepository.findOne("1")).thenReturn(null);
		
		// perform rest call to the controller
		mockMvc.perform(get("/api/notifications/dismiss/1").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		
		// verify interactions with the repository
		verify(notificationRepository, times(0)).save(any(Notification.class));		
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
