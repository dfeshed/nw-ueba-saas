package fortscale.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.AnalyticClickEvent;
import fortscale.domain.core.AnalyticErrorEvent;
import fortscale.domain.core.AnalyticEvent;
import fortscale.domain.core.AnalyticStateChangeEvent;
import fortscale.domain.core.dao.AnalyticEventsRepositoryImpl;
import fortscale.services.AnalyticEventService;
import fortscale.utils.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;


@Service("analyticEventService")
public class AnalyticEventServiceImpl implements AnalyticEventService {

    private static Logger logger = Logger.getLogger(AnalyticEventServiceImpl.class);

    @Autowired
    private AnalyticEventsRepositoryImpl analyticEventsRepository;

    private static final String BODY_ANALYTICS_EVENT_LIST_NAME = "Events";
    private static final String JSON_ANALYTICS_EVENT_TYPE_NAME = "eventType";

    private static final String ANALYTIC_TYPE_CLICK = "click";
    private static final String ANALYTIC_TYPE_STATE_CHANGE = "stateChange";
    private static final String ANALYTIC_TYPE_ERROR = "error";

    /**
     * Parses the received body into a JSONObject, and validates object has "Events" property
     *
     * @param body the received body from the POST request
     * @return A JSON object that is a list of analytic events
     */
    private JSONObject convertBodyStringToJsonObject(String body) {

        String errorMessageForJSONException = "POST body must have a valid json string.";
        String errorMessageForInvalidParameter = "POST body must have a \"" + BODY_ANALYTICS_EVENT_LIST_NAME +
                "\" property.";

        JSONObject params = null;
        try {
            params = new JSONObject(body);
        } catch (JSONException e) {
            throw new InvalidParameterException(errorMessageForJSONException);
        }

        // Validations
        if (! params.has(BODY_ANALYTICS_EVENT_LIST_NAME)) {
            throw new InvalidParameterException(errorMessageForInvalidParameter);
        }

        return params;
    }

    /**
     * Extracts a JSON list from the params object
     *
     * @param params A JSON object that is the params extracted from the POST body.
     * @return A JSON Array of analytic event items as received on the POST request
     */
    private JSONArray extractJSONArrayFromParamsObject(JSONObject params) {

        String errorMessageForInvalidParameter = "POST body's \"" + BODY_ANALYTICS_EVENT_LIST_NAME +
                "\" property must be a valid json array.";

        JSONArray analyticEventsStrings;
        // Get JSON Array of analytic events
        try {
            analyticEventsStrings = params.getJSONArray(BODY_ANALYTICS_EVENT_LIST_NAME);
        } catch (JSONException err) {
            throw new InvalidParameterException(errorMessageForInvalidParameter);
        }

        return analyticEventsStrings;
    }

    /**
     * Takes a JSON object represnting an analytic event, and returns its type
     *
     * @param analyticEventJSON A JSON representing an analytic event
     * @param i the index of the item. Used for error reporting.
     * @return the type of the analytic event.
     */
    private String getAnalyticType (JSONObject analyticEventJSON, int i) {

        String errorMessageForJSONException = "Analytic event index: " + i +
                " does not have the required \"" + JSON_ANALYTICS_EVENT_TYPE_NAME + "\" property.";

        String eventType = null;
        try {
            eventType = analyticEventJSON.getString(JSON_ANALYTICS_EVENT_TYPE_NAME);
        } catch (JSONException e) {
            throw new InvalidParameterException(errorMessageForJSONException);
        }
        return eventType;
    }

    /**
     * Takes a JSON object, and converts it into an instance of AnalyticEvent sub class. It then adds it to the list.
     *
     * @param mapper A Jackson mapper
     * @param analyticEvents A list of analytic events
     * @param obj A JSON object representing an analytic event
     * @param aClass The specific class of the analytic event
     * @param index The index of the analytic event. Used for error reporting.
     * @param <T> A sub class of AnalyticEvent
     */
    private <T extends AnalyticEvent> void addAnalyticEventToList (ObjectMapper mapper,
            List<AnalyticEvent> analyticEvents, JSONObject obj, Class<T> aClass, int index) {

        String errorMessageForInvalidParameter = "Could not convert analytic event index:" + index +
                " to AnalyticEvent using jackson.\r\n";
        T t = null;
        try {
            t = mapper.readValue(obj.toString(), aClass);
            analyticEvents.add(t);
        } catch (IOException e) {
            throw new InvalidParameterException(errorMessageForInvalidParameter + e.getMessage());
        }
    }

    /**
     * Populates a List of AnalyticEvent from a JSONArray
     * @param analyticEventsStrings A JSON array containing JSON strings representing analytic events
     * @return A list of AnalyticEvent sub class instances
     */
    private List<AnalyticEvent> extractAnalyticEventsList(JSONArray analyticEventsStrings) {
        // Create a new mapper
        ObjectMapper mapper = new ObjectMapper();
        // Create a new list
        List<AnalyticEvent> analyticEvents = new ArrayList<>();

        // Iterate JSONArray
        for (int i=0; i<analyticEventsStrings.length(); i+=1) {

            // Try and get the JSON object
            JSONObject obj = null;
            try {
                obj = analyticEventsStrings.getJSONObject(i);
            } catch (JSONException e) {
                throw new InvalidParameterException("Event id " + i + " is not a valid JSON object.");
            }

            // Get type
            String eventType = getAnalyticType(obj, i);

            // Convert JSON to instance and add by type to the list
            switch (eventType) {
                case ANALYTIC_TYPE_CLICK:
                    addAnalyticEventToList(mapper, analyticEvents, obj, AnalyticClickEvent.class, i);
                    break;
                case ANALYTIC_TYPE_STATE_CHANGE:
                    addAnalyticEventToList(mapper, analyticEvents, obj, AnalyticStateChangeEvent.class, i);
                    break;
                case ANALYTIC_TYPE_ERROR:
                    addAnalyticEventToList(mapper, analyticEvents, obj, AnalyticErrorEvent.class, i);
                    break;
            }

        }

        return analyticEvents;
    }

    /**
     * Takes the body from the POST request, parses it into a list of AnalyticEvent objects,
     * and inserts it into analytics repository
     *
     * @param body The received post body
     * @throws InvalidParameterException
     */
    @Override
    public void insertAnalyticEvents(String body) throws InvalidParameterException {

        JSONObject params = convertBodyStringToJsonObject(body);
        JSONArray analyticEventsStrings = extractJSONArrayFromParamsObject(params);
        List<AnalyticEvent> analyticEvents = extractAnalyticEventsList(analyticEventsStrings);
        insertAnalyticEvents(analyticEvents);
    }

    /**
     * Takes a list of AnalyticEvent instances and inserts them into analytics repository
     *
     * @param analyticEvents A list of AnalyticEvent instances
     * @throws InvalidParameterException
     */
    @Override
    public void insertAnalyticEvents(List<AnalyticEvent> analyticEvents) throws InvalidParameterException {
        analyticEventsRepository.insertAnalyticEvents(analyticEvents);
    }

}
