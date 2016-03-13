package fortscale.web.rest;

import fortscale.services.ForwardingService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.DataQueryController;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller @RequestMapping("/api/syslogforwarding") public class ApiSyslogForwardingController
		extends DataQueryController {

	private static Logger logger = Logger.getLogger(ApiSyslogForwardingController.class);

	private final static String IP = "ip";
	private final static String PORT = "port";
	private final static String FORWARDING_TYPE = "forwarding_type";
	private final static String USER_TAGS = "user_tags";
	private final static String ALERT_SEVERITY = "alert_severities";
	private final static String START_TIME = "start_time";
	private final static String END_TIME = "end_time";

	/**
	 * Alert forwarding service (for forwarding new alerts)
	 */
	@Autowired private ForwardingService forwardingService;

	@RequestMapping(value = "/forward_alerts", method = RequestMethod.POST) @LogException public @ResponseBody
	ResponseEntity forwardAlert(@RequestBody String body) {
		try {
			JSONObject params = new JSONObject(body);
			String ip = params.getString(IP);
			int port = params.getInt(PORT);
			String forwardingType = params.getString(FORWARDING_TYPE);
			String[] userTags = jsonArrayToStringArray(params.getJSONArray(USER_TAGS));
			String[] alertSeverity = jsonArrayToStringArray(params.getJSONArray(ALERT_SEVERITY));
			long startTime = params.getLong(START_TIME);
			long endTime = params.getLong(END_TIME);
			int numberOfForwardedAlerts = forwardingService.forwardAlertsByTimeRange(ip, port, forwardingType,
					userTags, alertSeverity, startTime, endTime);
			return ResponseEntity.ok().body("{ \"message\": \"" + "Forward " + numberOfForwardedAlerts + " Alerts\"}");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("{ \"message\": \"" + "Error forwarding alerts.  " + e.getMessage() + " \"}");
		}
	}

	private String[] jsonArrayToStringArray(JSONArray array) {
		List<String> list = new ArrayList<>();

		for (int i = 0; i < array.length(); i++) {
			list.add(array.getString(i));
		}

		return list.toArray(new String[list.size()]);
	}

}