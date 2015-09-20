package fortscale.services.notifications;

import fortscale.domain.events.VpnSession;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

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

	public JSONObject createNotifications(List<VpnSession> vpnSessions){
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

	private String buildIndex(VpnSession vpnSession){
		StringBuilder builder = new StringBuilder();
		builder.append(VPN_GEO_HOPPING_CAUSE).append("_").append(vpnSession.getUsername()).append("_").
				append(vpnSession.getCountry()).append("_").append(vpnSession.getCreatedAtEpoch());
		return builder.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {}
	
}