package fortscale.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.Alert;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.User;
import fortscale.domain.email.*;
import fortscale.services.*;
import fortscale.utils.email.EmailUtils;
import fortscale.utils.image.ImageUtils;
import fortscale.utils.jade.JadeUtils;
import fortscale.utils.logging.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;

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
	private static final String IMAGES_FOLDER = JADE_RESOURCES_FOLDER + "/assets.images";
	private static final String NEW_ALERT_JADE_INDEX = JADE_RESOURCES_FOLDER + "/templates/new-alert-email/index.jade";
	private static final String ALERT_SUMMARY_JADE_INDEX = JADE_RESOURCES_FOLDER +
			"/templates/alert-summary-email/index.jade";
	private static final String USER_THUMBNAIL = IMAGES_FOLDER + "/user_thumbnail.png";
	private static final String USER_DEFAULT_THUMBNAIL = IMAGES_FOLDER + "/user_default_thumbnail.png";
	private static final String ICON_CRITICAL = IMAGES_FOLDER + "/severity_icon_critical.png";
	private static final String ICON_HIGH = IMAGES_FOLDER + "/severity_icon_high.png";
	private static final String ICON_MEDIUM = IMAGES_FOLDER + "/severity_icon_medium.png";
	private static final String ICON_LOW = IMAGES_FOLDER + "/severity_icon_low.png";
	private static final String LOGO_IMAGE = IMAGES_FOLDER + "/logo.png";
	private static final String SHADOW_IMAGE = IMAGES_FOLDER + "/alert_details_shadow.png";
	private static final String SHADOW_CROPPED_IMAGE = IMAGES_FOLDER + "/alert_details_shadow_cropped.png";

	@Autowired
	private AlertsService alertsService;
	@Autowired
	private EmailUtils emailUtils;
	@Autowired
	private JadeUtils jadeUtils;
	@Autowired
	private ImageUtils imageUtils;
	@Autowired
	private AlertPrettifierService alertPrettifierService;
	@Autowired
	private ApplicationConfigurationService applicationConfigurationService;
	@Autowired
	private UserService userService;

	private EmailConfiguration emailConfiguration;
	private ObjectMapper objectMapper = new ObjectMapper();

	/**
	 *
	 * This method sends an email notification of an incoming new alert
	 *
	 * @param alert
	 */
	@Override
	public void sendNewAlertEmail(Alert alert) {
		//sanity
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
		User user = userService.findByUsername(alert.getEntityName());
		//sanity
		if (user == null) {
			logger.error("couldn't find username - {}", alert.getEntityName());
			return;
		}
		Map<String, Object> model = new HashMap();
		alertPrettifierService.prettify(alert);
		model.put("alert", alert);
		model.put("user", user);
		String html;
		try {
			html = jadeUtils.renderHTML(NEW_ALERT_JADE_INDEX, model);
		} catch (IOException ex) {
			logger.error("failed to render html - {}", ex);
			return;
		}
		Set<String> attachedFiles = new HashSet();
		attachedFiles.add(LOGO_IMAGE);
		attachedFiles.add(SHADOW_IMAGE);
		attachedFiles.add(SHADOW_CROPPED_IMAGE);
		switch (alert.getSeverity()) {
			case Critical: attachedFiles.add(ICON_CRITICAL); break;
			case High: attachedFiles.add(ICON_HIGH); break;
			case Medium: attachedFiles.add(ICON_MEDIUM); break;
			case Low: attachedFiles.add(ICON_LOW); break;
		}
		String thumbnail = userService.getUserThumbnail(user);
		if (thumbnail != null) {
			try {
				imageUtils.convertBase64ToPNG(thumbnail, USER_THUMBNAIL);
				attachedFiles.add(USER_THUMBNAIL);
			} catch (Exception ex) {
				logger.warn("Failed to convert user thumbnail");
				attachedFiles.add(USER_DEFAULT_THUMBNAIL);
			}
		} else {
			attachedFiles.add(USER_DEFAULT_THUMBNAIL);
		}
		//for each group check if they should be notified of the alert
		for (EmailGroup emailGroup : emailConfiguration.getEmailGroups()) {
			NewAlert newAlert = emailGroup.getNewAlert();
			if (newAlert.getSeverities().contains(alertSeverity)) {
				try {
					emailUtils.sendEmail(emailGroup.getUsers(), null, null, NEW_ALERT_SUBJECT, html,
							attachedFiles.toArray(new String[attachedFiles.size()]), true);
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
	public void sendAlertSummaryEmail(Frequency frequency) {
		//sanity
		if (!emailUtils.isEmailConfigured()) {
			return;
		}
		emailConfiguration = loadEmailConfiguration();
		if (emailConfiguration == null) {
			return;
		}
		Set<String> constantImages = new HashSet();
		constantImages.add(LOGO_IMAGE);
		constantImages.add(SHADOW_IMAGE);
		constantImages.add(SHADOW_CROPPED_IMAGE);
		for (EmailGroup emailGroup : emailConfiguration.getEmailGroups()) {
			AlertSummary alertSummary = emailGroup.getSummary();
			if (alertSummary.getFrequencies().contains(frequency)) {
				List<Alert> alerts = alertsService.getAlertSummary(alertSummary.getSeverities(),
						getDateTimeByFrequency(frequency));
				if (alerts.isEmpty()) {
					continue;
				}
				Set<String> severityIcons = new HashSet();
				for (Alert alert: alerts) {
					alertPrettifierService.prettify(alert);
					switch (alert.getSeverity()) {
						case Critical: severityIcons.add(ICON_CRITICAL); break;
						case High: severityIcons.add(ICON_HIGH); break;
						case Medium: severityIcons.add(ICON_MEDIUM); break;
						case Low: severityIcons.add(ICON_LOW); break;
					}
				}
				severityIcons.addAll(constantImages);
				String[] attachedFiles = severityIcons.toArray(new String[severityIcons.size()]);
				Map<String, Object> model = new HashMap();
				model.put("alerts", alerts);
				//TODO - how to add the list of users?
				model.put("users", null);
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
	private long getDateTimeByFrequency(Frequency frequency) {
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