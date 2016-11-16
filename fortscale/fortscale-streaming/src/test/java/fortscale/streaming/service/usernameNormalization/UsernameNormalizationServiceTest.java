
package fortscale.streaming.service.usernameNormalization;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class UsernameNormalizationServiceTest {

	UsernameNormalizationService usernameNormalizationService;

	@Before
	public void setUp(){
		usernameNormalizationService = new UsernameNormalizationService();
	}

	@Test public void getUsernameAsNormalizedUsername_should_return_username_in_lowercase() throws Exception {
		String userName = "USER_NAME";
		assertEquals(userName.toLowerCase(), usernameNormalizationService.getUsernameAsNormalizedUsername(userName,
				null, null));
	}

	@Test public void shouldDropRecord_should_return_false_for_not_null_normalizedUsername() throws Exception {

		usernameNormalizationService.setDropOnFail(true);
		assertFalse(usernameNormalizationService.shouldDropRecord("username","entityId"));
	}

	@Test public void shouldDropRecord_should_return_false_for_not_null_normalizedUsername_even_for_null_usernmae() throws Exception {
		usernameNormalizationService.setDropOnFail(true);
		assertFalse(usernameNormalizationService.shouldDropRecord(null,"entityId"));
	}

	@Test public void shouldDropRecord_should_return_true_for_null_normalizedUsername() throws Exception {
		usernameNormalizationService.setDropOnFail(true);
		assertTrue(usernameNormalizationService.shouldDropRecord(null, null));
	}

	@Test public void shouldDropRecord_should_return_true_for_null_normalizedUsername_for_not_null_usernmae() throws Exception {
		usernameNormalizationService.setDropOnFail(true);
		assertTrue(usernameNormalizationService.shouldDropRecord("username",null));
	}

	@Test public void shouldDropRecord_should_return_false_when_flag_is_false() throws Exception {
		usernameNormalizationService.setDropOnFail(false);
		assertFalse(usernameNormalizationService.shouldDropRecord(null,null));
	}

}
