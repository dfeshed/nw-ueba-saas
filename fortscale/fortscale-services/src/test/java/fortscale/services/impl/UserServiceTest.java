package fortscale.services.impl;


import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import junitparams.JUnitParamsRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoOperations;

import fortscale.domain.ad.UserMachine;
import fortscale.domain.ad.dao.AdGroupRepository;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.domain.ad.dao.AdUserThumbnailRepository;
import fortscale.domain.ad.dao.UserMachineDAO;
import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.Computer;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.dao.EventScoreDAO;
import fortscale.services.UserApplication;
import fortscale.utils.actdir.ADParser;






@RunWith(JUnitParamsRunner.class)
public class UserServiceTest {

	@Mock
	private MongoOperations mongoTemplate;
	
	@Mock
	private AdUserRepository adUserRepository;
	
	@Mock
	private AdUserThumbnailRepository adUserThumbnailRepository;
				
	@Mock
	private AdGroupRepository adGroupRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private ComputerRepository computerRepository;
	
	@Mock
	private UserMachineDAO userMachineDAO;
	
	@Mock
	private EventScoreDAO loginDAO;
				
	@Mock
	private EventScoreDAO sshDAO;
	
	@Mock
	private EventScoreDAO vpnDAO;
	
	@Mock
	private ImpalaWriterFactory impalaWriterFactory;
	
	@Mock
	private ADParser adUserParser; 
		
	@InjectMocks
	private UserServiceImpl	userService;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	
	@Test
	public void createNewApplicationUserDetailsTest(){
		User user = new NewUser("test");
		UserApplication userApplication = UserApplication.active_directory;
		String username = "usernameTest";
		boolean isNewVal = userService.createNewApplicationUserDetails(user, userApplication, username, true);
		assertEquals(true, isNewVal);
		assertEquals(username, user.getApplicationUserDetails(userApplication.getId()).getUserName());
		verify(userRepository, times(1)).save(user);
	}
	
	@Test
	public void createNewApplicationUserDetailsAlreadyExistTest(){
		User user = new User();
		UserApplication userApplication = UserApplication.active_directory;
		String username = "usernameTest";
		ApplicationUserDetails applicationUserDetails = new ApplicationUserDetails(userApplication.getId(), username);
		user.addApplicationUserDetails(applicationUserDetails);
		boolean isNewVal = userService.createNewApplicationUserDetails(user, userApplication, "differntName", true);
		assertEquals(false, isNewVal);
		assertEquals(username, user.getApplicationUserDetails(userApplication.getId()).getUserName());
		verify(userRepository, never()).save((User)any());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getUserMachinesTest(){
		User user = new User();
		user.setUsername("user_test");
		when(userRepository.findOne("123")).thenReturn(user);
		UserMachine machine1 = new UserMachine();
		machine1.setHostname("hostname1");
		UserMachine machine2 = new UserMachine();
		machine2.setHostname("hostname2");
		ArrayList<UserMachine> machinesList = new ArrayList<UserMachine>();
		machinesList.add(machine1);
		machinesList.add(machine2);
		when(userMachineDAO.findByUsername("user_test")).thenReturn(machinesList);

		Computer comp1 = spy(new Computer());
		comp1.setName("HOSTNAME1");
		comp1.setOperatingSystem("WIN");
		when(comp1.getIsSensitive()).thenReturn(false);
		when(comp1.getUsageClassifiersMap()).thenReturn(null);

		Computer comp2 = spy(new Computer());
		comp2.setName("HOSTNAME2");
		comp2.setOperatingSystem("LINUX");
		when(comp2.getIsSensitive()).thenReturn(true);
		when(comp2.getUsageClassifiersMap()).thenReturn(null);		
		ArrayList<Computer> computersList = new ArrayList<Computer>();
		computersList.add(comp1);
		computersList.add(comp2);
		
		when(computerRepository.getComputersFromNames(any(List.class))).thenReturn(computersList);
		
		userService.getUserMachines("123");
		
		assertEquals(machine1.getIsSensitive(), false);
		assertEquals(machine1.getOperatingSystem(), "WIN");
		assertEquals(machine1.getUsageClassifiers(), null);
		

		assertEquals(machine2.getIsSensitive(), true);
		assertEquals(machine2.getOperatingSystem(), "LINUX");
		assertEquals(machine2.getUsageClassifiers(), null);
		
	}
}
