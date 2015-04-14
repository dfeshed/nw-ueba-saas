package fortscale.services.notifications;

import fortscale.domain.core.Notification;
import fortscale.domain.core.NotificationResource;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.NotificationResourcesRepository;
import fortscale.domain.core.dao.NotificationsRepository;
import fortscale.domain.core.dao.UserRepository;
import fortscale.utils.TimestampUtils;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.logging.Logger;
import fortscale.utils.properties.PropertiesResolver;
import net.minidev.json.JSONObject;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;

import static fortscale.utils.ConversionUtils.convertToString;

/*
 * Implementation for sending notifications related to AMT sensitive account access.
 */
@Service
public class AmtActionToSensitiveAccountNotificationGenerator {
	private static Logger logger = Logger.getLogger(AmtActionToSensitiveAccountNotificationGenerator.class);
	private static final String AMT_ACTION_TO_SENSITIVE_ACCOUNT_CAUSE = "amt_action_to_sensitive_account";
	private static final String AMT_ACTION_TO_SENSITIVE_ACCOUNT_NAME = AMT_ACTION_TO_SENSITIVE_ACCOUNT_CAUSE;
	private static final String AMT_ACTION_TO_SENSITIVE_ACCOUNT_MSG_FOR_SINGLE = "   has accessed a VIP account - {{attributes.yid}}";
	private static final String AMT_RESET_PWD_MSG_FOR_AGG = "   have accessed a VIP account";
	private SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");

	@Autowired
	private NotificationsRepository notificationsRepository;
	@Autowired
	private NotificationResourcesRepository notificationResourcesRepository;

	@Autowired
	private UserRepository userRepository;

	private List<String> amtEventFields;

	/**
	 * This method will create notifications for a given username, YID and timestamp, based on the Json message.
	 * The notification will be related to some sensitive action that was taken on a sensitive YID.
	 *
	 * @param msg                - The Json message from the Kafka topic.
	 * @param normalizedUsername - The normalized username as was retrieved from the Json msg.
	 * @param yid                - The YID that was retrieved from the Json msg.
	 * @param dateTimeUnix       - The date time unix that was retrieved from the Json msg.
	 *
	 * @throws JSONException - The method might throw an exception due to a Json parsing exception.
	 */
	public void createNotifications(JSONObject msg, String normalizedUsername, String yid, long dateTimeUnix) throws JSONException {
		User user = userRepository.findByUsername(normalizedUsername);
		Notification notification = new Notification();

		notification.setTs(TimestampUtils.convertToSeconds(dateTimeUnix));
		notification.setIndex(buildIndex(normalizedUsername, yid, dateTimeUnix));
		notification.setGenerator_name(AmtActionToSensitiveAccountNotificationGenerator.class.getSimpleName());
		notification.setName(normalizedUsername);
		notification.setCause(AMT_ACTION_TO_SENSITIVE_ACCOUNT_CAUSE);
		notification.setUuid(UUID.randomUUID().toString());

		if (user != null) {
			notification.setDisplayName(user.getDisplayName());
			notification.setFsId(user.getId());
		} else {
			notification.setDisplayName(normalizedUsername);
			notification.setFsId(normalizedUsername);
		}

		notification.setAttributes(getAmtEventsAttributes(msg));
		logger.info("Adding AMT reset password with the index {}", notification.getIndex());

		try {
			notificationsRepository.save(notification);
		} catch (DuplicateKeyException e) {
			logger.info("Received AmtActionToSensitiveAccountNotificationGenerator notification duplication exception", e);
		} catch (Exception e) {
			logger.info("Received the following exception while trying to save new notifications to DB", e);
		}
	}

	private String buildIndex(String normalizedUsername, String yid, long dateTimeUnix) {
		StringBuilder builder = new StringBuilder();
		String day = format.format(new Date(TimestampUtils.convertToMilliSeconds(dateTimeUnix)));
		builder.append(AMT_ACTION_TO_SENSITIVE_ACCOUNT_CAUSE).append("_").append(normalizedUsername).append("_").append(yid).append("_").append(day);
		return builder.toString();
	}

	private Map<String, String> getAmtEventsAttributes(JSONObject msg) {
		Map<String, String> attributes = new HashMap<>();

		for (String field : amtEventFields) {
			try {
				String value = convertToString(msg.get(field));
				if (value != null) attributes.put(field, value);
			} catch (Exception e) {
				logger.debug("While extracting data from record, received an exception for the field {}", field);
			}
		}


		return attributes;
	}

	@PostConstruct
	public void postConstructor() throws Exception {
		// Add a notification resource
		NotificationResource notificationResource = notificationResourcesRepository.findByMsg_name(AMT_ACTION_TO_SENSITIVE_ACCOUNT_NAME);
		if (notificationResource == null) {
			notificationResource = new NotificationResource(AMT_ACTION_TO_SENSITIVE_ACCOUNT_NAME, AMT_ACTION_TO_SENSITIVE_ACCOUNT_MSG_FOR_SINGLE, AMT_RESET_PWD_MSG_FOR_AGG);
			notificationResourcesRepository.save(notificationResource);
		}

		// Get AMT session field
		PropertiesResolver resolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = resolver.getProperty("impala.data.amt.table.fields");
		amtEventFields = ImpalaParser.getTableFieldNames(impalaTableFields);
	}

}
