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
import java.util.*;

import static fortscale.utils.ConversionUtils.convertToString;

/**
 * Created by idanp on 10/18/2014.
 * Implementation for sending notification related to AMT sensetive account access
 */
@Service
public class AmtActionToSensitiveAccountNotificationGenerator  {


	private static Logger logger = Logger.getLogger(AmtActionToSensitiveAccountNotificationGenerator.class);

	private static final String AMT_ACTION_TO_SENSTIVIE_ACCOUNT_CAUSE = "amt_action_to_sensitive_account";
	private static final String AMT_ACTION_TO_SENSTIVIE_ACCOUNT_NAME = AMT_ACTION_TO_SENSTIVIE_ACCOUNT_CAUSE;
	private static final String AMT_ACTION_TO_SENSTIVIE_ACCOUNT_MSG_FOR_SINGLE = "  has accessed a VIP account - {{attributes.yid}}";
	private static final String AMT_RESET_PWD_MSG_FOR_AGG =  "   have accessed a VIP account";

	@Autowired
	private NotificationsRepository notificationsRepository;

	@Autowired
	private NotificationResourcesRepository notificationResourcesRepository;

	@Autowired
	private UserRepository userRepository;

	private List<String> amtEventFields;

	/**
	 * This Method will create notification for given user name , yid and time stamp based on the Json message
	 * The notification will be related to some sensitive action that taken on sensitive yid
	 * @param msg - The JSON message from the kafka topic
	 * @param normalizeUsername - The normalized user name as he retrieved from the JSON msg
	 * @param yid - The yid that retrieved from the JSON msg
	 * @param date_time_unix - The date time unix that retrieved from the JSON msg
	 * @throws JSONException - The method can throw an exception due to JSON parsing exception
	 */
	public void createNotifications(JSONObject msg, String normalizeUsername,String yid,long date_time_unix) throws JSONException {

		List<Notification> notifications = new ArrayList<>();
		User user = userRepository.findByUsername(normalizeUsername);
		Notification notification = new Notification();
		long ts = date_time_unix;
		notification.setTs(TimestampUtils.convertToSeconds(ts));
		notification.setIndex(buildIndex(normalizeUsername,yid,date_time_unix));
		notification.setGenerator_name(AmtActionToSensitiveAccountNotificationGenerator.class.getSimpleName());
		notification.setName(normalizeUsername);
		notification.setCause(AMT_ACTION_TO_SENSTIVIE_ACCOUNT_CAUSE);
		notification.setUuid(UUID.randomUUID().toString());
		if(user != null) {
			notification.setDisplayName(user.getDisplayName());
			notification.setFsId(user.getId());
		}else {
			notification.setDisplayName(normalizeUsername);
			notification.setFsId(normalizeUsername);
		}

		notification.setAttributes(getAmtEventsAttributes(msg));

		logger.info("adding amt reset password with the index {}", notification.getIndex());
		notifications.add(notification);


		try{
			notificationsRepository.save(notifications);
		} catch (DuplicateKeyException ex){
			logger.info("got AmtActionToSensitiveAccountNotificationGenerator notification duplication exception", ex);
		} catch (Exception e) {
			logger.info("got the following exception while trying to save new notifications to DB.", e);
		}
	}

	private String buildIndex(String NormalizeUsername, String yid, long date_time_unix){
		StringBuilder builder = new StringBuilder();
		builder.append(AMT_ACTION_TO_SENSTIVIE_ACCOUNT_CAUSE).append("_").append(NormalizeUsername).append("_").append(yid).append("_").append(date_time_unix);

		return builder.toString();
	}


	private Map<String, String> getAmtEventsAttributes(JSONObject msg){
		Map<String, String> attributes = new HashMap<>();
		for (String field : amtEventFields) {
			try {
				String value = convertToString(msg.get(field));
				if(value != null){
					attributes.put(field, value);
				}
			} catch (Exception e) {
				logger.debug("while extracting data from Record got an exception for the field {}.", field);

			}
		}
		return attributes;
	}

	@PostConstruct
	public void postConstructor() throws Exception {
		//add notification resource
		NotificationResource notificationResource = notificationResourcesRepository.findByMsg_name(AMT_ACTION_TO_SENSTIVIE_ACCOUNT_NAME);
		if(notificationResource == null){
			notificationResource = new NotificationResource(AMT_ACTION_TO_SENSTIVIE_ACCOUNT_NAME, AMT_ACTION_TO_SENSTIVIE_ACCOUNT_MSG_FOR_SINGLE, AMT_RESET_PWD_MSG_FOR_AGG);
			notificationResourcesRepository.save(notificationResource);
		}

		//Get amt session field
		PropertiesResolver resolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = resolver.getProperty("impala.data.amt.table.fields");
		amtEventFields = ImpalaParser.getTableFieldNames(impalaTableFields);

	}


}
