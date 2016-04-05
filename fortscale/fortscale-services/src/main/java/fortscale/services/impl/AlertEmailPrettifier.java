package fortscale.services.impl;

import fortscale.domain.core.Alert;
import fortscale.domain.core.EmailAlertDecorator;
import fortscale.domain.core.EmailEvidenceDecorator;
import fortscale.domain.core.Evidence;
import fortscale.services.AlertPrettifierService;
import fortscale.services.LocalizationService;
import fortscale.utils.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Amir Keren on 18/01/16.
 */

public class AlertEmailPrettifier implements AlertPrettifierService<EmailAlertDecorator> {

	@Autowired
	private EvidenceEmailPrettifier evidenceEmailPrettifier;

	@Autowired
	public LocalizationService localizationService;

	private static final String SHORT_DATE_FORMAT = "EEE, MM/dd/yy";
	private static final String LONG_DATE_FORMAT = "MM/dd/yy HH:mm:ss";

	public void setEvidenceEmailPrettifier(EvidenceEmailPrettifier evidenceEmailPrettifier) {
		this.evidenceEmailPrettifier = evidenceEmailPrettifier;
	}

	private String decorateDate (long lDate, String dateFormat) {
		Date date;

		try {
			date = new Date(lDate);
		} catch (RuntimeException e) {
			return "Unknown date";
		}

		return TimeUtils.getUtcFormat(date, dateFormat);
	}

	@Override
	public EmailAlertDecorator prettify(Alert alert) {
		return prettify(alert, false);
	}

	@Override
	public EmailAlertDecorator prettify(Alert alert, boolean noEvidencePrettify) {

		// Create EmailAlert from alert
		EmailAlertDecorator emailAlert = new EmailAlertDecorator(alert);

		// Prettify start date
		emailAlert.setStartDateLong(decorateDate(alert.getStartDate(), LONG_DATE_FORMAT));
		emailAlert.setStartDateShort(decorateDate(alert.getStartDate(), SHORT_DATE_FORMAT));

		// Prettify end date
		emailAlert.setEndDateLong(decorateDate(alert.getEndDate(), LONG_DATE_FORMAT));
		emailAlert.setEndDateShort(decorateDate(alert.getEndDate(), SHORT_DATE_FORMAT));
		emailAlert.setName(decorateName(alert));

		if (!noEvidencePrettify) {
			// Iterate through evidences and prettify each evidence
			emailAlert.getEvidences().forEach(evidence ->
					emailAlert.getEmailEvidences().add(evidenceEmailPrettifier.prettify(evidence)));

			// Sort evidences by date start DESC
			Collections.sort(emailAlert.getEmailEvidences(), (o1, o2) -> {
				long result = o2.getStartDate() - o1.getStartDate();
				int iResult = 0;
				if (result > 0) {
					iResult = 1;
				} else if (result < 0) {
					iResult = -1;
				}
				return iResult;
			});
		}

		return emailAlert;
	}

	/**
	 * Return a decorated indicator name
	 *
	 * @param alert The name will be extracted from the evidence
	 * @return Decorated indicator (evidence) name
	 */
	private String decorateName(Alert alert) {

		return localizationService.getAlertName(alert);
	}


}