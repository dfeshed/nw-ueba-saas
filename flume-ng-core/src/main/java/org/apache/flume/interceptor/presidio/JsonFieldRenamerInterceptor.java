package org.apache.flume.interceptor.presidio;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This interceptor is used to modify the names of the fields in the received JSON according to the given configuration.
 * Returns the same JSON with the updated values
 */
public class JsonFieldRenamerInterceptor extends AbstractPresidioJsonInterceptor {

    private static final Logger logger = LoggerFactory
            .getLogger(JsonFieldRenamerInterceptor.class);

    private final List<String> originFields;
    private final List<String> destinationFields;
    private final Boolean deleteNullFields;
    private String originFieldsDelim;

    JsonFieldRenamerInterceptor(List<String> originFields, List<String> destinationFields, Boolean deleteMissingFields, String originFieldsDelim) {
        this.originFields = originFields;
        this.destinationFields = destinationFields;
        this.deleteNullFields = deleteMissingFields;
        this.originFieldsDelim = originFieldsDelim;
    }

    @Override
    public Event doIntercept(Event event) {
        final String eventBodyAsString = new String(event.getBody());

        JsonObject eventBodyAsJson;
        eventBodyAsJson = new JsonParser().parse(eventBodyAsString).getAsJsonObject();


        String currFieldsString;
        for (int i = 0; i < originFields.size(); i++) {
            currFieldsString = originFields.get(i);
            final String currDestField = destinationFields.get(i);
            if (currFieldsString.startsWith("[") && currFieldsString.endsWith("]")) {
                currFieldsString = currFieldsString.substring(1, currFieldsString.length() - 1);
                final String[] currOriginFields = currFieldsString.split(originFieldsDelim);
                for (String currOriginField : currOriginFields) {
                    final boolean isRenameDone = handleField(eventBodyAsJson, currOriginField, currDestField);
                    if (isRenameDone) {
                        break;
                    }
                }
            } else {
                handleField(eventBodyAsJson, currFieldsString, currDestField);
            }
        }

        event.setBody(eventBodyAsJson.toString().getBytes());
        return event;
    }

    private boolean handleField(JsonObject eventBodyAsJson, String originField, String destField) {
        JsonElement jsonElement = eventBodyAsJson.get(originField);
        boolean originFieldExists = eventBodyAsJson.has(originField);
        if (originFieldExists) {
            if (jsonElement == null || jsonElement.isJsonNull()) {
                if (deleteNullFields) {
                    eventBodyAsJson.remove(originField);
                    originFieldExists = false;
                }
            } else {
                if (!originField.equals(destField)) { //when we use the multi orig field option(with []), sometimes the original field can be identical to the dest field
                    eventBodyAsJson.add(destField, jsonElement);
                    eventBodyAsJson.remove(originField);
                } else {
                    logger.trace("Rename for field {} was not done since original field name and dest field name are identical.", originField);
                }
            }
        }

        return originFieldExists;
    }


    /**
     * Builder which builds new instance of the JsonFieldRenamerInterceptor.
     */
    public static class Builder extends AbstractPresidioInterceptorBuilder {

        static final String ORIGIN_FIELDS_CONF_NAME = "originFieldsList";
        static final String DESTINATION_FIELDS_CONF_NAME = "destinationFieldsList";
        static final String DELETE_NULL_FIELDS = "deleteNullFields";
        static final String DELIMITER_CONF_NAME = "delimiter";
        static final String ORIGIN_FIELDS_DELIM_CONF_NAME = "originFieldsDelim";

        private static final String DEFAULT_DELIMITER_VALUE = ",";
        private static final String DEFAULT_ORIGIN_FIELDS_DELIM_VALUE = ";";
        private static final Boolean DEFAULT_DELETE_NULL_FIELDS_VALUE = true;

        private List<String> originFields;
        private List<String> destinationFields;
        private Boolean deleteNullFields;
        private String originFieldsDelim;


        @Override
        public void doConfigure(Context context) {
            deleteNullFields = context.getBoolean(DELETE_NULL_FIELDS, DEFAULT_DELETE_NULL_FIELDS_VALUE);

            String delimiter = context.getString(DELIMITER_CONF_NAME, DEFAULT_DELIMITER_VALUE);
            originFieldsDelim = context.getString(ORIGIN_FIELDS_DELIM_CONF_NAME, DEFAULT_ORIGIN_FIELDS_DELIM_VALUE);

            final String[] originFields = getStringArrayFromConfiguration(context, ORIGIN_FIELDS_CONF_NAME, delimiter);
            final String[] destinationFields = getStringArrayFromConfiguration(context, DESTINATION_FIELDS_CONF_NAME, delimiter);

            Preconditions.checkArgument(originFields.length == destinationFields.length,
                    "originFieldsList length is not equals destinationFieldsList length. originFieldsList: %s destinationFieldsList: %s",
                    originFields, destinationFields);


            String currOriginField;
            String currDestinationField;
            this.originFields = new ArrayList<>();
            this.destinationFields = new ArrayList<>();
            for (int i = 0; i < originFields.length; i++) {
                currOriginField = originFields[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currOriginField), "originFieldsList(index=%s) can not be empty. %s=%s.",
                        i, ORIGIN_FIELDS_CONF_NAME, Arrays.toString(originFields));
                this.originFields.add(currOriginField);

                currDestinationField = destinationFields[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currDestinationField), "currDestinationField(index=%s) can not be empty. %s=%s.",
                        i, DESTINATION_FIELDS_CONF_NAME, Arrays.toString(destinationFields));
                this.destinationFields.add(currDestinationField);
            }

        }

        @Override
        public AbstractPresidioJsonInterceptor doBuild() {
            logger.info("Creating JsonFieldRenamerInterceptor: {}={}, {}={}",
                    ORIGIN_FIELDS_CONF_NAME, originFields, DESTINATION_FIELDS_CONF_NAME, destinationFields);
            return new JsonFieldRenamerInterceptor(originFields, destinationFields, deleteNullFields, originFieldsDelim);
        }

    }
}
