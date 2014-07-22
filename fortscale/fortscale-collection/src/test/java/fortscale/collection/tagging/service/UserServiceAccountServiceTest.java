package fortscale.collection.tagging.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import fortscale.collection.tagging.service.impl.UserServiceAccountServiceImpl;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.impl.UsernameNormalizer;

public class UserServiceAccountServiceTest {

	@Mock
	private UserRepository repository;
	@Mock
	private UsernameNormalizer userNormalizer;
	@InjectMocks
	private UserServiceAccountServiceImpl service;
	
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
	public void first_run_mongo_is_empty() throws Exception {
		// arrange
		when(repository.findByUserServiceAccount(true)).thenReturn( new ArrayList<User>());
		when(userNormalizer.normalize("user2")).thenReturn("user2");
		service.setFilePath(getFile("user1\nuser2\nuser3\nuser4"));
		service.setDeletionSymbol("-");
		service.update();
				
		ArgumentCaptor<String> captorUser = ArgumentCaptor.forClass(String.class);
		verify(repository,times(4)).findByUsername(captorUser.capture());		
		assertEquals(true, captorUser.getAllValues().contains("user2"));

	}
	
	@Test
	public void mongo_has_some_data_add_new_user() throws Exception {
		// arrange
		when(repository.findByUserServiceAccount(true)).thenReturn(getUsersList("user1,user2,user3,user4"));
		when(userNormalizer.normalize("user5")).thenReturn("user5");
		
		service.setFilePath(getFile("user5"));
		service.setDeletionSymbol("-");
		service.update();
			
		ArgumentCaptor<String> captorUser = ArgumentCaptor.forClass(String.class);
		verify(repository).findByUsername(captorUser.capture());		
		assertEquals("user5", captorUser.getValue());
	}
	
	@Test
	public void mongo_has_some_data_remove_user() throws Exception {
		// arrange
		when(repository.findByUsername(anyString())).thenReturn( new User());
		when(repository.findByUserServiceAccount(true)).thenReturn(getUsersList("user1,user2,user3,user4"));
		when(userNormalizer.normalize("user3")).thenReturn("user3");

		service.setFilePath(getFile("-user3"));
		service.setDeletionSymbol("-");
		service.update();
			
		ArgumentCaptor<User> captorUser = ArgumentCaptor.forClass(User.class);
		ArgumentCaptor<Boolean> captorBool = ArgumentCaptor.forClass(Boolean.class);
		verify(repository,times(1)).updateUserServiceAccount(captorUser.capture(),captorBool.capture());		
		assertEquals(false, captorBool.getValue());
		assertNotNull(captorUser.getValue());
	}	
}
