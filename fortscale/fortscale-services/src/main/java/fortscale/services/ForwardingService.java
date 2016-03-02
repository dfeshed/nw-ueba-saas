package fortscale.services;

import fortscale.domain.core.Alert;
import fortscale.domain.email.Frequency;

/**
 * Created by Amir Keren on 19/01/16.
 */
public interface ForwardingService {

	void forwardNewAlert(Alert alert);
	void forwardLatestAlerts(Frequency frequency);

}