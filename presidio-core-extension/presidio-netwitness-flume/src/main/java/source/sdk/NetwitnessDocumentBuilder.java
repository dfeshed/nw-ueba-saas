package source.sdk;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractDocument;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class NetwitnessDocumentBuilder {

    private static Logger logger = LoggerFactory.getLogger(NetwitnessDocumentBuilder.class);
    private static NetwitnessDocumentBuilder INSTANCE;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private NetwitnessDocumentBuilder() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
    }


    public static NetwitnessDocumentBuilder getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new NetwitnessDocumentBuilder();
        }

        return INSTANCE;
    }


    public AbstractDocument buildDocument(Schema schema, Map<String, Object> netwitnessEvent) {
        JSONObject jsonObject = getJsonFromMap(netwitnessEvent);
        AbstractDocument document = null;
        try {
            switch (schema) {
                case AUTHENTICATION: {
                    document = objectMapper.readValue(jsonObject.toString(), domain.NetwitnessAuthenticationMessage.class);
                    break;
                }
                default: {
                    throw new Exception("Failed to get events. Unsupported schema:" + schema.toString());
                }
            }
        } catch (Exception e) {
            final String errorMessage = String.format("Failed to buildDocument for schema:%s, netwitnessEvent:%s", schema, netwitnessEvent);
            logger.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
        return document;
    }


    private JSONObject getJsonFromMap(Map<String, Object> map) throws JSONException {
        JSONObject jsonData = new JSONObject();
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (value instanceof Map<?, ?>) {
                value = getJsonFromMap((Map<String, Object>) value);
            } else if(value instanceof Object[]) {
                Object[] arr = (Object[]) value;
                value = arr.length > 0? arr[0]: null;
            }
            if (value!=null) {
                jsonData.put(key, value);
            }
        }
        return jsonData;
    }
}