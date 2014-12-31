package fortscale.collection.morphlines.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import fortscale.collection.morphlines.RecordExtensions;
import org.apache.commons.lang.StringUtils;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.typesafe.config.Config;

import fortscale.collection.morphlines.RecordToVpnSessionConverter;
import fortscale.domain.events.VpnSession;
import fortscale.domain.schema.VpnEvents;
import fortscale.services.event.VpnService;
import fortscale.services.notifications.VpnGeoHoppingNotificationGenerator;



public class VpnSessionUpdateMorphCmdBuilder implements CommandBuilder {	
	private static Logger logger = LoggerFactory.getLogger(VpnSessionUpdateMorphCmdBuilder.class);
	
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("VpnSessionUpdate");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new VpnSessionUpdate(this, config, parent, child, context);
	}

	@Configurable(preConstruction=true)
	public class VpnSessionUpdate extends AbstractCommand {
		@Autowired
		private VpnEvents vpnEvents;
		@Autowired
		private RecordToVpnSessionConverter recordToVpnSessionConverter;
		@Autowired
		private VpnService vpnService;
		@Autowired
		private VpnGeoHoppingNotificationGenerator vpnGeoHoppingNotificationGenerator;
		
		private final String countryIsoCodeFieldName;
		private final String longtitudeFieldName;
		private final String latitudeFieldName;
		private final String sessionIdFieldName;
		private final Integer vpnGeoHoppingCloseSessionThresholdInHours;
		private final Integer vpnGeoHoppingOpenSessionThresholdInHours;
		private final Boolean runGeoHopping;
		private String addSessionDataFieldName;


		public VpnSessionUpdate(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.longtitudeFieldName = getConfigs().getString(config, "longtitude_field");
			this.latitudeFieldName = getConfigs().getString(config, "latitude_field");
			this.countryIsoCodeFieldName = getConfigs().getString(config, "country_code_field");
			this.sessionIdFieldName = getConfigs().getString(config, "session_id_field");
			this.vpnGeoHoppingCloseSessionThresholdInHours = getConfigs().getInt(config, "geo_hopping_close_session_threshold");
			this.vpnGeoHoppingOpenSessionThresholdInHours = getConfigs().getInt(config, "geo_hopping_open_session_threshold");
			this.runGeoHopping = getConfigs().getBoolean(config, "run_geo_hopping", true);
			this.addSessionDataFieldName = getConfigs().getString(config, "add_session_data");
			
			validateArguments();
		}
		
		

		@Override
		protected boolean doProcess(Record inputRecord) {
			if(vpnService == null){
				logger.warn("vpnService is null while processing morphline command {}. probably the spring configuration context was not loaded", VpnSessionUpdate.class);
				return super.doProcess(inputRecord);
			}

			VpnSession vpnSession = recordToVpnSessionConverter.convert(inputRecord, countryIsoCodeFieldName, longtitudeFieldName, latitudeFieldName, sessionIdFieldName);

			// check if failed event
			if(vpnSession.getClosedAt() == null && vpnSession.getCreatedAt() == null){
				//right now we don't use fail status for updating vpn session. There is a JIRA for this (FV-4413).
				return super.doProcess(inputRecord);
			}

			// validate fields: session-ID or (Normalize-username and source-IP)
			if (StringUtils.isEmpty(vpnSession.getSessionId()) && (StringUtils.isEmpty(vpnSession.getNormalizeUsername()) || StringUtils.isEmpty(vpnSession.getSourceIp()))) {
				logger.warn("vpnSession should have either sessionId or username and sourceIP. Original record is: {}", inputRecord.toString());
				return super.doProcess(inputRecord);
			}

			/**
			 * when <code>addSessionData</code> is false: if there is a close session event without an open event we drop this session
			 * if true: we can create a session without the stat session event as we have all attributes in the close session event.
			 */
			Boolean isAddSessionData = RecordExtensions.getBooleanValue(inputRecord, addSessionDataFieldName);
			if(vpnSession.getClosedAt() != null && isAddSessionData){
				VpnSession vpnOpenSession = getOpenSessionDataToRecord(vpnSession);
				if(vpnOpenSession == null){
					logger.debug("got close vpn session for non existing or failed session");
					return true;
				} else{
					addOpenSessionDataToRecord(inputRecord, vpnOpenSession);
				}
			}
			
			if(runGeoHopping){
				processGeoHopping(vpnSession);
			}
			
			if(vpnSession.getCreatedAt() != null){
				vpnService.createOrUpdateOpenVpnSession(vpnSession);
			} else{
				vpnService.updateCloseVpnSession(vpnSession);
			}
			
			return super.doProcess(inputRecord);

		}
		
		private VpnSession getOpenSessionDataToRecord(VpnSession closeVpnSessionData){
			VpnSession vpnOpenSession = null;
			if(closeVpnSessionData.getSessionId() != null){
				vpnOpenSession = vpnService.findBySessionId(closeVpnSessionData.getSessionId());
			} else{
				vpnOpenSession = vpnService.findByNormalizeUsernameAndSourceIp(closeVpnSessionData.getNormalizeUsername(), closeVpnSessionData.getSourceIp());
			}
			return vpnOpenSession;
		}
		
		
		private void addOpenSessionDataToRecord(Record record, VpnSession openVpnSessionData){
			if(record.get(vpnEvents.NORMALIZED_USERNAME).isEmpty()){
				record.put(vpnEvents.NORMALIZED_USERNAME, openVpnSessionData.getNormalizeUsername());
			}
			if(record.get(vpnEvents.USERNAME).isEmpty()){
				record.put(vpnEvents.USERNAME, openVpnSessionData.getUsername());
			}
			if(record.get(vpnEvents.HOSTNAME).isEmpty()){
				record.put(vpnEvents.HOSTNAME, openVpnSessionData.getHostname());
			}
			if(record.get(vpnEvents.SOURCE_IP).isEmpty()){
				record.put(vpnEvents.SOURCE_IP, openVpnSessionData.getSourceIp());
				record.put(vpnEvents.CITY, openVpnSessionData.getCity());
				record.put(vpnEvents.COUNTRY, openVpnSessionData.getCountry());
				record.put(countryIsoCodeFieldName, openVpnSessionData.getCountryIsoCode());
				record.put(vpnEvents.ISP, openVpnSessionData.getIsp());
				record.put(vpnEvents.IPUSAGE, openVpnSessionData.getIspUsage());
				record.put(vpnEvents.REGION, openVpnSessionData.getRegion());
				record.put(longtitudeFieldName, openVpnSessionData.getLongtitude());
				record.put(latitudeFieldName, openVpnSessionData.getLatitude());
			}
			if(record.get(vpnEvents.LOCAL_IP).isEmpty()){
				record.put(vpnEvents.LOCAL_IP, openVpnSessionData.getLocalIp());
			}
		}
		
		private void processGeoHopping(VpnSession curVpnSession){
			if(curVpnSession.getClosedAt() == null){
				List<VpnSession> vpnSessions = vpnService.getGeoHoppingVpnSessions(curVpnSession, vpnGeoHoppingCloseSessionThresholdInHours, vpnGeoHoppingOpenSessionThresholdInHours);;
				if(curVpnSession.getGeoHopping()){
					List<VpnSession> notificationList = new ArrayList<>();
					notificationList.add(curVpnSession);
					for(VpnSession vpnSession: vpnSessions){
						if(!vpnSession.getGeoHopping()){
							vpnSession.setGeoHopping(true);
							vpnService.saveVpnSession(vpnSession);
							notificationList.add(vpnSession);
						}
					}
					
					//create notifications for the vpn sessions
					vpnGeoHoppingNotificationGenerator.createNotifications(notificationList);
				}
				
				
			}
		}
	}
}

