package fortscale.services.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.junit.Test;
import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import fortscale.domain.ad.AdComputer;
import fortscale.domain.core.Computer;
import fortscale.domain.core.ComputerUsageClassifier;
import fortscale.domain.core.ComputerUsageType;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.computer.EndpointDetectionService;

public class UserServiceAccountServiceTest {

	@Mock
	private UserRepository repository;
	@Mock
	private UsernameNormalizer userNormalizer;
	@InjectMocks
	private UserServiceAccountServiceImpl service;
	
	//@Captor
	//private ArgumentCaptor<User> captorUser = ArgumentCaptor.forClass(User.class);
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	private String getFile(String fileData) throws IOException {
 	   File temp = File.createTempFile("UserServiceAccountServiceTest", ".file");
       BufferedWriter output = new BufferedWriter(new FileWriter(temp));
	   output.write(fileData);
	   output.close();
 	   return temp.getAbsolutePath();
	}
	
	private User getNewUser(String user) {
		User u = new User();
		u.setUsername(user);
		return u;
	}
	private List<User> getUsersList(String userList) {
		List<User> users = new ArrayList<User>();
		for (String user : userList.split(",")) {
			users.add(getNewUser(user));	
		}
		return users;
	}
	
	
	
	@Test
	public void first_run_mongo_is_empty() {
		// arrange
		when(repository.findByUserServiceAccount(true)).thenReturn( new ArrayList<User>());
		when(userNormalizer.normalize("user2")).thenReturn("user2");
		try {
			service.filePath = getFile("user1\nuser2\nuser3\nuser4");
			service.deletionSymbol = "-";
			service.afterPropertiesSet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		service.isUserServiceAccount("user2");
		
		ArgumentCaptor<String> captorUser = ArgumentCaptor.forClass(String.class);
		verify(repository,times(4)).findByUsername(captorUser.capture());		
		assertEquals(true, captorUser.getAllValues().contains("user2"));

	}
	
	@Test
	public void mongo_has_some_data_add_new_user() {
		// arrange
		when(repository.findByUserServiceAccount(true)).thenReturn(getUsersList("user1,user2,user3,user4"));
		when(userNormalizer.normalize("user5")).thenReturn("user5");
		try {
			service.filePath = getFile("user5");
			service.deletionSymbol = "-";
			service.afterPropertiesSet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		service.isUserServiceAccount("user5");
		
		ArgumentCaptor<String> captorUser = ArgumentCaptor.forClass(String.class);
		verify(repository).findByUsername(captorUser.capture());		
		assertEquals("user5", captorUser.getValue());
	}
	
	@Test
	public void mongo_has_some_data_remove_user() {
		// arrange
		when(repository.findByUsername(anyString())).thenReturn( new User());
		when(repository.findByUserServiceAccount(true)).thenReturn(getUsersList("user1,user2,user3,user4"));
		when(userNormalizer.normalize("user3")).thenReturn("user3");
		try {
			service.filePath = getFile("-user3");
			service.deletionSymbol = "-";
			service.afterPropertiesSet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		service.isUserServiceAccount("user3");
		
		ArgumentCaptor<User> captorUser = ArgumentCaptor.forClass(User.class);
		ArgumentCaptor<Boolean> captorBool = ArgumentCaptor.forClass(Boolean.class);
		verify(repository,times(1)).updateUserServiceAccount(captorUser.capture(),captorBool.capture());		
		assertEquals(false, captorBool.getValue());
		assertNotNull(captorUser.getValue());
	}	
}
