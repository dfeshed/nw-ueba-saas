package fortscale.services.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fortscale.services.cache.CacheHandler;
import org.junit.Test;
import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import fortscale.domain.ad.AdComputer;
import fortscale.domain.core.Computer;
import fortscale.domain.core.ComputerUsageClassifier;
import fortscale.domain.core.ComputerUsageType;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.services.computer.EndpointDetectionService;

public class ComputerServiceTest {

	@Mock
	private ComputerRepository repository;
	@Mock
	private EndpointDetectionService endpointDetectionService;
	@InjectMocks
	private ComputerServiceImpl service;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		CacheHandler<String, Computer> cache = new CacheHandler<String, Computer>(Computer.class) {

			Map<String,Computer> simpleCacheImpl = new HashMap<>();

			@Override public Computer get(String key) {
				return simpleCacheImpl.get(key);
			}

			@Override public void put(String key, Computer value) {
				simpleCacheImpl.put(key,value);
			}

			@Override public void remove(String key) {
				simpleCacheImpl.remove(key);
			}

			@Override public void close() throws IOException {
				simpleCacheImpl = null;
			}
		};
		service.setCache(cache);
	}
	
	private AdComputer getAdComputer() {
		AdComputer computer = new AdComputer();
		computer.setCn("my-pc");
		computer.setDistinguishedName("CN=my-pc,DC=FORTSCALE,DC=dom");
		computer.setOperatingSystem("Windows 8.1 Enterprise N");
		computer.setOperatingSystemServicePack(null);
		computer.setOperatingSystemVersion("6.3 (9600)");
		computer.setWhenChanged("2014/03/30T20:40:40");
		computer.setWhenCreated("2014/03/30T20:40:40");
		
		return computer;
	}
	
	private Computer getComputer() {
		Computer computer = new Computer();
		computer.setDistinguishedName("CN=my-pc,DC=FORTSCALE,DC=dom");
		computer.setOperatingSystem("Windows 8.1 Enterprise N");
		computer.setOperatingSystemServicePack(null);
		computer.setOperatingSystemVersion("6.3 (8600)");
		computer.putUsageClassifier(new ComputerUsageClassifier("login", ComputerUsageType.Desktop));
		
		return computer;
	}
	
	@Test
	public void isHostnameInAD_should_return_false_in_case_of_null_hostname() {
		boolean actual = service.isHostnameInAD(null);
		assertFalse(actual);
	}
	
	@Test
	public void isHostnameInAD_should_return_false_in_case_of_empty_hostname() {
		boolean actual = service.isHostnameInAD("");
		assertFalse(actual);
	}
	
	@Test
	public void isHostnameInAD_should_return_true_in_case_of_hostname_that_is_in_the_computer_repository() {
		when(repository.findByName("MY-PC")).thenReturn(getComputer());
		
		boolean actual = service.isHostnameInAD("MY-PC");
		assertTrue(actual);
	}
	
	@Test
	public void isHostnameInAD_should_match_computer_name_without_case_sensitivity() {
		when(repository.findByName("MY-PC")).thenReturn(getComputer());
		
		boolean actual = service.isHostnameInAD("My-pC");
		assertTrue(actual);
	}
	
	
	@Test
	public void update_should_lookup_mongo_if_the_latest_changed_is_same_as_the_given_computer() {
		// arrange
		when(repository.getLatestWhenChanged()).thenReturn(new Date(1396201240000L));
		when(repository.findByName("my-pc")).thenReturn(null);
		when(repository.save(any(Computer.class))).thenReturn(null);
		
		// act
		AdComputer computer = getAdComputer();
		service.updateComputerWithADInfo(computer);
		
		// assert
		verify(repository, times(1)).save(any(Computer.class));
	}
	
	@Test
	public void update_should_not_clean_classifier_data_in_mongo() {
		// arrange
		when(repository.getLatestWhenChanged()).thenReturn(new Date(1396201240000L));
		when(repository.findByName("MY-PC")).thenReturn(getComputer());
		when(repository.save(any(Computer.class))).thenReturn(null);
		
		// act
		AdComputer computer = getAdComputer();
		service.updateComputerWithADInfo(computer);
		
		// assert
		verify(repository, times(1)).findByName(anyString());
		ArgumentCaptor<Computer> captor = ArgumentCaptor.forClass(Computer.class);
		verify(repository, times(1)).save(captor.capture());
		assertNotNull(captor.getValue().getUsageClassifier("login"));
		assertEquals("6.3 (9600)", captor.getValue().getOperatingSystemVersion());
	}
	
	@Test
	public void getClusterGroupNameForHostname_should_replace_string_according_to_regex() {
		service.setClusterGroupsRegexProperty("(?i)FS-DC-\\d\\d# # #FS-DC");
		
		String actual = service.getClusterGroupNameForHostname("fs-dc-01.fortscale.com");
		
		assertEquals("FS-DC", actual);
	}
	
}
