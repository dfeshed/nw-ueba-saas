package org.apache.flume.interceptor.presidio;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This interceptor is used to join fields from the received JSON into a new object (map)
 * Returns the same JSON with a new map containing the values.
 */
public class JsonMapCreatorInterceptor extends AbstractPresidioInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JsonMapCreatorInterceptor.class);

    private final List<String> fieldsToPut;
    private final String mapKeyName;
    private final Boolean deleteFields;
    private final Boolean overrideExistingMap;
    private final Map<String, String> defaultValueConfigurations;

    JsonMapCreatorInterceptor(List<String> fieldsToPut, String mapKeyName, Boolean deleteFields,
                              Map<String, String> defaultValueConfigurations, Boolean overrideExistingMap) {
        this.fieldsToPut = fieldsToPut;
        this.mapKeyName = mapKeyName;
        this.deleteFields = deleteFields;
        this.defaultValueConfigurations = defaultValueConfigurations;
        this.overrideExistingMap = overrideExistingMap;
    }

    @Override
    public Event doIntercept(Event event) {
        final String eventBodyAsString = new String(event.getBody());
        JsonObject eventBodyAsJson = new JsonParser().parse(eventBodyAsString).getAsJsonObject();

        JsonObject mapToAdd;
        if (overrideExistingMap || !eventBodyAsJson.has(mapKeyName)) {
            mapToAdd = new JsonObject();
        } else {
            mapToAdd = eventBodyAsJson.getAsJsonObject(mapKeyName);
        }

        for (String fieldToPut : fieldsToPut) {
            if (eventBodyAsJson.has(fieldToPut)) {
                final JsonElement jsonElement = eventBodyAsJson.get(fieldToPut);
                if (jsonElement == null || jsonElement.isJsonNull()) {
                    logger.trace("Field {} is null: Can't put in map", fieldToPut);
                } else {
                    mapToAdd.addProperty(fieldToPut, jsonElement.getAsString());
                }
                if (deleteFields) {
                    logger.trace("Removing origin field {}.", fieldToPut);
                    eventBodyAsJson.remove(fieldToPut);
                }
            } else {
                if (defaultValueConfigurations.keySet().contains(fieldToPut)) {
                    final String defaultValue = defaultValueConfigurations.get(fieldToPut);
                    logger.trace("Adding default value {} to field {} since the event didn't contain the field.", fieldToPut, defaultValue);
                    mapToAdd.addProperty(fieldToPut, defaultValue);
                }
                logger.trace("The event does not contain field {}.", fieldToPut);
            }
        }

        if (mapToAdd.entrySet().size() > 0) {
            eventBodyAsJson.add(mapKeyName, mapToAdd);
        }

        event.setBody(eventBodyAsJson.toString().getBytes());
        return event;
    }

    /**
     * Builder which builds new instance of the JsonMapCreatorInterceptor.
     */
    public static class Builder extends AbstractPresidioInterceptorBuilder {

        static final String FIELDS_TO_PUT_CONF_NAME = "fieldsToPut";
        static final String MAP_KEY_NAME_CONF_NAME = "mapKeyName";
        static final String DELETE_FIELDS_CONF_NAME = "deleteFields";
        static final String DEFAULT_VALUES_CONF_NAME = "defaultValues";
        static final String DELIMITER_CONF_NAME = "delimiter";
        static final String DEFAULT_DELIMITER_VALUE = ",";
        static final String DEFAULT_VALUES_DELIMITER_CONF_NAME = "defaultValuesDelimiter";
        static final String DEFAULT_VALUES_DELIMITER_DEFAULT_VALUE = ">";
        static final String OVERRIDE_EXISTING_MAP_NAME = "overrideExistingMap";
        static final Boolean OVERRIDE_EXISTING_MAP_DEFAULT_VALUE = false;

        private List<String> fieldsToPut;
        private String mapKey;
        private Boolean deleteFields;
        private Boolean overrideExistingMap;
        private Map<String, String> defaultValueConfigurations;

        @Override
        public void doConfigure(Context context) {
            String delimiter = context.getString(DELIMITER_CONF_NAME, DEFAULT_DELIMITER_VALUE);

            deleteFields = context.getBoolean(DELETE_FIELDS_CONF_NAME, false);

            overrideExistingMap = context.getBoolean(OVERRIDE_EXISTING_MAP_NAME, OVERRIDE_EXISTING_MAP_DEFAULT_VALUE);

            mapKey = context.getString(MAP_KEY_NAME_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(mapKey), MAP_KEY_NAME_CONF_NAME + " can not be empty.");

            String fieldsToPutArrayAsString = context.getString(FIELDS_TO_PUT_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(fieldsToPutArrayAsString), FIELDS_TO_PUT_CONF_NAME + " can not be empty.");
            final String[] fieldsToPutArray = fieldsToPutArrayAsString.split(delimiter);
            String currFieldToPut;
            fieldsToPut = new ArrayList<>();
            for (int i = 0; i < fieldsToPutArray.length; i++) {
                currFieldToPut = fieldsToPutArray[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currFieldToPut), "%s(index=%s) can not be empty. %s=%s.", FIELDS_TO_PUT_CONF_NAME, i, FIELDS_TO_PUT_CONF_NAME, fieldsToPutArrayAsString);
                fieldsToPut.add(currFieldToPut);
            }


            String defaultsDelimiter = context.getString(DEFAULT_VALUES_DELIMITER_CONF_NAME, DEFAULT_VALUES_DELIMITER_DEFAULT_VALUE);
            String defaultValuesAsString = context.getString(DEFAULT_VALUES_CONF_NAME);
            if (StringUtils.isNotEmpty(defaultValuesAsString)) {
                final String[] defaultValuesArray = defaultValuesAsString.split(delimiter);
                defaultValueConfigurations = new HashMap<>();
                String currDefaultValueConfig;
                for (int i = 0; i < defaultValuesArray.length; i++) {
                    currDefaultValueConfig = defaultValuesArray[i];
                    Preconditions.checkArgument(StringUtils.isNotEmpty(currDefaultValueConfig), "%s(index=%s) can not be empty. %s=%s.", DEFAULT_VALUES_DELIMITER_CONF_NAME, i, DEFAULT_VALUES_DELIMITER_CONF_NAME, defaultValuesAsString);
                    final String[] split = currDefaultValueConfig.split(defaultsDelimiter);
                    final String currFieldName = split[0];
                    final String currDefaultValue = split[1];
                    Preconditions.checkArgument(StringUtils.isNotEmpty(currFieldName), String.format("invalid default value config %s. must be of format some_field_name%ssome_default_value.", currFieldName, DEFAULT_DELIMITER_VALUE));
                    Preconditions.checkArgument(StringUtils.isNotEmpty(currDefaultValue), String.format("invalid default value config %s. must be of format some_field_name%ssome_default_value.", currDefaultValue, DEFAULT_DELIMITER_VALUE));
                    defaultValueConfigurations.put(currFieldName, currDefaultValue);
                }
            }
        }

        @Override
        public AbstractPresidioInterceptor doBuild() {
            final JsonMapCreatorInterceptor jsonMapCreatorInterceptor = new JsonMapCreatorInterceptor(fieldsToPut, mapKey,
                    deleteFields, defaultValueConfigurations, overrideExistingMap);
            logger.info("Creating JsonMapCreatorInterceptor: {}", jsonMapCreatorInterceptor);
            return jsonMapCreatorInterceptor;
        }
    }

}
