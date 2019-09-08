package presidio.input.core.services.transformation.transformer;

import fortscale.domain.core.entityattributes.*;
import fortscale.domain.lastoccurrenceinstant.reader.LastOccurrenceInstantReader;
import fortscale.utils.transform.AbstractJsonObjectTransformer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.core.spring.TlsTransformerConfigTest;
import presidio.sdk.api.domain.rawevents.TlsRawEvent;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@Import({TlsTransformerConfigTest.class})
public class NewOccurrenceTransformerTest extends TransformerJsonTest implements ApplicationContextAware {

    @Autowired
    private ApplicationContext applicationContext;

    @MockBean(name = "lastOccurrenceInstantReaderCache")
    private LastOccurrenceInstantReader lastOccurrenceInstantReader;

    @Test
    public void testHierarchyEntityAttributesTransformations() throws IOException {
        TlsRawEvent tlsRawEvent = generateTlsRawEvent();
        for (NewOccurrenceTransformer transformer: generateNewOccurrenceTransformers()) {
            tlsRawEvent = transformTlsEvent(tlsRawEvent, transformer);
        }
        Assert.assertTrue(tlsRawEvent.getDomain().getIsNewOccurrence());
        Assert.assertTrue(tlsRawEvent.getSslSubject().getIsNewOccurrence());
        Assert.assertTrue(tlsRawEvent.getDstAsn().getIsNewOccurrence());
        Assert.assertTrue(tlsRawEvent.getDstPort().getIsNewOccurrence());
        Assert.assertTrue(tlsRawEvent.getDstCountry().getIsNewOccurrence());
        Assert.assertTrue(tlsRawEvent.getDstOrg().getIsNewOccurrence());
        Assert.assertTrue(tlsRawEvent.getJa3().getIsNewOccurrence());
    }

    @Test
    public void testBrokenFirstLevelHierarchy() throws IOException {
        TlsRawEvent tlsRawEvent = new TlsRawEvent(Instant.now(), "TLS", "dataSource", null, "", "", "", "",
                null,
                null, new Domain("google.com"),
                new DestinationOrganization("dstOrg"),
                new DestinationAsn("dstAsn"), 0L, 0L, "", "",
                new Ja3("ja3"), "", "",
                new DestinationPort("dstPort"), null, null, null);
        TlsRawEvent transformed = transformTlsEvent(tlsRawEvent, generateNewOccurrenceTransformers().get(0));
        Assert.assertNull(transformed.getSslSubject());
    }

    private TlsRawEvent transformTlsEvent(TlsRawEvent tlsRawEvent,
                                          NewOccurrenceTransformer transformer) {
        try {
            return (TlsRawEvent) transformEvent(tlsRawEvent, transformer, TlsRawEvent.class);
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

    private List<NewOccurrenceTransformer> generateNewOccurrenceTransformers() throws IOException {
        Mockito.when(lastOccurrenceInstantReader.read(Mockito.any(), Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(Instant.now());
        List<AbstractJsonObjectTransformer> abstractJsonObjectTransformers = loadTransformers("NewOccurrenceTransformers.json");
        return abstractJsonObjectTransformers.stream().map(t -> {
            applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(t, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
            return (NewOccurrenceTransformer)t;
        }).collect(Collectors.toList());
    }

    @Override
    String getResourceFilePath() {
        return "NewOccurrenceTransformer.json";
    }

    @Override
    Class getTransformerClass() {
        return NewOccurrenceTransformer.class;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
