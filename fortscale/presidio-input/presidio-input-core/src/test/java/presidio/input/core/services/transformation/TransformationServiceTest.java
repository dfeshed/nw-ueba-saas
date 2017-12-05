package presidio.input.core.services.transformation;

import fortscale.common.general.Schema;
import fortscale.domain.core.EventResult;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.core.services.transformation.managers.TransformationService;
import presidio.input.core.spring.InputCoreConfigurationTest;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;
import presidio.sdk.api.domain.transformedevents.AuthenticationTransformedEvent;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by Efrat Noam on 11/8/17.
 */
@RunWith(SpringRunner.class)
public class TransformationServiceTest {

    @Autowired
    TransformationService transformationService;

    @Test
    public void testRunAuthenticationSchemaSrcMachineTransformations_unresolvedMachineNameAndId() {

        AuthenticationRawEvent authenticationRawEvent = createAuthenticationEvent(Instant.now(), "10.20.3.40", "1.34.56.255", "12.4.6.74", "10.65.20.88");
        List<AbstractInputDocument> events = Arrays.asList(authenticationRawEvent);
        List<AbstractInputDocument> transformedEvents = transformationService.run(events, Schema.AUTHENTICATION);

        Assert.assertEquals("" ,((AuthenticationTransformedEvent) transformedEvents.get(0)).getSrcMachineCluster());
        Assert.assertEquals("" ,((AuthenticationTransformedEvent) transformedEvents.get(0)).getSrcMachineId());
        Assert.assertEquals("" ,((AuthenticationTransformedEvent) transformedEvents.get(0)).getDstMachineCluster());
        Assert.assertEquals("" ,((AuthenticationTransformedEvent) transformedEvents.get(0)).getDstMachineId());
    }

    @Test
    public void testRunAuthenticationSchemaSrcMachineTransformations_resolvedMachineNameAndId() {

        AuthenticationRawEvent authenticationRawEvent = createAuthenticationEvent(Instant.now(), "nameSPBGDCW01.prod.quest.corp", "idSPBGDCW01.prod.quest.corp", "nameSPBGDCW02.prod.quest.corp", "idSPBGDCW02.prod.quest.corp");
        List<AbstractInputDocument> events = Arrays.asList(authenticationRawEvent);
        List<AbstractInputDocument> transformedEvents = transformationService.run(events, Schema.AUTHENTICATION);

        Assert.assertEquals("nameSPBGDCW.prod.quest.corp" ,((AuthenticationTransformedEvent) transformedEvents.get(0)).getSrcMachineCluster());
        Assert.assertEquals("idSPBGDCW01.prod.quest.corp" ,((AuthenticationTransformedEvent) transformedEvents.get(0)).getSrcMachineId());
        Assert.assertEquals("nameSPBGDCW.prod.quest.corp" ,((AuthenticationTransformedEvent) transformedEvents.get(0)).getDstMachineCluster());
        Assert.assertEquals("idSPBGDCW02.prod.quest.corp" ,((AuthenticationTransformedEvent) transformedEvents.get(0)).getDstMachineId());
    }

    private AuthenticationRawEvent createAuthenticationEvent(Instant eventDate,
                                                             String srcMachineName,
                                                             String srcMachineId,
                                                             String dstMachineName,
                                                             String dstMachineId) {
        return new AuthenticationRawEvent(
                eventDate,
                "eventId",
                "dataSource",
                "userId",
                "operationType",
                null,
                EventResult.SUCCESS,
                "userName",
                "userDisplayName",
                null,
                srcMachineId,
                srcMachineName,
                dstMachineId,
                dstMachineName,
                "dstMachineDomain",
                "resultCode");
    }

    @Configuration
    @Import({
            InputCoreConfigurationTest.class,
            MongodbTestConfig.class,
            PresidioMonitoringConfiguration.class})
    @EnableSpringConfigured
    public static class springConfig {
        @MockBean
        private MetricRepository metricRepository;

        @Bean
        public static TestPropertiesPlaceholderConfigurer inputCoreTestConfigurer() {
            Properties properties = new Properties();
            properties.put("page.iterator.page.size", "1000");
            properties.put("enable.metrics.export", "false");
            properties.put("output.events.limit", "1000");
            properties.put("operation.type.category.mapping.file.path", "file:/home/presidio/presidio-core/configurations/operation-type-category-mapping.json");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }

    }
}
