package fortscale.services;

import fortscale.domain.core.Alert;
import fortscale.domain.email.Frequency;

/**
 * Created by Tomer Dvir on 22/02/16.
 */
public interface AlertSyslogForwardingService {

	boolean forwardNewAlert(Alert alert);
	int forwardAlertsByTimeRange(String ip, int
			port, String forwardingType, String sendingMethod,
			String[] userTags, String[] alertSeverity, long startTime, long endTime) throws RuntimeException;
}