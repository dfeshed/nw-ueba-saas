package fortscale.services.impl;

import fortscale.domain.core.Alert;
import fortscale.domain.email.Frequency;
import fortscale.services.AlertEmailService;
import fortscale.services.ForwardingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by Amir Keren on 19/01/16.
 */
public class ForwadingServiceImpl implements ForwardingService {

	@Value("${email.forwarding.enabled:false}")
	private boolean emailForwardingEnabled;

	@Autowired
	AlertEmailService alertEmailService;

	@Override
	public void forwardNewAlert(Alert alert) {
		if (emailForwardingEnabled) {
			alertEmailService.sendNewAlertEmail(alert);
		}
	}

	@Override
	public void forwardLatestAlerts(Frequency frequency) {
		if (emailForwardingEnabled) {
			alertEmailService.sendAlertSummaryEmail(frequency);
		}
	}

}