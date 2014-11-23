package fortscale.services.ipresolving;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import com.google.common.cache.Cache;

import fortscale.domain.events.DhcpEvent;
import fortscale.domain.events.dao.DhcpEventRepository;

public class DhcpResolverTest {

	@Mock
	private DhcpEventRepository dhcpEventRepository;
	
	@Mock
	private Cache<String, DhcpEvent> cache;
	
	@InjectMocks
	private DhcpResolver dhcpResolver;
	
	private long now;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		dhcpResolver.setCache(cache);
		
		now = System.currentTimeMillis();
	}
	
		
	private DhcpEvent createDhcpEvent(String ip, String hostname, String action, long timestampepoch, long expiration) {
		DhcpEvent event = new DhcpEvent();
		event.setIpaddress(ip);
		event.setAction(action);
		event.setHostname(hostname);
		event.setTimestampepoch(timestampepoch);
		event.setExpiration(expiration);
		return event;
	}
	
	@Test
	public void addDhcpEvent_should_skip_events_for_the_same_ip_hostname_and_expiration() {
		when(cache.getIfPresent("192.168.1.1")).thenReturn(createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200));
		
		dhcpResolver.addDhcpEvent(createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+110, now+200));
		
		verify(cache, times(0)).put(anyString(), any(DhcpEvent.class));
		verify(dhcpEventRepository, times(0)).save(any(DhcpEvent.class));
	}
	
	@Test
	public void addDhcpEvent_should_replace_the_cache_if_the_item_in_cache_is_older_than_given_event() {
		// mock old cache value
		when(cache.getIfPresent("192.168.1.1")).thenReturn(createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200));
		
		// act
		dhcpResolver.addDhcpEvent(createDhcpEvent("192.168.1.1", "or-me", DhcpEvent.ASSIGN_ACTION, now+300, now+400));
		
		// verify
		verify(cache).put("192.168.1.1", createDhcpEvent("192.168.1.1", "or-me", DhcpEvent.ASSIGN_ACTION, now+300, now+400));
	}
	
	@Test
	public void addDhcpEvent_should_not_update_the_cache_if_the_item_in_cache_is_newer_than_given_event() {
		// mock old cache value
		when(cache.getIfPresent("192.168.1.1")).thenReturn(createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+300, now+400));
		
		// act
		dhcpResolver.addDhcpEvent(createDhcpEvent("192.168.1.1", "or-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200));
		
		// verify
		verify(cache, times(0)).put(anyString(), any(DhcpEvent.class));
		
	}
	
	@Test
	public void addDhcpEvent_should_update_expiration_time_in_cached_event() {
		when(cache.getIfPresent("192.168.1.1")).thenReturn(createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200));
		
		// act
		dhcpResolver.addDhcpEvent(createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.RELEASE_ACTION, now+150, now+150));
		
		// verify
		verify(cache).put("192.168.1.1", createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+150));
	}
	
	@Test
	public void getLatestDhcpEventBeforeTimestamp_should_return_dhcp_event_from_cache_if_it_is_not_expired_before_given_timestamp() {
		DhcpEvent cached = createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200);
		when(cache.getIfPresent("192.168.1.1")).thenReturn(cached);
		
		// act
		DhcpEvent actual = dhcpResolver.getLatestDhcpEventBeforeTimestamp("192.168.1.1", now+150);
		
		// assert
		assertEquals(cached, actual);		
	}
	
	@Test
	public void getLatestDhcpEventBeforeTimestamp_should_return_event_from_repository_if_cached_event_is_expired_at_given_timestamp() {
		DhcpEvent cached = createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200);
		when(cache.getIfPresent("192.168.1.1")).thenReturn(cached);
		
		DhcpEvent saved = createDhcpEvent("192.168.1.1", "not", DhcpEvent.ACTION_FIELD_NAME, now+210, now+500);
		when(dhcpEventRepository.findByIpaddressAndTimestampepochLessThan(anyString(), any(Long.class), any(Pageable.class))).thenReturn(Arrays.asList(saved));
		
		// act
		DhcpEvent actual = dhcpResolver.getLatestDhcpEventBeforeTimestamp("192.168.1.1", now+250);
		
		// assert
		assertEquals(saved, actual);
	}
	
}
