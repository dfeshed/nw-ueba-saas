package fortscale.collection.morphlines.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
		private RecordToVpnSessionConverter recordToVpnSessionConverter;
		@Autowired
		private VpnService vpnService;
		@Autowired
		private VpnGeoHoppingNotificationGenerator vpnGeoHoppingNotificationGenerator;
		
		private final String countryIsoCodeFieldName;
		private final String longtitudeFieldName;
		private final String latitudeFieldName;
		

		public VpnSessionUpdate(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.longtitudeFieldName = getConfigs().getString(config, "longtitude_field");
			this.latitudeFieldName = getConfigs().getString(config, "latitude_field");
			this.countryIsoCodeFieldName = getConfigs().getString(config, "country_code_field");
			
			validateArguments();
		}
		
		

		@Override
		protected boolean doProcess(Record inputRecord) {
			if(vpnService == null){
				logger.warn("vpnService is null while processing morphline command {}. probably the spring configuration context was not loaded", VpnSessionUpdate.class);
				return super.doProcess(inputRecord);
			}
			
			VpnSession vpnSession = recordToVpnSessionConverter.convert(inputRecord, countryIsoCodeFieldName, longtitudeFieldName, latitudeFieldName);
			
			processGeoHopping(vpnSession);
			
			if(vpnSession.getCreatedAt() != null){
				vpnService.createOrUpdateOpenVpnSession(vpnSession);
			} else{
				vpnService.updateCloseVpnSession(vpnSession);
			}
			
			return super.doProcess(inputRecord);

		}
		
		private void processGeoHopping(VpnSession curVpnSession){
			if(curVpnSession.getClosedAt() == null){
				List<VpnSession> vpnSessions = vpnService.getGeoHoppingVpnSessions(curVpnSession);
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

