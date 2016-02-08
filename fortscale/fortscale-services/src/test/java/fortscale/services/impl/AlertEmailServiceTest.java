package fortscale.services.impl;

import fortscale.domain.email.Frequency;
import fortscale.services.AlertEmailService;
import fortscale.services.ApplicationConfigurationService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

/**
 * Created by Amir Keren on 2/8/16.
 */
public class AlertEmailServiceTest {

	private static final String CONFIGURATION_NAMESPACE = "system.email";

	@Mock
	private ApplicationConfigurationService applicationConfigurationService;
	@Mock
	private AlertEmailService alertEmailService;

	@Before
	public void setup() {
		Map<String, String> emailConfig = new HashMap();
		emailConfig.put(EmailServiceImpl.USERNAME_KEY, "ak091283@gmail.com");
		emailConfig.put(EmailServiceImpl.PASSWORD_KEY, "Kman2k16");
		emailConfig.put(EmailServiceImpl.PORT_KEY, "587");
		emailConfig.put(EmailServiceImpl.HOST_KEY, "smtp.gmail.com");
		emailConfig.put(EmailServiceImpl.AUTH_KEY, "tls");
		when(applicationConfigurationService.getApplicationConfigurationByNamespace(EmailServiceImpl.CONFIGURATION_NAMESPACE)).thenReturn(emailConfig);
	}

	@Test
	@Ignore
	public void testEmailSend() throws Exception {
		alertEmailService.sendAlertSummaryEmail(Frequency.Daily);
	}

}