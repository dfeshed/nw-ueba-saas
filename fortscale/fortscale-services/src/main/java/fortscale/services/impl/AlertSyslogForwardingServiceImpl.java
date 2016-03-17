package fortscale.services.impl;

import fortscale.domain.core.Alert;
import fortscale.domain.core.Severity;
import fortscale.domain.core.User;
import fortscale.services.*;
import fortscale.utils.logging.Logger;
import fortscale.utils.syslog.SyslogSender;
import org.apache.commons.configuration.ConfigurationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by tomerd on 21/02/2016.
 */
@Service("alertSyslogForwardingService") public class AlertSyslogForwardingServiceImpl
		implements AlertSyslogForwardingService, InitializingBean {

	private static Logger logger = Logger.getLogger(AlertSyslogForwardingServiceImpl.class);

	public static final String SPILTER = ",";

	public static final String CONFIGURATION_NAMESPACE = "system.syslogforwarding";

	public final static String ALERT_FORWARDING_KEY = CONFIGURATION_NAMESPACE + ".enabled";

	public static final String IP_KEY = CONFIGURATION_NAMESPACE + ".ip";
	public static final String PORT_KEY = CONFIGURATION_NAMESPACE + ".port";
	public static final String USER_TYPES_KEY = CONFIGURATION_NAMESPACE + ".usertypes";
	public static final String ALERT_SEVERITY_KEY = CONFIGURATION_NAMESPACE + ".alertseverity";
	public static final String FORWARDING_TYPE_KEY = CONFIGURATION_NAMESPACE + ".forwardingtype";

	@Autowired private AlertsService alertsService;
	@Autowired private ApplicationConfigurationService applicationConfigurationService;
	@Autowired private UserService userService;

	private String ip;
	private int port;
	private String[] alertSeverity;
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

	@Override public int forwardAlertsByTimeRange(String ip, int port, String forwardingType, String[] userTags,
			String[] alertSeverity, long startTime, long endTime) throws RuntimeException {

		List<Alert> alerts = alertsService.getAlertsByTimeRange(startTime, endTime, Arrays.asList(alertSeverity));

		SyslogSender sender = new SyslogSender(ip, port, "tcp");

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
		Map<String, String> applicationConfiguration = applicationConfigurationService.getApplicationConfigurationByNamespace(CONFIGURATION_NAMESPACE);

		String isEnabled = applicationConfiguration.get(ALERT_FORWARDING_KEY);
		if (isEnabled == null || isEnabled == "false") {
			return;
		}

		try {
			ip = applicationConfiguration.get(IP_KEY);
			port = Integer.valueOf(applicationConfiguration.get(PORT_KEY));
			String userTagsValue = applicationConfiguration.get(USER_TYPES_KEY);
			applicationConfiguration.get(USER_TYPES_KEY);
			if (userTagsValue == null) {
				userTags = new String[] {};
			} else {
				userTags = userTagsValue.split(SPILTER);
			}
			String alertSeverityValue = applicationConfiguration.get(ALERT_SEVERITY_KEY);
			if (alertSeverityValue == null) {
				alertSeverity = new String[] {};
			} else {
				alertSeverity = alertSeverityValue.split(SPILTER);
			}
			forwardingType = ForwardingType.valueOf(applicationConfiguration.get(FORWARDING_TYPE_KEY));

			syslogSender = new SyslogSender(ip, port, "tcp");

			baseUrl = "https://" + InetAddress.getLocalHost().getHostName() + ":8443/fortscale-webapp/index.html#/alerts/";
		} catch (Exception e) {
			throw new ConfigurationException("Error creating syslog forwarder - Configuration error");
		}
	}

	private boolean filterAlert(Alert alert) {
		if (filterBySeverity(alert.getSeverity())) {
			return true;
		}
		if (filterByUserType(alert.getEntityName(), userTags)) {
			return true;
		}

		return false;
	}

	private boolean filterBySeverity(Severity severity) {

		// If alert severity is not present, do not filter
		if (alertSeverity.length == 0) {
			return false;
		}
		return (!Arrays.asList(alertSeverity).contains(severity.name()));
	}

	private boolean filterByUserType(String entityName, String[] tags) {

		// If userTags is not present, do not filter
		if (tags.length == 0) {
			return false;
		}

		User user = userService.findByUsername(entityName);

		for (String tag : tags) {
			if (user.hasTag(tag)) {
				return false;
			}
		}

		return true;
	}

	private String generateAlertPath(Alert alert) {
		return baseUrl + alert.getId() + "/" + alert.getEvidences().get(0).getId() + "/gen/overview";
	}
}
