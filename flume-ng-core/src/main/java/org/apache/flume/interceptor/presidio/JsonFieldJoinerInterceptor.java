package org.apache.flume.interceptor.presidio;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.apache.flume.persistency.mongo.PresidioFilteredEventsMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This interceptor is used to join 2 fields in the received JSON (append and put in a single field).
 * Returns the same JSON with the new field {@link #targetField}, and with/without filtering the {@link #baseField} and {@link #toAppendField} fields according to {@link #removeBaseField} and {@link #removeToAppendField}
 */
public class JsonFieldJoinerInterceptor extends AbstractInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JsonFieldJoinerInterceptor.class);

    private final String baseField;
    private final String toAppendField;
    private final String targetField;
    private final Boolean filterOnMissingBaseField;
    private final Boolean filterOnMissingToAppendField;
    private final Boolean removeBaseField;
    private final Boolean removeToAppendField;

    JsonFieldJoinerInterceptor(String baseField, String toAppendField, String targetField, Boolean filterOnMissingBaseField, Boolean filterOnMissingToAppendField, Boolean removeBaseField, Boolean removeToAppendField) {
        this.baseField = baseField;
        this.toAppendField = toAppendField;
        this.targetField = targetField;
        this.filterOnMissingBaseField = filterOnMissingBaseField;
        this.filterOnMissingToAppendField = filterOnMissingToAppendField;
        this.removeBaseField = removeBaseField;
        this.removeToAppendField = removeToAppendField;
    }

    @Override
    public Event doIntercept(Event event) {

        final String eventBodyAsString = new String(event.getBody());
        JsonObject eventBodyAsJson = new JsonParser().parse(eventBodyAsString).getAsJsonObject();

        final JsonElement baseFieldValue = eventBodyAsJson.get(this.baseField);
        final JsonElement toAppendFieldValue = eventBodyAsJson.get(this.toAppendField);
        final JsonElement targetFieldOriginalValue = eventBodyAsJson.get(this.targetField);

        final String baseValue;
        final String toAppendValue;

        if (baseFieldValue == null || baseFieldValue.isJsonNull() || baseFieldValue.toString().equals("null")) {
            if (filterOnMissingBaseField) {
                logger.warn("Failed to join fields. Base field doesn't exist. for event {}. interceptor configuration: {}", event, this);
                PresidioFilteredEventsMongoRepository.saveFailedEvents(event);
                return null;
            } else {
                baseValue = "";
                logger.debug("Base field: '{}' doesn't exist. Appending empty String", this.toAppendField);
            }
        } else {
            baseValue = baseFieldValue.getAsString();
        }

        if (toAppendFieldValue == null || toAppendFieldValue.toString().equals("null")) {
            if (filterOnMissingToAppendField) {
                logger.warn("Failed to join fields. To Append field doesn't exist for event {}. interceptor configuration: {}", event, this);
                PresidioFilteredEventsMongoRepository.saveFailedEvents(event);
                return null;
            } else {
                toAppendValue = "";
                logger.debug("To append field: '{}' doesn't exist. Appending empty String", this.toAppendField);
            }
        } else {
            toAppendValue = toAppendFieldValue.getAsString();
        }

        final String result = baseValue + toAppendValue;
        if (targetFieldOriginalValue != null) {
            logger.warn("Target field {} already exists with value {}. Will be overridden with {}.", targetField, toAppendFieldValue, result);
        }

        eventBodyAsJson.addProperty(targetField, result);
        logger.trace("Field {} was appended to field {} and the result[{}] was placed in field {}.", baseField, toAppendField, result, targetField);

        if (removeBaseField) {
            logger.trace("Removing base field {}.", baseField);
            eventBodyAsJson.remove(baseField);
        }

        if (removeToAppendField) {
            logger.trace("Removing to append field {}.", toAppendField);
            eventBodyAsJson.remove(toAppendField);
        }

        event.setBody(eventBodyAsJson.toString().getBytes());
        return event;
    }


    /**
     * Builder which builds new instance of the JsonFieldAppenderInterceptor.
     */
    public static class Builder extends AbstractPresidioInterceptorBuilder {

        static final String BASE_FIELD_CONF_NAME = "base_field";
        static final String TO_APPEND_FIELD_CONF_NAME = "to_append_field";
        static final String TARGET_FIELD_CONF_NAME = "target_field";

        static final String FILTER_ON_MISSING_BASE_FIELD_CONF_NAME = "filter_on_missing_base_field";
        static final String FILTER_ON_MISSING_TO_APPEND_FIELD_CONF_NAME = "filter_on_missing_to_append_field";
        static final String REMOVE_BASE_FIELD_CONF_NAME = "remove_base_field";
        static final String REMOVE_TO_APPEND_CONF_NAME = "remove_to_append_field";

        private static final boolean DEFAULT_FILTER_ON_MISSING_BASE_FIELD_VALUE = false;
        private static final boolean DEFAULT_FILTER_ON_MISSING_TO_APPEND_FIELD_VALUE = false;
        private static final boolean DEFAULT_REMOVE_BASE_FIELD_VALUE = false;
        private static final boolean DEFAULT_REMOVE_TO_APPEND_VALUE = false;

        private String baseField;
        private String toAppendField;
        private String targetField;
        private Boolean filterOnMissingBaseField;
        private Boolean filterOnMissingToAppendField;
        private Boolean removeBaseField;
        private Boolean removeToAppendField;

        @Override
        public void configure(Context context) {
            baseField = context.getString(BASE_FIELD_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(baseField), BASE_FIELD_CONF_NAME + " can not be empty.");

            toAppendField = context.getString(TO_APPEND_FIELD_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(toAppendField), TO_APPEND_FIELD_CONF_NAME + " can not be empty.");

            targetField = context.getString(TARGET_FIELD_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(targetField), TARGET_FIELD_CONF_NAME + " can not be empty.");

            filterOnMissingBaseField = context.getBoolean(FILTER_ON_MISSING_BASE_FIELD_CONF_NAME, DEFAULT_FILTER_ON_MISSING_BASE_FIELD_VALUE);

            filterOnMissingToAppendField = context.getBoolean(FILTER_ON_MISSING_TO_APPEND_FIELD_CONF_NAME, DEFAULT_FILTER_ON_MISSING_TO_APPEND_FIELD_VALUE);

            removeBaseField = context.getBoolean(REMOVE_BASE_FIELD_CONF_NAME, DEFAULT_REMOVE_BASE_FIELD_VALUE);

            removeToAppendField = context.getBoolean(REMOVE_TO_APPEND_CONF_NAME, DEFAULT_REMOVE_TO_APPEND_VALUE);
        }

        @Override
        public Interceptor build() {
            final JsonFieldJoinerInterceptor jsonFieldJoinerInterceptor = new JsonFieldJoinerInterceptor(baseField, toAppendField,
                    targetField, filterOnMissingBaseField, filterOnMissingToAppendField, removeBaseField, removeToAppendField);
            logger.info("Creating JsonFieldJoinerInterceptor: {}", jsonFieldJoinerInterceptor);
            return jsonFieldJoinerInterceptor;
        }
    }
}
