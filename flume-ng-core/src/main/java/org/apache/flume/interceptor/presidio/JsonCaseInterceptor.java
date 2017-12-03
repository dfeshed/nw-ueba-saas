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
 * This interceptor is used to modify the case of given fields in the received JSON.
 * Returns the same JSON with the updated case
 */
public class JsonCaseInterceptor extends AbstractPresidioJsonInterceptor {

    private static final Logger logger = LoggerFactory
            .getLogger(JsonCaseInterceptor.class);
    private static final String UPPERCASE = "TO_UPPERCASE";
    private static final String LOWERCASE = "TO_LOWERCASE";

    protected static final List<String> SUPPORTED_OPERATIONS = Arrays.asList(UPPERCASE, LOWERCASE);

    private final List<String> originFields;
    private final List<String> operation;

    JsonCaseInterceptor(List<String> originFields, List<String> operation) {
        this.originFields = originFields;
        this.operation = operation;
    }

    @Override
    public Event doIntercept(Event event) {
        final String eventBodyAsString = new String(event.getBody());

        JsonObject eventBodyAsJson;
        eventBodyAsJson = new JsonParser().parse(eventBodyAsString).getAsJsonObject();


        String currField;
        String currOperation;
        JsonElement jsonElement;
        for (int i = 0; i < originFields.size(); i++) {
            currField = originFields.get(i);
            jsonElement = eventBodyAsJson.get(currField);
            if (jsonElement == null || jsonElement.isJsonNull()) {
                logger.trace("Field does not exist: {}", currField);
            } else {
                currOperation = operation.get(i);
                switch (currOperation) {
                    case UPPERCASE:
                        eventBodyAsJson.addProperty(currField, jsonElement.getAsString().toUpperCase());
                        break;
                    case LOWERCASE:
                        eventBodyAsJson.addProperty(currField, jsonElement.getAsString().toLowerCase());
                        break;
                    default:
                        logger.warn("Unsupported operation value: {}. Supported values: {}.",
                                currOperation, SUPPORTED_OPERATIONS);
                        break;
                }
            }
        }


        event.setBody(eventBodyAsJson.toString().getBytes());
        return event;
    }

    /**
     * Builder which builds new instance of the JsonFilterInterceptor.
     */
    public static class Builder extends AbstractPresidioInterceptorBuilder {

        static final String ORIGIN_FIELDS_CONF_NAME = "originFieldsList";
        static final String OPERATIONS_CONF_NAME = "operationsList";
        static final String DELIMITER_CONF_NAME = "delimiter";

        private static final String DEFAULT_DELIMITER_VALUE = ",";

        private List<String> originFields;
        private List<String> operations;


        @Override
        public void doConfigure(Context context) {
            String delimiter = context.getString(DELIMITER_CONF_NAME, DEFAULT_DELIMITER_VALUE);

            final String[] originFields = getStringArrayFromConfiguration(context, ORIGIN_FIELDS_CONF_NAME, delimiter);
            final String[] operations = getStringArrayFromConfiguration(context, OPERATIONS_CONF_NAME, delimiter);

            Preconditions.checkArgument(originFields.length == operations.length,
                    "originFieldsList length is not equal to operations length. originFieldsList: %s operations: %s",
                    originFields, operations);


            String currOriginFilter;
            String currOperation;
            this.originFields = new ArrayList<>();
            this.operations = new ArrayList<>();
            for (int i = 0; i < originFields.length; i++) {
                currOriginFilter = originFields[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currOriginFilter), "originFieldsList(index=%s) can not be empty. %s=%s.",
                        i, ORIGIN_FIELDS_CONF_NAME, Arrays.toString(originFields));
                this.originFields.add(currOriginFilter);

                currOperation = operations[i];
                Preconditions.checkArgument(StringUtils.isNotEmpty(currOperation), "currOperation(index=%s) can not be empty. %s=%s.",
                        i, OPERATIONS_CONF_NAME, Arrays.toString(operations));
                Preconditions.checkArgument(JsonCaseInterceptor.SUPPORTED_OPERATIONS.contains(currOperation),
                        String.format("Unsupported operation value: %s. Supported values: %s.", currOperation, SUPPORTED_OPERATIONS));
                this.operations.add(currOperation);
            }

        }

        @Override
        public AbstractPresidioJsonInterceptor doBuild() {
            logger.info("Creating JsonCaseInterceptor: {}={}, {}={}",
                    ORIGIN_FIELDS_CONF_NAME, originFields, OPERATIONS_CONF_NAME, operations);
            return new JsonCaseInterceptor(originFields, operations);
        }

    }
}
