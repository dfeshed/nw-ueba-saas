package fortscale.services.impl;

import fortscale.domain.core.*;
import fortscale.services.AlertsService;
import fortscale.services.UserService;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by Tomer Dvir
 */
@RunWith(MockitoJUnitRunner.class)
public class AlertSyslogForwardingServiceTest {


	@InjectMocks
	private AlertSyslogForwardingServiceImpl alertSyslogForwardingService;

	@Mock
	private ApplicationConfigurationServiceImpl applicationConfigurationService;
	@Mock
	private AlertsService alertsService;
	@Mock
	private UserService userService;

	private User user;
	private List<Alert> alerts;

	@Before
	public void setUp() throws Exception {
		ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();

		user = new User();
		user.setUsername("alrusr51@somebigcompany.com");
		user.setDisplayName("Alert User");
		Set<String> tags = new HashSet();
		tags.add("admin");
		user.setTags(tags);
		UserAdInfo adInfo = new UserAdInfo();
		adInfo.setPosition("Manager");
		adInfo.setDepartment("IT");
		user.setAdInfo(adInfo);
		alerts = new ArrayList();
		List<Evidence> evidences = new ArrayList();
		List<String> dataEntitiesIds = new ArrayList();
		dataEntitiesIds.add("kerberos_logins");
		evidences.add(new Evidence(EntityType.User, "normalized_username", user.getUsername(), EvidenceType.
				AnomalySingleEvent, 1454641200000l, 1454641200000l, "failure_code", "0x12", dataEntitiesIds, 99,
				Severity.Critical, 1, EvidenceTimeframe.Hourly));
		alerts.add(new Alert("Suspicious Hourly User Activity", 1454641200000l, 1454644799000l, EntityType.User,
				user.getUsername(), evidences, 1, 90, Severity.Critical, AlertStatus.Open, AlertFeedback.None, user.getId(), null,0.0,true));

		when(userService.findByUsername(anyString())).thenReturn(user);

		Optional<String> ip = Optional.of("192.168.0.28");
		when(applicationConfigurationService.getApplicationConfigurationAsString(AlertSyslogForwardingServiceImpl.IP_KEY)).thenReturn(ip);
		Optional<String> port = Optional.of("514");
		when(applicationConfigurationService.getApplicationConfigurationAsString(AlertSyslogForwardingServiceImpl.PORT_KEY)).thenReturn(port);
		Optional<String> forwardingType = Optional.of("ALERT");
		when(applicationConfigurationService.getApplicationConfigurationAsString(AlertSyslogForwardingServiceImpl.FORWARDING_TYPE_KEY)).thenReturn(forwardingType);
		Optional<String> alertSeverity = Optional.of("Critical,High,Medium");
		when(applicationConfigurationService.getApplicationConfigurationAsString(AlertSyslogForwardingServiceImpl.ALERT_SEVERITY_KEY)).thenReturn(alertSeverity);
		Optional<String> userTypes = Optional.of("executive,service,admin");
		when(applicationConfigurationService.getApplicationConfigurationAsString(AlertSyslogForwardingServiceImpl.USER_TYPES_KEY)).thenReturn(userTypes);
		Optional<String> enabled = Optional.of("true");
		when(applicationConfigurationService.getApplicationConfigurationAsString(AlertSyslogForwardingServiceImpl.ALERT_FORWARDING_KEY)).thenReturn(enabled);

		alertSyslogForwardingService.afterPropertiesSet();
	}


	@Test
	@Ignore
	public void testNewAlert() throws Exception {
		//alertSyslogForwardingService.forwardNewAlert(alerts.get(0));
	}

}