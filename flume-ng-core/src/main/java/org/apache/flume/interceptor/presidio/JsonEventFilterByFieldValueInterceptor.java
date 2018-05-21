package org.apache.flume.interceptor.presidio;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonEventFilterByFieldValueInterceptor extends AbstractPresidioJsonInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JsonEventFilterByFieldValueInterceptor.class);

    private final List<String> fields;
    private final List<String> regexList;
    private final Operation operation;
    private final Boolean filterOut;

    public JsonEventFilterByFieldValueInterceptor(List<String> fields, List<String> regexList, Operation operation, Boolean filterOut) {
        this.fields = fields;
        this.regexList = regexList;
        this.operation = operation;
        this.filterOut = filterOut;
    }

    @Override
    public Event doIntercept(Event event) {
        final String eventBodyAsString = new String(event.getBody());
        JsonObject eventBodyAsJson = new JsonParser().parse(eventBodyAsString).getAsJsonObject();
        String currField;
        String currFieldValue = null;
        String currRegex;
        Pattern pattern;
        Matcher matcher;
        for (int i = 0; i < fields.size(); i++) {
            currField = fields.get(i);

            final JsonElement jsonElement = eventBodyAsJson.get(currField);
            currRegex = regexList.get(i);
            boolean isMatched;
            if (jsonElement != null && !jsonElement.isJsonNull()) {
                currFieldValue = jsonElement.getAsString();
                pattern = Pattern.compile(currRegex);
                matcher = pattern.matcher(currFieldValue);
                isMatched = matcher.matches();
            } else {
                isMatched = false;
            }


            if (isMatched) {
                if (operation == Operation.OR) {
                    if(filterOut) {
                        logger.trace("Filtering event {} because it matched the following filter: field: {}, fieldValue: {}, regex: {}.", eventBodyAsJson, currField, currFieldValue, currRegex);
                        String failureReason = String.format("Filtering event because field %s matched regular expression. The values was %s", currField, currFieldValue);
                        monitoringService.reportFailedEventMetric(failureReason, 1);
                        return null;
                    } else {
                        return event;
                    }
                }
            } else {
                if (operation == Operation.AND) {
                    if(filterOut) {
                        return event;
                    } else{
                        monitoringService.reportFailedEventMetric("EVENT_FILTERED_ACCORDING_TO_CONFIGURATION2",1);
                        return null;
                    }
                }
            }
        }

        if (operation == Operation.OR) { /* Nothing matched */
            if(filterOut) {
                return event;
            } else{
                logger.trace("Filtering event {} because it didn't match any pattern", eventBodyAsJson);
                String failureReason = String.format("Filtering event because it didn't match any pattern");
                monitoringService.reportFailedEventMetric(failureReason, 1);
                return null;
            }
        } else {  /* All matched */
            if(filterOut) {
                monitoringService.reportFailedEventMetric("EVENT_FILTERED_ACCORDING_TO_CONFIGURATION2", 1);
                return null;
            } else {
                return event;
            }
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("fields", fields)
                .toString();
    }

    /**
     * Builder which builds new instance of the JsonFilterInterceptor.
     */
    public static class Builder extends AbstractPresidioInterceptorBuilder {

        static final String FIELDS_CONF_NAME = "fields";
        static final String REGEX_LIST_CONF_NAME = "regexList";
        static final String DELIMITER_CONF_NAME = "delimiter";
        static final String DEFAULT_DELIMITER_VALUE = ",";
        static final String OPERATION_CONF_NAME = "operation";
        static final String DEFAULT_OP_VALUE = "OR";
        static final String FILTER_OUT_CONF_NAME = "filter_out";
        static final boolean DEFAULT_FILTER_OUT_VALUE = true;


        private List<String> fields;
        private List<String> regexList;
        private Operation operation;
        private Boolean filterOut;

        @Override
        public void doConfigure(Context context) {
            String fieldsArrayAsString = context.getString(FIELDS_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(fieldsArrayAsString), FIELDS_CONF_NAME + " can not be empty.");

            String regexListArrayAsString = context.getString(REGEX_LIST_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(regexListArrayAsString), REGEX_LIST_CONF_NAME + " can not be empty.");

            String delim = context.getString(DELIMITER_CONF_NAME, DEFAULT_DELIMITER_VALUE);

            String opAsString = context.getString(OPERATION_CONF_NAME, DEFAULT_OP_VALUE);
            operation = Operation.createOperation(opAsString);

            filterOut = context.getBoolean(FILTER_OUT_CONF_NAME, DEFAULT_FILTER_OUT_VALUE);

            final String[] fieldArray = fieldsArrayAsString.split(delim);
            String currField;
            fields = new ArrayList<>();
            for (int i = 0; i < fieldArray.length; i++) {
                currField = fieldArray[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currField), "%s(index=%s) can not be empty. %s=%s.", FIELDS_CONF_NAME, i, FIELDS_CONF_NAME, fieldsArrayAsString);
                fields.add(currField);
            }

            final String[] regexArray = regexListArrayAsString.split(delim);
            String currRegex;
            regexList = new ArrayList<>();
            for (int i = 0; i < fieldArray.length; i++) {
                currRegex = regexArray[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currRegex), "%s(index=%s) can not be empty. %s=%s.", REGEX_LIST_CONF_NAME, i, REGEX_LIST_CONF_NAME, regexListArrayAsString);
                regexList.add(currRegex);
            }


            Preconditions.checkArgument(fields.size() == regexList.size(), String.format("%s and %s parameters must be of same length", FIELDS_CONF_NAME, REGEX_LIST_CONF_NAME));
        }

        @Override
        public AbstractPresidioJsonInterceptor doBuild() {
            final JsonEventFilterByFieldValueInterceptor jsonFilterByFieldValueInterceptor = new JsonEventFilterByFieldValueInterceptor(fields, regexList, operation, filterOut);
            logger.info("Creating JsonFilterByFieldValueInterceptor: {}", jsonFilterByFieldValueInterceptor);
            return jsonFilterByFieldValueInterceptor;
        }
    }

    private enum Operation {
        AND("AND"), OR("OR");

        private String name;

        Operation(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Operation createOperation(String operationName) throws IllegalArgumentException {
            return Operation.valueOf(operationName.toUpperCase());
        }
    }
}


