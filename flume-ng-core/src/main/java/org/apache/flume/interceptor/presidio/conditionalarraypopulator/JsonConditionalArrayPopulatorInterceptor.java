package org.apache.flume.interceptor.presidio.conditionalarraypopulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.Validate;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.presidio.AbstractPresidioInterceptorBuilder;
import org.apache.flume.interceptor.presidio.AbstractPresidioJsonInterceptor;
import org.json.JSONObject;

/**
 * Deserializes an {@link Event} to a {@link JSONObject},
 * and uses a {@link ConditionalArrayPopulator} for interception.
 *
 * @author Lior Govrin.
 */
public class JsonConditionalArrayPopulatorInterceptor extends AbstractPresidioJsonInterceptor {
    private ConditionalArrayPopulator conditionalArrayPopulator;

    public JsonConditionalArrayPopulatorInterceptor(ConditionalArrayPopulator conditionalArrayPopulator) {
        this.conditionalArrayPopulator = conditionalArrayPopulator;
    }

    @Override
    public Event doIntercept(Event event) {
        JsonObject jsonObject = getJsonObject(event);
        jsonObject = conditionalArrayPopulator.checkAndPopulateArray(jsonObject);
        setJsonObject(event, jsonObject);
        return event;
    }

    public static final class Builder extends AbstractPresidioInterceptorBuilder {
        public static final String CONFIGURATION_KEY = "configuration";
        private static final Logger logger = Logger.getLogger(Builder.class);
        private static final ObjectMapper objectMapper = new ObjectMapper();

        private ConditionalArrayPopulator conditionalArrayPopulator;

        @Override
        protected AbstractPresidioJsonInterceptor doBuild() {
            return new JsonConditionalArrayPopulatorInterceptor(conditionalArrayPopulator);
        }

        @Override
        protected void doConfigure(Context context) {
            String configuration = context.getString(CONFIGURATION_KEY);

            try {
                conditionalArrayPopulator = objectMapper.readValue(configuration, ConditionalArrayPopulator.class);
                Validate.notNull(conditionalArrayPopulator, "%s was deserialized to null.", CONFIGURATION_KEY);
            } catch (Exception e) {
                String msg = String.format("Failed deserializing %s.", configuration);
                logger.error(msg, e);
                throw new IllegalArgumentException(msg, e);
            }
        }
    }
}
