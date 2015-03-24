package fortscale.services.ipresolving;

import fortscale.domain.events.ComputerLoginEvent;
import fortscale.domain.events.dao.ComputerLoginEventRepository;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ComputerLoginResolverTest {


	@Mock
	private ComputerLoginEventRepository computerLoginEventRepository;

	@Mock
	private CacheHandler<String,ComputerLoginEvent> cache;

	@Mock
	private CacheHandler<String,Range> ipBlackListCache;

	@InjectMocks
	private ComputerLoginResolver computerLoginResolver;



	private long now;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		computerLoginResolver.setShouldUseBlackList(true);
		computerLoginResolver.setUseCacheForResolving(true);
		computerLoginResolver.setIpToHostNameUpdateResolutionInMins(60);
		computerLoginResolver.setLeaseTimeInMins(1);
		computerLoginResolver = spy(computerLoginResolver);
		now = System.currentTimeMillis();
	}

	private ComputerLoginEvent createComputerLoginEvent(String ip, String hostname, long timestampepoch) {
		ComputerLoginEvent event = new ComputerLoginEvent();
		event.setIpaddress(ip);
		event.setHostname(hostname);
		event.setTimestampepoch(timestampepoch);
		return event;
	}


	@Test
	public void isToUpdate_should_return_true_when_not_in_cache(){
		ComputerLoginEvent newEvent = createComputerLoginEvent("192.168.1.1", "pick-me", now + 100);
		assertEquals(true, computerLoginResolver.isToUpdate(newEvent));
	}

	@Test
	public void isToUpdate_should_return_true_when_cached_resolved_is_for_other_name(){
		ComputerLoginEvent newEvent = createComputerLoginEvent("192.168.1.1", "pick-me", now + 100);
		ComputerLoginEvent cached = createComputerLoginEvent("192.168.1.1", "pick-me2", now + 100);
		when(cache.get("192.168.1.1")).thenReturn(cached);
		assertEquals(true, computerLoginResolver.isToUpdate(newEvent));
	}

	@Test
	public void isToUpdate_should_return_true_when_cached_resolved_is_old(){
		ComputerLoginEvent newEvent = createComputerLoginEvent("192.168.1.1", "pick-me", now + 60*60*1000 +150);
		ComputerLoginEvent cached = createComputerLoginEvent("192.168.1.1", "pick-me", now + 100);
		when(cache.get("192.168.1.1")).thenReturn(cached);
		assertEquals(true, computerLoginResolver.isToUpdate(newEvent));
	}

	@Test
	public void isToUpdate_should_return_false_when_cached_resolved_same_name_newer(){
		ComputerLoginEvent newEvent = createComputerLoginEvent("192.168.1.1", "pick-me", now + 90);
		ComputerLoginEvent cached = createComputerLoginEvent("192.168.1.1", "pick-me", now + 100);
		when(cache.get("192.168.1.1")).thenReturn(cached);
		assertEquals(false, computerLoginResolver.isToUpdate(newEvent));
	}

	@Test
	public void getComputerLoginEvent_should_update_blacklist_with_ip() {
		// act
		ComputerLoginEvent actual = computerLoginResolver.getComputerLoginEvent("192.168.1.1", now + 150);

		// assert
		verify(computerLoginResolver, times(1)).addToBlackList("192.168.1.1",now+150, now+150);
	}

	@Test
	public void getComputerLoginEvent_should_return_computer_login_event_from_cache() {
		ComputerLoginEvent cached = createComputerLoginEvent("192.168.1.1", "pick-me", now + 100);
		when(cache.get("192.168.1.1")).thenReturn(cached);

		// act
		ComputerLoginEvent actual = computerLoginResolver.getComputerLoginEvent("192.168.1.1", now + 100);

		// assert
		assertEquals(cached, actual);
	}

	@Test
	public void getComputerLoginEvent_should_return_null_when_time_range_in_blacklist() {
		ComputerLoginEvent cached = createComputerLoginEvent("192.168.1.1", "pick-me", now + 100);
		when(cache.get("192.168.1.1")).thenReturn(cached);
		when(ipBlackListCache.containsKey("192.168.1.1")).thenReturn(true);
		when(ipBlackListCache.get("192.168.1.1")).thenReturn(Range.between(now+90,now+270));

		// act
		ComputerLoginEvent actual = computerLoginResolver.getComputerLoginEvent("192.168.1.1", now + 100);

		// assert
		assertEquals(null, actual);
	}

	@Test
	public void getComputerLoginEvent_should_return_event_from_repository_should_not_use_cache() {
		computerLoginResolver.setUseCacheForResolving(false);
		ComputerLoginEvent cached = createComputerLoginEvent("192.168.1.1", "pick-me", now + 100);
		when(cache.get("192.168.1.1")).thenReturn(cached);

		ComputerLoginEvent saved = createComputerLoginEvent("192.168.1.1", "pick-me", now + 250);
		when(computerLoginEventRepository.findByIpaddressAndTimestampepochBetween(anyString(), any(Long.class), any(Long.class),any(Pageable.class))).thenReturn(Arrays.asList(saved));

		// act
		ComputerLoginEvent actual = computerLoginResolver.getComputerLoginEvent("192.168.1.1", now + 250);

		// assert
		assertEquals(saved, actual);
	}

	@Test
	public void getComputerLoginEvent_should_return_event_from_repository_cached_event_not_relevant() {
		ComputerLoginEvent cached = createComputerLoginEvent("192.168.1.1", "pick-me", now + 100);
		when(cache.get("192.168.1.1")).thenReturn(cached);

		ComputerLoginEvent saved = createComputerLoginEvent("192.168.1.1", "pick-me", now + 60250);
		when(computerLoginEventRepository.findByIpaddressAndTimestampepochBetween(anyString(), any(Long.class), any(Long.class),any(Pageable.class))).thenReturn(Arrays.asList(saved));

		// act
		ComputerLoginEvent actual = computerLoginResolver.getComputerLoginEvent("192.168.1.1", now + 60250);

		// assert
		assertEquals(saved, actual);
	}


	@Test
	public void addToBlackList_should_do_nothing_since_should_not_use_blacklist(){
		ComputerLoginEvent cached = createComputerLoginEvent("192.168.1.1", "pick-me", now + 100);
		computerLoginResolver.setShouldUseBlackList(false);
		computerLoginResolver.addToBlackList(cached.getIpaddress(),cached.getTimestampepoch() - 100, cached.getTimestampepoch() + 2 );
		verify(ipBlackListCache,never()).put(anyString(),any(Range.class));
	}

	@Test
	public void addToBlackList_should_add_ip_to_blacklist_with_empty_range(){

		computerLoginResolver.addToBlackList("192.168.1.1",456l,789l);
		verify(ipBlackListCache, times(1)).put(anyString(), eq(Range.between(456l, Long.MAX_VALUE)));
	}

	@Test
	public void addToBlackList_should_add_ip_to_blacklist_with_only_start_timestamp(){
		ComputerLoginEvent cached = createComputerLoginEvent("192.168.1.1", "pick-me", now + 100);
		computerLoginResolver.addToBlackList(cached.getIpaddress(),cached.getTimestampepoch() - 100, cached.getTimestampepoch() + 210);
		verify(ipBlackListCache, times(1)).put(eq(cached.getIpaddress()),eq(Range.between(cached.getTimestampepoch() - 100,Long.MAX_VALUE)));
	}

	@Test
	public void addToBlackList_should_add_ip_to_blacklist_with_only_end_timestamp(){
		ComputerLoginEvent saved = createComputerLoginEvent("192.168.1.1", "pick-me", now + 210);
		when(computerLoginEventRepository.findByIpaddressAndTimestampepochGreaterThanEqual(anyString(), any(Long.class), any(Pageable.class))).thenReturn(Arrays.asList(saved));
		computerLoginResolver.addToBlackList(saved.getIpaddress(), 456l,789l);
		verify(ipBlackListCache, times(1)).put(eq(saved.getIpaddress()),eq(Range.between(456l,now+210)));
	}

	@Test
	public void addToBlackList_should_add_ip_to_blacklist_with_both_start_timestamp_end_timestamp(){
		ComputerLoginEvent cached = createComputerLoginEvent("192.168.1.1", "pick-me", now + 100);
		ComputerLoginEvent saved = createComputerLoginEvent("192.168.1.1", "pick-me", now + 210);
		when(computerLoginEventRepository.findByIpaddressAndTimestampepochGreaterThanEqual(anyString(), any(Long.class), any(Pageable.class))).thenReturn(Arrays.asList(saved));
		computerLoginResolver.addToBlackList(cached.getIpaddress(), cached.getTimestampepoch() - 100, cached.getTimestampepoch() + 210);
		verify(ipBlackListCache, times(1)).put(eq(saved.getIpaddress()),eq(Range.between(cached.getTimestampepoch() - 100,now+210)));
	}

	@Test
	public void addToBlackList_should_not_add_ip_to_blacklist_when_exists_range_is_newer(){
		ComputerLoginEvent cached = createComputerLoginEvent("192.168.1.1", "pick-me", now + 100);
		ComputerLoginEvent saved = createComputerLoginEvent("192.168.1.1", "pick-me", now + 210);
		when(computerLoginEventRepository.findByIpaddressAndTimestampepochGreaterThanEqual(anyString(), any(Long.class), any(Pageable.class))).thenReturn(Arrays.asList(saved));
		when(ipBlackListCache.containsKey(cached.getIpaddress())).thenReturn(true);
		when(ipBlackListCache.get(cached.getIpaddress())).thenReturn(Range.between(now+600,Long.MAX_VALUE));
		computerLoginResolver.addToBlackList(cached.getIpaddress(), cached.getTimestampepoch() - 100, cached.getTimestampepoch() + 210);
		verify(ipBlackListCache, never()).put(eq(saved.getIpaddress()),any(Range.class));
	}


	@Test
	public void removeFromBlackList_should_do_nothing_since_should_not_use_blacklist(){
		ComputerLoginEvent cached = createComputerLoginEvent("192.168.1.1", "pick-me", now + 100);
		computerLoginResolver.setShouldUseBlackList(false);
		computerLoginResolver.removeFromBlackList(cached);
		verify(ipBlackListCache,never()).remove(anyString());
		verify(ipBlackListCache,never()).put(eq(cached.getIpaddress()),any(Range.class));
	}

	@Test
	public void removeFromBlackList_should_remove_from_blacklist_when_new_time_range_intersect_with_the_old_one(){
		ComputerLoginEvent newEvent = createComputerLoginEvent("192.168.1.1", "pick-me", now + 100);
		when(ipBlackListCache.containsKey(newEvent.getIpaddress())).thenReturn(true);
		when(ipBlackListCache.get(newEvent.getIpaddress())).thenReturn(Range.between(now + 150, now + 270));
		computerLoginResolver.removeFromBlackList(newEvent);
		verify(ipBlackListCache, times(1)).remove(newEvent.getIpaddress());
	}

	@Test
	public void removeFromBlackList_should_update_blacklist_when_new_time_range_limits_the_old_one(){
		ComputerLoginEvent newEvent = createComputerLoginEvent("192.168.1.1", "pick-me", now + 100);
		when(ipBlackListCache.containsKey(newEvent.getIpaddress())).thenReturn(true);
		when(ipBlackListCache.get(newEvent.getIpaddress())).thenReturn(Range.between(now+90,now+270));
		computerLoginResolver.removeFromBlackList(newEvent);
		verify(ipBlackListCache,times(1)).put(eq(newEvent.getIpaddress()), eq(Range.between(now + 90, now + 100)));
	}
}