package fortscale.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.Alert;
import fortscale.domain.email.*;
import fortscale.services.AlertEmailService;
import fortscale.services.AlertsService;
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

	@Autowired
	private AlertsService alertsService;
	@Autowired
	private EmailUtils emailUtils;
	@Autowired
	private JadeUtils jadeUtils;
	@Autowired
	private AlertPrettifierImpl alertPrettifier;

	private EmailConfiguration emailConfiguration;
	private ObjectMapper objectMapper = new ObjectMapper();
	//TODO - add attachments
	private String[] attachedFiles = null;

	@Override
	public void sendNewAlert(Alert alert) {
		emailConfiguration = loadEmailConfiguration();
		if (emailConfiguration != null) {
			String alertSeverity = alert.getSeverity().name();
			//if one of the groups contains the alert severity
			if (emailConfiguration.shouldSendNewAlert(alertSeverity)) {
				Map<String, Object> model = new HashMap();
				alertPrettifier.prettify(alert);
				model.put("alert", alert);
				String html;
				try {
					html = jadeUtils.renderHTML("", model);
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
		}
	}

	@Override
	public void sendAlertSummary() {
		emailConfiguration = loadEmailConfiguration();
		if (emailConfiguration != null) {
			for (EmailGroup emailGroup : emailConfiguration.getEmailGroups()) {
				AlertSummary alertSummary = emailGroup.getSummary();
				Map<String, Object> model = new HashMap();
				//TODO - what to do with the frequencies?
				List<Alert> alerts = alertsService.getAlertSummary(alertSummary.getSeverities(), 0);
				for (Alert alert: alerts) {
					alertPrettifier.prettify(alert);
				}
				model.put("alerts", alerts);
				String html;
				try {
					html = jadeUtils.renderHTML("", model);
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
		//TODO - fetch configuration from mongo
		String config = "{json}";
		EmailConfiguration emailConfiguration = null;
		if (config == null) {
			logger.warn("no email configuration found");
		} else {
			try {
				emailConfiguration = objectMapper.readValue(config, EmailConfiguration.class);
			} catch (Exception ex) {
				logger.error("failed to load email configuration - {}", ex);
			}
		}
		return emailConfiguration;
	}

}