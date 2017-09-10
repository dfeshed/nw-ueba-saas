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


/**
 * This interceptor is used to remove certain (redundant) fields from the received JSON
 * Returns the same JSON without the aforementioned fields
 */
public class JsonFilterInterceptor extends AbstractInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JsonFilterInterceptor.class);

    private final List<String> fieldsToFilter;

    JsonFilterInterceptor(List<String> fieldsToFilter) {
        this.fieldsToFilter = fieldsToFilter;
    }

    @Override
    public Event doIntercept(Event event) {
        final String eventBodyAsString = new String(event.getBody());
        JsonObject eventBodyAsJson = new JsonParser().parse(eventBodyAsString).getAsJsonObject();
        for (String fieldToFilter : fieldsToFilter) {
            if (eventBodyAsJson.remove(fieldToFilter) != null) {
                logger.trace("Field {} was removed.", fieldToFilter);
            }
        }

        event.setBody(eventBodyAsJson.toString().getBytes());
        return event;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("fieldsToFilter", fieldsToFilter)
                .toString();
    }

    /**
     * Builder which builds new instance of the JsonFilterInterceptor.
     */
    public static class Builder extends AbstractPresidioInterceptorBuilder {

        static final String FIELDS_TO_FILTER_CONF_NAME = "fields_to_filter";
        static final String DELIMITER_CONF_NAME = "delimiter";
        static final String DEFAULT_DELIMITER_VALUE = ",";

        private List<String> fieldsToFilter;

        @Override
        public void configure(Context context) {
            String fieldsToFilterArrayAsString = context.getString(FIELDS_TO_FILTER_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(fieldsToFilterArrayAsString), FIELDS_TO_FILTER_CONF_NAME + " can not be empty.");

            String delim = context.getString(DELIMITER_CONF_NAME, DEFAULT_DELIMITER_VALUE);

            final String[] fieldToFilterArray = fieldsToFilterArrayAsString.split(delim);
            String currFieldToFilter;
            fieldsToFilter = new ArrayList<>();
            for (int i = 0; i < fieldToFilterArray.length; i++) {
                currFieldToFilter = fieldToFilterArray[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currFieldToFilter), "%s(index=%s) can not be empty. %s=%s.", FIELDS_TO_FILTER_CONF_NAME, i, FIELDS_TO_FILTER_CONF_NAME, fieldsToFilterArrayAsString);
                fieldsToFilter.add(currFieldToFilter);
            }

        }

        @Override
        public Interceptor build() {
            final JsonFilterInterceptor jsonFilterInterceptor = new JsonFilterInterceptor(fieldsToFilter);
            logger.info("Creating JsonFilterInterceptor: {}", jsonFilterInterceptor);
            return jsonFilterInterceptor;
        }
    }
}
