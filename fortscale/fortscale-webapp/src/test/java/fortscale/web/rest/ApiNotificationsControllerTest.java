package fortscale.web.rest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

import org.mockito.*;
import org.springframework.data.domain.Sort;

import fortscale.domain.core.Notification;
import fortscale.domain.core.dao.NotificationResourcesRepository;
import fortscale.domain.core.dao.NotificationsRepository;
import fortscale.domain.core.NotificationResource;
import fortscale.web.beans.DataBean;
import fortscale.web.rest.ApiNotificationsController;

public class ApiNotificationsControllerTest {

	@Mock
	private NotificationsRepository notificationRepository;
	@Mock
	private NotificationResourcesRepository notificationResourcesRepository;
	@InjectMocks
	private ApiNotificationsController controller;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		// set up notification repository mocked behavior
		List<Notification> notifications = new ArrayList<Notification>();
		notifications.add(new Notification(1, "a", "a", "a", "a", "a", "a", "a"));
		notifications.add(new Notification(2, "b", "b", "b", "b", "b", "b", "b"));
		notifications.add(new Notification(3, "c", "c", "c", "c", "c", "c", "c"));
		notifications.add(new Notification(4, "d", "d", "d", "d", "d", "d", "d"));
		
		when(notificationRepository.findByTsGreaterThan(anyInt(), any(Sort.class))).thenReturn(notifications);
		
		// set up notification resource repository mocked behavior
		NotificationResource res = new NotificationResource("x", "x", "x");
		when(notificationResourcesRepository.findByMsg_name(anyString())).thenReturn(res);
		
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
