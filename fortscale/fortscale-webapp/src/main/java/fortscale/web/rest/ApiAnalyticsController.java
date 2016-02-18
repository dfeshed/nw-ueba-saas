package fortscale.web.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
class Analytic {

    private String eventType;
    private String computerId;
    private String tabId;
    private String stateName;
    private long timeStamp;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getComputerId() {
        return computerId;
    }

    public void setComputerId(String computerId) {
        this.computerId = computerId;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    Analytic () {}

    Analytic (Analytic analytic) {
        this.eventType = analytic.eventType;
        this.computerId = analytic.computerId;
        this.tabId = analytic.tabId;
        this.stateName = analytic.stateName;
        this.timeStamp = analytic.timeStamp;
    }
    Analytic (
            @JsonProperty("eventType") String eventType,
            @JsonProperty("computerId") String computerId,
            @JsonProperty("tabId") String tabId,
            @JsonProperty("stateName") String stateName,
            @JsonProperty("timeStamp") long timeStamp) {
        this.eventType = eventType;
        this.computerId = computerId;
        this.tabId = tabId;
        this.stateName = stateName;
        this.timeStamp = timeStamp;
    }
}

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
class Analytics {
    private List<Analytic> events;

    Analytics () {}

    Analytics (Analytics analytics) {
        this.events = analytics.events;
    }

    Analytics (@JsonProperty("events") List<Analytic> events) {
        this.events = events;
    }

}

@Controller
@RequestMapping("/api/analytics")
public class ApiAnalyticsController  extends BaseController {
    private static Logger logger = Logger.getLogger(ApiUserController.class);

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

        ObjectMapper mapper = new ObjectMapper();

        try {
            Analytics analytics = mapper.readValue(body, Analytics.class);
        } catch (IOException e) {
            logger.error("ObjectMapper failed to parse JSON", e);
        }

        ResponseEntity<String> responseEntity = new ResponseEntity<>("{}", HttpStatus.ACCEPTED);

        return responseEntity;    }
}
