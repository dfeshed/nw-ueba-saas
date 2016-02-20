package fortscale.web.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.AlertStatus;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.exceptions.InvalidParameterException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
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
class AnalyticEvent {
    private long id;
    private String eventType;
    private String computerId;
    private String tabId;
    private String stateName;
    private long timeStamp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    protected AnalyticEvent() {}

    protected AnalyticEvent(AnalyticEvent analyticEvent) {
        this.id = analyticEvent.id;
        this.eventType = analyticEvent.eventType;
        this.computerId = analyticEvent.computerId;
        this.tabId = analyticEvent.tabId;
        this.stateName = analyticEvent.stateName;
        this.timeStamp = analyticEvent.timeStamp;
    }
    protected AnalyticEvent(
            @JsonProperty("id") long id,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("computerId") String computerId,
            @JsonProperty("tabId") String tabId,
            @JsonProperty("stateName") String stateName,
            @JsonProperty("timeStamp") long timeStamp) {
        this.id = id;
        this.eventType = eventType;
        this.computerId = computerId;
        this.tabId = tabId;
        this.stateName = stateName;
        this.timeStamp = timeStamp;
    }
}

class AnalyticClickEvent extends AnalyticEvent {
    private String elementSelector;

    public String getElementSelector() {
        return elementSelector;
    }

    public void setElementSelector(String elementSelector) {
        this.elementSelector = elementSelector;
    }

    public AnalyticClickEvent () {}

    public AnalyticClickEvent (AnalyticClickEvent analyticClickEvent) {
        super(analyticClickEvent);
        this.elementSelector = analyticClickEvent.elementSelector;

    }

    public AnalyticClickEvent (
            @JsonProperty("id") long id,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("computerId") String computerId,
            @JsonProperty("tabId") String tabId,
            @JsonProperty("stateName") String stateName,
            @JsonProperty("timeStamp") long timeStamp,
            @JsonProperty("elementSelector") String elementSelector) {
        super(id, eventType, computerId, tabId, stateName, timeStamp);
        this.elementSelector = elementSelector;
    }
}

class AnalyticStateChangeEvent extends AnalyticEvent {
    private String toState;

    public String getElementSelector() {
        return toState;
    }

    public void setElementSelector(String elementSelector) {
        this.toState = elementSelector;
    }

    public AnalyticStateChangeEvent() {
    }

    public AnalyticStateChangeEvent(AnalyticStateChangeEvent analyticStateChangeEvent) {
        super(analyticStateChangeEvent);
        this.toState = analyticStateChangeEvent.toState;

    }

    public AnalyticStateChangeEvent(
            @JsonProperty("id") long id,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("computerId") String computerId,
            @JsonProperty("tabId") String tabId,
            @JsonProperty("stateName") String stateName,
            @JsonProperty("timeStamp") long timeStamp,
            @JsonProperty("toState") String toState) {
        super(id, eventType, computerId, tabId, stateName, timeStamp);
        this.toState = toState;
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
    public ResponseEntity storeAnalytics (@RequestBody String body) throws InvalidParameterException {

        JSONObject params = new JSONObject(body);
        // Validations
        if (! params.has("events")) {
            throw new InvalidParameterException("POST body must have an \"events\" property.");
        }
        JSONArray analyticEventsStrings;
        try {
            analyticEventsStrings = params.getJSONArray("events");
        } catch (JSONException err) {
            throw new InvalidParameterException("POST body's \"events\" property must be a valid array.");
        }

        ObjectMapper mapper = new ObjectMapper();
        List<AnalyticEvent> analyticEvents = new ArrayList<>();

            for (int i=0; i<analyticEventsStrings.length(); i+=1) {
                JSONObject obj = analyticEventsStrings.getJSONObject(i);
                String eventType = obj.getString("eventType");

                switch (eventType) {
                    case "click":
                        try {
                            AnalyticClickEvent analyticClickEvent =
                                    mapper.readValue(obj.toString(), AnalyticClickEvent.class);
                            analyticEvents.add(analyticClickEvent);
                        } catch (IOException e) {
                            logger.error("ObjectMapper failed to parse JSON", e);
                        }
                        break;
                    case "stateChange":
                        try {
                            AnalyticStateChangeEvent analyticStateChangeEvent =
                                    mapper.readValue(obj.toString(), AnalyticStateChangeEvent.class);
                            analyticEvents.add(analyticStateChangeEvent);
                        } catch (IOException e) {
                            logger.error("ObjectMapper failed to parse JSON", e);
                        }
                        break;
                }

            }

        ResponseEntity<String> responseEntity = new ResponseEntity<>("{}", HttpStatus.ACCEPTED);

        return responseEntity;
    }
}
