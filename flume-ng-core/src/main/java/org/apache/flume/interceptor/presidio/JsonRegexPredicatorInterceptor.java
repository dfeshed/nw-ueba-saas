package org.apache.flume.interceptor.presidio;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This interceptor is used to indicate whether a field's value matches a regex.
 * Returns the same JSON with additional predicate.
 */
public class JsonRegexPredicatorInterceptor extends AbstractInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(JsonRegexPredicatorInterceptor.class);

    private final List<String> valueFields;
    private final List<String> predicatorFields;
    private final List<String> regexList;
    private final Boolean deleteFields;

    JsonRegexPredicatorInterceptor(List<String> valueFields, List<String> predicitionFields, List<String> regexList, Boolean deleteFields) {
        this.valueFields = valueFields;
        this.predicatorFields = predicitionFields;
        this.regexList = regexList;
        this.deleteFields = deleteFields;
    }

    @Override
    public Event doIntercept(Event event) {
        final String eventBodyAsString = new String(event.getBody());

        JsonObject eventBodyAsJson;
        eventBodyAsJson = new JsonParser().parse(eventBodyAsString).getAsJsonObject();

        String currValueField;
        String currPredicatorField;
        String currRegexField;
        Pattern pattern;
        Matcher matcher;
        String fieldValue;
        for (int i = 0; i < valueFields.size(); i++) {
            currValueField = valueFields.get(i);
            currPredicatorField = predicatorFields.get(i);
            currRegexField = regexList.get(i);

            if (eventBodyAsJson.has(currValueField)) {
                // When the json value contains double backslash ("//") 
                // using JsonObject.getAsString() will return a string with single backslash.
                // This workaround will return the correct value. 
                fieldValue = eventBodyAsJson.get(currValueField).getAsJsonPrimitive().toString();
                fieldValue = fieldValue.substring(1, fieldValue.length() - 1);

                pattern = Pattern.compile(currRegexField);
                matcher = pattern.matcher(fieldValue);
                eventBodyAsJson.addProperty(currPredicatorField, matcher.matches());

                if (deleteFields) {
                    logger.trace("Removing origin field {}.", currValueField);
                    eventBodyAsJson.remove(currValueField);
                }
            } else {
                logger.trace("Current event doesn't contain the key: {}", currValueField);
            }
        }

        event.setBody(eventBodyAsJson.toString().getBytes());
        return event;
    }

    /**
     * Builder which builds new instance of the JsonRegexPredicatorInterceptor.
     */
    public static class Builder extends AbstractPresidioInterceptorBuilder {

        static final String VALUE_FIELDS_CONF_NAME = "valueFieldsList";
        static final String PREDICATOR_FIELDS_CONF_NAME = "predicatorFieldsList";
        static final String REGEX_CONF_NAME = "regexList";
        static final String DELETE_FIELDS_CONF_NAME = "deleteFields";
        static final String DELIMITER_CONF_NAME = "delimiter";
        private static final String DEFAULT_DELIMITER_VALUE = ",";

        private List<String> valueFields;
        private List<String> predicatorFields;
        private List<String> regexList;
        private Boolean deleteFields;

        @Override
        public void doConfigure(Context context) {
            String delimiter = context.getString(DELIMITER_CONF_NAME, DEFAULT_DELIMITER_VALUE);
            deleteFields = context.getBoolean(DELETE_FIELDS_CONF_NAME, false);

            final String[] valueFields = getStringArrayFromConfiguration(context, VALUE_FIELDS_CONF_NAME, delimiter);
            final String[] predicatorFields = getStringArrayFromConfiguration(context, PREDICATOR_FIELDS_CONF_NAME, delimiter);
            final String[] regexFields = getStringArrayFromConfiguration(context, REGEX_CONF_NAME, delimiter);


            Preconditions.checkArgument(valueFields.length == predicatorFields.length,
                    "%s length is not equals %s length. %s: %s %s: %s",
                    VALUE_FIELDS_CONF_NAME, PREDICATOR_FIELDS_CONF_NAME,
                    VALUE_FIELDS_CONF_NAME, Arrays.toString(valueFields), PREDICATOR_FIELDS_CONF_NAME, Arrays.toString(predicatorFields));

            Preconditions.checkArgument(valueFields.length == regexFields.length,
                    "%s length is not equals %s length. %s: %s %s: %s",
                    VALUE_FIELDS_CONF_NAME, REGEX_CONF_NAME,
                    VALUE_FIELDS_CONF_NAME, Arrays.toString(valueFields), REGEX_CONF_NAME, Arrays.toString(regexFields));


            String currValueFilter;
            String currPredicatorFilter;
            String currRegexFilter;
            this.valueFields = new ArrayList<>();
            this.predicatorFields = new ArrayList<>();
            this.regexList = new ArrayList<>();

            for (int i = 0; i < valueFields.length; i++) {
                currValueFilter = valueFields[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currValueFilter), "%s (index=%s) can not be empty. %s=%s.",
                        VALUE_FIELDS_CONF_NAME, i, VALUE_FIELDS_CONF_NAME, Arrays.toString(valueFields));
                this.valueFields.add(currValueFilter);

                currPredicatorFilter = predicatorFields[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currPredicatorFilter), "%s(index=%s) can not be empty. %s=%s.",
                        PREDICATOR_FIELDS_CONF_NAME, i, PREDICATOR_FIELDS_CONF_NAME, Arrays.toString(predicatorFields));
                this.predicatorFields.add(currPredicatorFilter);

                currRegexFilter = regexFields[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currRegexFilter), "%s(index=%s) can not be empty. %s=%s.",
                        REGEX_CONF_NAME, i, REGEX_CONF_NAME, Arrays.toString(regexFields));
                this.regexList.add(currRegexFilter);
            }

        }

        @Override
        public Interceptor build() {
            logger.info("Creating JsonRegexPredicatorInterceptor: {}={}, {}={}, {}={}",
                    VALUE_FIELDS_CONF_NAME, valueFields, PREDICATOR_FIELDS_CONF_NAME, predicatorFields,
                    REGEX_CONF_NAME, regexList);
            return new JsonRegexPredicatorInterceptor(valueFields, predicatorFields, regexList, deleteFields);
        }

    }
}
