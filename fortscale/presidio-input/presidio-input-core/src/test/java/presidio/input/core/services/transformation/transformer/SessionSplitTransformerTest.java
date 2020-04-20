package presidio.input.core.services.transformation.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.entityattributes.*;
import fortscale.domain.sessionsplit.cache.ISessionSplitStoreCache;
import fortscale.domain.sessionsplit.cache.SessionSplitStoreCacheConfiguration;
import fortscale.domain.sessionsplit.cache.SessionSplitStoreCacheImpl;
import fortscale.domain.sessionsplit.records.SessionSplitTransformerKey;
import fortscale.domain.sessionsplit.records.SessionSplitTransformerValue;
import org.json.JSONObject;
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
import presidio.input.core.spring.TransformerConfigTest;
import presidio.sdk.api.domain.rawevents.TlsRawEvent;
import presidio.sdk.api.domain.transformedevents.TlsTransformedEvent;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@Import({TransformerConfigTest.class})
public class SessionSplitTransformerTest extends TransformerJsonTest implements ApplicationContextAware {

    @Autowired
    private ApplicationContext applicationContext;

    @MockBean
    private ISessionSplitStoreCache sessionSplitStoreCache;


    /**
     * Test there are no failures, where no SessionSplit field exist
     * @throws IOException
     */
    @Test
    public void testSessionSplitFieldNull() throws IOException {
        SessionSplitTransformer sessionSplitTransformer = (SessionSplitTransformer) loadTransformer(getResourceFilePath());
        TlsTransformedEvent tlsTransformedEvent = new TlsTransformedEvent(generateTlsRawEvent(null, null, null, null, null,
                null, null, null, "123", "123", "123", new DestinationPort("dstPort")));
        ObjectMapper objectMapper = createObjectMapper();
        JSONObject jsonObject = new JSONObject(objectMapper.writeValueAsString(tlsTransformedEvent));
        TlsTransformedEvent tlsTransformedEventWithId = objectMapper.readValue(jsonObject.toString(), TlsTransformedEvent.class);
        applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(sessionSplitTransformer, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
        transformEvent(tlsTransformedEventWithId, sessionSplitTransformer, TlsTransformedEvent.class);
    }

    /**
     * Test there are no failures, where SessionSplitValues don't exist (sslSubject, ja3, ja3s, sslCas)
     * @throws IOException
     */
    @Test
    public void testSessionSplitZeroAndNullSessionSplitValues() throws IOException {
        SessionSplitTransformer sessionSplitTransformer = (SessionSplitTransformer) loadTransformer(getResourceFilePath());
        TlsTransformedEvent tlsTransformedEvent = new TlsTransformedEvent(generateTlsRawEvent(0, null, null, null, null,
                null, null, null, "123", "123", "123", new DestinationPort("dstPort")));
        ObjectMapper objectMapper = createObjectMapper();
        JSONObject jsonObject = new JSONObject(objectMapper.writeValueAsString(tlsTransformedEvent));
        TlsTransformedEvent tlsTransformedEventWithId = objectMapper.readValue(jsonObject.toString(), TlsTransformedEvent.class);
        applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(sessionSplitTransformer, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
        transformEvent(tlsTransformedEventWithId, sessionSplitTransformer, TlsTransformedEvent.class);
    }

    /***
     * todo: until redis will be ready
     * Test enrichment, where SessionSplit > 0
     * @throws IOException
     */
    @Test
    public void testSessionSplitGreaterThanZero() throws IOException {
        SessionSplitTransformer sessionSplitTransformer = (SessionSplitTransformer) loadTransformer(getResourceFilePath());
        TlsTransformedEvent tlsTransformedEvent = new TlsTransformedEvent(generateTlsRawEvent(1, null, null,
                null, null, null, null, null, "123", "124", "125", new DestinationPort("126")));
        ObjectMapper objectMapper = createObjectMapper();
        JSONObject jsonObject = new JSONObject(objectMapper.writeValueAsString(tlsTransformedEvent));
        TlsTransformedEvent tlsTransformedEventWithId = objectMapper.readValue(jsonObject.toString(), TlsTransformedEvent.class);
        applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(sessionSplitTransformer, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

        SessionSplitTransformerKey key = new SessionSplitTransformerKey("123", "124", "126", "125");
        List<String> sslCas = Collections.singletonList("sslCas");
        SessionSplitTransformerValue value = new SessionSplitTransformerValue(0, "sslSbject", sslCas, "ja3", "ja3s");
        Mockito.when(sessionSplitStoreCache.read(key)).thenReturn(value);

        TlsTransformedEvent result = (TlsTransformedEvent) transformEvent(tlsTransformedEventWithId, sessionSplitTransformer, TlsTransformedEvent.class);
        Assert.assertEquals("sslSbject", result.getSslSubject().getName());
        Assert.assertEquals(sslCas, result.getSslCas());
        Assert.assertEquals("ja3", result.getJa3().getName());
        Assert.assertEquals("ja3s", result.getJa3s());
    }


    /**
     * Test there is no enrichment, where SessionSplit > 0 and SessionSplitValues don't exist (sslSubject, ja3, ja3s, sslCas)
     * @throws IOException
     */
    @Test
    public void testSessionSplitGreaterThanZeroAndNullSessionSplitValues() throws IOException {
        SessionSplitTransformer sessionSplitTransformer = (SessionSplitTransformer) loadTransformer(getResourceFilePath());
        TlsTransformedEvent tlsTransformedEvent = new TlsTransformedEvent(generateTlsRawEvent(1, null, null,
                null, null, null, null, null, "123", "124", "125", new DestinationPort("126")));
        ObjectMapper objectMapper = createObjectMapper();
        JSONObject jsonObject = new JSONObject(objectMapper.writeValueAsString(tlsTransformedEvent));
        TlsTransformedEvent tlsTransformedEventWithId = objectMapper.readValue(jsonObject.toString(), TlsTransformedEvent.class);
        applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(sessionSplitTransformer, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

        SessionSplitTransformerKey key = new SessionSplitTransformerKey("123", "124", "126", "125");
        SessionSplitTransformerValue value = new SessionSplitTransformerValue(0, null, null, null, null);
        Mockito.when(sessionSplitStoreCache.read(key)).thenReturn(value);

        TlsTransformedEvent result = (TlsTransformedEvent) transformEvent(tlsTransformedEventWithId, sessionSplitTransformer, TlsTransformedEvent.class);
        Assert.assertNull(result.getSslSubject());
        Assert.assertNull(result.getSslCas());
        Assert.assertNull(result.getJa3());
        Assert.assertNull(result.getJa3s());
    }

    /**
     * Test there is no enrichment, where SessionSplit > 0 and there are no appropriate SessionSplitValue saved in sessionSplitStoreCache
     * @throws IOException
     */
    @Test
    public void testSessionSplitGreaterThanZeroAndNoSessionSplitValue() throws IOException {
        SessionSplitTransformer sessionSplitTransformer = (SessionSplitTransformer) loadTransformer(getResourceFilePath());
        TlsTransformedEvent tlsTransformedEvent = new TlsTransformedEvent(generateTlsRawEvent(1, null, null,
                null, null, null, null, null, "123", "124", "125", new DestinationPort("126")));
        ObjectMapper objectMapper = createObjectMapper();
        JSONObject jsonObject = new JSONObject(objectMapper.writeValueAsString(tlsTransformedEvent));
        TlsTransformedEvent tlsTransformedEventWithId = objectMapper.readValue(jsonObject.toString(), TlsTransformedEvent.class);
        applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(sessionSplitTransformer, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

        SessionSplitTransformerKey key = new SessionSplitTransformerKey("123", "124", "126", "125");
        Mockito.when(sessionSplitStoreCache.read(key)).thenReturn(null);

        TlsTransformedEvent result = (TlsTransformedEvent) transformEvent(tlsTransformedEventWithId, sessionSplitTransformer, TlsTransformedEvent.class);
        Assert.assertNull(result.getSslSubject());
        Assert.assertNull(result.getSslCas());
        Assert.assertNull(result.getJa3());
        Assert.assertNull(result.getJa3s());
    }

    /**
     * Test there is no enrichment with missed zero event.
     * sessionSplitStoreCache get SessionSplitTransformerValue of closed session - checked by (value.getSessionSplit() == eventSessionSplit - 1).
     * @throws IOException
     */
    @Test
    public void testSessionSplitMissedEvent() throws IOException {
        SessionSplitTransformer sessionSplitTransformer = (SessionSplitTransformer) loadTransformer(getResourceFilePath());
        TlsTransformedEvent tlsTransformedEvent = new TlsTransformedEvent(generateTlsRawEvent(1, null, null,
                null, null, null, null, null, "123", "124", "125", new DestinationPort("126")));
        ObjectMapper objectMapper = createObjectMapper();
        JSONObject jsonObject = new JSONObject(objectMapper.writeValueAsString(tlsTransformedEvent));
        TlsTransformedEvent tlsTransformedEventWithId = objectMapper.readValue(jsonObject.toString(), TlsTransformedEvent.class);
        applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(sessionSplitTransformer, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

        SessionSplitTransformerKey key = new SessionSplitTransformerKey("123", "124", "126", "125");
        List<String> sslCas = Collections.singletonList("sslCas");
        SessionSplitTransformerValue value = new SessionSplitTransformerValue(1, "sslSbject", sslCas, "ja3", "ja3s");
        Mockito.when(sessionSplitStoreCache.read(key)).thenReturn(value);

        TlsTransformedEvent result = (TlsTransformedEvent) transformEvent(tlsTransformedEventWithId, sessionSplitTransformer, TlsTransformedEvent.class);
        Assert.assertNull(result.getSslSubject());
        Assert.assertNull(result.getSslCas());
        Assert.assertNull(result.getJa3());
        Assert.assertNull(result.getJa3s());
    }



    private TlsRawEvent generateTlsRawEvent(Integer sessionSplit, SslSubject sslSubject, Ja3 ja3,
                                            DestinationOrganization destinationOrganization,
                                            DestinationCountry destinationCountry, DestinationAsn destinationAsn, Domain domain, String ja3s,
                                            String srcIp, String dstIp, String srcPort, DestinationPort destinationPort) {
        Instant laterInstant = Instant.now().plusSeconds(10000L * 182 * 60 * 60);
        return new TlsRawEvent(laterInstant, "TLS", "dataSource", null, srcIp, dstIp, srcPort, "",
                destinationCountry,  sslSubject, domain, destinationOrganization,
                destinationAsn, 0L, 0L, "", "",
                ja3, ja3s, "", destinationPort, null, null, sessionSplit);
    }

    @Override
    String getResourceFilePath() {
        return "SessionSplitTransformer.json";
    }

    @Override
    Class getTransformerClass() {
        return SessionSplitTransformer.class;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
