package presidio.input.core.services.transformation.transformer;

import fortscale.common.general.Schema;
import fortscale.domain.lastoccurrenceinstant.reader.LastOccurrenceInstantReader;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.newoccurrencewrappers.Domain;
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
        NewOccurrenceTransformer occurrenceTransformer = generateNewOccurrenceTransformer("domain.isNewOccurrence");
        Assert.assertFalse(tlsRawEvent.getDomain().getIsNewOccurrence());
        occurrenceTransformer.transform(createSingletonList(tlsRawEvent));
        Assert.assertTrue(tlsRawEvent.getDomain().getIsNewOccurrence());
    }

    @Test
    public void testHierarchySslSubjectTransformation() {
        TlsRawEvent tlsRawEvent = generateTlsRawEvent();
        NewOccurrenceTransformer occurrenceTransformer = generateNewOccurrenceTransformer("sslSubject.isNewOccurrence");
        Assert.assertFalse(tlsRawEvent.getSslSubject().getIsNewOccurrence());
        occurrenceTransformer.transform(createSingletonList(tlsRawEvent));
        Assert.assertTrue(tlsRawEvent.getSslSubject().getIsNewOccurrence());
    }

    private TlsRawEvent generateTlsRawEvent() {
        Instant firstInstant = Instant.now();
        Instant laterInstant = firstInstant.plusSeconds(10000L);
        return new TlsRawEvent(laterInstant, "TLS", "dataSource", null, "", "", "", "", "",
                new SslSubject("ssl", false), new Domain("google.com", false), "", "", 0L, 0L, "", "", "", "", "",
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