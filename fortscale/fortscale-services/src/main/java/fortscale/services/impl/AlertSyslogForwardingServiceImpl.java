package fortscale.services.impl;

import fortscale.domain.core.Alert;
import fortscale.domain.core.Severity;
import fortscale.domain.core.User;
import fortscale.domain.email.Frequency;
import fortscale.services.AlertSyslogForwardingService;
import fortscale.services.AlertsService;
import fortscale.services.ApplicationConfigurationService;
import fortscale.services.UserService;
import fortscale.utils.logging.Logger;
import fortscale.utils.syslog.SyslogSender;
import org.apache.commons.configuration.ConfigurationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * Created by tomerd on 21/02/2016.
 */
public class AlertSyslogForwardingServiceImpl implements AlertSyslogForwardingService, InitializingBean {

	private static Logger logger = Logger.getLogger(AlertSyslogForwardingServiceImpl.class);

	public static final String TAGS_SPILTER = ",";

	public static final String IP_KEY = "system.alertsSyslogForwarding.ip";
	public static final String PORT_KEY = "system.alertsSyslogForwarding.port";
	public static final String SENDING_METHOD_KEY = "system.alertsSyslogForwarding.sendingmethod";
	public static final String USER_TYPES_KEY = "system.alertsSyslogForwarding.usertypes";
	public static final String ALERT_SEVERITY_KEY = "system.alertsSyslogForwarding.alertseverity";

	@Autowired private AlertsService alertsService;
	@Autowired private ApplicationConfigurationService applicationConfigurationService;
	@Autowired private UserService userService;

	private String ip;
	private int port;
	private String sendingMethod;
	private String baseUrl;
	private SyslogSender syslogSender;

	@Override public void afterPropertiesSet() throws Exception {
		loadConfiguration();
	}

	@Override public void forwardNewAlert(Alert alert) {
		if (syslogSender != null && !filterAlert(alert)) {
			String rawAlert = "Alert URL: " + generateAlertPath(alert) + alert.toString();
			syslogSender.sendEvent(rawAlert);
		}
	}

	@Override public void forwardHistoricalAlerts(Frequency frequency) {
		// TODO
	}

	private void loadConfiguration() throws ConfigurationException, UnknownHostException {
		Optional<String> optionalReader;

		optionalReader = applicationConfigurationService.readFromConfigurationService(IP_KEY);
		if (optionalReader.isPresent()) {
			ip = optionalReader.get();
		} else {
			throw new ConfigurationException("Error creating syslog forwarder - missing ip configuration key ");
		}

		optionalReader = applicationConfigurationService.readFromConfigurationService(PORT_KEY);
		if (optionalReader.isPresent()) {
			port = Integer.valueOf(optionalReader.get());
		} else {
			throw new ConfigurationException("Error creating syslog forwarder - missing port configuration key ");
		}

		optionalReader = applicationConfigurationService.readFromConfigurationService(SENDING_METHOD_KEY);
		if (optionalReader.isPresent()) {
			sendingMethod = optionalReader.get();
		} else {
			throw new ConfigurationException("Error creating syslog forwarder - missing sending method configuration key ");
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
		Optional<String> reader = applicationConfigurationService.readFromConfigurationService(ALERT_SEVERITY_KEY);
		if (!reader.isPresent()) {
			return false;
		}

		Severity severityFromConfig = Severity.valueOf(reader.get());

		return (severityFromConfig.compareTo(severity) <= 0);

	}

	private boolean filterByUserType(String entityName) {
		Optional<String> reader = applicationConfigurationService.readFromConfigurationService(USER_TYPES_KEY);
		if (!reader.isPresent()) {
			return false;
		}

		String tags = reader.get();
		User user = userService.findByUsername(entityName);

		for (String tag : tags.split(TAGS_SPILTER)) {
			if (user.hasTag(tag)) {
				return true;
			}
		}

		return false;
	}

	private String generateAlertPath(Alert alert) {
		return baseUrl + alert.getId() + "/" + alert.getEvidences().get(0).getId() + "/gen/overview";
	}
}
