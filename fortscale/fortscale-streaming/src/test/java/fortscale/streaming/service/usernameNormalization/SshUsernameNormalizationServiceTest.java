package fortscale.streaming.service.usernameNormalization;

import fortscale.services.impl.UsernameNormalizer;
import net.minidev.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SshUsernameNormalizationServiceTest {


	SshUsernameNormalizationService sshUsernameNormalizationService;

	@Before
	public void setUp(){
		sshUsernameNormalizationService = new SshUsernameNormalizationService();
	}


	@Test public void normalizeUsername_should_return_UsernameNormalizer_normalize_output() throws Exception {
		String userName = "USER_NAME";
		String domainName = "DOMAIN_NAME";
		String normalizedUsername = "user_name_nnn";
		UsernameNormalizer usernameNormalizer = mock(UsernameNormalizer.class);
		when(usernameNormalizer.normalize(userName, domainName)).thenReturn(normalizedUsername);
		when(usernameNormalizer.normalize(userName.toLowerCase(), domainName.toLowerCase())).thenReturn(normalizedUsername
				.toUpperCase());
		sshUsernameNormalizationService.setUsernameNormalizer(usernameNormalizer);
		assertEquals(normalizedUsername, sshUsernameNormalizationService.normalizeUsername(userName, domainName));
	}

	@Test public void getUsernameAsNormalizedUsername_should_use_target_machine_name_when_UsernameNormalizer_is_not_null() throws Exception {
		String userName = "USER_NAME";
		String targetMachine = "TARGET_MACHINE";
		UsernameNormalizer usernameNormalizer = mock(UsernameNormalizer.class);
		sshUsernameNormalizationService.setUsernameNormalizer(usernameNormalizer);
		JSONObject message = mock(JSONObject.class);
		when(message.get(anyString())).thenReturn(targetMachine);
		assertEquals(userName + "@" + targetMachine, sshUsernameNormalizationService.getUsernameAsNormalizedUsername(userName,message));
	}

	@Test public void getUsernameAsNormalizedUsername_should_return_lowercase_username_when_UsernameNormalizer_is_null() throws Exception {
		String userName = "USER_NAME";
		String targetMachine = "TARGET_MACHINE";
		JSONObject message = mock(JSONObject.class);
		when(message.get(anyString())).thenReturn(targetMachine);
		assertEquals(userName.toLowerCase(), sshUsernameNormalizationService.getUsernameAsNormalizedUsername(userName,message));
	}



	@Test public void shouldDropRecord_should_return_false_for_not_null_normalizedUsername() throws Exception {

		sshUsernameNormalizationService.setDropOnFail(true);
		assertFalse(sshUsernameNormalizationService.shouldDropRecord("username","normalizedUsername"));
	}

	@Test public void shouldDropRecord_should_return_false_for_not_null_normalizedUsername_even_for_null_usernmae() throws Exception {
		sshUsernameNormalizationService.setDropOnFail(true);
		assertFalse(sshUsernameNormalizationService.shouldDropRecord(null,"normalizedUsername"));
	}

	@Test public void shouldDropRecord_should_return_true_for_null_normalizedUsername() throws Exception {
		sshUsernameNormalizationService.setDropOnFail(true);
		assertTrue(sshUsernameNormalizationService.shouldDropRecord(null, null));
	}

	@Test public void shouldDropRecord_should_return_true_for_null_normalizedUsername_for_not_null_usernmae() throws Exception {
		sshUsernameNormalizationService.setDropOnFail(true);
		assertTrue(sshUsernameNormalizationService.shouldDropRecord("username",null));
	}

	@Test public void shouldDropRecord_should_return_false_when_flag_is_false() throws Exception {
		sshUsernameNormalizationService.setDropOnFail(false);
		assertFalse(sshUsernameNormalizationService.shouldDropRecord(null,null));
	}

}