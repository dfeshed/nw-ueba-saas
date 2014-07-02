package fortscale.services.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;

public class HigePrivilegedAccountServiceTest {

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

	private User getNewUser(String user,Boolean isHigePrivilegedAccount, String accountType) {
		User u = getNewUser(user);
		switch(accountType){
		case "administrator":
			u.setAdministratorAccount(isHigePrivilegedAccount);
			break;
		case "executive":
			u.setExecutiveAccount(isHigePrivilegedAccount);
			break;
		}
		
		return u;
	}
	
	private List<User> getUsersList(String userList,String higePrivilegedList, String accountType) {
		List<User> result = new ArrayList<User>();
		String[] users = userList.split(",");
		String[] values = higePrivilegedList.split(",");
		for (int i=0;i<users.length;i++) {
			result.add(getNewUser(users[i],Boolean.parseBoolean(values[i]),accountType));
		}
		return result;
	}
	
	

	
	@Test
	public void no_admin_file() throws Exception {
		// arrange
		when(adminUsers.contains("user2")).thenReturn(false);
		@SuppressWarnings("unchecked")
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
		when(repository.findByUserInGroup(anyListOf(String.class))).thenReturn(new ArrayList<User>());
		@SuppressWarnings("unchecked")
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
		List<User> users1 = getUsersList("user1,user2,user3","true,true,false","administrator");
		when((repository.findByUserInGroup(anyListOf(String.class)))).thenReturn(users1);
		when(adminUsers.contains("user1")).thenReturn(true);
		when(adminUsers.contains("user2")).thenReturn(true);
		when(adminUsers.contains("user3")).thenReturn(true);
		List<User> users2 = getUsersList("user1,user2,user3,user4,user5","true,true,false,false,false","administrator");
		@SuppressWarnings("unchecked")
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
				//assertEquals(true, captorBool.getAllValues().get(i));
			}			
		}
	}
	
	@Test
	public void user_removed_from_group() throws Exception {
		// arrange		
		List<User> users1 = getUsersList("user1,user2","true,true","administrator");
		when((repository.findByUserInGroup(anyListOf(String.class)))).thenReturn(users1);
		when(adminUsers.contains("user1")).thenReturn(true);
		when(adminUsers.contains("user2")).thenReturn(true);
		when(adminUsers.contains("user3")).thenReturn(false);
		List<User> users2 = getUsersList("user1,user2,user3,user4,user5","true,true,true,false,false","administrator");
		
		@SuppressWarnings("unchecked")
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
