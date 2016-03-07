package fortscale.services;

import fortscale.domain.core.Alert;
import fortscale.domain.email.Frequency;

/**
 * Created by Amir Keren on 19/01/16.
 */
public interface ForwardingService {

	void forwardNewAlert(Alert alert);
	void forwardLatestAlerts(Frequency frequency);
	int forwardAlertsByTimeRange(String ip, int port, String forwardingType, String sendingMethod,
			String[] userTags, String[] alertSeverity, long startTime, long endTime);

}