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
 * Created by Amir Keren on 13/07/2016.
 *
 * Supporting information for VPN Lateral Movement
 */
@JsonTypeName("vpnGeoHoppingSupportingInformation")
public class VpnLateralMovementSupportingInformation extends NotificationSupportingInformation {

	private static Logger logger = Logger.getLogger(VpnLateralMovementSupportingInformation.class);

	public static final String VPN_SESSION_EVENTS = "vpn_session_events";
	public static final String USER_ACTIVITY_EVENTS = "user_activity_events";

	private List<VpnSession> rawEvents;

	public VpnLateralMovementSupportingInformation() {}

	@Override
	public void setData(String json, boolean isBDPRunning) {
		ObjectMapper mapper = new ObjectMapper();
		if (isBDPRunning) { //we get two different kinds of jsons, need to deserialize them differently
			mapper.registerModule(new JodaModule());
		}
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			VPNLateralMovementDTO vpnLateralMovementDTO = mapper.readValue(json,
					VPNLateralMovementDTO.class);
			this.rawEvents = vpnLateralMovementDTO.getRawEvents();
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

	/**
	 * Internal DTO for marshal / unmarshal JSON
	 */
	public static class VPNLateralMovementDTO {

		private List<VpnSession> rawEvents;
		private List<VpnSession> userActivity;

		public VPNLateralMovementDTO() {}

		public VPNLateralMovementDTO(List<VpnSession> rawEvents, List<VpnSession> userActivity) {
			this.rawEvents = rawEvents;
			this.userActivity = userActivity;
		}

		public List<VpnSession> getRawEvents() {
			return rawEvents;
		}

		public void setRawEvents(List<VpnSession> rawEvents) {
			this.rawEvents = rawEvents;
		}

		public List<VpnSession> getUserActivity() {
			return userActivity;
		}

		public void setUserActivity(List<VpnSession> userActivity) {
			this.userActivity = userActivity;
		}

	}

}