package org.apache.flume.interceptor.presidio.transform;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.Validate;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.presidio.AbstractPresidioInterceptorBuilder;
import org.apache.flume.interceptor.presidio.AbstractPresidioJsonInterceptor;
import org.json.JSONObject;

import java.io.File;

public class TransformerInterceptor extends AbstractPresidioJsonInterceptor {

    private IJsonObjectTransformer transformer;

    public TransformerInterceptor(IJsonObjectTransformer transformer) {
        this.transformer = transformer;
    }

    @Override
    public Event doIntercept(Event event) {
        String body = new String(event.getBody());
        JSONObject jsonObject = new JSONObject(body);
        jsonObject = transformer.transform(jsonObject);
        if(jsonObject != null) {
            event.setBody(jsonObject.toString().getBytes());
            return event;
        } else{
            return null;
        }
    }

    public static final class Builder extends AbstractPresidioInterceptorBuilder {
        public static final String CONFIGURATION_KEY = "configuration";
        public static final String CONFIGURATION_FILE_PATH = "configuration_path";
        private static final Logger logger = Logger.getLogger(Builder.class);
        private static final ObjectMapper objectMapper = new ObjectMapper();

        private IJsonObjectTransformer transformer;

        @Override
        protected AbstractPresidioJsonInterceptor doBuild() {
            return new TransformerInterceptor(transformer);
        }

        @Override
        protected void doConfigure(Context context) {
            String configuration = context.getString(CONFIGURATION_KEY, null);
            if(configuration != null){
                try {
                    transformer = objectMapper.readValue(configuration, IJsonObjectTransformer.class);
                } catch (Exception e) {
                    String msg = String.format("Failed deserializing %s.", configuration);
                    logger.error(msg, e);
                    throw new IllegalArgumentException(msg, e);
                }
            } else {
                String configurationFilePath = context.getString(CONFIGURATION_FILE_PATH);
                try {
                    transformer = objectMapper.readValue(new File(configurationFilePath), IJsonObjectTransformer.class);
                } catch (Exception e) {
                    String msg = String.format("Failed deserializing %s.", configurationFilePath);
                    logger.error(msg, e);
                    throw new IllegalArgumentException(msg, e);
                }
            }
        }
    }
}
