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

import java.util.*;

@Component("amtLoginAsMailNotificationGenerator")
public class AmtLoginAsMailNotificationGenerator implements InitializingBean {

	private static Logger logger = Logger.getLogger(AmtLoginAsMailNotificationGenerator.class);

	private static final String CAUSE = "amt_login_as_mail";
	private static final String MSG_NAME = CAUSE;
	private static final String MSG_FOR_SINGLE = " performed loginasmail without an appropriate action path";
	private static final String MSG_FOR_AGG = MSG_FOR_SINGLE;



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
		notification.setGenerator_name(AmtLoginAsMailNotificationGenerator.class.getSimpleName());
		notification.setName(normalizeUsername);
		notification.setCause(CAUSE);
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
			logger.info("got AmtLoginAsMailNotificationGenerator notification duplication exception", ex);
		} catch (Exception e) {
			logger.info("got the following exception while trying to save new notifications to DB.", e);
		}
	}

	private String buildIndex(String NormalizeUsername, String yid, long date_time_unix){
		StringBuilder builder = new StringBuilder();
		builder.append(CAUSE).append("_").append(NormalizeUsername).append("_").append(yid).append("_").append(date_time_unix);

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
		NotificationResource notificationResource = notificationResourcesRepository.findByMsg_name(MSG_NAME);
		if(notificationResource == null){
			notificationResource = new NotificationResource(MSG_NAME, MSG_FOR_SINGLE, MSG_FOR_AGG);
			notificationResourcesRepository.save(notificationResource);
		}

		//Get vpn session fields


		PropertiesResolver resolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = resolver.getProperty("impala.data.amt.table.fields");
		amtEventFields = ImpalaParser.getTableFieldNames(impalaTableFields);

	}

}
