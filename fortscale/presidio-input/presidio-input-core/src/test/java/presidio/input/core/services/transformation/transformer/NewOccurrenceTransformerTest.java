package presidio.input.core.services.transformation.transformer;

import fortscale.common.general.Schema;
import fortscale.domain.lastoccurrenceinstant.reader.LastOccurrenceInstantReader;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.newoccurrencewrappers.Domain;
import presidio.sdk.api.domain.newoccurrencewrappers.Ja3;
import presidio.sdk.api.domain.newoccurrencewrappers.NewOccurrenceWrapper;
import presidio.sdk.api.domain.newoccurrencewrappers.SslSubject;
import presidio.sdk.api.domain.rawevents.TlsRawEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class NewOccurrenceTransformerTest {

    @Test
    public void testHierarchyDomainTransformation() {
        TlsRawEvent tlsRawEvent = generateTlsRawEvent();
        assertNewOccurrenceTransformation(tlsRawEvent, tlsRawEvent.getDomain(), "domain.isNewOccurrence");
    }

    @Test
    public void testHierarchySslSubjectTransformation() {
        TlsRawEvent tlsRawEvent = generateTlsRawEvent();
        assertNewOccurrenceTransformation(tlsRawEvent ,tlsRawEvent.getSslSubject(), "sslSubject.isNewOccurrence");
    }

    @Test
    public void testHierarchyJa3Transformation() {
        TlsRawEvent tlsRawEvent = generateTlsRawEvent();
        assertNewOccurrenceTransformation(tlsRawEvent, tlsRawEvent.getJa3(), "ja3.isNewOccurrence");
    }

    private void assertNewOccurrenceTransformation(TlsRawEvent tlsRawEvent,
                                                   NewOccurrenceWrapper newOccurrenceWrapper,
                                                   String transformFieldName) {
        NewOccurrenceTransformer occurrenceTransformer = generateNewOccurrenceTransformer(transformFieldName);
        Assert.assertFalse(newOccurrenceWrapper.getIsNewOccurrence());
        occurrenceTransformer.transform(createSingletonList(tlsRawEvent));
        Assert.assertTrue(newOccurrenceWrapper.getIsNewOccurrence());
    }

    private TlsRawEvent generateTlsRawEvent() {
        Instant firstInstant = Instant.now();
        Instant laterInstant = firstInstant.plusSeconds(10000L);
        return new TlsRawEvent(laterInstant, "TLS", "dataSource", null, "", "", "", "", "",
                new SslSubject("ssl", false), new Domain("google.com", false), "", "", 0L, 0L, "", "",
                new Ja3("ja3", false), "", "",
                "", null, null, null);
    }

    private NewOccurrenceTransformer generateNewOccurrenceTransformer(String booleanFieldName) {
        LastOccurrenceInstantReader occurrenceInstantReader = Mockito.mock(LastOccurrenceInstantReader.class);
        Mockito.when(occurrenceInstantReader.read(Mockito.any(), Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(Instant.now());
        return new NewOccurrenceTransformer(occurrenceInstantReader,
                Schema.TLS, "eventId", "dateTime", Duration.ZERO.plusSeconds(10L),
                booleanFieldName);
    }

    private ArrayList<AbstractInputDocument> createSingletonList(TlsRawEvent tlsRawEvent) {
        ArrayList<AbstractInputDocument> documents = new ArrayList<>();
        documents.add(tlsRawEvent);
        return documents;
    }
}