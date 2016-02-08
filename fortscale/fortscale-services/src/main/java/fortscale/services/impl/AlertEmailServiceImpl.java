package fortscale.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.*;
import fortscale.domain.email.AlertSummary;
import fortscale.domain.email.EmailGroup;
import fortscale.domain.email.Frequency;
import fortscale.domain.email.NewAlert;
import fortscale.services.*;
import fortscale.utils.image.ImageUtils;
import fortscale.utils.jade.JadeUtils;
import fortscale.utils.logging.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Amir Keren on 17/01/16.
 */
@Service("alertEmailService")
public class AlertEmailServiceImpl implements AlertEmailService, InitializingBean {

	private static Logger logger = Logger.getLogger(AlertEmailServiceImpl.class);

	public static final String CONFIGURATION_KEY = "system.alertsEmail.settings";

	private static final String USER_CID = "user";
	private static final String SHADOW_CID = "shadow";
	private static final String USER_HOME_DIR = System.getProperty("user.home");

	@Value("${jade.resources.folder}")
	private String resourcesFolder;

	@Autowired
	private AlertsService alertsService;
	@Autowired
	private EmailServiceImpl emailServiceImpl;
	@Autowired
	private JadeUtils jadeUtils;
	@Autowired
	private ImageUtils imageUtils;
	@Autowired
	private AlertPrettifierService<EmailAlertDecorator> alertPrettifierService;
	@Autowired
	private ApplicationConfigurationService applicationConfigurationService;
	@Autowired
	private UserService userService;

	private String baseUrl;
	private List<EmailGroup> emailConfiguration;
	private ObjectMapper objectMapper;
	private Map<String, String> cidToFilePath;
	private String newAlertJadeIndex;
	private String alertSummaryJadeIndex;
	private String userThumbnail;
	private String userDefaultThumbnail;
	private String shadowImage;

	/**
	 *
	 * This method populates the mail attachments map
	 *
	 * @return
	 */
	private Map<String,String> populateCIDToFilePathMap(String imageFolder) {
		Map<String, String> resultMap = new HashMap();
		final String ICON_CRITICAL_CID = "critical";
		final String ICON_CRITICAL = imageFolder + "/severity_icon_critical.png";
		final String ICON_HIGH_CID = "high";
		final String ICON_HIGH = imageFolder + "/severity_icon_high.png";
		final String ICON_MEDIUM_CID = "medium";
		final String ICON_MEDIUM = imageFolder + "/severity_icon_medium.png";
		final String ICON_LOW_CID = "low";
		final String ICON_LOW = imageFolder + "/severity_icon_low.png";
		final String LOGO_CID = "logo";
		final String LOGO_IMAGE = imageFolder + "/logo.png";
		resultMap.put(ICON_CRITICAL_CID, ICON_CRITICAL);
		resultMap.put(ICON_HIGH_CID, ICON_HIGH);
		resultMap.put(ICON_MEDIUM_CID, ICON_MEDIUM);
		resultMap.put(ICON_LOW_CID, ICON_LOW);
		resultMap.put(LOGO_CID, LOGO_IMAGE);
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
		if (!emailServiceImpl.isEmailConfigured()) {
			return;
		}
		emailConfiguration = loadAlertEmailConfiguration();
		if (emailConfiguration == null || emailConfiguration.isEmpty()) {
			logger.warn("no email configuration found");
			return;
		}
		if (!isEmailConfigurationValid(true)) {
			logger.warn("email configuration is invalid");
			return;
		}
		String alertSeverity = alert.getSeverity().name();
		//if none of the groups contain the alert severity
		if (!shouldSendNewAlert(alertSeverity)) {
			return;
		}
		User user = userService.findByUsername(alert.getEntityName());
		if (user == null) {
			logger.error("couldn't find username - {}", alert.getEntityName());
			return;
		}
		Map<String, Object> model = new HashMap();
		EmailAlertDecorator emailAlert = alertPrettifierService.prettify(alert);
		model.put("baseUrl", baseUrl);
		model.put("alert", emailAlert);
		model.put("user", user);
		String html;
		try {
			html = jadeUtils.renderHTML(newAlertJadeIndex, model);
		} catch (Exception ex) {
			logger.error("failed to render html - {}", ex);
			return;
		}
		String thumbnail = userService.getUserThumbnail(user);
		HashMap<String, String> attachmentsMap = new HashMap(cidToFilePath);
		if (thumbnail != null) {
			try {
				imageUtils.convertBase64ToImg(thumbnail, userThumbnail, "png");
				attachmentsMap.put(USER_CID, userThumbnail);
			} catch (Exception ex) {
				logger.warn("Failed to convert user thumbnail");
			}
		} else {
			attachmentsMap.put(USER_CID, userDefaultThumbnail);
		}
		Set<Severity> severities = alert.getEvidences().stream().map(Evidence::getSeverity).collect(Collectors.toSet());
		severities.add(alert.getSeverity());
		Set<Severity> allSeverities = new HashSet(Arrays.asList(Severity.values()));
		//leave only the severities *not* appearing in the alert and its indicators
		allSeverities.removeAll(severities);
		//remove the unused severities from the attachments map
		allSeverities.forEach(severity -> attachmentsMap.remove(severity.name().toLowerCase()));
		attachmentsMap.put(SHADOW_CID, shadowImage);
		DateTime now = new DateTime();
		String date = now.toString("MMMM") + " " + now.getDayOfMonth() + ", " + now.getYear();
		String newAlertSubject = String.format("Fortscale %s Alert Notification, %s", alert.getSeverity().name(), date);
		//for each group check if they should be notified of the alert
		for (EmailGroup emailGroup : emailConfiguration) {
			NewAlert newAlert = emailGroup.getNewAlert();
			if (newAlert.getSeverities().contains(alertSeverity)) {
				try {
					emailServiceImpl.sendEmail(emailGroup.getUsers(), null, null, newAlertSubject, html, attachmentsMap,
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
	 * This method validates the email configuration
	 *
	 * @param isNewAlert
	 * @return
	 */
	private boolean isEmailConfigurationValid(boolean isNewAlert) {
		for (EmailGroup emailGroup: emailConfiguration) {
			if (emailGroup == null) {
				return false;
			}
			List<String> severities;
			if (isNewAlert) {
				NewAlert newAlert = emailGroup.getNewAlert();
				if (newAlert == null) {
					return false;
				}
				severities = newAlert.getSeverities();
			} else {
				AlertSummary alertSummary = emailGroup.getSummary();
				if (alertSummary == null) {
					return false;
				}
				List<Frequency> frequencies = alertSummary.getFrequencies();
				if (frequencies == null || frequencies.isEmpty()) {
					return false;
				}
				severities = alertSummary.getSeverities();
			}
			if (severities == null || severities.isEmpty()) {
				return false;
			}
			for (String severity: severities) {
				if (Severity.valueOf(severity) == null) {
					return false;
				}
			}
		}
		return true;
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
		if (!emailServiceImpl.isEmailConfigured()) {
			logger.warn("no email configuration found");
			return;
		}
		emailConfiguration = loadAlertEmailConfiguration();
		if (emailConfiguration == null || emailConfiguration.isEmpty()) {
			logger.warn("no alert email configuration found");
			return;
		}
		if (!isEmailConfigurationValid(false)) {
			logger.warn("email configuration is invalid");
			return;
		}
		DateTime now = new DateTime();
		for (EmailGroup emailGroup : emailConfiguration) {
			AlertSummary alertSummary = emailGroup.getSummary();
			if (alertSummary.getFrequencies().contains(frequency)) {
				DateTime startTime = getDateTimeByFrequency(frequency, now);
				List<Alert> alerts = alertsService.getAlertSummary(alertSummary.getSeverities(), startTime.getMillis());
				List<EmailAlertDecorator> emailAlerts = new ArrayList();
				if (alerts.isEmpty()) {
					continue;
				}
				alerts.forEach(alert -> emailAlerts.add(alertPrettifierService.prettify(alert, true)));
				Map<String, Object> model = new HashMap();
				String dateRange = getDateRangeByTimeFrequency(frequency, now);
				String alertSummarySubject = String.format("Fortscale %s Alert Notification, %s", frequency.name(),
						dateRange);
				model.put("baseUrl", baseUrl);
				model.put("dateRange", dateRange);
				model.put("alertsSeverity", getAlertsSeverityHistogram(alerts));
				model.put("alerts", emailAlerts);
				String html;
				try {
					html = jadeUtils.renderHTML(alertSummaryJadeIndex, model);
				} catch (Exception ex) {
					logger.error("failed to render html - {}", ex);
					return;
				}
				try {
					emailServiceImpl.sendEmail(emailGroup.getUsers(), null, null, alertSummarySubject, html, cidToFilePath,
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
		for (Severity severity: Severity.values()) {
			severityHistogram.put(severity.name(), 0);
		}
		for (Alert alert: alerts) {
			severityHistogram.put(alert.getSeverity().name(), severityHistogram.get(alert.getSeverity().name()) + 1);
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
	private String getDateRangeByTimeFrequency(Frequency frequency, DateTime now) {
		DateTime date = getDateTimeByFrequency(frequency, now);
		switch (frequency) {
			case Daily: return date.toString("MMMM") + " " + date.getDayOfMonth() + ", " + date.getYear();
			case Weekly: {
				if (now.getMonthOfYear() == date.getMonthOfYear()) {
					return date.toString("MMMM") + " " + date.getDayOfMonth() + "-" + now.getDayOfMonth() + ", " +
							now.getYear();
				}
				return date.toString("MMMM") + " " + date.getDayOfMonth() + "-" + now.toString("MMMM") +" " +
						now.getDayOfMonth() + ", " + now.getYear();
			}
			case Monthly: return date.toString("MMMM") + " " + date.getYear();
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
	private DateTime getDateTimeByFrequency(Frequency frequency, DateTime now) {
		DateTime date;
		switch (frequency) {
			case Daily: date = now.minusDays(1); break;
			case Weekly: date = now.minusWeeks(1); break;
			case Monthly: date = now.minusMonths(1); break;
			default: date = now;
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
	private List<EmailGroup> loadAlertEmailConfiguration() {
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
		}
		return emailConfiguration;
	}

	/**
	 * This method acts as a constructor
	 *
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		baseUrl = "https://" + InetAddress.getLocalHost().getHostName() + ":8443/fortscale-webapp/";
		objectMapper = new ObjectMapper();
		resourcesFolder = USER_HOME_DIR + "/" + resourcesFolder;
		String imageFolder = resourcesFolder + "/assets/images";
		newAlertJadeIndex = resourcesFolder + "/templates/new-alert-email/index.jade";
		alertSummaryJadeIndex = resourcesFolder + "/templates/alert-summary-email/index.jade";
		userThumbnail = imageFolder + "/user_thumbnail.png";
		userDefaultThumbnail = imageFolder + "/user_default_thumbnail.png";
		shadowImage = imageFolder + "/alert_details_shadow_cropped.png";
		cidToFilePath = populateCIDToFilePathMap(imageFolder);
	}

}