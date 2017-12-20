package presidio.input.core;


import fortscale.common.shell.PresidioExecutionService;
import fortscale.common.shell.command.PresidioCommands;
import fortscale.utils.shell.BootShim;
import fortscale.utils.shell.BootShimConfig;
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
import org.springframework.shell.core.CommandResult;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.core.services.impl.InputExecutionServiceImpl;
import presidio.input.core.spring.InputCoreConfigurationTest;
import presidio.monitoring.aspect.MonitoringAspects;
import presidio.monitoring.aspect.MonitroingAspectSetup;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.endPoint.PresidioMetricBucket;
import presidio.monitoring.endPoint.PresidioSystemMetricsFactory;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.MetricCollectingServiceImpl;
import presidio.monitoring.services.MetricConventionApplyer;
import presidio.monitoring.services.PresidioMetricConventionApplyer;
import presidio.output.sdk.impl.spring.OutputDataServiceConfig;

import java.util.Properties;


@RunWith(SpringRunner.class)
public class FortscaleInputCoreApplicationTest {
    public static final String EXECUTION_COMMAND = "run  --schema DLPFILE --start_date 2017-06-13T07:00:00.00Z --end_date 2017-06-13T09:00:00.00Z --fixed_duration_strategy 3600";

    @Autowired
    private BootShim bootShim;

    @Autowired
    private PresidioExecutionService processService;

    @Autowired
    private MetricCollectingService metricCollectingService;
    @MockBean
    MetricRepository metricRepository;

    @Test
    public void contextLoads() throws Exception {
        Assert.assertTrue(processService instanceof InputExecutionServiceImpl);
    }

    @Test
    public void inputCoreShellTest() {
        CommandResult commandResult = bootShim.getShell().executeCommand(EXECUTION_COMMAND);
        Assert.assertTrue(commandResult.isSuccess());
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

        private String applicationName = "input-core";


        @Bean
        public MetricCollectingService metricCollectingService() {
            return new MetricCollectingServiceImpl(presidioMetricEndPoint());
        }

        @Bean
        public MetricConventionApplyer metricConventionApplyer() {
            return new PresidioMetricConventionApplyer(applicationName);
        }

        @Bean
        public PresidioMetricBucket presidioMetricEndPoint() {
            return new PresidioMetricBucket(new PresidioSystemMetricsFactory(applicationName), metricConventionApplyer());
        }

        @Bean
        public MonitoringAspects monitoringAspects() {
            return new MonitoringAspects();
        }

        @Bean
        public MonitroingAspectSetup monitroingAspectSetup() {
            return new MonitroingAspectSetup(presidioMetricEndPoint());
        }

        @Bean
        public static TestPropertiesPlaceholderConfigurer inputCoreTestConfigurer() {
            Properties properties = new Properties();
            properties.put("page.iterator.page.size", "1000");
            properties.put("enable.metrics.export", "false");
            properties.put("input.events.retention.in.days", "2");
            properties.put("operation.type.category.mapping.file.path", "file:/home/presidio/presidio-core/configurations/operation-type-category-mapping.json");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }

    }
}
