package fortscale.streaming.service.vpn;

import fortscale.domain.events.VpnSession;
import fortscale.domain.schema.VpnEvents;
import fortscale.utils.TimestampUtils;
import net.minidev.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fortscale.utils.ConversionUtils.*;

@Component
public class RecordToVpnSessionConverter {
	
	@Autowired
	private VpnEvents vpnEvents;
	
	

	public VpnSession convert(JSONObject event, String countryIsoCodeFieldName, String longtitudeFieldName, String latitudeFieldName, String sessionIdFieldName){
		String status = convertToString(event.get(vpnEvents.STATUS));
//		String status = RecordExtensions.getStringValue(event, vpnEvents.STATUS);
		boolean isFailed = false;
		Long epochtime = convertToLong(event.get(vpnEvents.DATE_TIME_UNIX));
//		Long epochtime = RecordExtensions.getLongValue(event, vpnEvents.DATE_TIME_UNIX);
		epochtime = TimestampUtils.convertToMilliSeconds(epochtime);
		VpnSession vpnSession = new VpnSession();
		switch(status){
		case "CLOSED":
			vpnSession.setClosedAtEpoch(epochtime);
			vpnSession.setClosedAt(new DateTime(epochtime, DateTimeZone.UTC ));
			break;
		case "SUCCESS":
			vpnSession.setCreatedAtEpoch(epochtime);
			vpnSession.setCreatedAt(new DateTime(epochtime, DateTimeZone.UTC));
			break;
		default:
			isFailed = true;
		}
		
		if(!isFailed){
			vpnSession.setCity(convertToString(event.get(vpnEvents.CITY)));
			vpnSession.setCountry(convertToString(event.get(vpnEvents.COUNTRY)));
			vpnSession.setCountryIsoCode(convertToString(event.get(countryIsoCodeFieldName)));
			vpnSession.setDataBucket(convertToInteger(event.get(vpnEvents.DATA_BUCKET)));
			vpnSession.setDuration(convertToInteger(event.get(vpnEvents.DURATION)));
			vpnSession.setHostname(convertToString(event.get(vpnEvents.HOSTNAME)));
			vpnSession.setIsp(convertToString(event.get(vpnEvents.ISP)));
			vpnSession.setIspUsage(convertToString(event.get(vpnEvents.IPUSAGE)));
			vpnSession.setLocalIp(convertToString(event.get(vpnEvents.LOCAL_IP)));
			vpnSession.setNormalizeUsername(convertToString(event.get(vpnEvents.NORMALIZED_USERNAME)));
			vpnSession.setReadBytes(convertToLong(event.get(vpnEvents.READ_BYTES)));
			vpnSession.setRegion(convertToString(event.get(vpnEvents.REGION)));
			vpnSession.setSourceIp(convertToString(event.get(vpnEvents.SOURCE_IP)));
			vpnSession.setTotalBytes(convertToLong(event.get(vpnEvents.TOTAL_BYTES)));
			vpnSession.setUsername(convertToString(event.get(vpnEvents.USERNAME)));
			vpnSession.setWriteBytes(convertToLong(event.get(vpnEvents.WRITE_BYTES)));
			vpnSession.setLatitude(convertToDouble(event.get(latitudeFieldName)));
			vpnSession.setLongtitude(convertToDouble(event.get(longtitudeFieldName)));
			vpnSession.setSessionId(convertToString(event.get(sessionIdFieldName)));
		}
		
		return vpnSession;
	}
}
