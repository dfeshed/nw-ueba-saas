package fortscale.services.ipresolving;

import fortscale.domain.events.DhcpEvent;
import fortscale.domain.events.dao.DhcpEventRepository;
import fortscale.services.cache.CacheHandler;
import org.apache.commons.lang3.Range;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class DhcpResolverTest {

	@Mock
	private DhcpEventRepository dhcpEventRepository;
	
	@Mock
	private CacheHandler<String,DhcpEvent> cache;

	@SuppressWarnings("rawtypes")
	@Mock
	private CacheHandler<String,Range> ipBlackListCache;

	@InjectMocks
	private DhcpResolver dhcpResolver;


	
	private long now;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		dhcpResolver.setShouldUseBlackList(true);
		dhcpResolver = spy(dhcpResolver);
		dhcpResolver.initMetricsClass(null);
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
		when(cache.get("192.168.1.1")).thenReturn(createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200));
		
		dhcpResolver.addDhcpEvent(createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+110, now+200));
		
		verify(cache, times(0)).put(anyString(), any(DhcpEvent.class));
		verify(dhcpResolver, times(0)).removeFromBlackList(any(DhcpEvent.class));
		verify(dhcpEventRepository, times(0)).save(any(DhcpEvent.class));
	}

	@Test
	public void addDhcpEvent_should_expire_existing_assignment_once_a_new_assignment_is_created() {
		when(cache.get("192.168.1.1")).thenReturn(createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200));

		dhcpResolver.addDhcpEvent(createDhcpEvent("192.168.1.1", "or-me", DhcpEvent.ASSIGN_ACTION, now+150, now+300));

		verify(dhcpEventRepository).save(createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now + 100, now + 150));
		verify(dhcpEventRepository).save(createDhcpEvent("192.168.1.1", "or-me", DhcpEvent.ASSIGN_ACTION, now + 150, now + 300));
		verify(dhcpResolver, times(2)).removeFromBlackList(any(DhcpEvent.class));
	}

	@Test
	public void addDhcpEvent_should_replace_the_cache_if_the_item_in_cache_is_older_than_given_event() {
		// mock old cache value
		when(cache.get("192.168.1.1")).thenReturn(createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200));
		
		// act
		dhcpResolver.addDhcpEvent(createDhcpEvent("192.168.1.1", "or-me", DhcpEvent.ASSIGN_ACTION, now+300, now+400));
		
		// verify
		verify(cache).put("192.168.1.1", createDhcpEvent("192.168.1.1", "or-me", DhcpEvent.ASSIGN_ACTION, now+300, now+400));
		verify(dhcpResolver, times(1)).removeFromBlackList(any(DhcpEvent.class));
	}
	
	@Test
	public void addDhcpEvent_should_not_update_the_cache_if_the_item_in_cache_is_newer_than_given_event() {
		// mock old cache value
		when(cache.get("192.168.1.1")).thenReturn(createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+300, now+400));
		
		// act
		dhcpResolver.addDhcpEvent(createDhcpEvent("192.168.1.1", "or-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200));
		
		// verify
		verify(cache, times(0)).put(anyString(), any(DhcpEvent.class));
		verify(dhcpResolver, times(0)).removeFromBlackList(any(DhcpEvent.class));
	}
	
	@Test
	public void addDhcpEvent_should_update_expiration_time_in_cached_event() {
		when(cache.get("192.168.1.1")).thenReturn(createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200));
		
		// act
		dhcpResolver.addDhcpEvent(createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.RELEASE_ACTION, now+150, now+150));
		
		// verify
		verify(cache).put("192.168.1.1", createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+150));
	}

	@Test
	public void getLatestDhcpEventBeforeTimestamp_should_update_blacklist_with_ip() {
		// act
		dhcpResolver.getLatestDhcpEventBeforeTimestamp("192.168.1.1", now+150);

		// assert
		verify(dhcpResolver, times(1)).addToBlackList("192.168.1.1",0,now+150);
	}

	@Test
	public void getLatestDhcpEventBeforeTimestamp_should_return_dhcp_event_from_cache_if_it_is_not_expired_before_given_timestamp() {
		DhcpEvent cached = createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200);
		when(cache.get("192.168.1.1")).thenReturn(cached);
		
		// act
		DhcpEvent actual = dhcpResolver.getLatestDhcpEventBeforeTimestamp("192.168.1.1", now+150);
		
		// assert
		assertEquals(cached, actual);		
	}

	@Test
	public void getLatestDhcpEventBeforeTimestamp_should_return_null_when_time_range_in_blacklist() {
		DhcpEvent cached = createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200);
		when(cache.get("192.168.1.1")).thenReturn(cached);
		when(ipBlackListCache.containsKey("192.168.1.1")).thenReturn(true);
		when(ipBlackListCache.get("192.168.1.1")).thenReturn(Range.between(now+100,now+270));

		// act
		DhcpEvent actual = dhcpResolver.getLatestDhcpEventBeforeTimestamp("192.168.1.1", now+150);

		// assert
		assertEquals(null, actual);
	}
	
	@Test
	public void getLatestDhcpEventBeforeTimestamp_should_return_event_from_repository_if_cached_event_is_expired_at_given_timestamp() {
		DhcpEvent cached = createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200);
		when(cache.get("192.168.1.1")).thenReturn(cached);
		
		DhcpEvent saved = createDhcpEvent("192.168.1.1", "not", DhcpEvent.ACTION_FIELD_NAME, now+210, now+500);
		when(dhcpEventRepository.findByIpaddressAndTimestampepochLessThan(anyString(), any(Long.class), any(Pageable.class))).thenReturn(Arrays.asList(saved));
		
		// act
		DhcpEvent actual = dhcpResolver.getLatestDhcpEventBeforeTimestamp("192.168.1.1", now+250);
		
		// assert
		assertEquals(saved, actual);
	}

	@Test
	public void addToBlackList_should_do_nothing_since_should_not_use_blacklist(){
		DhcpEvent cached = createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200);
		dhcpResolver.setShouldUseBlackList(false);
		dhcpResolver.addToBlackList(cached.getIpaddress(),cached.getTimestampepoch(), cached.getTimestampepoch() + 100);
		verify(ipBlackListCache,never()).put(anyString(),any(Range.class));
	}

	@Test
	public void addToBlackList_should_add_ip_to_blacklist_with_empty_range(){

		dhcpResolver.addToBlackList("192.168.1.1",123l, 456l);
		verify(ipBlackListCache, times(1)).put(anyString(), eq(Range.between(123l, Long.MAX_VALUE)));
	}

	@Test
	public void addToBlackList_should_add_ip_to_blacklist_with_only_start_timestamp(){
		DhcpEvent cached = createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200);
		dhcpResolver.addToBlackList(cached.getIpaddress(),cached.getTimestampepoch() + 100, cached.getTimestampepoch() + 210);
		verify(ipBlackListCache, times(1)).put(eq(cached.getIpaddress()),eq(Range.between(now+200,Long.MAX_VALUE)));
	}

	@Test
	public void addToBlackList_should_add_ip_to_blacklist_with_only_end_timestamp(){
		DhcpEvent saved = createDhcpEvent("192.168.1.1", "not", DhcpEvent.ACTION_FIELD_NAME, now+210, now+500);
		when(dhcpEventRepository.findByIpaddressAndTimestampepochGreaterThanEqual(anyString(), any(Long.class), any(Pageable.class))).thenReturn(Arrays.asList(saved));
		dhcpResolver.addToBlackList(saved.getIpaddress(), 0l, 456l);
		verify(ipBlackListCache, times(1)).put(eq(saved.getIpaddress()),eq(Range.between(0l,now+210)));
	}

	@Test
	public void addToBlackList_should_add_ip_to_blacklist_with_both_start_timestamp_end_timestamp(){
		DhcpEvent cached = createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200);
		DhcpEvent saved = createDhcpEvent("192.168.1.1", "not", DhcpEvent.ACTION_FIELD_NAME, now+210, now+500);
		when(dhcpEventRepository.findByIpaddressAndTimestampepochGreaterThanEqual(anyString(), any(Long.class), any(Pageable.class))).thenReturn(Arrays.asList(saved));
		dhcpResolver.addToBlackList(cached.getIpaddress(), cached.getTimestampepoch() + 100, cached.getTimestampepoch() + 210);
		verify(ipBlackListCache, times(1)).put(eq(saved.getIpaddress()),eq(Range.between(now+200,now+210)));
	}

	@Test
	public void addToBlackList_should_not_add_ip_to_blacklist_when_exists_range_is_newer(){
		DhcpEvent cached = createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200);
		DhcpEvent saved = createDhcpEvent("192.168.1.1", "not", DhcpEvent.ACTION_FIELD_NAME, now+210, now+500);
		when(dhcpEventRepository.findByIpaddressAndTimestampepochGreaterThanEqual(anyString(), any(Long.class), any(Pageable.class))).thenReturn(Arrays.asList(saved));
		when(ipBlackListCache.containsKey(cached.getIpaddress())).thenReturn(true);
		when(ipBlackListCache.get(cached.getIpaddress())).thenReturn(Range.between(now+600,Long.MAX_VALUE));
		dhcpResolver.addToBlackList(cached.getIpaddress(), cached.getTimestampepoch() + 205, cached.getTimestampepoch() + 210);
		verify(ipBlackListCache, never()).put(eq(saved.getIpaddress()),any(Range.class));
	}


	@Test
	public void removeFromBlackList_should_do_nothing_since_should_not_use_blacklist(){
		DhcpEvent cached = createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200);
		dhcpResolver.setShouldUseBlackList(false);
		dhcpResolver.removeFromBlackList(cached);
		verify(ipBlackListCache,never()).remove(anyString());
		verify(ipBlackListCache,never()).put(eq(cached.getIpaddress()),any(Range.class));
	}

	@Test
	public void removeFromBlackList_should_remove_from_blacklist_when_new_time_range_intersact_with_the_old_one(){
		DhcpEvent newEvent = createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200);
		when(ipBlackListCache.containsKey(newEvent.getIpaddress())).thenReturn(true);
		when(ipBlackListCache.get(newEvent.getIpaddress())).thenReturn(Range.between(now+150,now+270));
		dhcpResolver.removeFromBlackList(newEvent);
		verify(ipBlackListCache, times(1)).remove(newEvent.getIpaddress());
	}

	@Test
	public void removeFromBlackList_should_update_blacklist_when_new_time_range_limits_the_old_one(){
		DhcpEvent newEvent = createDhcpEvent("192.168.1.1", "pick-me", DhcpEvent.ASSIGN_ACTION, now+100, now+200);
		when(ipBlackListCache.containsKey(newEvent.getIpaddress())).thenReturn(true);
		when(ipBlackListCache.get(newEvent.getIpaddress())).thenReturn(Range.between(now+90,now+270));
		dhcpResolver.removeFromBlackList(newEvent);
		verify(ipBlackListCache,times(1)).put(eq(newEvent.getIpaddress()), eq(Range.between(now + 90, now + 100)));
	}





	
}
