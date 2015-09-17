package fortscale.services.notifications;

import fortscale.domain.core.Notification;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.NotificationsRepository;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.events.VpnSession;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.util.*;

@Component("vpnGeoHoppingNotificationGenerator")
public class VpnGeoHoppingNotificationGenerator implements InitializingBean {

	private static Logger logger = Logger.getLogger(VpnGeoHoppingNotificationGenerator.class);
	
	public static final String VPN_GEO_HOPPING_CAUSE = "vpn_geo_hopping";
	public static final String NOTIFICATION_ENTITY = "vpn";

	@Value("${collection.evidence.notification.score.field}")
	private String notificationScoreField;
	@Value("${collection.evidence.notification.value.field}")
	private String notificationValueField;
	@Value("${collection.evidence.notification.normalizedusername.field}")
	private String normalizedUsernameField;
	@Value("${collection.evidence.notification.entity.field}")
	private String notificationEntityField;
	@Value("${collection.evidence.notification.starttimestamp.field}")
	private String notificationStartTimestampField;
	@Value("${collection.evidence.notification.endtimestamp.field}")
	private String notificationEndTimestampField;
	@Value("${collection.evidence.notification.type.field}")
	private String notificationTypeField;
	@Value("${collection.evidence.notification.numofevents.field}")
	private String notificationNumOfEventsField;
	@Value("${collection.evidence.notification.score}")
	private String score;
	@Value("${collection.evidence.notification.supportinginformation.field}")
	private String notificationSupportingInformationField;

	@Autowired
	private NotificationsRepository notificationsRepository;
	@Autowired
	private UserRepository userRepository;

	private List<String> vpnSessionFields;

	public void createNotifications(List<VpnSession> vpnSessions){
		List<Notification> notifications = new ArrayList<>();
		for(VpnSession vpnSession: vpnSessions){
			User user = userRepository.findByUsername(vpnSession.getNormalizedUserName());
			Notification notification = new Notification();
			long ts = vpnSession.getClosedAtEpoch() != null ? vpnSession.getClosedAtEpoch() : vpnSession.getCreatedAtEpoch();
			notification.setTs(TimestampUtils.convertToSeconds(ts));
			notification.setIndex(buildIndex(vpnSession));
			notification.setGenerator_name(VpnGeoHoppingNotificationGenerator.class.getSimpleName());
			notification.setName(vpnSession.getNormalizedUserName());
			notification.setCause(VPN_GEO_HOPPING_CAUSE);
			notification.setUuid(UUID.randomUUID().toString());
			if(user != null){
				notification.setDisplayName(user.getDisplayName());
				notification.setFsId(user.getId());
			} else{
				notification.setDisplayName(vpnSession.getNormalizedUserName());
				notification.setFsId(vpnSession.getNormalizedUserName());
			}

			notification.setAttributes(getVpnSessionAttributes(vpnSession));

			logger.info("adding geo hopping notification with the index {}", notification.getIndex());
			notifications.add(notification);
		}

		try{
			notificationsRepository.save(notifications);
		} catch (DuplicateKeyException ex){
			logger.info("got geo hopping notification duplication exception", ex);
		} catch (Exception e) {
			logger.info("got the following exception while trying to save new notifications to DB.", e);
		}
	}

	private String buildIndex(VpnSession vpnSession){
		StringBuilder builder = new StringBuilder();
		builder.append(VPN_GEO_HOPPING_CAUSE).append("_").append(vpnSession.getUsername()).append("_").append(vpnSession.getCountry()).append("_").append(vpnSession.getCreatedAtEpoch());

		return builder.toString();
	}

	private Map<String, String> getVpnSessionAttributes(VpnSession vpnSession){
		Map<String, String> attributes = new HashMap<>();
		for (String field : vpnSessionFields) {
			try {
				String value = BeanUtils.getProperty(vpnSession, field);
				if(value != null){
					attributes.put(field, value);
				}
			} catch (Exception e) {
				logger.debug("while extracting data from {} got an exception for the field {}.", vpnSession.getClass(), field);
				logger.debug("while extracting data from VpnSession got an exception", e);
			}
		}
		return attributes;
	}

	public JSONObject createIndicator(List<VpnSession> vpnSessions){
		if (vpnSessions.size() < 2) {
			return null;
		}

		List<Long> sessionsTimeframe = getSessionsTimeframe(vpnSessions);

		if (sessionsTimeframe == null || sessionsTimeframe.size() != 2){
			logger.debug("Can't find time frame for vpn session");
			return null;
		}

		long startTimestamp = sessionsTimeframe.get(0);
		long endTimestamp = sessionsTimeframe.get(1);
		String index = buildIndex(vpnSessions.get(0));
		JSONObject evidence = new JSONObject();
		evidence.put(notificationScoreField, score);
		evidence.put(notificationStartTimestampField, startTimestamp);
		evidence.put(notificationEndTimestampField, endTimestamp);
		evidence.put(notificationTypeField, VPN_GEO_HOPPING_CAUSE);
		evidence.put(notificationValueField, vpnSessions.get(0).getCountry());
		evidence.put(notificationNumOfEventsField, vpnSessions.size());
		evidence.put(notificationSupportingInformationField, "");
		List<String> entities = new ArrayList();
		entities.add(NOTIFICATION_ENTITY);
		evidence.put(notificationEntityField, entities);
		evidence.put(normalizedUsernameField, vpnSessions.get(0).getNormalizedUserName());
		evidence.put("index", index);
		logger.info("adding geo hopping notification with the index {}", index);

		return evidence;
	}

	/**
	 * Get the session timeframe
	 * @param vpnSessions
	 * @return
	 */
	private List<Long> getSessionsTimeframe(List<VpnSession> vpnSessions) {

		// list to hold the start and end time
		List<Long> sessionsTimeframe = new ArrayList<Long>();
		long startTime = Long.MAX_VALUE;
		long endTime = 0;

		// Find the minimum start time and maximum end time
		for(VpnSession session : vpnSessions) {
			if (session.getCreatedAtEpoch() != null && session.getCreatedAtEpoch() < startTime) {
				startTime = session.getCreatedAtEpoch();
			}
			if (session.getClosedAtEpoch()!= null && session.getClosedAtEpoch() > endTime) {
				endTime = session.getClosedAtEpoch();
			}
			else if (session.getCreatedAtEpoch()!= null && session.getCreatedAtEpoch() > endTime) {
				endTime = session.getCreatedAtEpoch();
			}

		}

		// If we found timeframe, add it to list
		if (startTime != Long.MAX_VALUE && endTime != 0) {
			sessionsTimeframe.add(startTime);
			sessionsTimeframe.add(endTime);
		}

		return sessionsTimeframe;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		//Get vpn session fields
		vpnSessionFields = new ArrayList<>();
		for(PropertyDescriptor propertyDescriptor: PropertyUtils.getPropertyDescriptors(VpnSession.class)){
			String fieldName = propertyDescriptor.getName();
			vpnSessionFields.add(fieldName);
		}
	}
	
}