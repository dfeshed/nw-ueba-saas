package org.flume.interceptor.json;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.flume.interceptor.base.AbstractPresidioInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class PresidioJsonFilterInterceptor extends AbstractPresidioInterceptor {

    public static final String FIELDS_TO_FILTER_CONF_NAME = "fields_to_filter";
    public static final String DELIMITER_CONF_NAME = "delimiter";

    private static final Logger logger = LoggerFactory
            .getLogger(PresidioJsonFilterInterceptor.class);

    private final List<String> fieldsToFilter;

    public PresidioJsonFilterInterceptor(List<String> fieldsToFilter) {
        this.fieldsToFilter = fieldsToFilter;
    }

    @Override
    public Event intercept(Event event) {
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


    /**
     * Builder which builds new instance of the PresidioJsonFilterInterceptor.
     */
    public static class Builder implements Interceptor.Builder {

        private static final String DEFAULT_DELIMITER_VALUE = ",";

        private List<String> fieldsToFilter;

        @Override
        public void configure(Context context) {
            String fieldsToFilterArrayAsString = context.getString(FIELDS_TO_FILTER_CONF_NAME, "");
            Preconditions.checkArgument(StringUtils.isNotEmpty(fieldsToFilterArrayAsString), FIELDS_TO_FILTER_CONF_NAME + " can not be empty.");

            String delim = context.getString(DELIMITER_CONF_NAME, DEFAULT_DELIMITER_VALUE);

            final String[] fieldToFilterArray = fieldsToFilterArrayAsString.split(delim);
            String currFieldToFilter;
            fieldsToFilter = new ArrayList<>();
            for (int i = 0; i < fieldToFilterArray.length; i++) {
                currFieldToFilter = fieldToFilterArray[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currFieldToFilter), "field_to_filter(index={}) can not be empty. {}={}.", i, FIELDS_TO_FILTER_CONF_NAME, fieldsToFilterArrayAsString);
                fieldsToFilter.add(currFieldToFilter);
            }

        }

        @Override
        public Interceptor build() {
            logger.info("Creating PresidioJsonFilterInterceptor: {}={}", FIELDS_TO_FILTER_CONF_NAME, fieldsToFilter);
            return new PresidioJsonFilterInterceptor(fieldsToFilter);
        }
    }
}
