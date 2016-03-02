package fortscale.services.impl;

import fortscale.domain.core.Alert;
import fortscale.domain.core.Severity;
import fortscale.domain.core.User;
import fortscale.domain.email.Frequency;
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
import java.util.Optional;

/**
 * Created by tomerd on 21/02/2016.
 */
@Service("alertSyslogForwardingService")
public class AlertSyslogForwardingServiceImpl implements AlertSyslogForwardingService, InitializingBean {

	private static Logger logger = Logger.getLogger(AlertSyslogForwardingServiceImpl.class);

	public static final String SPILTER = ",";

	public static final String IP_KEY = "system.syslogforwarding.ip";
	public static final String PORT_KEY = "system.syslogforwarding.port";
	public static final String SENDING_METHOD_KEY = "system.syslogforwarding.sendingmethod";
	public static final String USER_TYPES_KEY = "system.syslogforwarding.usertypes";
	public static final String ALERT_SEVERITY_KEY = "system.syslogforwarding.alertseverity";
	public static final String FORWARDING_TYPE_KEY = "system.syslogforwarding.forwardingtype";

	@Autowired private AlertsService alertsService;
	@Autowired private ApplicationConfigurationService applicationConfigurationService;
	@Autowired private UserService userService;

	private String ip;
	private int port;
	private String sendingMethod;
	private String[] alertSeverity;
	private String[] userTags;
	private String baseUrl;
	private SyslogSender syslogSender;
	private ForwardingType forwardingType;

	@Override public void afterPropertiesSet() throws Exception {
		loadConfiguration();
	}

	@Override public boolean forwardNewAlert(Alert alert) {
		if (syslogSender != null && !filterAlert(alert)) {
			String rawAlert = "Alert URL: " + generateAlertPath(alert) + " ";
			switch (forwardingType) {
			case ALERT:
				rawAlert += alert.toString(false);
				break;
			case ALERT_AND_INDICATORS:
				rawAlert += alert.toString(true);
				break;
			default:
				return false;
			}

			syslogSender.sendEvent(rawAlert);
			return true;
		}

		return false;
	}

	@Override public int forwardAlertsByTimeRange(long startTime, long endTime) {
		List<Alert> alerts = alertsService.getAlertsByTimeRange(startTime, endTime, Arrays.asList(alertSeverity));

		int counter = 0;

		for (Alert alert : alerts) {
			if (forwardNewAlert(alert)) {
				counter++;
			}
		}

		return counter;
	}

	private void loadConfiguration() throws ConfigurationException, UnknownHostException {
		Optional<String> optionalReader;

		// Read the IP from the config
		optionalReader = applicationConfigurationService.readFromConfigurationService(IP_KEY);
		if (optionalReader.isPresent()) {
			ip = optionalReader.get();
		} else {
			throw new ConfigurationException("Error creating syslog forwarder - missing ip configuration key ");
		}

		// Read the port from the config
		optionalReader = applicationConfigurationService.readFromConfigurationService(PORT_KEY);
		if (optionalReader.isPresent()) {
			port = Integer.valueOf(optionalReader.get());
		} else {
			throw new ConfigurationException("Error creating syslog forwarder - missing port configuration key ");
		}

		// Read the sending method the config
		optionalReader = applicationConfigurationService.readFromConfigurationService(SENDING_METHOD_KEY);
		if (optionalReader.isPresent()) {
			sendingMethod = optionalReader.get();
		} else {
			throw new ConfigurationException("Error creating syslog forwarder - missing sending method configuration key ");
		}

		// Read the forwarding type from the config
		optionalReader = applicationConfigurationService.readFromConfigurationService(FORWARDING_TYPE_KEY);
		if (optionalReader.isPresent()) {
			forwardingType = ForwardingType.valueOf(optionalReader.get());
		} else {
			throw new ConfigurationException("Error creating syslog forwarder - missing forwarding type configuration key ");
		}

		// // Read the alert severity from the config
		optionalReader = applicationConfigurationService.readFromConfigurationService(ALERT_SEVERITY_KEY);
		if (optionalReader.isPresent()) {
			alertSeverity = optionalReader.get().split(SPILTER);
		} else {
			alertSeverity = new String[]{};
		}

		// // Read the alert severity from the config
		optionalReader = applicationConfigurationService.readFromConfigurationService(USER_TYPES_KEY);
		if (optionalReader.isPresent()) {
			userTags = optionalReader.get().split(SPILTER);
		} else {
			userTags = new String[]{};
		}

		syslogSender = new SyslogSender(ip, port, sendingMethod);

		baseUrl = "https://" + InetAddress.getLocalHost().getHostName() + ":8443/fortscale-webapp/index.html#/alerts/";
	}

	private boolean filterAlert(Alert alert) {
		if (filterBySeverity(alert.getSeverity())) {
			return true;
		}
		if (filterByUserType(alert.getEntityName())) {
			return true;
		}

		return false;
	}

	private boolean filterBySeverity(Severity severity) {
		return (!Arrays.asList(alertSeverity).contains(severity.name()));
	}

	private boolean filterByUserType(String entityName) {
		User user = userService.findByUsername(entityName);

		for (String tag : userTags) {
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
