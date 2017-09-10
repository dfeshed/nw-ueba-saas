
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

/**
 * This interceptor is used to replace certain values in the received JSON.
 * Returns the same JSON without with the new values.
 * i.e (using default delimiters) - the given input 'type#fileDelete>file_delete' means - for field 'type' change value from 'fileDelete' to 'file_delete'.
 */
public class JsonFieldValueReplacerInterceptor extends AbstractInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JsonFilterInterceptor.class);


    private final List<FieldValueReplacement> replacements;

    JsonFieldValueReplacerInterceptor(List<FieldValueReplacement> replacements) {
        this.replacements = replacements;
    }

    @Override
    public Event doIntercept(Event event) {
        final String eventBodyAsString = new String(event.getBody());
        JsonObject eventBodyAsJson = new JsonParser().parse(eventBodyAsString).getAsJsonObject();

        String currField;
        String currOriginalValue;
        String currNewValue;
        for (FieldValueReplacement replacement : replacements) {
            currField = replacement.field;
            currOriginalValue = replacement.originalValue;
            currNewValue = replacement.newValue;
            final JsonElement realValueForField = eventBodyAsJson.get(currField);
            if (realValueForField != null) {
                final String realValueAsString = realValueForField.getAsString();
                if (realValueAsString.equals(currOriginalValue)) {
                    eventBodyAsJson.addProperty(currField, currNewValue);
                    logger.trace("Field {} was replaced from {} to {}.", currField, currOriginalValue, currNewValue);
                } else {
                    logger.info("Field {} exists but real value [{}] doesn't match the given original value [{}]. Replacement [{}]. No replacement made", currField, realValueAsString, currOriginalValue, replacement);
                }
            } else {
                logger.warn("Field {} doesn't exist. Replacement [{}]. No replacement made", currField, replacement);
            }
        }

        event.setBody(eventBodyAsJson.toString().getBytes());
        return event;
    }


    /**
     * This class represents a replacement. For field {@link #field} replace {@link #originalValue} with {@link #newValue}
     */
    public static class FieldValueReplacement {
        final String field;
        final String originalValue;
        final String newValue;

        FieldValueReplacement(String field, String originalValue, String newValue) {
            this.field = field;
            this.originalValue = originalValue;
            this.newValue = newValue;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("field", field)
                    .append("originalValue", originalValue)
                    .append("newValue", newValue)
                    .toString();
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("replacements", replacements)
                .toString();
    }

    /**
     * Builder which builds new instance of the JsonFieldValueReplacerInterceptor.
     */
    public static class Builder implements Interceptor.Builder {

        static final String REPLACEMENTS_CONF_NAME = "replacements";

        static final String DELIMITER_CONF_NAME = "delimiter";
        static final String FIELD_DELIMITER_CONF_NAME = "field_delimiter";
        static final String VALUES_DELIMITER_CONF_NAME = "value_delimiter";

        private static final String DEFAULT_DELIMITER_VALUE = ",";
        private static final String DEFAULT_FIELD_DELIMITER_VALUE = "#";
        private static final String DEFAULT_VALUES_DELIMITER_VALUE = ">";

        private List<FieldValueReplacement> replacements;

        @Override
        public void configure(Context context) {
            String replacementsAsString = context.getString(REPLACEMENTS_CONF_NAME);
            Preconditions.checkArgument(StringUtils.isNotEmpty(replacementsAsString), REPLACEMENTS_CONF_NAME + " can not be empty.");

            String delim = context.getString(DELIMITER_CONF_NAME, DEFAULT_DELIMITER_VALUE);
            String fieldDelim = context.getString(FIELD_DELIMITER_CONF_NAME, DEFAULT_FIELD_DELIMITER_VALUE);
            String valueDelim = context.getString(VALUES_DELIMITER_CONF_NAME, DEFAULT_VALUES_DELIMITER_VALUE);



            /* separate to replacements */
            final String[] replacementsArray = replacementsAsString.split(delim);

            /* just initializations */
            String currReplacementAsString;
            replacements = new ArrayList<>();
            String[] currValuesArray;
            String currOriginalValue;
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
                Preconditions.checkArgument(StringUtils.isNotEmpty(currValuesAsString), "{} - Invalid value %s. Values (original>new) can't be empty.", REPLACEMENTS_CONF_NAME, currReplacementAsString);

                /* parse 'original value' and 'new value' from 'values' */
                currValuesArray = currValuesAsString.split(valueDelim);
                currOriginalValue = currValuesArray[0];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currOriginalValue), "%s - Invalid value %s. Original value can't be empty.", REPLACEMENTS_CONF_NAME, currReplacementAsString);
                currNewValue = currValuesArray[1];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currNewValue), "%s - Invalid value %s. New value can't be empty.", REPLACEMENTS_CONF_NAME, currReplacementAsString);


                FieldValueReplacement currReplacement = new FieldValueReplacement(currField, currOriginalValue, currNewValue);
                replacements.add(currReplacement);
            }

        }

        @Override
        public Interceptor build() {
            final JsonFieldValueReplacerInterceptor jsonFieldValueReplacerInterceptor = new JsonFieldValueReplacerInterceptor(replacements);
            logger.info("Creating JsonFieldValueReplacerInterceptor: {}", jsonFieldValueReplacerInterceptor);
            return jsonFieldValueReplacerInterceptor;
        }
    }
}
