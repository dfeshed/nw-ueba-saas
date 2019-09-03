package presidio.input.core.services.transformation.transformer;

import fortscale.domain.core.entityattributes.*;
import fortscale.domain.lastoccurrenceinstant.reader.LastOccurrenceInstantReader;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.TlsRawEvent;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class NewOccurrenceTransformerTest extends TransformerJsonTest {

    @Test
    public void testHierarchyDomainTransformation() {
        TlsRawEvent tlsRawEvent = generateTlsRawEvent();
        TlsRawEvent transformed = transformTlsEvent(tlsRawEvent, "domain.isNewOccurrence");
        Assert.assertTrue(transformed.getDomain().getIsNewOccurrence());
    }

    @Test
    public void testHierarchySslSubjectTransformation() {
        TlsRawEvent tlsRawEvent = generateTlsRawEvent();
        TlsRawEvent transformed = transformTlsEvent(tlsRawEvent, "sslSubject.isNewOccurrence");
        Assert.assertTrue(transformed.getSslSubject().getIsNewOccurrence());
    }

    @Test
    public void testHierarchyJa3Transformation() {
        TlsRawEvent tlsRawEvent = generateTlsRawEvent();
        TlsRawEvent transformed = transformTlsEvent(tlsRawEvent, "ja3.isNewOccurrence");
        Assert.assertTrue(transformed.getJa3().getIsNewOccurrence());
    }

    @Test
    public void testHierarchyDestinationOrganizationTransformation() {
        TlsRawEvent tlsRawEvent = generateTlsRawEvent();
        TlsRawEvent transformed = transformTlsEvent(tlsRawEvent, "dstOrg.isNewOccurrence");
        Assert.assertTrue(transformed.getDstOrg().getIsNewOccurrence());
    }

    @Test
    public void testHierarchyDestinationCountryTransformation() {
        TlsRawEvent tlsRawEvent = generateTlsRawEvent();
        TlsRawEvent transformed = transformTlsEvent(tlsRawEvent, "dstCountry.isNewOccurrence");
        Assert.assertTrue(transformed.getDstCountry().getIsNewOccurrence());
    }

    @Test
    public void testHierarchyDestinationPortTransformation() {
        TlsRawEvent tlsRawEvent = generateTlsRawEvent();
        TlsRawEvent transformed = transformTlsEvent(tlsRawEvent, "dstPort.isNewOccurrence");
        Assert.assertTrue(transformed.getDstPort().getIsNewOccurrence());
    }

    @Test
    public void testHierarchyDestinationAsnTransformation() {
        TlsRawEvent tlsRawEvent = generateTlsRawEvent();
        TlsRawEvent transformed = transformTlsEvent(tlsRawEvent, "dstAsn.isNewOccurrence");
        Assert.assertTrue(transformed.getDstAsn().getIsNewOccurrence());
    }

    private TlsRawEvent transformTlsEvent(TlsRawEvent tlsRawEvent,
                                   String transformFieldName) {
        NewOccurrenceTransformer occurrenceTransformer = generateNewOccurrenceTransformer(transformFieldName);
        try {
            return (TlsRawEvent) transformEvent(tlsRawEvent, occurrenceTransformer, TlsRawEvent.class);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private TlsRawEvent generateTlsRawEvent() {
        Instant laterInstant = Instant.now().plusSeconds(10000L * 182 * 60 * 60);
        return new TlsRawEvent(laterInstant, "TLS", "dataSource", null, "", "", "", "",
                new DestinationCountry("dstCountry"),
                new SslSubject("ssl"), new Domain("google.com"),
                new DestinationOrganization("dstOrg"),
                new DestinationAsn("dstAsn"), 0L, 0L, "", "",
                new Ja3("ja3"), "", "",
                new DestinationPort("dstPort"), null, null, null);
    }

    private NewOccurrenceTransformer generateNewOccurrenceTransformer(String booleanFieldName) {
        LastOccurrenceInstantReader occurrenceInstantReader = Mockito.mock(LastOccurrenceInstantReader.class);
        Mockito.when(occurrenceInstantReader.read(Mockito.any(), Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(Instant.now());
        NewOccurrenceTransformer newOccurrenceTransformer = new NewOccurrenceTransformer(
                "name",
                "tls",
                "sslSubject.name",
                booleanFieldName);
        newOccurrenceTransformer.setLastOccurrenceInstantReader(occurrenceInstantReader);
        newOccurrenceTransformer.setTransformationWaitingDuration(Duration.ZERO);
        newOccurrenceTransformer.setWorkflowStartDate(Instant.EPOCH);
        return newOccurrenceTransformer;
    }

    private ArrayList<AbstractInputDocument> createSingletonList(TlsRawEvent tlsRawEvent) {
        ArrayList<AbstractInputDocument> documents = new ArrayList<>();
        documents.add(tlsRawEvent);
        return documents;
    }

    @Override
    String getResourceFilePath() {
        return "NewOccurrenceTransformer.json";
    }

    @Override
    Class getTransformerClass() {
        return NewOccurrenceTransformer.class;
    }
}
