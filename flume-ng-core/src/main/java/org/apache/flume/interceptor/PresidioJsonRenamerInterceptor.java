package org.apache.flume.interceptor;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.flume.interceptor.base.AbstractPresidioInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PresidioJsonRenamerInterceptor extends AbstractPresidioInterceptor {

    public static final String ORIGIN_FIELDS_CONF_NAME = "originFieldsList";
    public static final String DESTINATION_FIELDS_CONF_NAME = "destinationFieldsList";
    public static final String DELIMITER_CONF_NAME = "delimiter";

    private static final Logger logger = LoggerFactory
            .getLogger(PresidioJsonRenamerInterceptor.class);

    private final List<String> originFields;
    private final List<String> destinationFields;

    public PresidioJsonRenamerInterceptor(List<String> originFields, List<String> destinationFields) {
        this.originFields = originFields;
        this.destinationFields = destinationFields;
    }

    @Override
    public Event intercept(Event event) {
        final String eventBodyAsString = new String(event.getBody());
        JsonObject eventBodyAsJson = new JsonParser().parse(eventBodyAsString).getAsJsonObject();

        String currField;
        for (int i = 0; i < originFields.size(); i++) {
            currField = originFields.get(i);
            if (eventBodyAsJson.has(currField)){
                eventBodyAsJson.add(destinationFields.get(i), eventBodyAsJson.get(currField));
                eventBodyAsJson.remove(currField);
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

        private List<String> originFields;
        private List<String> destinationFields;

        @Override
        public void configure(Context context) {
            String delimiter = context.getString(DELIMITER_CONF_NAME, DEFAULT_DELIMITER_VALUE);

            final String[] originFields = getStringArrayFromConfiguration(context, ORIGIN_FIELDS_CONF_NAME, delimiter);
            final String[] destinationFields = getStringArrayFromConfiguration(context, DESTINATION_FIELDS_CONF_NAME, delimiter);

            Preconditions.checkArgument(originFields.length == destinationFields.length,
                    "originFieldsList length is not equals destinationFieldsList length. originFieldsList: {} destinationFieldsList: {}",
                    originFields, destinationFields);


            String currOriginFilter;
            String currDestinationFilter;
            this.originFields = new ArrayList<>();
            this.destinationFields = new ArrayList<>();
            for (int i = 0; i < originFields.length; i++) {
                currOriginFilter = originFields[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currOriginFilter), "originFieldsList(index={}) can not be empty. {}={}.",
                        i, ORIGIN_FIELDS_CONF_NAME, Arrays.toString(originFields));
                this.originFields.add(currOriginFilter);

                currDestinationFilter = destinationFields[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currDestinationFilter), "currDestinationFilter(index={}) can not be empty. {}={}.",
                        i,DESTINATION_FIELDS_CONF_NAME , Arrays.toString(destinationFields));
                this.destinationFields.add(currDestinationFilter);
            }

        }

        @Override
        public Interceptor build() {
            logger.info("Creating PresidioJsonFilterInterceptor: {}={}, {}={}",
                    ORIGIN_FIELDS_CONF_NAME, originFields, DESTINATION_FIELDS_CONF_NAME, destinationFields);
            return new PresidioJsonRenamerInterceptor(originFields, destinationFields);
        }

        private String[] getStringArrayFromConfiguration(Context context, String key, String delimiter){
            String arrayAsString = context.getString(key, "");
            Preconditions.checkArgument(StringUtils.isNotEmpty(arrayAsString),
                    key + " can not be empty.");

            return arrayAsString.split(delimiter);
        }
    }
}
