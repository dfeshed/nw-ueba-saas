package fortscale.web.rest;

import fortscale.services.ForwardingService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.DataQueryController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller @RequestMapping("/api/syslogforwarding") public class ApiSyslogForwardingController
		extends DataQueryController {

	private static Logger logger = Logger.getLogger(ApiSyslogForwardingController.class);

	/**
	 * Alert forwarding service (for forwarding new alerts)
	 */
	@Autowired private ForwardingService forwardingService;

	@RequestMapping(value = "/forward_alerts", method = RequestMethod.GET) @LogException public @ResponseBody
	ResponseEntity forwardAlert(@RequestParam String ip, @RequestParam int port, @RequestParam String forwardingType,
			@RequestParam String sendingMethod, @RequestParam String[] userTags, @RequestParam String[] alertSeverity,
			@RequestParam long startTime, @RequestParam long endTime) {
		try {
			int numberOfForwardedAlerts = forwardingService.forwardAlertsByTimeRange(ip, port, forwardingType,
					sendingMethod, userTags, alertSeverity, startTime, endTime);
			return ResponseEntity.ok().body("{ \"message\": \"" + "Forward " + numberOfForwardedAlerts + " Alerts\"}");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("{ \"message\": \"" + "Error forwarding alerts.  " + e.getMessage() + " \"}");
		}
	}

}