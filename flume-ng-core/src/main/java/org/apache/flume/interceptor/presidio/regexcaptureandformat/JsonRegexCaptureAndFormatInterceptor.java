package org.apache.flume.interceptor.presidio.regexcaptureandformat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.Validate;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.presidio.AbstractPresidioInterceptorBuilder;
import org.apache.flume.interceptor.presidio.AbstractPresidioJsonInterceptor;

/**
 * Deserializes an {@link Event} to a {@link JsonObject},
 * and uses a {@link JsonRegexCaptorAndFormatter} for interception.
 *
 * @author Lior Govrin.
 */
public class JsonRegexCaptureAndFormatInterceptor extends AbstractPresidioJsonInterceptor {
    private JsonRegexCaptorAndFormatter jsonRegexCaptorAndFormatter;

    public JsonRegexCaptureAndFormatInterceptor(JsonRegexCaptorAndFormatter jsonRegexCaptorAndFormatter) {
        this.jsonRegexCaptorAndFormatter = jsonRegexCaptorAndFormatter;
    }

    @Override
    public Event doIntercept(Event event) {
        JsonObject jsonObject = getJsonObject(event);
        jsonObject = jsonRegexCaptorAndFormatter.captureAndFormat(jsonObject);
        setJsonObject(event, jsonObject);
        return event;
    }

    public static final class Builder extends AbstractPresidioInterceptorBuilder {
        public static final String CONFIGURATION_KEY = "configuration";
        private static final Logger logger = Logger.getLogger(Builder.class);
        private static final ObjectMapper objectMapper = new ObjectMapper();

        private JsonRegexCaptorAndFormatter jsonRegexCaptorAndFormatter;

        @Override
        protected AbstractPresidioJsonInterceptor doBuild() {
            return new JsonRegexCaptureAndFormatInterceptor(jsonRegexCaptorAndFormatter);
        }

        @Override
        protected void doConfigure(Context context) {
            String configuration = context.getString(CONFIGURATION_KEY);

            try {
                jsonRegexCaptorAndFormatter = objectMapper.readValue(configuration, JsonRegexCaptorAndFormatter.class);
                Validate.notNull(jsonRegexCaptorAndFormatter, "%s was deserialized to null.", CONFIGURATION_KEY);
            } catch (Exception e) {
                String msg = String.format("Failed deserializing %s.", configuration);
                logger.error(msg, e);
                throw new IllegalArgumentException(msg, e);
            }
        }
    }
}
