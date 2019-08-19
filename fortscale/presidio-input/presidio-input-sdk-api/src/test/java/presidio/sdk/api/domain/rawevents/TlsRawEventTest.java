package presidio.sdk.api.domain.rawevents;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fortscale.domain.core.entityattributes.*;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TlsRawEventTest {

    private ObjectMapper objectMapper = createObjectMapper();

    @Test
    public void testDeserialComplexObject() {
        TlsRawEvent tlsRawEvent = createTlsRawEvent();
        try {
            String rawEventStr = objectMapper.writeValueAsString(tlsRawEvent);
            TlsRawEvent rawEventDeserialized = objectMapper.readValue(rawEventStr, TlsRawEvent.class);
            assertPojosAreEqual(tlsRawEvent, rawEventDeserialized);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test(expected = InvalidDefinitionException.class)
    public void testDeserialFailureOnMissingName() throws IOException {
        TlsRawEvent tlsRawEvent = createTlsRawEvent();
        String missingOccurrenceStr = objectMapper
                .writeValueAsString(tlsRawEvent)
                .replace("\"dstCountry\":{\"name\":\"dstCountry\",\"isNewOccurrence\":null}",
                        "\"dstCountry\":{\"isNewOccurrence\":null}");
        objectMapper.readValue(missingOccurrenceStr, tlsRawEvent.getClass());
    }

    private void assertPojosAreEqual(TlsRawEvent tlsRawEvent, TlsRawEvent rawEventDeserialized) {
        assertEquals(tlsRawEvent.getDomain(), rawEventDeserialized.getDomain());
        assertEquals(tlsRawEvent.getSslSubject(), rawEventDeserialized.getSslSubject());
        assertEquals(tlsRawEvent.getJa3(), rawEventDeserialized.getJa3());
        assertEquals(tlsRawEvent.getDstOrg(), rawEventDeserialized.getDstOrg());
        assertEquals(tlsRawEvent.getDstCountry(), rawEventDeserialized.getDstCountry());
        assertEquals(tlsRawEvent.getDstPort(), rawEventDeserialized.getDstPort());
        assertEquals(tlsRawEvent.getDstAsn(), rawEventDeserialized.getDstAsn());
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        return objectMapper;
    }

    private TlsRawEvent createTlsRawEvent() {
        return new TlsRawEvent(Instant.now(), "TLS", "dataSource", null, "", "", "", "",
                new DestinationCountry("dstCountry"),
                new SslSubject("ssl"), new Domain("google.com"),
                new DestinationOrganization("dstOrg"), new DestinationAsn("dstAsn"), 0L, 0L, "", "",
                new Ja3("ja3"), "", "",
                new DestinationPort("dstPort"), null, null, null);

    }
}