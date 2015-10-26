package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.events.VpnSession;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by galiar on 22/10/2015.
 *
 * Supporting information for Geo hopping indicator - it's open and close vpn events.
 */
@JsonTypeName("vpnGeoHoppingSupportingInformation")
public class VpnGeoHoppingSupportingInformation extends NotificationSupportingInformation {

	private static Logger logger = LoggerFactory.getLogger(VpnGeoHoppingSupportingInformation.class);

	private List<VpnSession> rawEvents;

	public VpnGeoHoppingSupportingInformation(){}

	@Override
	public void setData(Evidence evidence, String json) {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			rawEvents = mapper.readValue(json, new TypeReference<List<VpnSession>>(){});
		} catch (IOException ex) {
			logger.error("String is not a valid JSON object {}", ex.getMessage());
		}
	}

	public List<VpnSession> getRawEvents() {
		return rawEvents;
	}

	public void setRawEvents(List<VpnSession> rawEvents) {
		this.rawEvents = rawEvents;
	}


}
