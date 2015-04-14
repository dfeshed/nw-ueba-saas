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
import org.kitesdk.morphline.api.Record;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by rotemn on 9/18/2014.
 * Implementation for sending notification related to AMT PWD Reset notification
 */

@Component("amtResetPwdNotificationGenerator")
public class AmtResetPwdNotificationGenerator  implements InitializingBean {

	private static Logger logger = Logger.getLogger(AmtResetPwdNotificationGenerator.class);

	private static final String AMT_RESET_PWD_CAUSE = "amt_reset_pwd";
	private static final String AMT_RESET_PWD_MSG_NAME = AMT_RESET_PWD_CAUSE;
	private static final String AMT_RESET_PWD_MSG_FOR_SINGLE = " reset password without an appropriate action path";
	private static final String AMT_RESET_PWD_MSG_FOR_AGG = AMT_RESET_PWD_MSG_FOR_SINGLE;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");



	@Autowired
	private NotificationsRepository notificationsRepository;

	@Autowired
	private NotificationResourcesRepository notificationResourcesRepository;

	@Autowired
	private UserRepository userRepository;

	private List<String> amtEventFields;



	public void createNotifications(Record record){

		String normalizeUsername = record.get("normalized_username").get(0).toString();
		String yid = record.get("yid").get(0).toString();
		long date_time_unix = Long.parseLong(record.get("date_time_unix").get(0).toString());

		List<Notification> notifications = new ArrayList<>();
		User user = userRepository.findByUsername(normalizeUsername);
		Notification notification = new Notification();
		long ts = date_time_unix;
		notification.setTs(TimestampUtils.convertToSeconds(ts));
		notification.setIndex(buildIndex(normalizeUsername,yid,date_time_unix));
		notification.setGenerator_name(AmtResetPwdNotificationGenerator.class.getSimpleName());
		notification.setName(normalizeUsername);
		notification.setCause(AMT_RESET_PWD_CAUSE);
		notification.setUuid(UUID.randomUUID().toString());
		if(user != null) {
			notification.setDisplayName(user.getDisplayName());
			notification.setFsId(user.getId());
		}else {
			notification.setDisplayName(normalizeUsername);
			notification.setFsId(normalizeUsername);
		}

		notification.setAttributes(getAmtEventsAttributes(record));

		logger.info("adding amt reset password with the index {}", notification.getIndex());
		notifications.add(notification);


		try{
			notificationsRepository.save(notifications);
		} catch (DuplicateKeyException ex){
			logger.info("got AmtResetPwdNotificationGenerator notification duplication exception", ex);
		} catch (Exception e) {
			logger.info("got the following exception while trying to save new notifications to DB.", e);
		}
	}

	private String buildIndex(String NormalizeUsername, String yid, long date_time_unix){
		StringBuilder builder = new StringBuilder();
		String day = format.format(new Date(TimestampUtils.convertToMilliSeconds(date_time_unix)));
		builder.append(AMT_RESET_PWD_CAUSE).append("_").append(NormalizeUsername).append("_").append(yid).append("_").append(day);

		return builder.toString();
	}


	private Map<String, String> getAmtEventsAttributes(Record record){
		Map<String, String> attributes = new HashMap<>();
		for (String field : amtEventFields) {
			try {
				String value = record.get(field).get(0).toString();
				if(value != null){
					attributes.put(field, value);
				}
			} catch (Exception e) {
				logger.debug("while extracting data from Record got an exception for the field {}.", field);

			}
		}
		return attributes;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		//add notification resource
		NotificationResource notificationResource = notificationResourcesRepository.findByMsg_name(AMT_RESET_PWD_MSG_NAME);
		if(notificationResource == null){
			notificationResource = new NotificationResource(AMT_RESET_PWD_MSG_NAME, AMT_RESET_PWD_MSG_FOR_SINGLE, AMT_RESET_PWD_MSG_FOR_AGG);
			notificationResourcesRepository.save(notificationResource);
		}

		//Get vpn session fields


		PropertiesResolver resolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = resolver.getProperty("impala.data.amt.table.fields");
		amtEventFields = ImpalaParser.getTableFieldNames(impalaTableFields);

	}





}
