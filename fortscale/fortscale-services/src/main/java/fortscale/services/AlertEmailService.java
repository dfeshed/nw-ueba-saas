package fortscale.services;

import fortscale.domain.core.Alert;
import fortscale.domain.email.Frequency;

/**
 * Created by Amir Keren on 17/01/16.
 */
public interface AlertEmailService {

	void sendNewAlertEmail(Alert alert);
	void sendAlertSummaryEmail(Frequency frequency);

}