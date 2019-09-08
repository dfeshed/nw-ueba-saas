package presidio.input.core.services.transformation.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.entityattributes.*;
import org.json.JSONObject;
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
import presidio.input.core.services.transformation.transformer.SessionSplitTransformer.SessionSplitTransformer;
import presidio.input.core.spring.TlsTransformerConfigTest;
import presidio.sdk.api.domain.rawevents.TlsRawEvent;
import presidio.sdk.api.domain.transformedevents.TlsTransformedEvent;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.io.IOException;
import java.time.Instant;

@RunWith(SpringRunner.class)
@Import({TlsTransformerConfigTest.class})
public class SessionSplitTransformerTest extends TransformerJsonTest implements ApplicationContextAware {

    @Autowired
    private ApplicationContext applicationContext;

    @MockBean
    private PresidioInputPersistencyService inputPersistencyService;

    @Test
    public void testSessionSplitNull() throws IOException {
        SessionSplitTransformer sessionSplitTransformer = (SessionSplitTransformer) loadTransformer(getResourceFilePath());
        TlsTransformedEvent tlsTransformedEvent = new TlsTransformedEvent(generateTlsRawEvent());
        ObjectMapper objectMapper = createObjectMapper();
        JSONObject jsonObject = new JSONObject(objectMapper.writeValueAsString(tlsTransformedEvent));
        jsonObject.put("id", "12345");
        TlsTransformedEvent tlsTransformedEventWithId = objectMapper.readValue(jsonObject.toString(), TlsTransformedEvent.class);
        Mockito.when(inputPersistencyService.count(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(0L);
        applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(sessionSplitTransformer, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
        sessionSplitTransformer.postAutowireProcessor();
        transformEvent(tlsTransformedEventWithId, sessionSplitTransformer, TlsTransformedEvent.class);
    }

    private TlsRawEvent generateTlsRawEvent() {
        Instant laterInstant = Instant.now().plusSeconds(10000L * 182 * 60 * 60);
        return new TlsRawEvent(laterInstant, "TLS", "dataSource", null, "", "", "", "",
                new DestinationCountry("dstCountry"),
                new SslSubject("ssl"), new Domain("google.com"),
                new DestinationOrganization("dstOrg"),
                new DestinationAsn("dstAsn"), 0L, 0L, "", "",
                new Ja3("ja3"), "", "",
                new DestinationPort("dstPort"), null, null, 2);
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
