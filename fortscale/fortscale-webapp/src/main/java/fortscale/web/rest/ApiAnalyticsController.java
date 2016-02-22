package fortscale.web.rest;

import fortscale.services.impl.AnalyticEventServiceImpl;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/api/analytics")
public class ApiAnalyticsController  extends BaseController {

    private static Logger logger = Logger.getLogger(ApiUserController.class);

    @Autowired
    private AnalyticEventServiceImpl analyticEventService;

    /**
     * The API to get all users. GET: /api/analytics
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @LogException
    public ResponseEntity getAnalytics() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("{}", HttpStatus.OK);

        return responseEntity;
    }

    /**
     * The API to get all users. GET: /api/analytics
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @LogException
    public ResponseEntity storeAnalytics (@RequestBody String body) {


        ResponseEntity<String> responseEntity;
        try {
            analyticEventService.insertAnalyticEvents(body);
            responseEntity = new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception ex) {
            responseEntity = new ResponseEntity<>("{ \"message\": \"" + ex.getMessage() + "\" }",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }
}
