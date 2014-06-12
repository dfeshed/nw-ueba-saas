package fortscale.services.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.junit.Test;
import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import fortscale.domain.ad.AdComputer;
import fortscale.domain.core.Computer;
import fortscale.domain.core.ComputerUsageClassifier;
import fortscale.domain.core.ComputerUsageType;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.computer.EndpointDetectionService;

public class AdministratorAccountServiceTest {

	@Mock
	private UserRepository repository;
	@Mock
	private Set<String> adminUsers;
	@InjectMocks
	private AdministratorAccountServiceImpl administratorAccountService;
	
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

	private User getNewUser(String user,Boolean isAdministratorAccount) {
		User u = getNewUser(user);
		u.setAdministratorAccount(isAdministratorAccount);
		return u;
	}
	
	private List<User> getUsersList(String userList) {
		List<User> users = new ArrayList<User>();
		for (String user : userList.split(",")) {
			users.add(getNewUser(user));	
		}
		return users;
	}
	
	private List<User> getUsersList(String userList,String adminList) {
		List<User> result = new ArrayList<User>();
		String[] users = userList.split(",");
		String[] values = adminList.split(",");
		for (int i=0;i<users.length;i++) {
			result.add(getNewUser(users[i],Boolean.parseBoolean(values[i])));
		}
		return result;
	}

	
	@Test
	public void no_admin_file() throws Exception {
		// arrange
		when(adminUsers.contains("user2")).thenReturn(false);
		Page<User> pages = mock(Page.class);
		when(pages.getContent()).thenReturn(new ArrayList<User>());
		when(repository.findAll(any(Pageable.class))).thenReturn(pages);
		
		administratorAccountService.setFilePath(null);
		administratorAccountService.afterPropertiesSet();
				
		assertEquals(false, administratorAccountService.isUserAdministrator("user2"));

	}
	
	@Test
	public void group_not_in_repository() throws Exception {
		// arrange
		when(repository.findByUserInGroup(anyList())).thenReturn(new ArrayList<User>());
		Page<User> pages = mock(Page.class);
		when(pages.getContent()).thenReturn(new ArrayList<User>());
		when(repository.findAll(any(Pageable.class))).thenReturn(pages);		
		administratorAccountService.setFilePath(getFile("group1,group2"));
		administratorAccountService.afterPropertiesSet();
			
		assertEquals(false, administratorAccountService.isUserAdministrator("user2"));
	}
	
	@Test
	public void add_admin_tag_to_user() throws Exception {
		// arrange		
		List<User> users1 = getUsersList("user1,user2,user3","true,true,false");
		when((repository.findByUserInGroup(anyList()))).thenReturn(users1);
		when(adminUsers.contains("user1")).thenReturn(true);
		when(adminUsers.contains("user2")).thenReturn(true);
		when(adminUsers.contains("user3")).thenReturn(true);
		List<User> users2 = getUsersList("user1,user2,user3,user4,user5","true,true,false,false,false");
		Page<User> pages = mock(Page.class);
		when(pages.getContent()).thenReturn(users2);
		when(repository.findAll(any(Pageable.class))).thenReturn(pages);
		
		administratorAccountService.setFilePath(getFile("group1,group2"));
		administratorAccountService.afterPropertiesSet();
			
		ArgumentCaptor<User> captorUser = ArgumentCaptor.forClass(User.class);
		ArgumentCaptor<Boolean> captorBool = ArgumentCaptor.forClass(Boolean.class);
		verify(repository,times(3)).updateAdministratorAccount(captorUser.capture(),captorBool.capture());		
		for(int i=0;i<captorUser.getAllValues().size();i++) {
			if (captorUser.getAllValues().get(i).getUsername().equals("user3")) {
				assertEquals(true, captorBool.getAllValues().get(i));
			}			
		}
	}
	
	@Test
	public void user_removed_from_group() throws Exception {
		// arrange		
		List<User> users1 = getUsersList("user1,user2","true,true");
		when((repository.findByUserInGroup(anyList()))).thenReturn(users1);
		when(adminUsers.contains("user1")).thenReturn(true);
		when(adminUsers.contains("user2")).thenReturn(true);
		when(adminUsers.contains("user3")).thenReturn(false);
		List<User> users2 = getUsersList("user1,user2,user3,user4,user5","true,true,true,false,false");
		Page<User> pages = mock(Page.class);
		when(pages.getContent()).thenReturn(users2);
		when(repository.findAll(any(Pageable.class))).thenReturn(pages);
		
		administratorAccountService.setFilePath(getFile("group1,group2"));
		administratorAccountService.afterPropertiesSet();
			
		ArgumentCaptor<User> captorUser = ArgumentCaptor.forClass(User.class);
		ArgumentCaptor<Boolean> captorBool = ArgumentCaptor.forClass(Boolean.class);
		verify(repository,times(3)).updateAdministratorAccount(captorUser.capture(),captorBool.capture());		
		for(int i=0;i<captorUser.getAllValues().size();i++) {
			if (captorUser.getAllValues().get(i).getUsername().equals("user3")) {
				assertEquals(false, captorBool.getAllValues().get(i));
			}
			else {
				assertEquals(true, captorBool.getAllValues().get(i));
			}
			
		}
		
		
	}
}
