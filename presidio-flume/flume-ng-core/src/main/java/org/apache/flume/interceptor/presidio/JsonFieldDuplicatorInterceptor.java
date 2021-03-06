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
 * This interceptor is used to duplicate certain values  in the received JSON.
 * Returns the same JSON without new keys.
 */
public class JsonFieldDuplicatorInterceptor extends AbstractPresidioJsonInterceptor {
    private static final Logger logger = LoggerFactory
            .getLogger(JsonFieldDuplicatorInterceptor.class);

    private final List<String> originFields;
    private final List<String> destinationFields;
    private final Boolean deleteNullFields;

    public JsonFieldDuplicatorInterceptor(List<String> originFields, List<String> destinationFields, Boolean deleteNullFields) {
        this.originFields = originFields;
        this.destinationFields = destinationFields;
        this.deleteNullFields = deleteNullFields;
    }

    @Override
    public Event doIntercept(Event event) {

        JsonObject eventBodyAsJson = getJsonObject(event);

        String currField;
        for (int i = 0; i < originFields.size(); i++) {
            currField = originFields.get(i);
            JsonElement jsonElement = eventBodyAsJson.get(currField);
            if (eventBodyAsJson.has(currField)) {
                if (jsonElement == null || jsonElement.isJsonNull()) {
                    if (deleteNullFields) {
                        eventBodyAsJson.remove(currField);
                    }
                } else {
                    eventBodyAsJson.add(destinationFields.get(i), jsonElement);
                }
            }
        }

        setJsonObject(event, eventBodyAsJson);
        return event;
    }

    /**
     * Builder which builds new instance of the JsonFieldDuplicatorInterceptor.
     */
    public static class Builder extends AbstractPresidioInterceptorBuilder {

        static final String ORIGIN_FIELDS_CONF_NAME = "originFieldsList";
        static final String DESTINATION_FIELDS_CONF_NAME = "destinationFieldsList";
        static final String DELETE_NULL_FIELDS = "deleteNullFields";
        static final String DELIMITER_CONF_NAME = "delimiter";

        private static final String DEFAULT_DELIMITER_VALUE = ",";
        private static final Boolean DEFAULT_DELETE_NULL_FIELDS_VALUE = true;

        private List<String> originFields;
        private List<String> destinationFields;
        private Boolean deleteNullFields;


        @Override
        public void doConfigure(Context context) {
            deleteNullFields = context.getBoolean(DELETE_NULL_FIELDS, DEFAULT_DELETE_NULL_FIELDS_VALUE);

            String delimiter = context.getString(DELIMITER_CONF_NAME, DEFAULT_DELIMITER_VALUE);

            final String[] originFields = getStringArrayFromConfiguration(context, ORIGIN_FIELDS_CONF_NAME, delimiter);
            final String[] destinationFields = getStringArrayFromConfiguration(context, DESTINATION_FIELDS_CONF_NAME, delimiter);

            Preconditions.checkArgument(originFields.length == destinationFields.length,
                    "originFieldsList length is not equals destinationFieldsList length. originFieldsList: %s destinationFieldsList: %s",
                    originFields, destinationFields);


            String currOriginFilter;
            String currDestinationFilter;
            this.originFields = new ArrayList<>();
            this.destinationFields = new ArrayList<>();
            for (int i = 0; i < originFields.length; i++) {
                currOriginFilter = originFields[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currOriginFilter), "originFieldsList(index=%s) can not be empty. %s=%s.",
                        i, ORIGIN_FIELDS_CONF_NAME, Arrays.toString(originFields));
                this.originFields.add(currOriginFilter);

                currDestinationFilter = destinationFields[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currDestinationFilter), "currDestinationFilter(index=%s) can not be empty. %s=%s.",
                        i, DESTINATION_FIELDS_CONF_NAME, Arrays.toString(destinationFields));
                this.destinationFields.add(currDestinationFilter);
            }

        }

        @Override
        public AbstractPresidioJsonInterceptor doBuild() {
            logger.info("Creating JsonFieldDuplicatorInterceptor: {}={}, {}={}",
                    ORIGIN_FIELDS_CONF_NAME, originFields, DESTINATION_FIELDS_CONF_NAME, destinationFields);
            return new JsonFieldDuplicatorInterceptor(originFields, destinationFields, deleteNullFields);
        }

    }
}
