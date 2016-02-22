package fortscale.services.impl;

import fortscale.domain.core.Alert;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.Severity;
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

import java.util.Optional;

/**
 * Created by tomerd on 21/02/2016.
 */
public class AlertSyslogForwardingServiceImpl implements AlertSyslogForwardingService, InitializingBean {

	private static Logger logger = Logger.getLogger(AlertSyslogForwardingServiceImpl.class);

	public static final String IP_KEY = "system.alertsSyslogForwarding.ip";
	public static final String PORT_KEY = "system.alertsSyslogForwarding.port";
	public static final String SENDING_METHOD_KEY = "system.alertsSyslogForwarding.sendingmethod";

	@Autowired private AlertsService alertsService;
	@Autowired private ApplicationConfigurationService applicationConfigurationService;
	@Autowired private UserService userService;

	private String ip;
	private int port;
	private String sendingMethod;

	private SyslogSender syslogSender;

	@Override public void afterPropertiesSet() throws Exception {
		loadConfiguration();
	}

	@Override public void forwardNewAlert(Alert alert) {
		if (syslogSender != null && !filterAlert(alert) ) {
			syslogSender.sendEvent(alert.toString());
		}
	}

	@Override public void forwardHistoricalAlerts(Frequency frequency) {
		// TODO
	}

	private void loadConfiguration() throws ConfigurationException {
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
	}

	private boolean filterAlert(Alert alert) {
		if (filterBySeverity(alert.getSeverity())) {
			return true;
		}
		if (filterByUserType(alert.getEntityName())){
			return true;
		}

		return false;
	}

	private boolean filterByUserType(String entityName) {
		return false;
	}

	private boolean filterBySeverity(Severity severity) {
		return false;
	}
}
