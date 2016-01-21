package fortscale.services.impl;

import fortscale.domain.core.Alert;
import fortscale.domain.core.EmailAlertDecorator;
import fortscale.services.AlertPrettifierService;

/**
 * Created by Amir Keren on 18/01/16.
 */
public class AlertEmailPrettifier implements AlertPrettifierService<EmailAlertDecorator> {

	@Override
	public EmailAlertDecorator prettify(Alert alert) {
		return null;
	}

}