package presidio.input.core;


import fortscale.common.general.Schema;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.common.shell.command.PresidioCommands;
import fortscale.domain.core.EventResult;
import fortscale.utils.mongodb.util.ToCollectionNameTranslator;
import fortscale.utils.shell.BootShim;
import fortscale.utils.shell.BootShimConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.shell.core.CommandResult;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.core.services.impl.InputExecutionServiceImpl;
import presidio.input.core.spring.InputCoreConfigurationTest;
import presidio.monitoring.aspect.MonitoringAspects;
import presidio.monitoring.aspect.MonitroingAspectSetup;
import presidio.monitoring.endPoint.PresidioMetricEndPoint;
import presidio.monitoring.endPoint.PresidioSystemMetricsFactory;
import presidio.monitoring.factory.PresidioMetricFactory;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.MetricCollectingServiceImpl;
import presidio.output.sdk.impl.spring.OutputDataServiceConfig;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;
import presidio.sdk.api.domain.transformedevents.AuthenticationTransformedEvent;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Properties;


@RunWith(SpringRunner.class)
public class FortscaleInputCoreApplicationTest {
    public static final String EXECUTION_COMMAND = "run  --schema DLPFILE --start_date 2017-06-13T07:00:00.00Z --end_date 2017-06-13T09:00:00.00Z --fixed_duration_strategy 3600";

    @Autowired
    public ToCollectionNameTranslator toCollectionNameTranslator;

    @Autowired
    private BootShim bootShim;

    @Autowired
    private PresidioExecutionService processService;

    @Autowired
    private MetricCollectingService metricCollectingService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void contextLoads() throws Exception {
        Assert.assertTrue(processService instanceof InputExecutionServiceImpl);
    }

    @Test
    public void inputCoreShellTest() {
        CommandResult commandResult = bootShim.getShell().executeCommand(EXECUTION_COMMAND);
        Assert.assertTrue(commandResult.isSuccess());
    }

    @Test
    public void runAuthenticationEvent() throws Exception {

        Instant startTime = Instant.now();


        AuthenticationRawEvent authenticationRawEvent = new AuthenticationRawEvent(Instant.now(), "eventId",
                "dataSource", "userId", "operationType", null,
                EventResult.SUCCESS, "userName", "userDisplayName", null,
                "srcMachineId", "10.20.30.40", "dstMachineId",
                "dstMachineName", "dstMachineDomain", "resultCode");
        String collectionName = toCollectionNameTranslator.toCollectionName(Schema.AUTHENTICATION);
        mongoTemplate.save(authenticationRawEvent, collectionName);

        processService.run(Schema.AUTHENTICATION, startTime, startTime.plus(1L, ChronoUnit.HOURS), 0.5d);

        List<AuthenticationTransformedEvent> transformedEvents = mongoTemplate.findAll(AuthenticationTransformedEvent.class);
    }

    @Configuration
    @Import({
            InputCoreConfigurationTest.class,
            MongodbTestConfig.class,
            BootShimConfig.class,
            PresidioCommands.class,
            OutputDataServiceConfig.class})
    @EnableSpringConfigured
    public static class springConfig {
        @Bean
        public MetricCollectingService metricCollectingService() {
            return new MetricCollectingServiceImpl(presidioMetricEndPoint());
        }

        @Bean
        public PresidioMetricEndPoint presidioMetricEndPoint() {
            return new PresidioMetricEndPoint(new PresidioSystemMetricsFactory("input-core"));
        }

        @Bean
        public PresidioMetricFactory presidioMetricFactory() {
            return new PresidioMetricFactory("input-core");
        }

        @Bean
        public MonitoringAspects monitoringAspects() {
            return new MonitoringAspects();
        }

        @Bean
        public MonitroingAspectSetup monitroingAspectSetup() {
            return  new MonitroingAspectSetup(presidioMetricEndPoint(), presidioMetricFactory());
        }
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
