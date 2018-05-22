package org.apache.flume.interceptor.presidio;


import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.persistency.mongo.PresidioFilteredEventsMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This interceptor is used to modify a time field to EPOCH seconds
 * Returns the same JSON with the updated EPOCH seconds (in the same field or a new field depends on the configuration)
 */
public class JsonEpochInterceptor extends AbstractPresidioJsonInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JsonEpochInterceptor.class);

    private final String originField;
    private final String originFormat;
    private final String destinationField;

    public enum DateFormats {

        MILLIS;

        public static List<String> names() {
            return Arrays.stream(values()).map(Enum::name).collect(Collectors.toList());
        }

    }

    public JsonEpochInterceptor(String originField, String originFormat,String destinationField) {
        this.originField = originField;
        this.originFormat = originFormat;
        this.destinationField = destinationField;
    }


    @Override
    public Event doIntercept(Event event) {
        final String eventBodyAsString = new String(event.getBody());
        JsonObject eventBodyAsJson = new JsonParser().parse(eventBodyAsString).getAsJsonObject();
        JsonElement jsonElement = eventBodyAsJson.get(originField);

        if (jsonElement == null || jsonElement.isJsonNull()) {
            logger.trace("Field does not exist: {}", originField);
            return event;
        }

        final String originTimeValue = eventBodyAsJson.get(originField).getAsString();

        if  (DateFormats.MILLIS.toString().equalsIgnoreCase(originFormat)) {

            try {
                long timeInMillis = Long.valueOf(originTimeValue);
                long timeInSeconds = TimeUnit.SECONDS.convert(timeInMillis, TimeUnit.MILLISECONDS);
                eventBodyAsJson.addProperty(destinationField, Long.toString(timeInSeconds));
            } catch (NumberFormatException ex) {
                logger.warn("Failed to convert field {} to EPOCH. The value expected to be epoch milliseconds", event.getBody());
                PresidioFilteredEventsMongoRepository.saveFailedFlumeEvent(getApplicationName() + "-" + this.getClass().getSimpleName(), "Failed to convert field to EPOCH. The value expected to be epoch milliseconds", event);
                return null;
            }
        } else {
            logger.warn("Unsupported format: {}. Supported values: {}.",
                   originFormat, DateFormats.names());
        }

        event.setBody(eventBodyAsJson.toString().getBytes());
        return event;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("originField", originField)
                .append("originFormat", originFormat)
                .append("destinationField", destinationField)
                .toString();
    }

    /**
     * Builder which builds new instance of the JsonTimestampWithOffsetFormatterInterceptor.
     */
    public static class Builder extends AbstractPresidioInterceptorBuilder {

        static final String ORIGIN_FIELD_CONF_NAME = "originField";
        static final String ORIGIN_FORMAT_CONF_NAME = "originFormat";
        static final String DESTINATION_FIELD_CONF_NAME = "destinationField";

        private String originField;
        private String originFormat;
        private String destinationField;

        @Override
        public void doConfigure(Context context) {
            originField = context.getString(ORIGIN_FIELD_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(originField), ORIGIN_FIELD_CONF_NAME + " can not be empty.");

            destinationField = context.getString(DESTINATION_FIELD_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(destinationField), DESTINATION_FIELD_CONF_NAME + " can not be empty.");

            originFormat = context.getString(ORIGIN_FORMAT_CONF_NAME);
            Preconditions.checkArgument(DateFormats.names().contains(originFormat),
                    String.format("Unsupported operation value: %s. Supported values: %s.", originFormat, DateFormats.names() ));

        }

        @Override
        public AbstractPresidioJsonInterceptor doBuild() {
            final JsonEpochInterceptor jsonEpochInterceptor = new JsonEpochInterceptor(originField, originFormat, destinationField);
            logger.info("Creating JsonEpochInterceptor: {}", jsonEpochInterceptor);
            return jsonEpochInterceptor;
        }
    }
}
