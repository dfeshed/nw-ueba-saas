package fortscale.services.notifications;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fortscale.domain.core.Notification;
import fortscale.domain.core.NotificationResource;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.NotificationResourcesRepository;
import fortscale.domain.core.dao.NotificationsRepository;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.events.VpnSession;
import fortscale.domain.schema.VpnEvents;
import fortscale.utils.logging.Logger;


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
	
	public void createNotifications(List<VpnSession> vpnSessions){
		List<Notification> notifications = new ArrayList<>();
		for(VpnSession vpnSession: vpnSessions){
			User user = userRepository.findByUsername(vpnSession.getNormalizeUsername());
			Notification notification = new Notification();
			long ts = vpnSession.getClosedAtEpoch() != null ? vpnSession.getClosedAtEpoch() : vpnSession.getCreatedAtEpoch();
			notification.setTs(ts);
			notification.setGenerator_name(VpnGeoHoppingNotificationGenerator.class.getSimpleName());
			notification.setName(vpnSession.getNormalizeUsername());
			notification.setCause(VPN_GEO_HOPPING_CAUSE);
			notification.setDisplayName(vpnSession.getNormalizeUsername());
			notification.setUuid(UUID.randomUUID().toString());
			if(user != null){
				notification.setFsId(user.getId());
			} else{
				notification.setFsId(vpnSession.getNormalizeUsername());
			}
			
			notification.setAttributes(getVpnSessionAttributes(vpnSession));
			
			notifications.add(notification);
		}
		
		
		notificationsRepository.save(notifications);
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
