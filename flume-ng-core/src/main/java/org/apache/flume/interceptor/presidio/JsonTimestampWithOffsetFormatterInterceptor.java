package org.apache.flume.interceptor.presidio;



import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
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
    private final String timezoneField;
    private final String destinationField;
    private final String destinationFormat;

    public JsonTimestampWithOffsetFormatterInterceptor(String originField, String originFormat, String timezoneField, String destinationField, String destinationFormat) {
        this.originField = originField;
        this.originFormat = originFormat;
        this.timezoneField = timezoneField;
        this.destinationField = destinationField;
        this.destinationFormat = destinationFormat;
    }

    @Override
    public Event doIntercept(Event event) {
        final String eventBodyAsString = new String(event.getBody());
        JsonObject eventBodyAsJson = new JsonParser().parse(eventBodyAsString).getAsJsonObject();

        final JsonElement originFieldValue = eventBodyAsJson.get(originField);
        final JsonElement originFormatValue = eventBodyAsJson.get(originFormat);
        final JsonElement timezoneFieldValue = eventBodyAsJson.get(timezoneField);
        final JsonElement destinationFormatValue = eventBodyAsJson.get(destinationFormat);

        final String newTimestamp = getNewTimestamp(originFieldValue, originFormatValue, timezoneFieldValue, destinationFormatValue);

        eventBodyAsJson.addProperty(destinationField, newTimestamp);
        event.setBody(eventBodyAsJson.toString().getBytes());
        return event;
    }

    private String getNewTimestamp(JsonElement originFieldValue, JsonElement originFormatValue, JsonElement timezoneFieldValue, JsonElement destinationFormatValue) {

        /* get time as instant with the given time zone */
        final ZoneId originZoneId = ZoneId.ofOffset("UTC", ZoneOffset.ofHours(timezoneFieldValue.getAsInt()));
        final Instant originTimeAsInstant = Instant.from(DateTimeFormatter
                .ofPattern(originFormatValue.getAsString())
                .withZone(originZoneId)
                .parse(originFieldValue.getAsString()));

        /* configure target time format and time zone (all times should be in UTC) */
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(destinationFormatValue.getAsString());
        final ZoneId destinationZoneId = ZoneId.ofOffset("UTC", ZoneOffset.UTC); //always UTC

        /* format timestamp from its origin form to destination form (form = format + time zone) */
        return formatter.format(ZonedDateTime.ofInstant(originTimeAsInstant, destinationZoneId));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("originField", originField)
                .append("originFormat", originFormat)
                .append("timezoneField", timezoneField)
                .append("destinationField", destinationField)
                .append("destinationFormat", destinationFormat)
                .toString();
    }

    /**
     * Builder which builds new instance of the JsonTimestampWithOffsetFormatterInterceptor.
     */
    public static class Builder implements Interceptor.Builder {

        static final String ORIGIN_FIELD_CONF_NAME = "originField";
        static final String ORIGIN_FORMAT_CONF_NAME = "originFormat";
        static final String TIMEZONE_CONF_NAME = "timezoneField";
        static final String DESTINATION_FIELD_CONF_NAME = "destinationField";
        static final String DESTINATION_FORMAT_CONF_NAME = "destinationFormat";

        private String originField;
        private String originFormat;
        private String timezoneField;
        private String destinationField;
        private String destinationFormat;

        @Override
        public void configure(Context context) {
            originField = context.getString(ORIGIN_FIELD_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(originField), ORIGIN_FIELD_CONF_NAME + " can not be empty.");

            originFormat = context.getString(ORIGIN_FORMAT_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(originFormat), ORIGIN_FORMAT_CONF_NAME + " can not be empty.");

            timezoneField = context.getString(TIMEZONE_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(timezoneField), TIMEZONE_CONF_NAME + " can not be empty.");

            destinationField = context.getString(DESTINATION_FIELD_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(destinationField), DESTINATION_FIELD_CONF_NAME + " can not be empty.");

            destinationFormat = context.getString(DESTINATION_FORMAT_CONF_NAME, "yyyy-MM-dd'T'HH:mm:ss");
            Preconditions.checkArgument(StringUtils.isNotEmpty(destinationFormat), DESTINATION_FORMAT_CONF_NAME + " can not be empty.");
        }

        @Override
        public Interceptor build() {
            final JsonTimestampWithOffsetFormatterInterceptor jsonTimestampWithOffsetFormatterInterceptor = new JsonTimestampWithOffsetFormatterInterceptor(originField, originFormat, timezoneField, destinationField, destinationFormat);
            logger.info("Creating JsonTimestampWithOffsetFormatterInterceptor: {}", jsonTimestampWithOffsetFormatterInterceptor);
            return jsonTimestampWithOffsetFormatterInterceptor;
        }
    }
}
