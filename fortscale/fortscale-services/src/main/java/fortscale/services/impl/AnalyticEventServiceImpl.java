package fortscale.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.AnalyticClickEvent;
import fortscale.domain.core.AnalyticErrorEvent;
import fortscale.domain.core.AnalyticEvent;
import fortscale.domain.core.AnalyticStateChangeEvent;
import fortscale.services.AnalyticEventService;
import fortscale.utils.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;


@Service("analyticEventService")
public class AnalyticEventServiceImpl implements AnalyticEventService {

    private static Logger logger = Logger.getLogger(AnalyticEventServiceImpl.class);


    private JSONObject convertBodyStringToJsonObject(String body) {
        JSONObject params = null;
        try {
            params = new JSONObject(body);
        } catch (JSONException e) {
            throw new InvalidParameterException("POST body must have a valid json string.");
        }

        // Validations
        if (! params.has("events")) {
            throw new InvalidParameterException("POST body must have an \"events\" property.");
        }

        return params;
    }

    private JSONArray extractJSONArrayFromParamsObject(JSONObject params) {
        JSONArray analyticEventsStrings;
        // Get JSON Array of analytic events
        try {
            analyticEventsStrings = params.getJSONArray("events");
        } catch (JSONException err) {
            throw new InvalidParameterException("POST body's \"events\" property must be a valid array.");
        }

        return analyticEventsStrings;
    }

    private String getAnalyticType (JSONObject analyticEventJSON, int i) {

        String eventType = null;
        try {
            eventType = analyticEventJSON.getString("eventType");
        } catch (JSONException e) {
            throw new InvalidParameterException("Analytic event index: " + i +
                    " does not have the required \"eventType\" property.");
        }
        return eventType;
    }

    private <T extends AnalyticEvent> void addAnalyticEventToList (ObjectMapper mapper,
            List<AnalyticEvent> analyticEvents, JSONObject obj, Class<T> aClass, int index) {
        T t = null;
        try {
            t = mapper.readValue(obj.toString(), aClass);
            analyticEvents.add(t);
        } catch (IOException e) {
            throw new InvalidParameterException("Could not convert analytic event index:" + index +
                    " to AnalyticClickEvent. " + e.getMessage());
        }
    }

    private List<AnalyticEvent> populateAnalyticEventsList (JSONArray analyticEventsStrings) {
        ObjectMapper mapper = new ObjectMapper();
        List<AnalyticEvent> analyticEvents = new ArrayList<>();

        for (int i=0; i<analyticEventsStrings.length(); i+=1) {
            JSONObject obj = null;
            try {
                obj = analyticEventsStrings.getJSONObject(i);
            } catch (JSONException e) {
                throw new InvalidParameterException("Event id " + i + " is not a valid JSON object.");
            }

            String eventType = getAnalyticType(obj, i);

            switch (eventType) {
                case "click":
                    addAnalyticEventToList(mapper, analyticEvents, obj, AnalyticClickEvent.class, i);
                    break;
                case "stateChange":
                    addAnalyticEventToList(mapper, analyticEvents, obj, AnalyticStateChangeEvent.class, i);
                    break;
                case "error":
                    addAnalyticEventToList(mapper, analyticEvents, obj, AnalyticErrorEvent.class, i);
                    break;
            }

        }

        return analyticEvents;
    }

    @Override
    public void addAnalyticEvents(String body) throws InvalidParameterException {

        JSONObject params = convertBodyStringToJsonObject(body);

        JSONArray analyticEventsStrings = extractJSONArrayFromParamsObject(params);

        List<AnalyticEvent> analyticEvents = populateAnalyticEventsList(analyticEventsStrings);

        analyticEvents = analyticEvents;
    }

}
