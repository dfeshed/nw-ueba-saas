package fortscale.services;

import com.cloudbees.syslog.MessageFormat;
import fortscale.domain.core.alert.Alert;
import fortscale.domain.email.Frequency;

/**
 * Created by Amir Keren on 19/01/16.
 */
public interface ForwardingService {

	void forwardNewAlert(Alert alert);

	void forwardLatestAlerts(Frequency frequency);

	int forwardAlertsByTimeRange(String ip, int port, String forwardingType, String[] userTags, String[] alertSeverity,
                                 long startTime, long endTime, MessageFormat messageFormat);

}