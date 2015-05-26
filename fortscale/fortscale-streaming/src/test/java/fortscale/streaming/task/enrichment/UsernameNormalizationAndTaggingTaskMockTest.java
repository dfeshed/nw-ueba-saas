package fortscale.streaming.task.enrichment;

import org.junit.Before;
import org.junit.Test;
import fortscale.streaming.service.usernameNormalization.UsernameNormalizationService;
import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UsernameNormalizationAndTaggingTaskMockTest {

	private UsernameNormalizationService usernameNormalizationService;

	@Before
	public void setUp() throws Exception {
		usernameNormalizationService = mock(UsernameNormalizationService.class);
		when(usernameNormalizationService.normalizeUsername("user","domain.com")).thenReturn("user@domain.com");
		when(usernameNormalizationService.normalizeUsername("user@domain.com","domain.com")).thenReturn("user@domain"
				+ ".com");
		when(usernameNormalizationService.normalizeUsername("user","vpnConnect")).thenReturn("user@vpnConnect");
		when(usernameNormalizationService.normalizeUsername("user","pc-destmachine")).thenReturn("user@pc-destmachine");
		when(usernameNormalizationService.normalizeUsername("user","pc-sourcemachine")).thenReturn("user@domain.com");
	}

	@Test
	public void testKerberosLikeEventsUserNormalization1() throws Exception {
		String actual = usernameNormalizationService.normalizeUsername("user", "domain.com");
		String expected = "user@domain.com";
		assertEquals(expected, actual);
	}

	@Test
	public void testKerberosLikeEventsUserNormalization2() throws Exception {
		String actual = usernameNormalizationService.normalizeUsername("user@domain.com", "domain.com");
		String expected = "user@domain.com";
		assertEquals(expected, actual);
	}

	@Test
	public void testVPNLikeEventsUserNormalization() throws Exception {
		String actual = usernameNormalizationService.normalizeUsername("user", "vpnConnect");
		String expected = "user@vpnConnect";
		assertEquals(expected, actual);
	}

	@Test
	public void testSSHLikeEventsUserNormalization1() throws Exception {
		String actual = usernameNormalizationService.normalizeUsername("user", "pc-destmachine");
		String expected = "user@pc-destmachine";
		assertEquals(expected, actual);
	}

	@Test
	public void testSSHLikeEventsUserNormalization2() throws Exception {
		String actual = usernameNormalizationService.normalizeUsername("user", "pc-sourcemachine");
		String expected = "user@domain.com";
		assertEquals(expected, actual);
	}

}
