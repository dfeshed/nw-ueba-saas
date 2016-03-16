package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import fortscale.domain.events.VpnSession;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created by galiar on 22/10/2015.
 *
 * Supporting information for Geo hopping indicator - it's open and close vpn events.
 */
@JsonTypeName("vpnGeoHoppingSupportingInformation")
public class VpnGeoHoppingSupportingInformation extends NotificationSupportingInformation {


	private static Logger logger = LoggerFactory.getLogger(VpnGeoHoppingSupportingInformation.class);

	private List<VpnSession> rawEvents;

	public VpnGeoHoppingSupportingInformation() {}

	@Override
	public void setData(Evidence evidence, String json, boolean isBDPRunning) {

		ObjectMapper mapper = new ObjectMapper();
		if(isBDPRunning) { //we get two different kinds of jsons, need to deserialize them differently
			mapper.registerModule(new JodaModule());
		}
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

	@Override
	public List<Map<String, Object>> generateResult()
	{
		List<Map<String, Object>> resultMapList = new ArrayList<>();

		//TODO - Need to handle better the supporting information of geo hoping
		/*
		//for sotring the list by date time unix
		Comparator<VpnSession> comparator = (c1, c2) -> new Long(c2.getCreatedAtEpoch() - c1.getCreatedAtEpoch()).intValue();
		rawEvents.sort(comparator);

		for (VpnSession vpnSession : rawEvents)
		{
			Map<String, Object> featuresMap = new HashMap<>();
			featuresMap.put("id",vpnSession.getId());
			featuresMap.put("username",vpnSession.getUsername());
			featuresMap.put("sourceIp",vpnSession.getSourceIp());
			featuresMap.put("createdAtEpoch",vpnSession.getCreatedAtEpoch());
			featuresMap.put("localIp",vpnSession.getLocalIp());
			featuresMap.put("normalizedUserName",vpnSession.getNormalizedUserName());
			featuresMap.put("hostname",vpnSession.getHostname());
			featuresMap.put("country",vpnSession.getCountry());
			featuresMap.put("countryIsoCode",vpnSession.getCountryIsoCode());
			featuresMap.put("region",vpnSession.getRegion());
			featuresMap.put("city",vpnSession.getCity());
			featuresMap.put("isp",vpnSession.getIsp());
			featuresMap.put("ispUsage",vpnSession.getIspUsage());
			featuresMap.put("geoHopping",vpnSession.getGeoHopping());

			resultMapList.add(featuresMap);

		}*/

		return resultMapList;
	}


}
