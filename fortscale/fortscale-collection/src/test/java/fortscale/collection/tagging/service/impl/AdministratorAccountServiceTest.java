package fortscale.collection.tagging.service.impl;

import fortscale.domain.core.UserTagEnum;
import fortscale.services.UserService;
import fortscale.services.impl.ActiveDirectoryGroupsHelper;
import fortscale.services.impl.AdministratorAccountServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class AdministratorAccountServiceTest {


	@Mock
	private UserService userService;

	@Mock
	private ActiveDirectoryGroupsHelper activeDirectoryGroupsHelper;

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
	
	private Set<String> getUsersSet(String userList) {
		Set<String> result = new HashSet<String>();
		String[] users = userList.split(",");
		for (int i=0;i<users.length;i++) {
			result.add(users[i]);
		}
		return result;
	}

	
	@Test
	public void no_admin_file() throws Exception {
		// arrange
		HashSet<String> taggedUsers = new HashSet<String>();
		taggedUsers.add("user1");
		administratorAccountService.setTaggedUsers(taggedUsers);
		
		administratorAccountService.setFilePath(null);
		administratorAccountService.update();
				
		assertEquals(false, administratorAccountService.isUserAdministrator("user1"));
	}

	@Test
	public void remove_admin_tag_to_user() throws Exception {
		// arrange
		administratorAccountService.setPageSize(Integer.MAX_VALUE);
		Set<String> users1 = getUsersSet("user1,user2,user3");
		when(userService.findNamesInGroup(anyListOf(String.class),any(Pageable.class))).thenReturn(users1);
		Set<String> users2 = getUsersSet("user1,user2,user3,user4,user5");
		when(userService.findNamesByTag(UserTagEnum.admin.getId())).thenReturn(users2);
		administratorAccountService.setFilePath(getFile("group1,group2"));
		administratorAccountService.update();

		ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> captorBool = ArgumentCaptor.forClass(Boolean.class);
		verify(userService,times(2)).updateUserTag(eq(administratorAccountService.getTag().getId()),usernameCaptor.capture(),captorBool.capture());
		for(int i=0;i<usernameCaptor.getAllValues().size();i++) {
			if (usernameCaptor.getAllValues().get(i).equals("user4") || usernameCaptor.getAllValues().get(i).equals("user5")) {
				assertEquals(false, captorBool.getAllValues().get(i));
			}else{
				assertEquals(true, captorBool.getAllValues().get(i));
			}
		}
	}

	@Test
	public void user_removed_from_group() throws Exception {
		// arrange
		administratorAccountService.setPageSize(Integer.MAX_VALUE);
		Set<String> users1 = getUsersSet("user1,user2,user3,user4,user5");
		when(userService.findNamesInGroup(anyListOf(String.class),any(Pageable.class))).thenReturn(users1);
		Set<String> users2 = getUsersSet("user1,user2,user3");
		when(userService.findNamesByTag(UserTagEnum.admin.getId())).thenReturn(users2);
		administratorAccountService.setFilePath(getFile("group1,group2"));
		administratorAccountService.update();

		ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Boolean> captorBool = ArgumentCaptor.forClass(Boolean.class);
		verify(userService,times(2)).updateUserTag(eq(administratorAccountService.getTag().getId()),usernameCaptor.capture(),captorBool.capture());
		for(int i=0;i<usernameCaptor.getAllValues().size();i++) {
			assert(usernameCaptor.getAllValues().get(i).equals("user4") || usernameCaptor.getAllValues().get(i).equals("user5"));
			assertEquals(true, captorBool.getAllValues().get(i));
		}
	}
}
