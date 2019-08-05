package presidio.input.core.services.transformation.transformer;

import fortscale.common.general.Schema;
import fortscale.domain.lastoccurrenceinstant.reader.LastOccurrenceInstantReader;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.newoccurrencewrappers.Domain;
import presidio.sdk.api.domain.rawevents.TlsRawEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class NewOccurrenceTransformerTest {

    @Test
    public void testHierarchyDomainTransformation() {
        Instant firstInstant = Instant.now();
        Instant laterInstant = firstInstant.plusSeconds(10000L);
        TlsRawEvent tlsRawEvent = new TlsRawEvent(laterInstant, "TLS", "dataSource", null, "", "", "", "", "",
                "", new Domain("google.com", false), "", "", 0L, 0L, "", "", "", "", "",
                "", null, null, null);
        LastOccurrenceInstantReader occurrenceInstantReader = Mockito.mock(LastOccurrenceInstantReader.class);
        Mockito.when(occurrenceInstantReader.read(Mockito.any(), Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(Instant.now());
        NewOccurrenceTransformer occurrenceTransformer = new NewOccurrenceTransformer(occurrenceInstantReader,
                Schema.TLS, "eventId", "dateTime", Duration.ZERO.plusSeconds(10L),
                "domain.isNewOccurrence");
        ArrayList<AbstractInputDocument> documents = new ArrayList<>();
        documents.add(tlsRawEvent);

        Assert.assertFalse(tlsRawEvent.getDomain().getIsNewOccurrence());
        occurrenceTransformer.transform(documents);
        Assert.assertTrue(tlsRawEvent.getDomain().getIsNewOccurrence());
    }
}