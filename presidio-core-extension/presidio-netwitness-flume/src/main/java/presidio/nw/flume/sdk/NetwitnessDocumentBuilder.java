package presidio.nw.flume.sdk;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.nw.flume.domain.NetwitnessEvent;

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
        AbstractDocument document = null;
        try {
             String jsonResp = objectMapper.writeValueAsString(netwitnessEvent);
             NetwitnessEvent ns = new NetwitnessEvent();
             document = objectMapper.readValue(jsonResp, NetwitnessEvent.class);
        } catch (Exception e) {
            final String errorMessage = String.format("Failed to buildDocument for schema:%s, netwitnessEvent:%s", schema, netwitnessEvent);
            logger.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
        return document;
    }

}