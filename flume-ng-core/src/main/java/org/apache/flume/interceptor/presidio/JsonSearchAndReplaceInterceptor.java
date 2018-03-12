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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This interceptor is used to modify the of values in the received JSON according to the given patterns/replace-strings.
 * Returns the same JSON with the updated values
 * <p>
 * * The difference between this interceptor and {@link JsonFieldValueReplacerInterceptor} is that this interceptor changes the whole (field) value, the other finds the regex and replaces that part only.
 */
public class JsonSearchAndReplaceInterceptor extends AbstractPresidioJsonInterceptor {

    private static final Logger logger = LoggerFactory
            .getLogger(JsonSearchAndReplaceInterceptor.class);

    private final List<String> fields;
    private final List<String> searchPatterns;
    private final List<String> replaceStrings;


    public JsonSearchAndReplaceInterceptor(List<String> fields, List<String> searchPatterns, List<String> replaceStrings) {
        this.fields = fields;
        this.searchPatterns = searchPatterns;
        this.replaceStrings = replaceStrings;
    }

    @Override
    public Event doIntercept(Event event) {
        final String eventBodyAsString = new String(event.getBody());

        JsonObject eventBodyAsJson;
        eventBodyAsJson = new JsonParser().parse(eventBodyAsString).getAsJsonObject();


        JsonElement originalValue;
        for (int i = 0; i < fields.size(); i++) {
            String currField = fields.get(i);
            Pattern currSearchPattern = Pattern.compile(searchPatterns.get(i));
            String currReplaceString = replaceStrings.get(i);

            originalValue = eventBodyAsJson.get(currField);
            if (originalValue == null || originalValue.isJsonNull()) {
                logger.trace("Field does not exist: {}", currField);
            } else {
                final String originalValueAsString = originalValue.getAsString();
                Matcher matcher = currSearchPattern.matcher(originalValueAsString);
                String newValue = matcher.replaceAll(currReplaceString);
                eventBodyAsJson.addProperty(currField, newValue);
                logger.trace("Field {} was searched and replaces with search pattern {} and replace string {}. original value: {}, new value: {}", currField, currSearchPattern.toString(), currReplaceString, originalValueAsString, newValue);
            }
        }


        event.setBody(eventBodyAsJson.toString().getBytes());
        return event;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("fields", fields)
                .append("searchPatterns", searchPatterns)
                .append("replaceStrings", replaceStrings)
                .toString();
    }

    /**
     * Builder which builds new instance of the JsonSearchAndReplaceInterceptor.
     */
    public static class Builder extends AbstractPresidioInterceptorBuilder {

        private List<String> fields;
        private List<String> searchPatterns;
        private List<String> replaceStrings;

        public static final String DELIMITER_CONF_NAME = "delimiter";
        public static final String DEFAULT_DELIMITER_VALUE = ",";
        public static final String FIELDS_CONF_NAME = "fields";
        public static final String SEARCH_PATTERNS_CONF_NAME = "search_patterns";
        public static final String REPLACE_STRINGS_CONF_NAME = "replace_strings";


        @Override
        public void doConfigure(Context context) {
            String delimiter = context.getString(DELIMITER_CONF_NAME, DEFAULT_DELIMITER_VALUE);
            final String[] fields = getStringArrayFromConfiguration(context, FIELDS_CONF_NAME, delimiter);
            final String[] searchPatterns = getStringArrayFromConfiguration(context, SEARCH_PATTERNS_CONF_NAME, delimiter);
            final String[] replaceStrings = getStringArrayFromConfiguration(context, REPLACE_STRINGS_CONF_NAME, delimiter);

            Preconditions.checkArgument(fields.length == searchPatterns.length,
                    "fields length is not equal to searchPatterns length. fields: %s searchPatterns: %s",
                    fields, searchPatterns);

            Preconditions.checkArgument(fields.length == replaceStrings.length,
                    "fields length is not equal to replaceStrings length. fields: %s replaceStrings: %s",
                    fields, replaceStrings);


            String currField;
            String currSearchPattern;
            String currReplaceString;
            this.fields = new ArrayList<>();
            this.searchPatterns = new ArrayList<>();
            this.replaceStrings = new ArrayList<>();
            for (int i = 0; i < fields.length; i++) {
                currField = fields[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currField), "fields(index=%s) can not be empty. %s=%s.",
                        i, FIELDS_CONF_NAME, Arrays.toString(fields));
                this.fields.add(currField);

                currSearchPattern = searchPatterns[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currSearchPattern), "currSearchPattern(index=%s) can not be empty. %s=%s.",
                        i, SEARCH_PATTERNS_CONF_NAME, Arrays.toString(replaceStrings));
                this.searchPatterns.add(currSearchPattern);

                currReplaceString = replaceStrings[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currReplaceString), "new(index=%s) can not be empty. %s=%s.",
                        i, REPLACE_STRINGS_CONF_NAME, Arrays.toString(replaceStrings));
                this.replaceStrings.add(currReplaceString);
            }

        }

        @Override
        public AbstractPresidioJsonInterceptor doBuild() {
            interceptorName = "JsonSearchAndReplaceInterceptor";
            final JsonSearchAndReplaceInterceptor jsonSearchAndReplaceInterceptor = new JsonSearchAndReplaceInterceptor(fields, searchPatterns, replaceStrings);
            logger.info("Creating {}: {}", interceptorName, jsonSearchAndReplaceInterceptor);
            return jsonSearchAndReplaceInterceptor;
        }

    }
}
