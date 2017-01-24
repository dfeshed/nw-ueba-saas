package fortscale.services.impl;

import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.UserMachine;
import fortscale.domain.ad.dao.AdGroupRepository;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.domain.ad.dao.UserMachineDAO;
import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.Computer;
import fortscale.domain.core.User;
import fortscale.domain.core.UserAdInfo;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.dao.EventScoreDAO;
import fortscale.services.UserApplication;
import fortscale.utils.actdir.ADParser;
import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(JUnitParamsRunner.class)
public class UserServiceTest {
	@Mock
	private MongoOperations mongoTemplate;

	@Mock
	private AdUserRepository adUserRepository;

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
	private ADParser adUserParser;

	@Mock
	private UsernameService usernameService;

	@InjectMocks
	private UserServiceImpl userService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void createNewApplicationUserDetailsTest(){
		User user = new NewUser("test");
		UserApplication userApplication = UserApplication.active_directory;
		String username = "usernameTest";
		when(userRepository.save(user)).thenReturn(user);
		boolean isNewVal = userService.createNewApplicationUserDetails(user, userApplication.getId(), username, true);
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
		boolean isNewVal = userService.createNewApplicationUserDetails(user, userApplication.getId(), "differntName", true);
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
		when(comp1.getUsageClassifiers()).thenReturn(null);

		Computer comp2 = spy(new Computer());
		comp2.setName("HOSTNAME2");
		comp2.setOperatingSystem("LINUX");
		when(comp2.getIsSensitive()).thenReturn(true);
		when(comp2.getUsageClassifiers()).thenReturn(null);
		ArrayList<Computer> computersList = new ArrayList<Computer>();
		computersList.add(comp1);
		computersList.add(comp2);

		when(computerRepository.getComputersFromNames(any(List.class))).thenReturn(computersList);
		userService.initMetrics();
		userService.getUserMachines("123");

		assertEquals(machine1.getIsSensitive(), false);
		assertEquals(machine1.getOperatingSystem(), "WIN");
		assertEquals(machine1.getUsageClassifiers(), null);

		assertEquals(machine2.getIsSensitive(), true);
		assertEquals(machine2.getOperatingSystem(), "LINUX");
		assertEquals(machine2.getUsageClassifiers(), null);
	}

	@Test
	public void updateUserWithADInfoTest() {
		// Arrange
		int numOfUsers = 100;
		int pageSize = 23;

		List<AdUser> listOfAdUsers = new ArrayList<>(numOfUsers);
		userService.setPageSize(pageSize);

		for (int i = 0; i < numOfUsers; i++) {
			AdUser adUser = new AdUser();
			adUser.setDistinguishedName("distinguished" + i);
			adUser.setObjectGUID("user" + i);
			adUser.setsAMAccountName("account" + i);
			adUser.setUserPrincipalName("principal" + i);
			listOfAdUsers.add(adUser);
		}

		when(adUserRepository.count()).thenReturn((long)numOfUsers);
		Long timestampEpoch = new Long(0);
		String runtime = Instant.ofEpochSecond(timestampEpoch).toString();
		int numOfPages = ((numOfUsers - 1) / pageSize) + 1;
		for (int i = 0; i < numOfPages; i++) {
			PageRequest pageRequest = new PageRequest(i, pageSize);
			int first = i * pageSize;
			int last = Math.min((i + 1) * pageSize, numOfUsers);
			List<AdUser> subList = listOfAdUsers.subList(first, last);
			when(adUserRepository.findByRuntime(runtime, pageRequest)).thenReturn(subList);
		}
		when(userRepository.save(any(User.class))).thenReturn(new User());

		userService.setListOfBuiltInADUsers("Administrator,Guest,krbtgt");

		// Act
		userService.updateUserWithADInfo(runtime);

		// Assert
		verify(userRepository, times(numOfUsers)).save(any(User.class));
		verify(usernameService, times(numOfUsers)).updateUsernameInCache(any(User.class));
		verify(mongoTemplate, never()).updateFirst(any(Query.class), any(Update.class), any(Class.class));
	}

	@Test
	public void userNameSyncWithPrincipalNameForFirstTimeCreationTest()
	{
		User newUser = new User();
		AdUser adUser = new AdUser();
		adUser.setUserPrincipalName("principalTest@test.dom");
		adUser.setsAMAccountName("principalTest");
		adUser.setDistinguishedName("principalTestDN");
		adUser.setObjectGUID("12345");

		UserAdInfo userAdInfo = userService.createUserAdInfo(newUser,adUser,new Date(),new HashSet<>(),new HashSet<>(),true);

		assertTrue(newUser.getUsername().equals("principaltest@test.dom") && userAdInfo.getUserPrincipalName().equals("principalTest@test.dom"));


	}

	@Test
	public void userNameKeepPrincipalNameStaticForAnyADChangesTest()
	{
		User existUser = new User();
		existUser.setUsername("principaltest@test.dom");
		AdUser adUser = new AdUser();
		adUser.setUserPrincipalName("principalTest@test.dom2");
		adUser.setsAMAccountName("principalTest");
		adUser.setDistinguishedName("principalTestDN");
		adUser.setObjectGUID("12345");

		UserAdInfo userAdInfo = userService.createUserAdInfo(existUser,adUser,new Date(),new HashSet<>(),new HashSet<>(),false);

		assertTrue(existUser.getUsername().equals("principaltest@test.dom") && userAdInfo.getUserPrincipalName().equals("principalTest@test.dom2"));


	}

	@Test
	public void test_needToBeDeleted_method ()
	{
		userService.setListOfBuiltInADUsers("Administrator,Guest,krbtgt");

		User oldUser = mock(User.class);
		when(oldUser.getUsername()).thenReturn("administrator");
		boolean result  = userService.needToBeDeleted(oldUser);
		assertFalse(result);



	}
}
