package fortscale.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.Alert;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.email.*;
import fortscale.services.AlertEmailService;
import fortscale.services.AlertPrettifierService;
import fortscale.services.AlertsService;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.email.EmailUtils;
import fortscale.utils.jade.JadeUtils;
import fortscale.utils.logging.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Amir Keren on 17/01/16.
 */
@Service("alertEmailService")
public class AlertEmailServiceImpl implements AlertEmailService {

	private static Logger logger = Logger.getLogger(AlertEmailServiceImpl.class);

	//TODO - check exact phrasing
	private static final String NEW_ALERT_SUBJECT = "New Fortscale Alert";
	private static final String ALERT_SUMMARY_SUBJECT = "Fortscale Alerts Summary";

	private static final String CONFIGURATION_KEY = "system.emailConfiguration.settings";
	private static final String JADE_RESOURCES_FOLDER = "resources/dynamic.html";
	private static final String NEW_ALERT_JADE_INDEX = JADE_RESOURCES_FOLDER + "/templates/new-alert-email/index.jade";
	private static final String ALERT_SUMMARY_JADE_INDEX = JADE_RESOURCES_FOLDER +
			"/templates/alert-summary-email/index.jade";

	@Autowired
	private AlertsService alertsService;
	@Autowired
	private EmailUtils emailUtils;
	@Autowired
	private JadeUtils jadeUtils;
	@Autowired
	private AlertPrettifierService alertPrettifierService;
	@Autowired
	private ApplicationConfigurationService applicationConfigurationService;

	private EmailConfiguration emailConfiguration;
	private ObjectMapper objectMapper = new ObjectMapper();
	//TODO - add attachments
	private String[] attachedFiles = null;

	/**
	 *
	 * This method sends an email notification of an incoming new alert
	 *
	 * @param alert
	 */
	@Override
	public void sendNewAlert(Alert alert) {
		if (!emailUtils.isEmailConfigured()) {
			return;
		}
		emailConfiguration = loadEmailConfiguration();
		if (emailConfiguration == null) {
			return;
		}
		String alertSeverity = alert.getSeverity().name();
		//if none of the groups contain the alert severity
		if (!emailConfiguration.shouldSendNewAlert(alertSeverity)) {
			return;
		}
		Map<String, Object> model = new HashMap();
		alertPrettifierService.prettify(alert);
		model.put("alert", alert);
		String html;
		try {
			html = jadeUtils.renderHTML(NEW_ALERT_JADE_INDEX, model);
		} catch (IOException ex) {
			logger.error("failed to render html - {}", ex);
			return;
		}
		//for each group check if they should be notified of the alert
		for (EmailGroup emailGroup : emailConfiguration.getEmailGroups()) {
			NewAlert newAlert = emailGroup.getNewAlert();
			if (newAlert.getSeverities().contains(alertSeverity)) {
				try {
					emailUtils.sendEmail(emailGroup.getUsers(), null, null, NEW_ALERT_SUBJECT, html,
							attachedFiles, true);
				} catch (MessagingException | IOException ex) {
					logger.error("failed to send email - {}", ex);
					return;
				}
			}
		}
	}

	/**
	 *
	 * This method sends an email summary of the top alerts for a given frequency
	 *
	 * @param frequency
	 */
	@Override
	public void sendAlertSummary(EmailFrequency frequency) {
		if (!emailUtils.isEmailConfigured()) {
			return;
		}
		emailConfiguration = loadEmailConfiguration();
		if (emailConfiguration == null) {
			return;
		}
		for (EmailGroup emailGroup : emailConfiguration.getEmailGroups()) {
			AlertSummary alertSummary = emailGroup.getSummary();
			if (alertSummary.getFrequencies().contains(frequency)) {
				List<Alert> alerts = alertsService.getAlertSummary(alertSummary.getSeverities(),
						getDateTimeByFrequency(frequency));
				if (alerts.isEmpty()) {
					continue;
				}
				for (Alert alert: alerts) {
					alertPrettifierService.prettify(alert);
				}
				Map<String, Object> model = new HashMap();
				model.put("alerts", alerts);
				String html;
				try {
					html = jadeUtils.renderHTML(ALERT_SUMMARY_JADE_INDEX, model);
				} catch (IOException ex) {
					logger.error("failed to render html - {}", ex);
					return;
				}
				try {
					emailUtils.sendEmail(emailGroup.getUsers(), null, null, ALERT_SUMMARY_SUBJECT, html,
							attachedFiles, true);
				} catch (MessagingException | IOException ex) {
					logger.error("failed to send email - {}", ex);
					return;
				}
			}
		}
	}

	/**
	 *
	 * This method converts frequency to a time in milliseconds
	 *
	 * @param frequency
	 * @return
	 */
	private long getDateTimeByFrequency(EmailFrequency frequency) {
		DateTime date = new DateTime();
		switch (frequency) {
			case Daily: date = date.minusDays(1); break;
			case Weekly: date = date.minusWeeks(1); break;
			case Monthly: date = date.minusMonths(1); break;
		}
		return date.getMillis();
	}

	/**
	 *
	 * This method loads the email configuration from mongo
	 *
	 * @return
	 * @throws Exception
	 */
	private EmailConfiguration loadEmailConfiguration() {
		EmailConfiguration emailConfiguration = null;
		ApplicationConfiguration applicationConfiguration = applicationConfigurationService.
				getApplicationConfigurationByKey(CONFIGURATION_KEY);
		if (applicationConfiguration != null) {
			String config = applicationConfiguration.getValue();
			try {
				emailConfiguration = objectMapper.readValue(config, EmailConfiguration.class);
			} catch (Exception ex) {
				logger.error("failed to load email configuration - {}", ex);
			}
		} else {
			logger.warn("no email configuration found");
		}
		return emailConfiguration;
	}

}