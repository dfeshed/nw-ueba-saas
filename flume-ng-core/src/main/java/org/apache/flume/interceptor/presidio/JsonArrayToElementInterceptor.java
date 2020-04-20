package org.apache.flume.interceptor.presidio;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This interceptor is used to duplicate certain values  in the received JSON.
 * Returns the same JSON without new keys.
 */
public class JsonArrayToElementInterceptor extends AbstractPresidioJsonInterceptor {
    private static final Logger logger = LoggerFactory
            .getLogger(JsonArrayToElementInterceptor.class);

    private final String originField;
    private final String destinationField;
    private final int index;

    public JsonArrayToElementInterceptor(String originField, String destinationField, int index) {
        this.originField = originField;
        this.destinationField = destinationField;
        this.index = index;
    }

    @Override
    public Event doIntercept(Event event) {

        JsonObject eventBodyAsJson = getJsonObject(event);
        JsonElement jsonElement = eventBodyAsJson.get(originField);
        if (eventBodyAsJson.has(originField)) {
            if (jsonElement != null && !jsonElement.isJsonNull() && jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > index) {
                JsonElement singleElem = jsonElement.getAsJsonArray().get(index);
                eventBodyAsJson.add(destinationField, singleElem);
            }
        }

        setJsonObject(event, eventBodyAsJson);
        return event;
    }

    /**
     * Builder which builds new instance of the JsonFieldDuplicatorInterceptor.
     */
    public static class Builder extends AbstractPresidioInterceptorBuilder {

        static final String ORIGIN_FIELD_CONF_NAME = "originField";
        static final String DESTINATION_FIELD_CONF_NAME = "destinationField";
        static final String INDEX = "index";

        private String originField;
        private String destinationField;
        private int index;

        @Override
        public void doConfigure(Context context) {
            originField = context.getString(ORIGIN_FIELD_CONF_NAME);
            destinationField = context.getString(DESTINATION_FIELD_CONF_NAME);
            index = context.getInteger(INDEX);
        }

        @Override
        public AbstractPresidioJsonInterceptor doBuild() {
            logger.info("Creating JsonArrayToElementInterceptor: {}={}, {}={}",
                    ORIGIN_FIELD_CONF_NAME, originField, DESTINATION_FIELD_CONF_NAME, destinationField);
            return new JsonArrayToElementInterceptor(originField, destinationField, index);
        }

    }
}