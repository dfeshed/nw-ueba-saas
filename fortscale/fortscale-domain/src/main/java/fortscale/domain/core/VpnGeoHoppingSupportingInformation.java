package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import fortscale.domain.events.VpnSession;
import org.springframework.beans.factory.annotation.Value;
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

	private GeoHoppingSupportingInformation geoHoppingSupportingInformation;

	public VpnGeoHoppingSupportingInformation() {}

	@Override
	public void setData(Evidence evidence, String json, boolean isBDPRunning) {

		ObjectMapper mapper = new ObjectMapper();
		if(isBDPRunning) { //we get two different kinds of jsons, need to deserialize them differently
			mapper.registerModule(new JodaModule());
		}


		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		try {
			geoHoppingSupportingInformation = mapper.readValue(json, new TypeReference<GeoHoppingSupportingInformation>(){});


		} catch (IOException ex) {
			logger.error("String is not a valid JSON object {}", ex.getMessage());
		}
	}

	public List<VpnSession> getRawEvents() {
		return geoHoppingSupportingInformation.rawEvents;
	}

	public void setRawEvents(List<VpnSession> rawEvents) {
		if (rawEvents == null) {
			geoHoppingSupportingInformation.setRawEvents(rawEvents);
		}
	}


	public static class GeoHoppingSupportingInformation{
		private List<VpnSession> rawEvents;
		private int pairInstancesPerUser;
		private int pairInstancesGlobalUser;
		private int maximumGlobalSingleCity;

		public GeoHoppingSupportingInformation() {

		}

		public GeoHoppingSupportingInformation(List<VpnSession> rawEvents, int pairInstancesPerUser,
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
