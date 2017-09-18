package org.apache.flume.interceptor.presidio;


import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.apache.flume.persistency.mongo.PresidioFilteredEventsMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class JsonTimestampWithOffsetFormatterInterceptor extends AbstractInterceptor {


    private static final Logger logger = LoggerFactory.getLogger(JsonTimestampWithOffsetFormatterInterceptor.class);

    private final String originField;
    private final String originFormat;
    private final String timezoneOffsetField;
    private final String destinationField;
    private final String destinationFormat;
    private final Boolean removeOriginField;
    private final Boolean removeTimezoneOffsetField;

    public JsonTimestampWithOffsetFormatterInterceptor(String originField, String originFormat, String timezoneOffsetField, String destinationField, String destinationFormat, Boolean removeOriginField, Boolean removeTimezoneOffsetField) {
        this.originField = originField;
        this.originFormat = originFormat;
        this.timezoneOffsetField = timezoneOffsetField;
        this.destinationField = destinationField;
        this.destinationFormat = destinationFormat;
        this.removeOriginField = removeOriginField;
        this.removeTimezoneOffsetField = removeTimezoneOffsetField;
    }


    @Override
    public Event doIntercept(Event event) {
        final String eventBodyAsString = new String(event.getBody());
        JsonObject eventBodyAsJson = new JsonParser().parse(eventBodyAsString).getAsJsonObject();

        final String originTimestamp = eventBodyAsJson.get(originField).getAsString();

//        final int timezoneOffset = eventBodyAsJson.get(timezoneOffsetField).getAsInt();
        final int timezoneOffset = 0;
        final String newTimestamp;
        try {
            newTimestamp = getNewTimestamp(originTimestamp, originFormat, timezoneOffset, destinationFormat);
        } catch (Exception e) {
            logger.warn("Failed to get timestamp for event {}. interceptor configuration: {}", event, this, e);
            PresidioFilteredEventsMongoRepository.saveFailedFlumeEvent("Adapter-" + this.getClass().getSimpleName(), "Failed to get timestamp", event);
            return null;
        }
        eventBodyAsJson.addProperty(destinationField, newTimestamp);
        if (removeOriginField) {
            logger.trace("Removing origin field {}.", originField);
            eventBodyAsJson.remove(originField);
        }

        if (removeTimezoneOffsetField) {
            logger.trace("Removing timezone offset field {}.", timezoneOffsetField);
            eventBodyAsJson.remove(timezoneOffsetField);
        }


        event.setBody(eventBodyAsJson.toString().getBytes());
        return event;
    }

    private String getNewTimestamp(String originTimestamp, String originFormat, int timezoneOffset, String destinationFormat) {

        /* get time as instant with the given time zone */
        final ZoneId originZoneId = ZoneId.ofOffset("UTC", ZoneOffset.ofHours(timezoneOffset));
        final Instant originTimeAsInstant = Instant.from(DateTimeFormatter
                .ofPattern(originFormat)
                .withZone(originZoneId)
                .parse(originTimestamp));

        /* configure target time format and time zone (all times should be in UTC) */
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(destinationFormat);
        final ZoneId destinationZoneId = ZoneId.ofOffset("UTC", ZoneOffset.UTC); //always UTC

        /* format timestamp from its origin form to destination form (form = format + time zone) */
        return formatter.format(ZonedDateTime.ofInstant(originTimeAsInstant, destinationZoneId));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("originField", originField)
                .append("originFormat", originFormat)
                .append("timezoneOffsetField", timezoneOffsetField)
                .append("destinationField", destinationField)
                .append("destinationFormat", destinationFormat)
                .append("removeOriginField", removeOriginField)
                .append("removeTimezoneOffsetField", removeTimezoneOffsetField)
                .toString();
    }

    /**
     * Builder which builds new instance of the JsonTimestampWithOffsetFormatterInterceptor.
     */
    public static class Builder extends AbstractPresidioInterceptorBuilder {

        static final String ORIGIN_FIELD_CONF_NAME = "originField";
        static final String ORIGIN_FORMAT_CONF_NAME = "originFormat";
        static final String TIMEZONE_OFFSET_FIELD_CONF_NAME = "timezoneOffsetField";
        static final String DESTINATION_FIELD_CONF_NAME = "destinationField";
        static final String DESTINATION_FORMAT_CONF_NAME = "destinationFormat";
        static final String REMOVE_ORIGIN_CONF_NAME = "removeOrigin";
        static final String REMOVE_TIMEZONE_OFFSET_CONF_NAME = "removeTimezoneOffset";

        private String originField;
        private String originFormat;
        private String timezoneOffsetField;
        private String destinationField;
        private String destinationFormat;
        private Boolean removeOriginField;
        private Boolean removeTimezoneOffsetField;

        @Override
        public void configure(Context context) {
            originField = context.getString(ORIGIN_FIELD_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(originField), ORIGIN_FIELD_CONF_NAME + " can not be empty.");

            originFormat = context.getString(ORIGIN_FORMAT_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(originFormat), ORIGIN_FORMAT_CONF_NAME + " can not be empty.");

            timezoneOffsetField = context.getString(TIMEZONE_OFFSET_FIELD_CONF_NAME, null);
            Preconditions.checkArgument(!timezoneOffsetField.equals(""), TIMEZONE_OFFSET_FIELD_CONF_NAME + " can not be empty.");

            destinationField = context.getString(DESTINATION_FIELD_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(destinationField), DESTINATION_FIELD_CONF_NAME + " can not be empty.");

            destinationFormat = context.getString(DESTINATION_FORMAT_CONF_NAME, "yyyy-MM-dd'T'HH:mm:ss");
            Preconditions.checkArgument(StringUtils.isNotEmpty(destinationFormat), DESTINATION_FORMAT_CONF_NAME + " can not be empty.");

            removeOriginField = context.getBoolean(REMOVE_ORIGIN_CONF_NAME, true);
            Preconditions.checkArgument(removeOriginField != null, REMOVE_ORIGIN_CONF_NAME + " can not be empty.");

            removeTimezoneOffsetField = context.getBoolean(REMOVE_TIMEZONE_OFFSET_CONF_NAME, true);
            Preconditions.checkArgument(removeTimezoneOffsetField != null, REMOVE_TIMEZONE_OFFSET_CONF_NAME + " can not be empty.");
        }

        @Override
        public Interceptor build() {
            final JsonTimestampWithOffsetFormatterInterceptor jsonTimestampWithOffsetFormatterInterceptor = new JsonTimestampWithOffsetFormatterInterceptor(originField, originFormat, timezoneOffsetField, destinationField, destinationFormat, removeOriginField, removeTimezoneOffsetField);
            logger.info("Creating JsonTimestampWithOffsetFormatterInterceptor: {}", jsonTimestampWithOffsetFormatterInterceptor);
            return jsonTimestampWithOffsetFormatterInterceptor;
        }
    }
}
