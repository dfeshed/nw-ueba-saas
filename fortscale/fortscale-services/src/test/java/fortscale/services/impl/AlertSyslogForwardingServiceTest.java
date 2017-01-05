package fortscale.services.impl;

import fortscale.domain.core.*;
import fortscale.services.AlertsService;
import fortscale.services.LocalizationService;
import fortscale.services.UserService;
import org.junit.Before;
import org.junit.Ignore;
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
	@Mock
	private LocalizationService localizationService;

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
		alerts.add(new Alert("anomalous_admin_activity_normalized_username_daily", 1454641200000l, 1454644799000l, EntityType.User,
				user.getUsername(), evidences, 1, 90, Severity.Critical, AlertStatus.Open, AlertFeedback.None, user.getId(), null,0.0,true));

		when(userService.findByUsername(anyString())).thenReturn(user);

		String ipValue = "192.168.0.28";
		String portValue = "514";
		String forwardingTypeValue = "ALERT";
		String alertSeverityValue = "Critical,High,Medium";
		String userTypesValue = "executive,service,admin";
		String enabledValue = "true";

		Optional<String> ip = Optional.of(ipValue);
		when(applicationConfigurationService.getApplicationConfigurationAsString(AlertSyslogForwardingServiceImpl.IP_KEY)).thenReturn(ip);
		Optional<String> port = Optional.of(portValue);
		when(applicationConfigurationService.getApplicationConfigurationAsString(AlertSyslogForwardingServiceImpl.PORT_KEY)).thenReturn(port);
		Optional<String> forwardingType = Optional.of(forwardingTypeValue);
		when(applicationConfigurationService.getApplicationConfigurationAsString(AlertSyslogForwardingServiceImpl.FORWARDING_TYPE_KEY)).thenReturn(forwardingType);
		Optional<String> alertSeverity = Optional.of(alertSeverityValue);
		when(applicationConfigurationService.getApplicationConfigurationAsString(AlertSyslogForwardingServiceImpl.ALERT_SEVERITY_KEY)).thenReturn(alertSeverity);
		Optional<String> userTypes = Optional.of(userTypesValue);
		when(applicationConfigurationService.getApplicationConfigurationAsString(AlertSyslogForwardingServiceImpl.USER_TYPES_KEY)).thenReturn(userTypes);
		Optional<String> enabled = Optional.of(enabledValue);
		when(applicationConfigurationService.getApplicationConfigurationAsString(AlertSyslogForwardingServiceImpl.ALERT_FORWARDING_KEY)).thenReturn(enabled);

		alertSyslogForwardingService.afterPropertiesSet();

		Map<String, String> applicationConfigurationMap = new HashMap<>();
		applicationConfigurationMap.putIfAbsent(AlertSyslogForwardingServiceImpl.IP_KEY, ipValue);
		applicationConfigurationMap.putIfAbsent(AlertSyslogForwardingServiceImpl.PORT_KEY, portValue);
		applicationConfigurationMap.putIfAbsent(AlertSyslogForwardingServiceImpl.FORWARDING_TYPE_KEY, forwardingTypeValue);
		applicationConfigurationMap.putIfAbsent(AlertSyslogForwardingServiceImpl.ALERT_SEVERITY_KEY, alertSeverityValue);
		applicationConfigurationMap.putIfAbsent(AlertSyslogForwardingServiceImpl.USER_TYPES_KEY, userTypesValue);
		applicationConfigurationMap.putIfAbsent(AlertSyslogForwardingServiceImpl.ALERT_FORWARDING_KEY, enabledValue);
		when (applicationConfigurationService.getApplicationConfigurationByNamespace(AlertSyslogForwardingServiceImpl.CONFIGURATION_NAMESPACE)).thenReturn(applicationConfigurationMap);

		when(localizationService.getAlertName(alerts.get(0))).thenReturn("Anomalous Admin Activity");
		when(localizationService.getIndicatorName(alerts.get(0).getEvidences().get(0))).thenReturn("Failure Code Anomaly");
	}


	@Test
	@Ignore
	public void testNewAlert() throws Exception {
//		alertSyslogForwardingService.forwardNewAlert(alerts.get(0));
	}

}