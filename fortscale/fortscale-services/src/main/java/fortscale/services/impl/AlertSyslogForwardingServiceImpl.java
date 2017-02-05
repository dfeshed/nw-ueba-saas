package fortscale.services.impl;

import com.cloudbees.syslog.MessageFormat;
import fortscale.domain.core.alert.Alert;
import fortscale.domain.core.Severity;
import fortscale.domain.core.User;
import fortscale.domain.dto.DateRange;
import fortscale.services.*;
import fortscale.utils.syslog.SyslogSender;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service("alertSyslogForwardingService") public class AlertSyslogForwardingServiceImpl
		implements AlertSyslogForwardingService, InitializingBean {

	public static final String SPLITTER = ",";
	public static final String CONFIGURATION_NAMESPACE = "system.syslogforwarding";
	public final static String ALERT_FORWARDING_KEY = CONFIGURATION_NAMESPACE + ".enabled";
	public static final String IP_KEY = CONFIGURATION_NAMESPACE + ".ip";
	public static final String PORT_KEY = CONFIGURATION_NAMESPACE + ".port";
	public static final String USER_TYPES_KEY = CONFIGURATION_NAMESPACE + ".usertypes";
	public static final String ALERT_SEVERITY_KEY = CONFIGURATION_NAMESPACE + ".alertseverity";
	public static final String FORWARDING_TYPE_KEY = CONFIGURATION_NAMESPACE + ".forwardingtype";
	public static final String MESSAGE_FORMAT = CONFIGURATION_NAMESPACE + ".messageformat";
	private static final MessageFormat DEFAULT_MESSAGE_FORMAT = MessageFormat.RFC_3164;

	@Autowired private AlertsService alertsService;
	@Autowired private ApplicationConfigurationService applicationConfigurationService;
	@Autowired private UserService userService;
	@Autowired private LocalizationService localizationService;

	private List<String> alertSeverity;
	private String[] userTags;
	private String baseUrl;
	private SyslogSender syslogSender;
	private ForwardingType forwardingType;

	@Override public void afterPropertiesSet() throws Exception {
		loadConfiguration();
	}

	@Override public boolean forwardNewAlert(Alert alert) {
		try {
			loadConfiguration();
		} catch (Exception e) {
			return false;
		}

		if (syslogSender != null && !filterAlert(alert)) {
			String rawAlert = generateAlert(alert, forwardingType);
			return syslogSender.sendEvent(rawAlert);
		}

		return false;
	}

	@Override public int forwardAlertsByTimeRange(
			String ip, int port, String forwardingType, String[] userTags, String[] alertSeverity,
			long startTime, long endTime, MessageFormat syslogMessageFormat) throws RuntimeException {

		List<Alert> alerts = alertsService.getAlertsByTimeRange(
				new DateRange(startTime, endTime), Arrays.asList(alertSeverity));
		SyslogSender sender = new SyslogSender(ip, port, "tcp", syslogMessageFormat);
		ForwardingType forwardingTypeEnum = ForwardingType.valueOf(forwardingType);
		int counter = 0;

		for (Alert alert : alerts) {
			if (!filterByUserType(alert.getEntityName(), userTags)) {
				String rawAlert = generateAlert(alert, forwardingTypeEnum);
				if (sender.sendEvent(rawAlert)) {
					counter++;
				} else {
					throw new RuntimeException("Possibly unreachable destination");
				}
			}
		}

		return counter;
	}

	private String generateAlert(Alert alert, ForwardingType forwardingType) {
		prettifyAlert(alert);
		String rawAlert = "Alert URL: " + generateAlertPath(alert) + " ";
		switch (forwardingType) {
		case ALERT:
			rawAlert += alert.toString(false);
			return rawAlert;
		case ALERT_AND_INDICATORS:
			rawAlert += alert.toString(true);
			return rawAlert;
		default:
			return "";
		}
	}

	private void loadConfiguration() throws ConfigurationException, UnknownHostException {
		@SuppressWarnings("unchecked")
		Map<String, String> applicationConfiguration = applicationConfigurationService
				.getApplicationConfigurationByNamespace(CONFIGURATION_NAMESPACE);

		String isEnabled = applicationConfiguration.get(ALERT_FORWARDING_KEY);
		if (isEnabled == null || isEnabled.equals("false")) {
			return;
		}

		try {
			String ip = applicationConfiguration.get(IP_KEY);
			int port = Integer.valueOf(applicationConfiguration.get(PORT_KEY));
			String userTagsValue = applicationConfiguration.get(USER_TYPES_KEY);
			if (StringUtils.isBlank(userTagsValue)) {
				userTags = new String[] {};
			} else {
				userTags = userTagsValue.split(SPLITTER);
			}
			String alertSeverityValue = applicationConfiguration.get(ALERT_SEVERITY_KEY);
			if (StringUtils.isBlank(alertSeverityValue)) {
				alertSeverity = Collections.emptyList();
			} else {
				alertSeverity = Arrays.asList(alertSeverityValue.split(SPLITTER));
			}
			forwardingType = ForwardingType.valueOf(applicationConfiguration.get(FORWARDING_TYPE_KEY));

			// Getting the message format
			String messageFormatString = applicationConfiguration.get(MESSAGE_FORMAT);
			MessageFormat messageFormat;
			if (StringUtils.isEmpty(messageFormatString)) {
				messageFormat = DEFAULT_MESSAGE_FORMAT;
			} else {
				messageFormat = MessageFormat.valueOf(messageFormatString);
			}

			syslogSender = new SyslogSender(ip, port, "tcp", messageFormat);
			baseUrl = "https://" + InetAddress.getLocalHost().getHostName() +
					":8443/fortscale-webapp/index.html#/user/";
		} catch (Exception e) {
			throw new ConfigurationException("Error creating syslog forwarder - Configuration error. Error: " + e);
		}
	}

	private boolean filterAlert(Alert alert) {
		return filterBySeverity(alert.getSeverity()) || filterByUserType(alert.getEntityName(), userTags);
	}

	private boolean filterBySeverity(Severity severity) {
		// If alert severity is not present, do not filter
		if (alertSeverity.isEmpty()) {
			return false;
		}

		// If alert severity contains the severity name, do not filter
		return !alertSeverity.contains(severity.name());
	}

	private boolean filterByUserType(String entityName, String[] tags) {
		// If tags is not present, do not filter
		if (tags.length == 0) {
			return false;
		}

		// If user has one of the tags, do not filter
		User user = userService.findByUsername(entityName);

		for (String tag : tags) {
			if (user.hasTag(tag)) {
				return false;
			}
		}

		return true;
	}

	private String generateAlertPath(Alert alert) {
		return baseUrl + alert.getEntityId() + "/alert/" + alert.getId();
	}

	private void prettifyAlert(Alert alert) {
		alert.setName(localizationService.getAlertName(alert));
		alert.getEvidences().forEach(evidence -> evidence.setName(localizationService.getIndicatorName(evidence)));
	}
}
