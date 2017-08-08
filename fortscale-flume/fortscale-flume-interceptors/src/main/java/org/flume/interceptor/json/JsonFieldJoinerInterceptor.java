package org.flume.interceptor.json;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.flume.interceptor.base.AbstractPresidioInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonFieldJoinerInterceptor extends AbstractPresidioInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JsonFieldJoinerInterceptor.class);

    private final String baseField;
    private final String toAppendField;
    private final String targetField;
    private final Boolean removeBaseField;
    private final Boolean removeToAppendField;

    public JsonFieldJoinerInterceptor(String baseField, String toAppendField, String targetField, Boolean removeBaseField, Boolean removeToAppendField) {
        this.baseField = baseField;
        this.toAppendField = toAppendField;
        this.targetField = targetField;
        this.removeBaseField = removeBaseField;
        this.removeToAppendField = removeToAppendField;
    }

    @Override
    public Event intercept(Event event) {

        final JsonObject eventBodyAsJson = getEventBodyAsJson(event);

        final JsonElement baseFieldValue = eventBodyAsJson.get(this.baseField);
        final JsonElement toAppendFieldValue = eventBodyAsJson.get(this.toAppendField);
        final JsonElement targetFieldOriginalValue = eventBodyAsJson.get(this.targetField);
        if (baseFieldValue == null) {
            logger.warn("Failed to join fields. Base field doesn't exist. {]", this);
        }
        if (toAppendFieldValue == null) {
            logger.error("Failed to join fields. To append field doesn't exist. {}", this);
        }

        final String result = baseField + toAppendField;

        if (targetFieldOriginalValue != null) {
            logger.warn("Target field {} already exists with value {}. Will be overridden with {}.", targetField, toAppendFieldValue, result);
        }

        eventBodyAsJson.addProperty(targetField, result);
        logger.trace("Field {} was appended to field {} and the result[{}] was placed in field {}.", baseField, toAppendField, result, targetField);

        event.setBody(eventBodyAsJson.toString().getBytes());
        return event;
    }



    /**
     * Builder which builds new instance of the JsonFieldAppenderInterceptor.
     */
    public static class Builder implements Interceptor.Builder {

        private static final String BASE_FIELD_CONF_NAME = "base_field";
        private static final String TO_APPEND_FIELD_CONF_NAME = "to_append_field";
        private static final String TARGET_FIELD_CONF_NAME = "target_field";
        private static final String REMOVE_BASE_FIELD_CONF_NAME = "remove_base_field";
        private static final String REMOVE_TO_APPEND_CONF_NAME = "remove_to_append_field";

        private String baseField;
        private String toAppendField;
        private String targetField;
        private Boolean removeBaseField;
        private Boolean removeToAppendField;

        @Override
        public void configure(Context context) {
            baseField = context.getString(BASE_FIELD_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(baseField), BASE_FIELD_CONF_NAME + " can not be empty.");

            toAppendField = context.getString(TO_APPEND_FIELD_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(toAppendField), TO_APPEND_FIELD_CONF_NAME + " can not be empty.");

            targetField = context.getString(TO_APPEND_FIELD_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(targetField), TARGET_FIELD_CONF_NAME + " can not be empty.");

            removeBaseField = context.getBoolean(TO_APPEND_FIELD_CONF_NAME);
            Preconditions.checkArgument(removeBaseField != null, REMOVE_BASE_FIELD_CONF_NAME + " can not be empty.");

            removeToAppendField = context.getBoolean(REMOVE_TO_APPEND_CONF_NAME);
            Preconditions.checkArgument(removeToAppendField != null, REMOVE_TO_APPEND_CONF_NAME + " can not be empty.");
        }

        @Override
        public Interceptor build() {
            final JsonFieldJoinerInterceptor jsonFieldJoinerInterceptor = new JsonFieldJoinerInterceptor(baseField, toAppendField, targetField, removeBaseField, removeToAppendField);
            logger.info("Creating JsonFieldJoinerInterceptor: {}", jsonFieldJoinerInterceptor);
            return jsonFieldJoinerInterceptor;
        }
    }
}
