package presidio.input.core.services.transformation;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.general.Schema;
import fortscale.common.shell.command.PresidioCommands;
import fortscale.domain.core.EventResult;
import fortscale.utils.shell.BootShimConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.core.FortscaleInputCoreApplicationTest;
import presidio.input.core.services.impl.SchemaFactory;
import presidio.input.core.services.transformation.managers.*;
import presidio.input.core.spring.InputCoreConfigurationTest;
import presidio.monitoring.aspect.MonitoringAspects;
import presidio.monitoring.aspect.MonitroingAspectSetup;
import presidio.monitoring.endPoint.PresidioMetricEndPoint;
import presidio.monitoring.endPoint.PresidioSystemMetricsFactory;
import presidio.monitoring.factory.PresidioMetricFactory;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.MetricCollectingServiceImpl;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;
import presidio.sdk.api.domain.transformedevents.AuthenticationTransformedEvent;

import java.time.Instant;
import java.util.*;

/**
 * Created by Efrat Noam on 11/8/17.
 */
@RunWith(SpringRunner.class)
public class TransformationServiceTest {

    @Autowired
    TransformationService transformationService;

    @Test
    public void testRunAuthenticationSchemaSrcMachineTransformations_unresolvedMachineName() {

        AuthenticationRawEvent authenticationRawEvent = new AuthenticationRawEvent(Instant.now(), "eventId",
                "dataSource", "userId", "operationType", null,
                EventResult.SUCCESS, "userName", "userDisplayName", null,
                "srcMachineId", "10.20.30.40", "dstMachineId",
                "dstMachineName", "dstMachineDomain", "resultCode");
        List<AbstractInputDocument> events = Arrays.asList(authenticationRawEvent);
        List<AbstractInputDocument> transformedEvents = transformationService.run(events, Schema.AUTHENTICATION);

        Assert.assertEquals("",((AuthenticationTransformedEvent) transformedEvents.get(0)).getSrcMachineCluster());
    }

    @Test
    public void testRunAuthenticationSchemaSrcMachineTransformations_resolvedMachineName() {

        AuthenticationRawEvent authenticationRawEvent = new AuthenticationRawEvent(Instant.now(), "eventId",
                "dataSource", "userId", "operationType", null,
                EventResult.SUCCESS, "userName", "userDisplayName", null,
                "srcMachineId", "dwef043.fortscale.com", "dstMachineId",
                "dstMachineName", "dstMachineDomain", "resultCode");
        List<AbstractInputDocument> events = Arrays.asList(authenticationRawEvent);
        List<AbstractInputDocument> transformedEvents = transformationService.run(events, Schema.AUTHENTICATION);

        Assert.assertEquals("dwef.fortscale.com" ,((AuthenticationTransformedEvent) transformedEvents.get(0)).getSrcMachineCluster());
    }

    @Configuration
    @Import({
            InputCoreConfigurationTest.class,
            MongodbTestConfig.class,
            PresidioMonitoringConfiguration.class})
    @EnableSpringConfigured
    public static class springConfig {
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
