package fortscale.services.impl;

import fortscale.common.dataentity.DataEntitiesConfig;
import fortscale.common.dataentity.DataEntity;
import fortscale.domain.core.*;
import fortscale.domain.email.Frequency;
import fortscale.services.AlertsService;
import fortscale.services.LocalizationService;
import fortscale.services.UserService;
import fortscale.utils.image.ImageUtils;
import fortscale.utils.jade.JadeUtils;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.*;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * Created by Amir Keren on 2/8/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class AlertEmailServiceTest {

	private static final String RESOURCE_FOLDER = "fortscale/fortscale-core/fortscale/fortscale-collection/target/resources/dynamic-html";

	@InjectMocks
	private AlertEmailServiceImpl alertEmailService;
	@InjectMocks
	private EvidenceEmailPrettifier evidenceEmailPrettifier;

	@Mock
	private ApplicationConfigurationServiceImpl applicationConfigurationService;
	@Mock
	private AlertsService alertsService;
	@Mock
	private UserService userService;
	@Mock
	private DataEntitiesConfig dataEntitiesConfig;
	@Mock
	private LocalizationService localizationService;

	@Spy
	private AlertEmailPrettifier alertPrettifierService = new AlertEmailPrettifier();
	@Spy
	private EmailServiceImpl emailServiceImpl = new EmailServiceImpl();
	@Spy
	private JadeUtils jadeUtils = new JadeUtils();
	@Spy
	private ImageUtils imageUtils = new ImageUtils();

	private User user;
	private List<Alert> alerts;

	@Before
	public void setUp() throws Exception {
		Map<String, String> emailConfig = new HashMap();
		emailConfig.put(EmailServiceImpl.FROM_KEY, "amirk@fortscale.com");
		emailConfig.put(EmailServiceImpl.USERNAME_KEY, "");
		emailConfig.put(EmailServiceImpl.PASSWORD_KEY, "");
		emailConfig.put(EmailServiceImpl.PORT_KEY, "25");
		emailConfig.put(EmailServiceImpl.HOST_KEY, "smtp-relay-not-exists.gmail.com");
		emailConfig.put(EmailServiceImpl.AUTH_KEY, "none");
		String emailGroups = "[{\"users\":[\"amirk@fortscale.com\"],\"summary\":{\"severities\":[\"Critical\",\"High\",\"Medium\",\"Low\"],\"frequencies\":[\"Daily\",\"Weekly\",\"Monthly\"]},\"newAlert\":{\"severities\":[\"Critical\",\"High\",\"Medium\",\"Low\"]}}]";
		ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();
		applicationConfiguration.setValue(emailGroups);
		ApplicationConfiguration languageConfiguration = new ApplicationConfiguration();
		languageConfiguration.setValue("en");
		ApplicationConfiguration messageConfiguration = new ApplicationConfiguration();
		messageConfiguration.setValue("Failure Code");
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
				user.getUsername(), evidences, 1, 90, Severity.Critical, AlertStatus.Open, AlertFeedback.None, "",
				user.getId(), AlertTimeframe.Daily));
		DataEntity dataEntity = new DataEntity();
		dataEntity.setName("Kerberos");
		when(userService.findByUsername(anyString())).thenReturn(user);
		when(userService.getUserThumbnail(any(User.class))).thenReturn("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t7PEts19ZYw+6YP0iNqDrtOWLdwcY1410UDeZ10X3MfiYfVoOxaYr1v5EiBaA6s99gTX/Qbn6I4EwnrnXjr29dS04d1Ak/KS+g/c38Orqcr59xouvSUpdxI5sMf24+ADZgpl+OV7r/k1Z0hEKPjQSuL1V+4f20Iz9YP6lbcK1h9xlRouQykw2lQoguBXwQ1lJ7pvMNu0yPdaQ/e1s/7ET/ioceZg9Baxg367zf6+TVAFY2Gkb6G8mrk5GHLYlp0bQSwqA28zTVslBP5ECLSMmFPuD8olA1hseyLzeCPNF5571w0TXgYF6T3QGafHgt9oU9mhDgSOqPYD4WexwfLU7E0MdlmzzX/IlDV1VwPAH1/c+xuyoRdyyiAYu36CaTEAoP8A0P/aAAwDAQACAAMAAAAQ88888888oZ708888888884Rtd/g0888888886M/+/kU88888888u+xPjP+88888888smGOVo888888Ac8yuasIc8gc884oU43yQshLWK884AUc8nIyBCzjc8gMc80SSLb07G888Mk04NAHsh1sc888ss888KCwNug8888888888ITy8+5888888888sIk0Y3c888888885TPI5C8884Ag404zGW5NU888884MQSCT7HwE88888AM8zBlEUQc88888888GBQVUc888888888YGXf0zq68888888pOYdccc9X8848NP/MRYGS4/wDPPKLBfvr/AHzzzzzzzzwsJ90bzzzzzzzzzzzzzzzzzzzzzzz/xAAoEQEAAgEDAgUFAQEAAAAAAAABABEhMUFRMHEQYZGx0SCBocHh8ED/2gAIAQMBAT8Q6CrscviO0/YQW3O49RrujL8feaqDRoBKc0eUVDYbVf4l7SNVj0+OkFwGA8O3xHIdh+3hQmPmwewTyjeGVpyc9JxdmUrwVc0H7GCEIG8fLk6Gnapzors/qIQoeiR6WGGdUxttMiEZw+/eUzh3t7ovzCLbcri0U9uk4Pg7/wAizTYUu8vY+m8we7oVlvfvK4g841aUw4dPDcvHUAzrD9auYtehp0EW3J23i3TLnscEr964jVtP4gXS1lqYlRjh9YF0CW5jtlMNYax5Sgw1D/cRZQCkMQM1sTf2lymnQNHvF1YSDVo8eCoKsmY1f6p/vSDJSi+vS5ZIKrI25cwUbICW65V7/MbHB6d1+h8RIO4+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");
		when(alertsService.getAlertSummary(anyList(), anyLong())).thenReturn(alerts);
		when(emailServiceImpl.isEmailConfigured()).thenReturn(true);
		when(applicationConfigurationService.getApplicationConfigurationByKey(AlertEmailServiceImpl.CONFIGURATION_KEY)).
				thenReturn(applicationConfiguration);
		when(localizationService.getIndicatorName(any(Evidence.class))).thenReturn("Failure Code Anomaly");
		when(applicationConfigurationService.getApplicationConfigurationByKey("messages.en.evidence.failure_code")).
				thenReturn(messageConfiguration);
		when(dataEntitiesConfig.getLogicalEntity("kerberos_logins")).thenReturn(dataEntity);
		emailServiceImpl.loadEmailConfiguration(emailConfig);
		alertPrettifierService.setEvidenceEmailPrettifier(evidenceEmailPrettifier);
		alertEmailService.setResourcesFolder(RESOURCE_FOLDER);
		alertEmailService.afterPropertiesSet();


		//Return the alert name as the localized name
		alertPrettifierService.setLocalizationService(localizationService);
		Mockito.when(localizationService.getAlertName(Mockito.any(Alert.class))).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return ((Alert) args[0]).getName();
			}
		});

	}

	@Test
	@Ignore
	public void testAlertSummary() throws Exception {
		alertEmailService.sendAlertSummaryEmail(Frequency.Daily);
	}

	@Test
	@Ignore
	public void testNewAlert() throws Exception {
		alertEmailService.sendNewAlertEmail(alerts.get(0));
	}

}