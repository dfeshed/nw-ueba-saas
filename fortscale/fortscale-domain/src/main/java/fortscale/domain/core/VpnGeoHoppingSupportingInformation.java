package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import fortscale.domain.events.VpnSession;
import fortscale.utils.logging.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by galiar on 22/10/2015.
 *
 * Supporting information for Geo hopping indicator - it's open and close vpn events.
 */
@JsonTypeName("vpnGeoHoppingSupportingInformation")
public class VpnGeoHoppingSupportingInformation extends NotificationSupportingInformation {

	private static Logger logger = Logger.getLogger(VpnGeoHoppingSupportingInformation.class);

	private List<VpnSession> rawEvents;
	private Integer pairInstancesPerUser;
	private Integer pairInstancesGlobalUser;
	private Integer  maximumGlobalSingleCity;

	public VpnGeoHoppingSupportingInformation() {}

	@Override
	public void setData(String json, boolean isBDPRunning) {
		ObjectMapper mapper = new ObjectMapper();
		if(isBDPRunning) { //we get two different kinds of jsons, need to deserialize them differently
			mapper.registerModule(new JodaModule());
		}
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			VpnGeoHoppingSupportingInformationDTO geoHoppingSupportingInformation = mapper.readValue(json,
					VpnGeoHoppingSupportingInformationDTO.class);
			this.rawEvents = geoHoppingSupportingInformation.getRawEvents();
			this.pairInstancesPerUser = geoHoppingSupportingInformation.getPairInstancesPerUser();
			this.pairInstancesGlobalUser = geoHoppingSupportingInformation.getPairInstancesGlobalUser();
			this.maximumGlobalSingleCity = geoHoppingSupportingInformation.getMaximumGlobalSingleCity();
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
	public List<Map<String, Object>> generateResult() {
		List<Map<String, Object>> resultMapList = new ArrayList<>();
		return resultMapList;
	}

	public Integer getPairInstancesPerUser() {
		return pairInstancesPerUser;
	}

	public void setPairInstancesPerUser(Integer pairInstancesPerUser) {
		this.pairInstancesPerUser = pairInstancesPerUser;
	}

	public Integer getPairInstancesGlobalUser() {
		return pairInstancesGlobalUser;
	}

	public void setPairInstancesGlobalUser(Integer pairInstancesGlobalUser) {
		this.pairInstancesGlobalUser = pairInstancesGlobalUser;
	}

	public Integer getMaximumGlobalSingleCity() {
		return maximumGlobalSingleCity;
	}

	public void setMaximumGlobalSingleCity(Integer maximumGlobalSingleCity) {
		this.maximumGlobalSingleCity = maximumGlobalSingleCity;
	}

	/**
	 * Internal DTO for marshal / unmarshal JSON
	 */
	public static class VpnGeoHoppingSupportingInformationDTO {

		private List<VpnSession> rawEvents;
		private int pairInstancesPerUser;
		private int pairInstancesGlobalUser;
		private int  maximumGlobalSingleCity;

		public VpnGeoHoppingSupportingInformationDTO() {}

		public VpnGeoHoppingSupportingInformationDTO(List<VpnSession> rawEvents, int pairInstancesPerUser,
				int pairInstancesGlobalUser, int maximumGlobalSingleCity) {
			this.rawEvents = rawEvents;
			this.pairInstancesPerUser = pairInstancesPerUser;
			this.pairInstancesGlobalUser = pairInstancesGlobalUser;
			this.maximumGlobalSingleCity = maximumGlobalSingleCity;
		}

		public List<VpnSession> getRawEvents() {
			return rawEvents;
		}

		public void setRawEvents(List<VpnSession> rawEvents) {
			this.rawEvents = rawEvents;
		}

		public int getPairInstancesPerUser() {
			return pairInstancesPerUser;
		}

		public void setPairInstancesPerUser(int pairInstancesPerUser) {
			this.pairInstancesPerUser = pairInstancesPerUser;
		}

		public int getPairInstancesGlobalUser() {
			return pairInstancesGlobalUser;
		}

		public void setPairInstancesGlobalUser(int pairInstancesGlobalUser) {
			this.pairInstancesGlobalUser = pairInstancesGlobalUser;
		}

		public int getMaximumGlobalSingleCity() {
			return maximumGlobalSingleCity;
		}

		public void setMaximumGlobalSingleCity(int maximumGlobalSingleCity) {
			this.maximumGlobalSingleCity = maximumGlobalSingleCity;
		}

	}

}