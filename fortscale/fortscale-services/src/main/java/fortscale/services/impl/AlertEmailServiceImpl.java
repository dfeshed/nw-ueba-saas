package fortscale.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.Alert;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.User;
import fortscale.domain.email.AlertSummary;
import fortscale.domain.email.EmailGroup;
import fortscale.domain.email.Frequency;
import fortscale.domain.email.NewAlert;
import fortscale.services.*;
import fortscale.utils.email.EmailUtils;
import fortscale.utils.image.ImageUtils;
import fortscale.utils.jade.JadeUtils;
import fortscale.utils.logging.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Amir Keren on 17/01/16.
 */
@Service("alertEmailService")
public class AlertEmailServiceImpl implements AlertEmailService, InitializingBean {

	private static Logger logger = Logger.getLogger(AlertEmailServiceImpl.class);

	//TODO - check exact phrasing
	private static final String NEW_ALERT_SUBJECT = "New Fortscale Alert";
	private static final String ALERT_SUMMARY_SUBJECT = "Fortscale Alerts Summary";

	private static final String CONFIGURATION_KEY = "system.alertsEmail.settings";
	//TODO - generalize this
	private static final String JADE_RESOURCES_FOLDER = "/home/cloudera/fortscale/fortscale-core/fortscale/fortscale-services/src/main/resources/dynamic-html";
	//private static final String JADE_RESOURCES_FOLDER = "resources/dynamic-html";
	private static final String IMAGES_FOLDER = JADE_RESOURCES_FOLDER + "/assets/images";
	private static final String NEW_ALERT_JADE_INDEX = JADE_RESOURCES_FOLDER + "/templates/new-alert-email/index.jade";
	private static final String ALERT_SUMMARY_JADE_INDEX = JADE_RESOURCES_FOLDER +
			"/templates/alert-summary-email/index.jade";
	private static final String USER_THUMBNAIL = IMAGES_FOLDER + "/user_thumbnail.png";
	private static final String USER_DEFAULT_THUMBNAIL = IMAGES_FOLDER + "/user_default_thumbnail.png";
	private static final String USER_CID = "user";
	private static final String IMAGE_TYPE = "png";

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

	private String baseUrl;
	private List<EmailGroup> emailConfiguration;
	private ObjectMapper objectMapper;
	private Map<String, String> cidToFilePath;

	/**
	 *
	 * This method populates the mail attachments map
	 *
	 * @return
	 */
	private Map<String,String> populateCIDToFilePathMap() {
		Map<String, String> resultMap = new HashMap();
		final String ICON_CRITICAL_CID = "critical";
		final String ICON_CRITICAL = IMAGES_FOLDER + "/severity_icon_critical.png";
		final String ICON_HIGH_CID = "high";
		final String ICON_HIGH = IMAGES_FOLDER + "/severity_icon_high.png";
		final String ICON_MEDIUM_CID = "medium";
		final String ICON_MEDIUM = IMAGES_FOLDER + "/severity_icon_medium.png";
		final String ICON_LOW_CID = "low";
		final String ICON_LOW = IMAGES_FOLDER + "/severity_icon_low.png";
		final String LOGO_CID = "logo";
		final String LOGO_IMAGE = IMAGES_FOLDER + "/logo.png";
		final String SHADOW_CID = "shadow";
		final String SHADOW_IMAGE = IMAGES_FOLDER + "/alert_details_shadow_cropped.png";
		resultMap.put(ICON_CRITICAL_CID, ICON_CRITICAL);
		resultMap.put(ICON_HIGH_CID, ICON_HIGH);
		resultMap.put(ICON_MEDIUM_CID, ICON_MEDIUM);
		resultMap.put(ICON_LOW_CID, ICON_LOW);
		resultMap.put(LOGO_CID, LOGO_IMAGE);
		resultMap.put(SHADOW_CID, SHADOW_IMAGE);
		return resultMap;
	}

	/**
	 *
	 * This method sends an email notification of an incoming new alert
	 *
	 * @param alert
	 */
	@Override
	public void sendNewAlertEmail(Alert alert) {
		if (!emailUtils.isEmailConfigured()) {
			return;
		}
		emailConfiguration = loadEmailConfiguration();
		if (emailConfiguration == null) {
			return;
		}
		String alertSeverity = alert.getSeverity().name();
		//if none of the groups contain the alert severity
		if (!shouldSendNewAlert(alertSeverity)) {
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
		model.put("baseUrl", baseUrl);
		model.put("alert", alert);
		model.put("user", user);
		String html;
		try {
			html = jadeUtils.renderHTML(NEW_ALERT_JADE_INDEX, model);
		} catch (Exception ex) {
			logger.error("failed to render html - {}", ex);
			return;
		}
		String thumbnail = userService.getUserThumbnail(user);
		if (thumbnail != null) {
			try {
				imageUtils.convertBase64ToImg(thumbnail, USER_THUMBNAIL, IMAGE_TYPE);
				cidToFilePath.put(USER_CID, USER_THUMBNAIL);
			} catch (Exception ex) {
				logger.warn("Failed to convert user thumbnail");
			}
		} else {
			cidToFilePath.put(USER_CID, USER_DEFAULT_THUMBNAIL);
		}
		//for each group check if they should be notified of the alert
		for (EmailGroup emailGroup : emailConfiguration) {
			NewAlert newAlert = emailGroup.getNewAlert();
			if (newAlert.getSeverities().contains(alertSeverity)) {
				try {
					emailUtils.sendEmail(emailGroup.getUsers(), null, null, NEW_ALERT_SUBJECT, html, cidToFilePath,
							true);
				} catch (MessagingException | IOException ex) {
					logger.error("failed to send email - {}", ex);
					return;
				}
			}
		}
	}

	/**
	 *
	 * This is a helper method that determines if we should send a new alert or not
	 *
	 * @param alertSeverity
	 * @return
	 */
	private boolean shouldSendNewAlert(String alertSeverity) {
		for (EmailGroup emailGroup: emailConfiguration) {
			NewAlert newAlert = emailGroup.getNewAlert();
			if (newAlert.getSeverities().contains(alertSeverity)) {
				return true;
			}
		}
		return false;
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
		for (EmailGroup emailGroup : emailConfiguration) {
			AlertSummary alertSummary = emailGroup.getSummary();
			if (alertSummary.getFrequencies().contains(frequency)) {
				DateTime startTime = getDateTimeByFrequency(frequency);
				List<Alert> alerts = alertsService.getAlertSummary(alertSummary.getSeverities(), startTime.getMillis());
				if (alerts.isEmpty()) {
					continue;
				}
				for (Alert alert: alerts) {
					alertPrettifierService.prettify(alert);
				}
				Map<String, Object> model = new HashMap();
				model.put("baseUrl", baseUrl);
				model.put("dateRange", getDateRangeByTimeFrequency(frequency));
				//TODO - should we replace this with a string?
				model.put("alertsSeverity", getAlertsSeverityHistogram(alerts));
				model.put("alerts", alerts);
				String html;
				try {
					html = jadeUtils.renderHTML(ALERT_SUMMARY_JADE_INDEX, model);
				} catch (Exception ex) {
					logger.error("failed to render html - {}", ex);
					return;
				}
				try {
					emailUtils.sendEmail(emailGroup.getUsers(), null, null, ALERT_SUMMARY_SUBJECT, html, cidToFilePath,
							true);
				} catch (MessagingException | IOException ex) {
					logger.error("failed to send email - {}", ex);
					return;
				}
			}
		}
	}

	/**
	 *
	 * This method creates the alerts severity histogram for the html template
	 *
	 * @param alerts
	 * @return
	 */
	private Map<String, Integer> getAlertsSeverityHistogram(List<Alert> alerts) {
		Map<String, Integer> severityHistogram = new HashMap();
		for (Alert alert: alerts) {
			String severity = alert.getSeverity().name();
			if (severityHistogram.containsKey(severity)) {
				severityHistogram.put(severity, severityHistogram.get(severity) + 1);
			} else {
				severityHistogram.put(severity, 1);
			}
		}
		return severityHistogram;
	}

	/**
	 *
	 * This method generates the string date range for the html template
	 *
	 * @param frequency
	 * @return
	 */
	private String getDateRangeByTimeFrequency(Frequency frequency) {
		DateTime now = new DateTime();
		DateTime date = getDateTimeByFrequency(frequency);
		switch (frequency) {
			case Daily: return date.getDayOfMonth() + "/" + date.getMonthOfYear() + "/" + date.getYear();
			case Weekly: return date.getDayOfMonth() + "-" + now.getDayOfMonth() + "/" + now.getMonthOfYear() + "/" +
					now.getYear();
			//TODO - should this be the format??
			case Monthly: return date.getDayOfMonth() + "/" + date.getYear();
			default: return "";
		}
	}

	/**
	 *
	 * This method converts frequency to a time in milliseconds
	 *
	 * @param frequency
	 * @return
	 */
	private DateTime getDateTimeByFrequency(Frequency frequency) {
		DateTime date = new DateTime();
		switch (frequency) {
			case Daily: date = date.minusDays(1); break;
			case Weekly: date = date.minusWeeks(1); break;
			case Monthly: date = date.minusMonths(1); break;
		}
		return date;
	}

	/**
	 *
	 * This method loads the email configuration from mongo
	 *
	 * @return
	 * @throws Exception
	 */
	private List<EmailGroup> loadEmailConfiguration() {
		List<EmailGroup> emailConfiguration = null;
		ApplicationConfiguration applicationConfiguration = applicationConfigurationService.
				getApplicationConfigurationByKey(CONFIGURATION_KEY);
		if (applicationConfiguration != null) {
			String config = applicationConfiguration.getValue();
			try {
				emailConfiguration = objectMapper.readValue(config, new TypeReference<List<EmailGroup>>(){});
			} catch (Exception ex) {
				logger.error("failed to load email configuration - {}", ex);
			}
		} else {
			logger.warn("no email configuration found");
		}
		return emailConfiguration;
	}

	/**
	 * This method acts as a constructor
	 *
	 * @throws Exception
	 */
	@Override public void afterPropertiesSet() throws Exception {
		baseUrl = InetAddress.getLocalHost().getHostName();
		objectMapper = new ObjectMapper();
		cidToFilePath = populateCIDToFilePathMap();
	}

}