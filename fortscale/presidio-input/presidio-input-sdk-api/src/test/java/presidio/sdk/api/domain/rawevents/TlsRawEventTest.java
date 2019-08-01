package presidio.sdk.api.domain.rawevents;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TlsRawEventTest {

    @Test
    public void testDeserialComplexObject() {
        TlsRawEvent tlsRawEvent = new TlsRawEvent(Instant.now(), "TLS", "dataSource", null, "", "", "", "", "",
                "", new Domain("google.com", true), "", "", 0L, 0L, "", "", "", "", "",
                "", null, null, null);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);

        try {
            String rawEventStr = objectMapper.writeValueAsString(tlsRawEvent);
            TlsRawEvent rawEventDeserialized = objectMapper.readValue(rawEventStr, tlsRawEvent.getClass());
            assertEquals(tlsRawEvent.getDomain(), rawEventDeserialized.getDomain());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}