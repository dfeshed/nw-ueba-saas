
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This interceptor is used to replace certain values (regex) in the received JSON.
 * Returns the same JSON without with the new values.
 * i.e (using default delimiters) - the given input 'type#fileDelete>file_delete' means - for field 'type' change value from 'fileDelete' to 'file_delete'.
 * removeEscapeChars = true (default = false) will remove escape characters that java properties added before ':' and '=' chars. use if the regex-es you use contain  ':' or '='
 */
public class JsonFieldValueReplacerInterceptor extends AbstractInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JsonFilterInterceptor.class);


    private static final String EMPTY_STRING = "EMPTY_STRING";


    private final List<FieldValueReplacement> replacements;
    private final boolean removeEscapeChars;

    JsonFieldValueReplacerInterceptor(List<FieldValueReplacement> replacements, boolean removeEscapeChars) {
        this.replacements = replacements;
        this.removeEscapeChars = removeEscapeChars;
    }

    @Override
    public Event doIntercept(Event event) {
        final String eventBodyAsString = new String(event.getBody());
        JsonObject eventBodyAsJson = new JsonParser().parse(eventBodyAsString).getAsJsonObject();

        String currField;
        String currValueToReplaceRegexValue;
        String currNewValue;
        for (FieldValueReplacement replacement : replacements) {
            currField = replacement.field;
            currValueToReplaceRegexValue = replacement.valueToReplaceRegex;
            if (removeEscapeChars) {
                currValueToReplaceRegexValue = currValueToReplaceRegexValue.replace("\\:", ":");
                currValueToReplaceRegexValue = currValueToReplaceRegexValue.replace("\\=", "=");
            }
            currNewValue = replacement.newValue;
            if (currNewValue.equals(EMPTY_STRING)) {
                currNewValue = "";
            }
            final JsonElement realValueForField = eventBodyAsJson.get(currField);
            if (realValueForField != null) {
                final String realValueAsString = realValueForField.getAsString();
                if (Pattern.compile(currValueToReplaceRegexValue).matcher(realValueAsString).matches()) {
                    eventBodyAsJson.addProperty(currField, currNewValue);
                    logger.trace("Field {} was replaced from {} to {}.", currField, currValueToReplaceRegexValue, currNewValue);
                } else {
                    logger.info("Field {} exists but real value [{}] doesn't match the given valueToReplaceRegex value [{}]. Replacement [{}]. No replacement made", currField, realValueAsString, currValueToReplaceRegexValue, replacement);
                }
            } else {
                logger.warn("Field {} doesn't exist. Replacement [{}]. No replacement made", currField, replacement);
            }
        }

        event.setBody(eventBodyAsJson.toString().getBytes());
        return event;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("replacements", replacements)
                .append("removeEscapeChars", removeEscapeChars)
                .toString();
    }


    /**
     * This class represents a replacement. For field {@link #field} replace {@link #valueToReplaceRegex} with {@link #newValue}
     */
    public static class FieldValueReplacement {
        final String field;
        final String valueToReplaceRegex;
        final String newValue;

        FieldValueReplacement(String field, String valueToReplaceRegex, String newValue) {
            this.field = field;
            this.valueToReplaceRegex = valueToReplaceRegex;
            this.newValue = newValue;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("field", field)
                    .append("valueToReplaceRegex", valueToReplaceRegex)
                    .append("newValue", newValue)
                    .toString();
        }
    }


    /**
     * Builder which builds new instance of the JsonFieldValueReplacerInterceptor.
     */
    public static class Builder implements Interceptor.Builder {

        static final String REPLACEMENTS_CONF_NAME = "replacements";
        static final String REMOVE_ESCAPE_CHARS = "remove_escape_chars";

        static final String DELIMITER_CONF_NAME = "delimiter";
        static final String FIELD_DELIMITER_CONF_NAME = "field_delimiter";
        static final String VALUES_DELIMITER_CONF_NAME = "value_delimiter";


        private static final String DEFAULT_DELIMITER_VALUE = ",";
        private static final String DEFAULT_FIELD_DELIMITER_VALUE = "#";
        private static final String DEFAULT_VALUES_DELIMITER_VALUE = ">";
        private static final boolean DEFAULT_REMOVE_ESCAPE_CHARS = false;

        private List<FieldValueReplacement> replacements;
        private boolean removeEscapeChars;

        @Override
        public void configure(Context context) {
            String replacementsAsString = context.getString(REPLACEMENTS_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(replacementsAsString), REPLACEMENTS_CONF_NAME + " can not be empty.");

            String delim = context.getString(DELIMITER_CONF_NAME, DEFAULT_DELIMITER_VALUE);
            String fieldDelim = context.getString(FIELD_DELIMITER_CONF_NAME, DEFAULT_FIELD_DELIMITER_VALUE);
            String valueDelim = context.getString(VALUES_DELIMITER_CONF_NAME, DEFAULT_VALUES_DELIMITER_VALUE);
            removeEscapeChars = context.getBoolean(REMOVE_ESCAPE_CHARS, DEFAULT_REMOVE_ESCAPE_CHARS);



            /* separate to replacements */
            final String[] replacementsArray = replacementsAsString.split(delim);

            /* just initializations */
            String currReplacementAsString;
            replacements = new ArrayList<>();
            String[] currValuesArray;
            String valueToReplaceRegex;
            String currNewValue;
            String currField;
            String currValuesAsString;

            for (int i = 0; i < replacementsArray.length; i++) {
                /* get current 'replacement' */
                currReplacementAsString = replacementsArray[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currReplacementAsString), "%s(index=%s) can not be empty. %s=%s.", REPLACEMENTS_CONF_NAME, i, REPLACEMENTS_CONF_NAME, replacementsAsString);


                /* parse 'field' and 'values' from 'replacement' */
                final String[] currReplacementArray = currReplacementAsString.split(fieldDelim);
                currField = currReplacementArray[0];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currField), "%s - Invalid value %s. field can't be empty.", REPLACEMENTS_CONF_NAME, currReplacementAsString);
                currValuesAsString = currReplacementArray[1];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currValuesAsString), "{} - Invalid value %s. Values (valueToReplaceRegex>new) can't be empty.", REPLACEMENTS_CONF_NAME, currReplacementAsString);

                /* parse 'valueToReplaceRegex value' and 'new value' from 'values' */
                currValuesArray = currValuesAsString.split(valueDelim);
                valueToReplaceRegex = currValuesArray[0];
                Preconditions.checkArgument(StringUtils.isNotEmpty(valueToReplaceRegex), "%s - Invalid value %s. valueToReplaceRegex value can't be empty.", REPLACEMENTS_CONF_NAME, currReplacementAsString);
                currNewValue = currValuesArray[1];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currNewValue), "%s - Invalid value %s. New value can't be empty.", REPLACEMENTS_CONF_NAME, currReplacementAsString);


                FieldValueReplacement currReplacement = new FieldValueReplacement(currField, valueToReplaceRegex, currNewValue);
                replacements.add(currReplacement);
            }

        }

        @Override
        public Interceptor build() {
            final JsonFieldValueReplacerInterceptor jsonFieldValueReplacerInterceptor = new JsonFieldValueReplacerInterceptor(replacements, removeEscapeChars);
            logger.info("Creating JsonFieldValueReplacerInterceptor: {}", jsonFieldValueReplacerInterceptor);
            return jsonFieldValueReplacerInterceptor;
        }
    }
}
