package org.apache.flume.interceptor.presidio;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.apache.flume.interceptor.presidio.JsonMapCreatorInterceptor.Builder.*;

/**
 * This interceptor is used to join fields from the received JSON into a new object (map)
 * Returns the same JSON with a new map containing the values.
 * 
 */
public class JsonMapCreatorInterceptor extends AbstractInterceptor{

    private static final Logger logger = LoggerFactory.getLogger(JsonMapCreatorInterceptor.class);

    private final List<String> fieldsToJoin;
    private final String mapKey;
    private final Boolean deleteFields;

    public JsonMapCreatorInterceptor(List<String> fieldsToJoin, String mapKey, Boolean deleteFields) {
        this.fieldsToJoin = fieldsToJoin;
        this.mapKey = mapKey;
        this.deleteFields = deleteFields;
    }

    @Override
    public Event doIntercept(Event event) {
        final String eventBodyAsString = new String(event.getBody());
        JsonObject eventBodyAsJson = new JsonParser().parse(eventBodyAsString).getAsJsonObject();

        JsonObject mapToAdd = new JsonObject();

        for (String fieldToJoin : fieldsToJoin) {
            if (eventBodyAsJson.has(fieldToJoin)) {
                mapToAdd.addProperty(fieldToJoin, eventBodyAsJson.get(fieldToJoin).getAsString());

                if (deleteFields){
                    logger.trace("Removing origin field {}.", fieldToJoin);
                    eventBodyAsJson.remove(fieldToJoin);
                }
            }
        }

        if (mapToAdd.entrySet().size() > 0){
            eventBodyAsJson.add(mapKey, mapToAdd);
        } else {
            logger.debug("The event does not contains any of the configured fields. Map was not added to the event.");
        }

        event.setBody(eventBodyAsJson.toString().getBytes());
        return event;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(FIELDS_TO_JOIN_CONF_NAME, fieldsToJoin)
                .append(MAP_KEY_CONF_NAME, mapKey)
                .append(DELETE_FIELDS_CONF_NAME, deleteFields)
                .toString();
    }

    /**
     * Builder which builds new instance of the JsonMapCreatorInterceptor.
     */
    public static class Builder implements Interceptor.Builder {

        static final String FIELDS_TO_JOIN_CONF_NAME = "fieldsToJoin";
        static final String MAP_KEY_CONF_NAME = "mapKey";
        static final String DELETE_FIELDS_CONF_NAME = "deleteFields";
        static final String DELIMITER_CONF_NAME = "delimiter";
        static final String DEFAULT_DELIMITER_VALUE = ",";

        private List<String> fieldsToJoin;
        private String mapKey;
        private Boolean deleteFields;

        @Override
        public void configure(Context context) {
            String fieldsToJoinArrayAsString = context.getString(FIELDS_TO_JOIN_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(fieldsToJoinArrayAsString), FIELDS_TO_JOIN_CONF_NAME+ " can not be empty.");

            mapKey = context.getString(MAP_KEY_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(mapKey), MAP_KEY_CONF_NAME+ " can not be empty.");

            String delimiter = context.getString(DELIMITER_CONF_NAME, DEFAULT_DELIMITER_VALUE);
            deleteFields = context.getBoolean(DELETE_FIELDS_CONF_NAME, true);

            final String[] fieldToJoinArray = fieldsToJoinArrayAsString.split(delimiter);
            String currFieldToFilter;
            fieldsToJoin = new ArrayList<>();
            for (int i = 0; i < fieldToJoinArray.length; i++) {
                currFieldToFilter = fieldToJoinArray[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currFieldToFilter), "{}(index={}) can not be empty. {}={}.", FIELDS_TO_JOIN_CONF_NAME, i, FIELDS_TO_JOIN_CONF_NAME, fieldsToJoinArrayAsString);
                fieldsToJoin.add(currFieldToFilter);
            }
        }

        @Override
        public Interceptor build() {
            final JsonMapCreatorInterceptor jsonMapCreatorInterceptor = new JsonMapCreatorInterceptor(fieldsToJoin, mapKey, deleteFields);
            logger.info("Creating JsonFilterInterceptor: {}", jsonMapCreatorInterceptor);
            return jsonMapCreatorInterceptor;
        }
    }

}
