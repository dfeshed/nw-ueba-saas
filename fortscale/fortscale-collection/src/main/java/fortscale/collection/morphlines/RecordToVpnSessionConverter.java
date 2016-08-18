package fortscale.collection.morphlines;

import fortscale.collection.metrics.RecordToStringItemsProcessorMetric;
import fortscale.collection.metrics.RecordToVpnSessionConverterMetric;
import fortscale.domain.events.VpnSession;
import fortscale.domain.schema.VpnEvents;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.time.TimestampUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.kitesdk.morphline.api.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecordToVpnSessionConverter {
	
	@Autowired
	private VpnEvents vpnEvents;

	private RecordToVpnSessionConverterMetric metric;

	public RecordToVpnSessionConverter() {};

	public RecordToVpnSessionConverter(StatsService statsService, String name) {
		initMetricsClass(statsService,name);
	}

	public VpnSession convert(Record inputRecord, String countryIsoCodeFieldName, String longtitudeFieldName, String latitudeFieldName, String sessionIdFieldName){
		if (metric != null) {
			metric.record++;
		}
		String status = RecordExtensions.getStringValue(inputRecord, vpnEvents.STATUS);
		boolean isFailed = false;
		Long epochtime = RecordExtensions.getLongValue(inputRecord, vpnEvents.DATE_TIME_UNIX);
		epochtime = TimestampUtils.convertToMilliSeconds(epochtime);
		VpnSession vpnSession = new VpnSession();
		switch(status){
		case "CLOSED":
			if (metric != null) {
				metric.vpnSessionClosed++;
			}
			vpnSession.setClosedAtEpoch(epochtime);
			vpnSession.setClosedAt(new DateTime(epochtime, DateTimeZone.UTC ));
			break;
		case "SUCCESS":
			if (metric != null) {
				metric.vpnSessionSuccess++;
			}
			vpnSession.setCreatedAtEpoch(epochtime);
			vpnSession.setCreatedAt(new DateTime(epochtime, DateTimeZone.UTC));
			break;
		default:
			if (metric != null) {
				metric.vpnSessionFailed++;
			}
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
			vpnSession.setNormalizedUserName(RecordExtensions.getStringValue(inputRecord, vpnEvents.NORMALIZED_USERNAME, null));
			vpnSession.setReadBytes(RecordExtensions.getLongValue(inputRecord, vpnEvents.READ_BYTES, null));
			vpnSession.setRegion(RecordExtensions.getStringValue(inputRecord, vpnEvents.REGION, null));
			vpnSession.setSourceIp(RecordExtensions.getStringValue(inputRecord, vpnEvents.SOURCE_IP, null));
			vpnSession.setTotalBytes(RecordExtensions.getLongValue(inputRecord, vpnEvents.TOTAL_BYTES, null));
			vpnSession.setUsername(RecordExtensions.getStringValue(inputRecord, vpnEvents.USERNAME, null));
			vpnSession.setWriteBytes(RecordExtensions.getLongValue(inputRecord, vpnEvents.WRITE_BYTES, null));
			vpnSession.setLatitude(RecordExtensions.getDoubleValue(inputRecord,latitudeFieldName, null));
			vpnSession.setLongtitude(RecordExtensions.getDoubleValue(inputRecord,longtitudeFieldName, null));
			vpnSession.setSessionId(RecordExtensions.getStringValue(inputRecord, sessionIdFieldName, null));
		}
		
		return vpnSession;
	}

	public void initMetricsClass(StatsService statsService, String name){
		metric = new RecordToVpnSessionConverterMetric(statsService,name);
	}
}
