package fortscale.services.impl;


import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import junitparams.JUnitParamsRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoOperations;

import fortscale.domain.ad.dao.AdGroupRepository;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.domain.ad.dao.AdUserThumbnailRepository;
import fortscale.domain.ad.dao.UserMachineDAO;
import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.dao.AuthDAO;
import fortscale.domain.fe.dao.VpnDAO;
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
	private UserMachineDAO userMachineDAO;
	
	@Mock
	private AuthDAO loginDAO;
				
	@Mock
	private AuthDAO sshDAO;
	
	@Mock
	private VpnDAO vpnDAO;
	
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
}
