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
	public static final String NOTIFICATION_VALUE = "country";

	@Value("${collection.evidence.notification.score.field}")
	private String notificationScoreField;
	@Value("${collection.evidence.notification.value.field}")
	private String notificationValueField;
	@Value("${collection.evidence.notification.normalizedusername.field}")
	private String normalizedUsernameField;
	@Value("${collection.evidence.notification.entity.field}")
	private String notificationEntityField;
	@Value("${collection.evidence.notification.timestamp.field}")
	private String notificationTimestampField;
	@Value("${collection.evidence.notification.type.field}")
	private String notificationTypeField;
	@Value("${collection.evidence.notification.score}")
	private String score;

	public List<JSONObject> createNotifications(List<VpnSession> vpnSessions){
		List<JSONObject> evidenceList = new ArrayList();
		for(VpnSession vpnSession: vpnSessions){
			long ts = vpnSession.getClosedAtEpoch() != null ? vpnSession.getClosedAtEpoch() :
					vpnSession.getCreatedAtEpoch();
			String index = buildIndex(vpnSession);
			JSONObject evidence = new JSONObject();
			evidence.put(notificationScoreField, score);
			evidence.put(notificationTimestampField, ts);
			evidence.put(notificationTypeField, VPN_GEO_HOPPING_CAUSE);
			evidence.put(notificationValueField, NOTIFICATION_VALUE);
			evidence.put(notificationEntityField, NOTIFICATION_ENTITY);
			evidence.put(normalizedUsernameField, vpnSession.getNormalizedUserName());
			evidence.put("index", index);
			logger.info("adding geo hopping notification with the index {}", index);
			evidenceList.add(evidence);
		}
		return evidenceList;
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