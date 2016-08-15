package fortscale.web.rest;

import fortscale.domain.fetch.LogRepository;
import fortscale.services.LogRepositoryService;
import fortscale.web.beans.request.LogRepositoryRequest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ApiLogRepositoryControllerTest {

    @Mock
    private LogRepositoryService logRepositoryService;

    @InjectMocks
    private ApiLogRepositoryController controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {}

	@Test
	public void testLogRepositoryUpdate() {
		List<LogRepositoryRequest> logRepositoryRequests = new ArrayList<>();
		final String PASSWORD = "password";
		final String HOST = "aaa";
		final String USER = "user@user.com";
		final String ENCRYPTED_PASSWORD = "8bGagpbfO0hLMjKwrIc5SA==";
		final int PORT = 8089;
		LogRepositoryRequest settings = new LogRepositoryRequest();
		settings.setPassword(PASSWORD);
		settings.setHost(HOST);
		settings.setPort(PORT);
		settings.setUser(USER);
		logRepositoryRequests.add(settings);
		controller.updateLogRepository(logRepositoryRequests);
		Class<List<LogRepository>> LogRepositoryListClass = (Class<List<LogRepository>>) Collections.
				<LogRepository>emptyList().getClass();
		ArgumentCaptor<List<LogRepository>> argumentValue = ArgumentCaptor.forClass(LogRepositoryListClass);
		verify(logRepositoryService, times(1)).saveLogRepositoriesInDatabase(argumentValue.capture());
		Assert.assertEquals(1,argumentValue.getValue().size()); //Check that we have 1 connection in string
		LogRepository argumentConnection1 = argumentValue.getValue().get(0);
		Assert.assertEquals(HOST, argumentConnection1.getHost());
		Assert.assertEquals(PORT, argumentConnection1.getPort());
		Assert.assertEquals(USER, argumentConnection1.getUser());
		Assert.assertEquals(ENCRYPTED_PASSWORD, argumentConnection1.getPassword());
	}

	@Test
	public void testLogRepositoryUpdate_encrypt_password() {
		final String PASSWORD = "password";
		final String HOST = "aaa";
		final String USER = "user@domain.com";
		final String ENCRYPTED_PASSWORD = "8bGagpbfO0hLMjKwrIc5SA==";
		final int PORT = 8089;
		LogRepository oldSettings = new LogRepository();
		oldSettings.setPassword(PASSWORD + "1111");
		oldSettings.setUser(USER);
		Mockito.when(logRepositoryService.getLogRepositoriesFromDatabase()).thenReturn(Arrays.asList(oldSettings));
		LogRepositoryRequest settings = new LogRepositoryRequest();
		settings.setPassword(PASSWORD);
		settings.setHost(HOST);
		settings.setPort(PORT);
		settings.setUser(USER);
		settings.setEncryptedPassword(false);
		List<LogRepositoryRequest> logRepositoryRequests = new ArrayList<>();
		logRepositoryRequests.add(settings);
		controller.updateLogRepository(logRepositoryRequests);
		Class<List<LogRepository>> LogRepositoryListClass = (Class<List<LogRepository>>) Collections.
				<LogRepository>emptyList().getClass();
		ArgumentCaptor<List<LogRepository>> argumentValue = ArgumentCaptor.forClass(LogRepositoryListClass);
		verify(logRepositoryService, times(1)).saveLogRepositoriesInDatabase(argumentValue.capture());
		LogRepository argumentConnection1 = argumentValue.getValue().get(0);
		Assert.assertEquals(ENCRYPTED_PASSWORD, argumentConnection1.getPassword());
	}

	@Test
	public void testLogRepositoryUpdate_do_not_encrypt_password() {
		final String HOST = "aaa";
		final String USER = "user@domain.com";
		final String ENCRYPTED_PASSWORD = "ENCRYPTED_PASSWORD";
		final int PORT = 8089;
		LogRepository oldSettings = new LogRepository();
		oldSettings.setPassword(ENCRYPTED_PASSWORD);
		oldSettings.setUser(USER);
		Mockito.when(logRepositoryService.getLogRepositoriesFromDatabase()).thenReturn(Arrays.asList(oldSettings));
		LogRepositoryRequest settings = new LogRepositoryRequest();
		settings.setPassword(ENCRYPTED_PASSWORD);
		settings.setHost(HOST);
		settings.setPort(PORT);
		settings.setUser(USER);
		settings.setEncryptedPassword(true);
		List<LogRepositoryRequest> logRepositoryRequests = new ArrayList<>();
		logRepositoryRequests.add(settings);
		controller.updateLogRepository(logRepositoryRequests);
		Class<List<LogRepository>> LogRepositoryListClass = (Class<List<LogRepository>>) Collections.
				<LogRepository>emptyList().getClass();
		ArgumentCaptor<List<LogRepository>> argumentValue = ArgumentCaptor.forClass(LogRepositoryListClass);
		verify(logRepositoryService, times(1)).saveLogRepositoriesInDatabase(argumentValue.capture());
		LogRepository argumentConnection1 = argumentValue.getValue().get(0);
		Assert.assertEquals(ENCRYPTED_PASSWORD, argumentConnection1.getPassword());
	}

}