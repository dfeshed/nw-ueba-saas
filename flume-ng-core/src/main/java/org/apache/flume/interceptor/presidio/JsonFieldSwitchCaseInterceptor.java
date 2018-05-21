package org.apache.flume.interceptor.presidio;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;


public class JsonFieldSwitchCaseInterceptor extends AbstractPresidioJsonInterceptor {

    private static final Logger logger = LoggerFactory
            .getLogger(JsonFieldSwitchCaseInterceptor.class);

    private String conditionField;
    private Pattern patternCondition;
    private final String originField;
    private final String destinationField;
    private final String[] cases;
    private final String[] casesValues;

    JsonFieldSwitchCaseInterceptor(String conditionField, Pattern patternCondition,String originField,
                                   String destinationField, String[] cases, String[] casesValues) {
        this.conditionField = conditionField;
        this.patternCondition = patternCondition;
        this.originField = originField;
        this.destinationField = destinationField;
        this.cases = cases;
        this.casesValues = casesValues;
    }

    @Override
    public Event doIntercept(Event event) {
        final String eventBodyAsString = new String(event.getBody());

        JsonObject eventBodyAsJson;
        eventBodyAsJson = new JsonParser().parse(eventBodyAsString).getAsJsonObject();
        boolean conditionResult = testCondition(eventBodyAsJson);
        if(conditionResult) {
            handleField(eventBodyAsJson);
            event.setBody(eventBodyAsJson.toString().getBytes());
        }

        return event;
    }

    private boolean testCondition(JsonObject eventBodyAsJson){
        boolean conditionResult = true;
        if(conditionField != null){
            JsonElement jsonElement = eventBodyAsJson.get(conditionField);
            if(jsonElement == null || jsonElement.isJsonNull()){
                conditionResult = false;
            } else{
                // When the json value contains double backslash ("//")
                // using JsonObject.getAsString() will return a string with single backslash.
                // This workaround will return the correct value.
                String fieldValue = jsonElement.getAsString();
                conditionResult = patternCondition.matcher(fieldValue).matches();
            }
        }
        return conditionResult;
    }

    private void handleField(JsonObject eventBodyAsJson) {
        JsonElement jsonElement = eventBodyAsJson.get(originField);
        String destinationValue = null;
        if(jsonElement != null && !jsonElement.isJsonNull()){
            String originFieldValue = jsonElement.getAsString();
            for(int i = 0; i < cases.length; i++){
                String curCase = cases[i];
                if(originFieldValue.contains(curCase)){
                    destinationValue = casesValues[i];
                    break;
                }
            }
        }
        eventBodyAsJson.addProperty(destinationField, destinationValue);
    }


    /**
     * Builder which builds new instance of the JsonFieldSwitchCaseInterceptor.
     */
    public static class Builder extends AbstractPresidioInterceptorBuilder {

        static final String CONDITION_FIELD_CONF_NAME = "condition_field";
        static final String REGEX_CONDITION_CONF_NAME = "regex_condition";
        static final String ORIGIN_FIELD_CONF_NAME = "origin_field";
        static final String DESTINATION_FIELD_CONF_NAME = "destination_field";
        static final String CASES_CONF_NAME = "cases";
        static final String CASES_VALUES_CONF_NAME = "cases_values";
        static final String CASES_DELIM_CONF_NAME = "cases_delim";

        private static final String DEFAULT_DELIM_VALUE = ";";

        private String conditionField;
        private Pattern patternCondition;
        private String originField;
        private String destinationField;
        private String[] cases;
        private String[] casesValues;


        @Override
        public void doConfigure(Context context) {
            conditionField = context.getString(CONDITION_FIELD_CONF_NAME, null);
            if(conditionField != null){
                String regexCondition = context.getString(REGEX_CONDITION_CONF_NAME);
                patternCondition = Pattern.compile(regexCondition);
            }
            originField = context.getString(ORIGIN_FIELD_CONF_NAME);
            destinationField = context.getString(DESTINATION_FIELD_CONF_NAME);
            String casesDelimiter = context.getString(CASES_DELIM_CONF_NAME, DEFAULT_DELIM_VALUE);

            cases = getStringArrayFromConfiguration(context, CASES_CONF_NAME, casesDelimiter);
            casesValues = getStringArrayFromConfiguration(context, CASES_VALUES_CONF_NAME, DEFAULT_DELIM_VALUE);

            Preconditions.checkArgument(cases.length == casesValues.length,
                    "cases length is not equals casesValues length. cases: %s casesValues: %s",
                    cases, casesValues);
        }

        @Override
        public AbstractPresidioJsonInterceptor doBuild() {
            logger.info("Creating JsonFieldSwitchCaseInterceptor: {}={}, {}={}, {}={}, {}={}, {}={}, {}={}",
                    CONDITION_FIELD_CONF_NAME, conditionField, REGEX_CONDITION_CONF_NAME, patternCondition,
                    ORIGIN_FIELD_CONF_NAME, originField, DESTINATION_FIELD_CONF_NAME, destinationField,
                    CASES_CONF_NAME, cases, CASES_VALUES_CONF_NAME, casesValues);
            return new JsonFieldSwitchCaseInterceptor(conditionField, patternCondition, originField,
                    destinationField, cases, casesValues);
        }

    }
}
