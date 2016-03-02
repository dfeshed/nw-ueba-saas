package fortscale.services;

import fortscale.domain.core.Alert;
import fortscale.domain.email.Frequency;

/**
 * Created by Tomer Dvir on 22/02/16.
 */
public interface AlertSyslogForwardingService {

	void forwardNewAlert(Alert alert);
	void forwardAlertsByTimeRange(long startTime, long endTime);
}