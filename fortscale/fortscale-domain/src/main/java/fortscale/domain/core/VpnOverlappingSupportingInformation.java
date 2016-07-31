package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;

import java.io.IOException;
import java.util.*;

/**
 * Created by Amir Keren on 03/09/15.
 */
@JsonTypeName("vpnOverlappingSupportingInformation")
public class VpnOverlappingSupportingInformation extends NotificationSupportingInformation {

    private static Logger logger = Logger.getLogger(VpnOverlappingSupportingInformation.class);

    private List<VpnSessionOverlap> rawEvents;

    public List<VpnSessionOverlap> getRawEvents() {
        return rawEvents;
    }

    public VpnOverlappingSupportingInformation(){}

    @Override
    public void setData(String json, boolean isBDPRunning) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            rawEvents = mapper.readValue(json, new TypeReference<List<VpnSessionOverlap>>(){});
        } catch (IOException ex) {
            logger.error("String is not a valid JSON object {}", ex.getMessage());
        }
    }

	@Override
	public List<Map<String, Object>> generateResult() {
		List<Map<String, Object>> resultMapList = new ArrayList<>();
		//for sotring the list by date time unix
		Comparator<VpnSessionOverlap> comparator = (c1, c2) -> new Long(c1.getDate_time_unix() -
				c2.getDate_time_unix()).intValue();
		rawEvents.sort(comparator);
		for (VpnSessionOverlap vpnSessionOverlap : rawEvents) {
			Map<String, Object> featuresMap = new HashMap<>();
			featuresMap.put("username", vpnSessionOverlap.getUsername());
			featuresMap.put("session_score", vpnSessionOverlap.getEventscore());
			featuresMap.put("start_time", TimestampUtils.convertToMilliSeconds(vpnSessionOverlap.getDate_time_unix() -
					vpnSessionOverlap.getDuration()));
			featuresMap.put("end_time",TimestampUtils.convertToMilliSeconds(vpnSessionOverlap.getDate_time_unix()));
			featuresMap.put("duration",vpnSessionOverlap.getDuration());
			featuresMap.put("source_ip",vpnSessionOverlap.getSource_ip());
			featuresMap.put("local_ip",vpnSessionOverlap.getLocal_ip());
			featuresMap.put("read_bytes",vpnSessionOverlap.getReadbytes());
			featuresMap.put("write_bytes",vpnSessionOverlap.getWritebytes());
			featuresMap.put("data_bucket",vpnSessionOverlap.getDatabucket());
			featuresMap.put("hostname",vpnSessionOverlap.getHostname());
			featuresMap.put("country",vpnSessionOverlap.getCountry());
			resultMapList.add(featuresMap);
		}
		return resultMapList;
	}

    public void setRawEvents(List<VpnSessionOverlap> rawEvents) {
        this.rawEvents = rawEvents;
    }

}