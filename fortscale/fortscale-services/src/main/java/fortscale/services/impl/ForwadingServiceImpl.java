package fortscale.services.impl;

import fortscale.domain.core.Alert;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.email.Frequency;
import fortscale.services.AlertEmailService;
import fortscale.services.AlertSyslogForwardingService;
import fortscale.services.ApplicationConfigurationService;
import fortscale.services.ForwardingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by Amir Keren on 19/01/16.
 */
@Service("ForwardingService")
public class ForwadingServiceImpl implements ForwardingService {

	private final static String ALERT_FORWARDING_KEY = "system.syslogforwarding.enabled";

	@Value("${email.forwarding.enabled:true}") private boolean emailForwardingEnabled;

	@Autowired AlertEmailService alertEmailService;

	@Autowired AlertSyslogForwardingService alertSyslogForwardingService;

	@Autowired ApplicationConfigurationService applicationConfigurationService;

	@Override public void forwardNewAlert(Alert alert) {
		if (emailForwardingEnabled) {
			alertEmailService.sendNewAlertEmail(alert);
		}
		if (readBooleanFromConfigurationService(ALERT_FORWARDING_KEY)) {
			alertSyslogForwardingService.forwardNewAlert(alert);
		}
	}

	@Override public void forwardLatestAlerts(Frequency frequency) {
		if (emailForwardingEnabled) {
			alertEmailService.sendAlertSummaryEmail(frequency);
		}
	}

	@Override public int forwardAlertsByTimeRange(String ip, int port, String forwardingType, String[] userTags,
			String[] alertSeverity, long startTime, long endTime) throws RuntimeException {
		return alertSyslogForwardingService.forwardAlertsByTimeRange(ip, port, forwardingType, userTags, alertSeverity, startTime, endTime);
	}

	private Boolean readBooleanFromConfigurationService(String key) {
		ApplicationConfiguration applicationConfiguration = applicationConfigurationService.
				getApplicationConfigurationByKey(key);
		if (applicationConfiguration != null) {
			return Boolean.valueOf(applicationConfiguration.getValue());
		}

		return false;
	}
}