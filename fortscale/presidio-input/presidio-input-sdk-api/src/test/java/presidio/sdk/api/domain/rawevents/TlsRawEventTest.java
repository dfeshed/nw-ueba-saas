package presidio.sdk.api.domain.rawevents;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;
import presidio.sdk.api.domain.newoccurrencewrappers.*;

import java.io.IOException;
import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TlsRawEventTest {

    private ObjectMapper objectMapper = createObjectMapper();

    @Test
    public void testDeserialComplexObject() {
        TlsRawEvent tlsRawEvent = createTlsRawEvent(true);
        try {
            String rawEventStr = objectMapper.writeValueAsString(tlsRawEvent);
            TlsRawEvent rawEventDeserialized = objectMapper.readValue(rawEventStr, tlsRawEvent.getClass());
            assertPojosAreEqual(tlsRawEvent, rawEventDeserialized);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testDeserialComplexObjectMissingOccurrenceField() {
        TlsRawEvent tlsRawEvent = createTlsRawEvent(false);
        try {
            String missingOccurrenceStr = objectMapper
                    .writeValueAsString(tlsRawEvent)
                    .replace(",\"isNewOccurrence\":true", "");
            TlsRawEvent rawEventDeserialized = objectMapper.readValue(missingOccurrenceStr, tlsRawEvent.getClass());
            assertPojosAreEqual(tlsRawEvent, rawEventDeserialized);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
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

    private TlsRawEvent createTlsRawEvent(boolean isNewOccurrence) {
        return new TlsRawEvent(Instant.now(), "TLS", "dataSource", null, "", "", "", "",
                new DestinationCountry("dstCountry", isNewOccurrence),
                new SslSubject("ssl", isNewOccurrence), new Domain("google.com", isNewOccurrence),
                new DestinationOrganization("dstOrg", isNewOccurrence), new DestinationAsn("dstAsn", isNewOccurrence), 0L, 0L, "", "",
                new Ja3("ja3", isNewOccurrence), "", "",
                new DestinationPort("dstPort", isNewOccurrence), null, null, null);

    }
}