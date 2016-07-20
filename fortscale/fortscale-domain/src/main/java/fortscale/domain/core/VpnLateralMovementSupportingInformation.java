package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import fortscale.domain.core.dao.VpnLateralMovement;
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
@JsonTypeName("VpnLateralMovementSupportingInformation")
public class VpnLateralMovementSupportingInformation extends NotificationSupportingInformation {

	private static Logger logger = Logger.getLogger(VpnLateralMovementSupportingInformation.class);

	public static final String VPN_SESSION_EVENTS = "vpn_session_events";
	public static final String USER_ACTIVITY_EVENTS = "user_activity_events";

	private VPNLateralMovementDTO rawEvents;

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
			this.rawEvents = vpnLateralMovementDTO;
		} catch (IOException ex) {
			logger.error("String is not a valid JSON object {}", ex.getMessage());
		}
	}

	public VPNLateralMovementDTO getRawEvents() {
		return rawEvents;
	}

	public void setRawEvents(VPNLateralMovementDTO rawEvents) {
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

		private List<VpnSessionOverlap> vpn_session_events;
		private List<VpnLateralMovement> user_activity_events;

		public List<VpnSessionOverlap> getVpn_session_events() {
			return vpn_session_events;
		}

		public void setVpn_session_events(List<VpnSessionOverlap> vpn_session_events) {
			this.vpn_session_events = vpn_session_events;
		}

		public List<VpnLateralMovement> getUser_activity_events() {
			return user_activity_events;
		}

		public void setUser_activity_events(List<VpnLateralMovement> user_activity_events) {
			this.user_activity_events = user_activity_events;
		}

	}

}