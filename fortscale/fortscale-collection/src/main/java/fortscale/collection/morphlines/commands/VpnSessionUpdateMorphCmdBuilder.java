package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;

import org.joda.time.DateTime;
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

import fortscale.collection.morphlines.RecordExtensions;
import fortscale.domain.events.VpnSession;
import fortscale.domain.schema.VpnEvents;
import fortscale.services.event.VpnService;



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
		private VpnService vpnService;
		
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
			
			VpnSession vpnSession = new VpnSession();
			
			String status = RecordExtensions.getStringValue(inputRecord, vpnEvents.STATUS);
			boolean isFailed = false;
			switch(status){
			case "CLOSED":
				vpnSession.setClosedAtEpoch(RecordExtensions.getLongValue(inputRecord, vpnEvents.DATE_TIME_UNIX));
				vpnSession.setClosedAt(new DateTime(vpnSession.getClosedAtEpoch()));
				break;
			case "SUCCESS":
				vpnSession.setCreatedAtEpoch(RecordExtensions.getLongValue(inputRecord, vpnEvents.DATE_TIME_UNIX));
				vpnSession.setCreatedAt(new DateTime(vpnSession.getCreatedAtEpoch()));
				break;
			default:
				isFailed = true;
			}
			
			if(!isFailed){
				vpnSession.setCity(RecordExtensions.getStringValue(inputRecord, vpnEvents.CITY, null));
				vpnSession.setCountry(RecordExtensions.getStringValue(inputRecord, vpnEvents.COUNTRY, null));
				vpnSession.setCountryIsoCode(RecordExtensions.getStringValue(inputRecord, countryIsoCodeFieldName, null));
				vpnSession.setDataBucket(RecordExtensions.getIntegerValue(inputRecord, vpnEvents.DATA_BUCKET, null));
				vpnSession.setDuration(RecordExtensions.getIntegerValue(inputRecord, vpnEvents.DURATION, null));
				vpnSession.setHostname(RecordExtensions.getStringValue(inputRecord, vpnEvents.HOSTNAME, null));
				vpnSession.setIsp(RecordExtensions.getStringValue(inputRecord, vpnEvents.ISP, null));
				vpnSession.setIspUsage(RecordExtensions.getStringValue(inputRecord, vpnEvents.IPUSAGE, null));
				vpnSession.setLocalIp(RecordExtensions.getStringValue(inputRecord, vpnEvents.LOCAL_IP, null));
				vpnSession.setNormalizeUsername(RecordExtensions.getStringValue(inputRecord, vpnEvents.NORMALIZED_USERNAME));
				vpnSession.setReadBytes(RecordExtensions.getLongValue(inputRecord, vpnEvents.READ_BYTES, null));
				vpnSession.setRegion(RecordExtensions.getStringValue(inputRecord, vpnEvents.REGION, null));
				vpnSession.setSourceIp(RecordExtensions.getStringValue(inputRecord, vpnEvents.SOURCE_IP));
				vpnSession.setTotalBytes(RecordExtensions.getLongValue(inputRecord, vpnEvents.TOTAL_BYTES, null));
				vpnSession.setUsername(RecordExtensions.getStringValue(inputRecord, vpnEvents.USERNAME, null));
				vpnSession.setWriteBytes(RecordExtensions.getLongValue(inputRecord, vpnEvents.WRITE_BYTES, null));
				vpnSession.setLatitude(RecordExtensions.getDoubleValue(inputRecord,latitudeFieldName, null));
				vpnSession.setLongtitude(RecordExtensions.getDoubleValue(inputRecord,longtitudeFieldName, null));
			}
			
			if(status.equals("SUCCESS")){
				vpnService.createOrUpdateOpenVpnSession(vpnSession);
			} else{
				vpnService.updateCloseVpnSession(vpnSession);
			}
			
			return super.doProcess(inputRecord);

		}
	}
}