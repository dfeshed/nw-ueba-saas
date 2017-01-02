package fortscale.services;

import com.cloudbees.syslog.MessageFormat;
import fortscale.domain.core.Alert;

/**
 * Created by Tomer Dvir on 22/02/16.
 */
public interface AlertSyslogForwardingService {

	boolean forwardNewAlert(Alert alert);

	int forwardAlertsByTimeRange(String ip, int port, String forwardingType, String[] userTags, String[] alertSeverity,
								 long startTime, long endTime, MessageFormat messageFormat) throws RuntimeException;
}