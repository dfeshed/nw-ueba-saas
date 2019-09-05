package presidio.input.core.services.transformation.transformer;

import fortscale.domain.core.entityattributes.*;
import fortscale.domain.lastoccurrenceinstant.reader.LastOccurrenceInstantReader;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.TlsRawEvent;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Properties;

@Configuration
@RunWith(SpringRunner.class)
@Import({NewOccurrenceTransformerTest.PropertiesClass.class})
public class NewOccurrenceTransformerTest extends TransformerJsonTest implements ApplicationContextAware {

    @MockBean(name = "lastOccurrenceInstantReaderCache")
    private LastOccurrenceInstantReader lastOccurrenceInstantReader;

    @Test
    public void testHierarchyDomainTransformation() {
        TlsRawEvent tlsRawEvent = generateTlsRawEvent();
        TlsRawEvent transformed = transformTlsEvent(tlsRawEvent, "domain.name", "domain.isNewOccurrence");
        Assert.assertTrue(transformed.getDomain().getIsNewOccurrence());
    }

    @Test
    public void testHierarchySslSubjectTransformation() {
        TlsRawEvent tlsRawEvent = generateTlsRawEvent();
        TlsRawEvent transformed = transformTlsEvent(tlsRawEvent, "sslSubject.name", "sslSubject.isNewOccurrence");
        Assert.assertTrue(transformed.getSslSubject().getIsNewOccurrence());
    }

    @Test
    public void testHierarchyJa3Transformation() {
        TlsRawEvent tlsRawEvent = generateTlsRawEvent();
        TlsRawEvent transformed = transformTlsEvent(tlsRawEvent, "ja3.name", "ja3.isNewOccurrence");
        Assert.assertTrue(transformed.getJa3().getIsNewOccurrence());
    }

    @Test
    public void testHierarchyDestinationOrganizationTransformation() {
        TlsRawEvent tlsRawEvent = generateTlsRawEvent();
        TlsRawEvent transformed = transformTlsEvent(tlsRawEvent, "dstOrg.name", "dstOrg.isNewOccurrence");
        Assert.assertTrue(transformed.getDstOrg().getIsNewOccurrence());
    }

    @Test
    public void testHierarchyDestinationCountryTransformation() {
        TlsRawEvent tlsRawEvent = generateTlsRawEvent();
        TlsRawEvent transformed = transformTlsEvent(tlsRawEvent, "dstCountry.name", "dstCountry.isNewOccurrence");
        Assert.assertTrue(transformed.getDstCountry().getIsNewOccurrence());
    }

    @Test
    public void testHierarchyDestinationPortTransformation() {
        TlsRawEvent tlsRawEvent = generateTlsRawEvent();
        TlsRawEvent transformed = transformTlsEvent(tlsRawEvent, "dstPort.name", "dstPort.isNewOccurrence");
        Assert.assertTrue(transformed.getDstPort().getIsNewOccurrence());
    }

    @Test
    public void testHierarchyDestinationAsnTransformation() {
        TlsRawEvent tlsRawEvent = generateTlsRawEvent();
        TlsRawEvent transformed = transformTlsEvent(tlsRawEvent, "dstAsn.name", "dstAsn.isNewOccurrence");
        Assert.assertTrue(transformed.getDstAsn().getIsNewOccurrence());
    }

    @Test
    public void testBrokenFirstLevelHierarchy() {
        TlsRawEvent tlsRawEvent = new TlsRawEvent(Instant.now(), "TLS", "dataSource", null, "", "", "", "",
                null,
                null, new Domain("google.com"),
                new DestinationOrganization("dstOrg"),
                new DestinationAsn("dstAsn"), 0L, 0L, "", "",
                new Ja3("ja3"), "", "",
                new DestinationPort("dstPort"), null, null, null);
        TlsRawEvent transformed = transformTlsEvent(tlsRawEvent, "sslSubject.name", "sslSubject.isNewOccurrence");
        Assert.assertNull(transformed.getSslSubject());
    }

    @Test(expected = RuntimeException.class)
    // An edge case where the field name exists but the boolean field name doesn't
    public void testBrokenFirstLevelHierarchyBooleanField() {
        TlsRawEvent tlsRawEvent = new TlsRawEvent(Instant.now(), "TLS", "dataSource", null, "", "", "", "",
                null,
                new SslSubject("bla"), new Domain("google.com"),
                new DestinationOrganization("dstOrg"),
                new DestinationAsn("dstAsn"), 0L, 0L, "", "",
                new Ja3("ja3"), "", "",
                new DestinationPort("dstPort"), null, null, null);
        TlsRawEvent transformed = transformTlsEvent(tlsRawEvent, "sslSubject.name", "dstCountry.isNewOccurrence");
        Assert.assertNull(transformed.getSslSubject());
    }

    @Test
    public void testBrokenSecondLevelHierarchy() {
        TlsRawEvent tlsRawEvent = new TlsRawEvent(Instant.now(), "TLS", "dataSource", null, "", "", "", "",
                null,
                new SslSubject("bla"), new Domain("google.com"),
                new DestinationOrganization("dstOrg"),
                new DestinationAsn("dstAsn"), 0L, 0L, "", "",
                new Ja3("ja3"), "", "",
                new DestinationPort("dstPort"), null, null, null);
        TlsRawEvent transformed = transformTlsEvent(tlsRawEvent, "sslSubject.isNewOccurrence", "sslSubject.isNewOccurrence");
        Assert.assertNull(transformed.getSslSubject().getIsNewOccurrence());
    }

    private TlsRawEvent transformTlsEvent(TlsRawEvent tlsRawEvent,
                                   String inputFieldName,
                                   String booleanFieldName) {
        NewOccurrenceTransformer occurrenceTransformer = generateNewOccurrenceTransformer(inputFieldName, booleanFieldName);
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

    private NewOccurrenceTransformer generateNewOccurrenceTransformer(String inputFieldName, String booleanFieldName) {
        LastOccurrenceInstantReader occurrenceInstantReader = Mockito.mock(LastOccurrenceInstantReader.class);
        Mockito.when(occurrenceInstantReader.read(Mockito.any(), Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(Instant.now());
        NewOccurrenceTransformer newOccurrenceTransformer = new NewOccurrenceTransformer(
                "name",
                "tls",
                inputFieldName,
                booleanFieldName);

        //newOccurrenceTransformer.setLastOccurrenceInstantReader(occurrenceInstantReader);
        //newOccurrenceTransformer.setTransformationWaitingDuration(Duration.ZERO);
        //newOccurrenceTransformer.setWorkflowStartDate(Instant.EPOCH);
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

    @Configuration
    @EnableSpringConfigured
    public static class PropertiesClass {
        @Bean
        public static TestPropertiesPlaceholderConfigurer testPropertiesPlaceholderConfigurer() {
            Properties properties = new Properties();
            properties.put("dataPipeline.startTime", "2019-01-01T00:00:00Z");
            properties.put("presidio.input.core.transformation.waiting.duration", Duration.ZERO.toString());
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }
}
