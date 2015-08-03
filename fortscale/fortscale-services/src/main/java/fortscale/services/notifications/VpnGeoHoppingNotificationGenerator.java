package fortscale.services.notifications;

import fortscale.domain.core.NotificationResource;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.NotificationResourcesRepository;
import fortscale.domain.core.dao.NotificationsRepository;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.events.VpnSession;
import fortscale.domain.schema.VpnEvents;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.beans.PropertyDescriptor;
import java.util.*;


@Component("vpnGeoHoppingNotificationGenerator")
public class VpnGeoHoppingNotificationGenerator implements InitializingBean{
	private static Logger logger = Logger.getLogger(VpnGeoHoppingNotificationGenerator.class);
	
	public static final String VPN_GEO_HOPPING_CAUSE = "vpn_geo_hopping";
	public static final String VPN_GEO_HOPPING_MSG_NAME = VPN_GEO_HOPPING_CAUSE;
	public static final String VPN_GEO_HOPPING_MSG_FOR_SINGLE = " has concurrent sessions in different countries";
	public static final String VPN_GEO_HOPPING_MSG_FOR_AGG = " have concurrent sessions in different countries";
	
	
	
	@Autowired
	private NotificationsRepository notificationsRepository;
	
	@Autowired
	private NotificationResourcesRepository notificationResourcesRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private VpnEvents vpnEvents;
	
	private List<String> vpnSessionFields;
	
	public List<JSONObject> createNotifications(List<VpnSession> vpnSessions){
		List<JSONObject> evidenceList = new ArrayList();
		for(VpnSession vpnSession: vpnSessions){
			User user = userRepository.findByUsername(vpnSession.getNormalizedUserName());
			long ts = vpnSession.getClosedAtEpoch() != null ? vpnSession.getClosedAtEpoch() : vpnSession.getCreatedAtEpoch();
			String index = buildIndex(vpnSession);
			JSONObject evidence = new JSONObject();
			evidence.put("notification_score", 50);
			evidence.put("date_time_unix", ts);
			evidence.put("notification_type", VPN_GEO_HOPPING_CAUSE);
			evidence.put("notification_value", "country");
			evidence.put("notification_entity", "vpn");
			evidence.put("normalized_username", user.getUsername());
			evidence.put("index", index);
			logger.info("adding geo hopping notification with the index {}", index);
			evidenceList.add(evidence);
		}
		return evidenceList;
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

	@Override
	public void afterPropertiesSet() throws Exception {
		//add notification resource
		NotificationResource notificationResource = notificationResourcesRepository.findByMsg_name(VPN_GEO_HOPPING_MSG_NAME);
		if(notificationResource == null){
			notificationResource = new NotificationResource(VPN_GEO_HOPPING_MSG_NAME, VPN_GEO_HOPPING_MSG_FOR_SINGLE, VPN_GEO_HOPPING_MSG_FOR_AGG);
			notificationResourcesRepository.save(notificationResource);
		}
		
		//Get vpn session fields
		vpnSessionFields = new ArrayList<>();
		for(PropertyDescriptor propertyDescriptor: PropertyUtils.getPropertyDescriptors(VpnSession.class)){
			String fieldName = propertyDescriptor.getName();
			vpnSessionFields.add(fieldName);
		}
	}
	
}
