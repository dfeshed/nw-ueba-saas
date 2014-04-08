package fortscale.services.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Date;

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
	public void update_should_not_lookup_mongo_if_the_latest_changed_is_newer_than_the_given_computer() {
		// arrange
		when(repository.getLatestWhenChanged()).thenReturn(new Date(1397201240000L));
		
		// act
		AdComputer computer = getAdComputer();
		service.updateComputerWithADInfo(computer);
		
		// assert
		verify(repository, times(0)).findByName(anyString());
		verify(repository, times(0)).save(any(Computer.class));
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
		verify(repository, times(1)).findByName(anyString());
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
	
	
	
}
