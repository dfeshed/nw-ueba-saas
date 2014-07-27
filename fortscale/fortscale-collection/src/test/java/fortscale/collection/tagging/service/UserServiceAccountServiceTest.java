
package fortscale.collection.tagging.service;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import fortscale.collection.tagging.service.impl.UserServiceAccountServiceImpl;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.impl.UsernameNormalizer;

public class UserServiceAccountServiceTest {

	@Mock
	private UserRepository repository;
	@Mock
	private UsernameNormalizer secUsernameNormalizer;
	@InjectMocks
	private UserServiceAccountServiceImpl service;

	@Before
	public void setUp()
		throws Exception {

		MockitoAnnotations.initMocks(this);

		Set<String> users = new HashSet<String>();
		users.add("user1");
		users.add("user2");
		service.setServiceAccounts(users);
		service.setDeletionSymbol("-");
	}

	private String getFile(String fileData)
		throws IOException {

		File temp =
			File.createTempFile("UserServiceAccountServiceTest", ".file");
		BufferedWriter output = new BufferedWriter(new FileWriter(temp));
		output.write(fileData);
		output.close();
		return temp.getAbsolutePath();
	}


	@Test
	public void mongo_has_some_data_add_new_user()
		throws Exception {

		// arrange
		service.setFilePath(getFile("user3\nuser4"));
		when(secUsernameNormalizer.normalize(anyString())).thenReturn(
			"user3", "user4");
		when(repository.findIfUserExists(anyString())).thenReturn(
			true);
		service.setDeletionSymbol("-");
		service.update();

		verify(repository, times(2)).findIfUserExists(anyString());
		assertTrue(service.getServiceAccounts().contains("user3") == true);
		assertTrue(service.getServiceAccounts().contains("user4") == true);
	}

	@Test
	public void isUserTagged_should_return_true() {

		assertTrue(service.isUserTagged("user1") == true);
	}

	@Test
	public void remove_existing_user()
		throws Exception {

		service.setFilePath(getFile("-user1\nuser4"));
		when(secUsernameNormalizer.normalize(anyString())).thenReturn(
			"user4", "user1");
		when(repository.findIfUserExists(anyString())).thenReturn(
			true);
		service.setDeletionSymbol("-");
		service.update();

		verify(repository, times(2)).findIfUserExists(anyString());
		assertTrue(service.getServiceAccounts().contains("user1") == false);
		assertTrue(service.getServiceAccounts().contains("user4") == true);
	}

}
