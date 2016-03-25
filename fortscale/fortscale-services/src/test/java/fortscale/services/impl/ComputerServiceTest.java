package fortscale.services.impl;

import fortscale.domain.ad.AdComputer;
import fortscale.domain.core.Computer;
import fortscale.domain.core.ComputerUsageClassifier;
import fortscale.domain.core.ComputerUsageType;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.services.cache.CacheHandler;
import fortscale.services.computer.EndpointDetectionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ComputerServiceTest {

	@Mock
	private ComputerRepository repository;
	@Mock
	private EndpointDetectionService endpointDetectionService;

	private CacheHandler<String, Computer> cache;

	@InjectMocks
	private ComputerServiceImpl service;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		cache = spy(new CacheHandler<String, Computer>(Computer.class) {

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

			@Override public void clear() {
				simpleCacheImpl.clear();
			}

			@Override public void close() throws IOException {
				simpleCacheImpl = null;
			}
		});
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
	public void getComputerUsageType_should_return_computer_usage_type_when_computer_in_cache() {
		when(cache.get("MY-PC")).thenReturn(getComputer());
		ComputerUsageType computerUsageType = service.getComputerUsageType("MY-PC");
		assertEquals(ComputerUsageType.Desktop, computerUsageType);
	}

	@Test
	public void getComputerUsageType_should_return_computer_usage_type_when_computer_not_in_cache() {
		Computer computer= getComputer();
		computer.setName("MY-PC");
		when(cache.get("MY-PC")).thenReturn(null);
		when(repository.findByName("MY-PC")).thenReturn(computer);
		ComputerUsageType computerUsageType = service.getComputerUsageType("MY-PC");
		assertEquals(ComputerUsageType.Desktop, computerUsageType);
		verify(cache).put("MY-PC", computer);
	}

	@Test
	public void ensureComputerExists_should_add_new_computer_when_computer_doesnt_exists() {
		when(cache.get("MY-PC")).thenReturn(null);
		when(repository.findByName("MY-PC")).thenReturn(null);
		service.ensureComputerExists("MY-PC");
		verify(endpointDetectionService,times(1)).classifyNewComputer(any(Computer.class));
		verify(repository,times(1)).save(any(Computer.class));
		verify(cache,times(1)).put(eq("MY-PC"), any(Computer.class));
	}

	@Test
	public void ensureComputerExists_should_do_nothing_when_computer_exists_in_cache() {
		when(cache.get("MY-PC")).thenReturn(new Computer());
		service.ensureComputerExists("MY-PC");
		verify(endpointDetectionService,never()).classifyNewComputer(any(Computer.class));
		verify(repository,never()).save(any(Computer.class));
		verify(cache,never()).put(eq("MY-PC"), any(Computer.class));
	}

	@Test
	public void ensureComputerExists_should_add_computer_to_cache_when_computer_exists_in_mongo() {
		when(cache.get("MY-PC")).thenReturn(null);
		Computer computer = new Computer();
		computer.setName("MY-PC");
		when(repository.findByName("MY-PC")).thenReturn(computer);
		service.ensureComputerExists("MY-PC");
		verify(endpointDetectionService,never()).classifyNewComputer(any(Computer.class));
		verify(repository,never()).save(any(Computer.class));
		verify(cache,times(1)).put(eq("MY-PC"), any(Computer.class));
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
	public void classifyAllComputers_should_update_cache_and_repository_with_new_classified_computers() {
		List<Computer> computerList =  arrangeClassificationComputerInput();
		arrangeClassificationClassifyValues(computerList);
		service.classifyAllComputers();
		assertAndVerifyClassificationOutputs(computerList);
	}

	private List<Computer> arrangeClassificationComputerInput(){

		List<Computer> computerList = new ArrayList<>();
		for (int i = 0 ; i < 3; i++) {
			Computer computer = getComputer();
			computer.setName("MY-PC-" + i);
			computerList.add(computer);
		}
		List<Computer> computerList1 = new ArrayList<>();
		computerList1.add(computerList.get(0));
		computerList1.add(computerList.get(1));
		Page<Computer> computers1 = new PageImpl<>(computerList1);

		List<Computer> computerList2 = new ArrayList<>();
		computerList2.add(computerList.get(2));
		Page<Computer> computers2 = new PageImpl<>(computerList2);

		when(repository.findAll(any(Pageable.class))).thenReturn(computers1).thenReturn(computers2).thenReturn(null);
		return computerList;
	}

	private void arrangeClassificationClassifyValues(List<Computer> computerList){
		final Answer<Boolean> answer0 = new Answer<Boolean>() {
			@Override public Boolean answer(InvocationOnMock invocation) throws Throwable {
				Object[] arguments = invocation.getArguments();

				if (arguments != null &&
						arguments.length > 0 &&
						arguments[0] != null) {
					Computer computer = (Computer) arguments[0];
					computer.putUsageClassifier(new ComputerUsageClassifier("login", ComputerUsageType.Server));
				}
				return true;
			}
		};
		final Answer<Boolean> answer1 = new Answer<Boolean>() {
			@Override public Boolean answer(InvocationOnMock invocation) throws Throwable {
				Object[] arguments = invocation.getArguments();

				if (arguments != null &&
						arguments.length > 0 &&
						arguments[0] != null) {
					Computer computer = (Computer) arguments[0];
					computer.putUsageClassifier(new ComputerUsageClassifier("login", ComputerUsageType.Desktop));
				}
				return false;
			}
		};
		final Answer<Boolean> answer2 = new Answer<Boolean>() {
			@Override public Boolean answer(InvocationOnMock invocation) throws Throwable {
				Object[] arguments = invocation.getArguments();

				if (arguments != null &&
						arguments.length > 0 &&
						arguments[0] != null) {
					Computer computer = (Computer) arguments[0];
					computer.putUsageClassifier(new ComputerUsageClassifier("login", ComputerUsageType.Unknown));
				}
				return true;
			}
		};
		doAnswer(answer0).when(endpointDetectionService).classifyComputer(computerList.get(0));
		doAnswer(answer1).when (endpointDetectionService).classifyComputer(computerList.get(1));
		doAnswer(answer2).when (endpointDetectionService).classifyComputer(computerList.get(2));
	}

	private void assertAndVerifyClassificationOutputs(List<Computer> computerList){
		assertEquals(ComputerUsageType.Server, computerList.get(0).getUsageType());
		assertEquals(ComputerUsageType.Desktop,computerList.get(1).getUsageType());
		assertEquals(ComputerUsageType.Unknown,computerList.get(2).getUsageType());
		verify(repository,times(2)).save(anyCollection());
		verify(cache,times(1)).put("MY-PC-0",computerList.get(0));
		verify(cache,times(1)).put("MY-PC-2",computerList.get(2));
	}
}
