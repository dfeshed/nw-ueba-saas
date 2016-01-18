package fortscale.services;

import fortscale.domain.core.Alert;
import fortscale.domain.email.EmailFrequency;

/**
 * Created by Amir Keren on 17/01/16.
 */
public interface AlertEmailService {

	void sendNewAlert(Alert alert);
	void sendAlertSummary(EmailFrequency frequency);

}
