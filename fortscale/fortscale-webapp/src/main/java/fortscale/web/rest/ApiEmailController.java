package fortscale.web.rest;

import fortscale.services.EmailService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/email")
public class ApiEmailController extends BaseController {

    private static Logger logger = Logger.getLogger(ApiEmailController.class);

    private static final String TO_PARAM = "to";

    @Autowired
    private EmailService emailService;

    /**
     * This method sends a test email to the recipient in the body request
     *
     * @return
     */
    @LogException
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity sendTestEmail(@RequestParam(value = TO_PARAM) String to) {
        try {
            logger.info("Attempting to send test email");
            emailService.loadEmailConfiguration();
            if (!emailService.isEmailConfigured()) {
                logger.info("Email server not configured");
                return ResponseEntity.badRequest().body("{ \"message\": \"Email server not configured\"}");
            }
            boolean success = emailService.sendEmail(new String[]{to}, null, null, "Test Email",
                    "This is a test email", null, true);
            if (success) {
                logger.info("Test email sent");
                return ResponseEntity.ok().body("{ \"message\": \"Email sent successfully\" }");
            }
            return ResponseEntity.badRequest().body("{ \"message\": \"Error sending email\" }");
        } catch (Exception ex) {
            logger.error("Encountered error while trying to send test email - " + ex);
            return ResponseEntity.badRequest().body("{ \"message\": \"Error sending email. " + ex.getLocalizedMessage() + " \" }");
        }
    }

}