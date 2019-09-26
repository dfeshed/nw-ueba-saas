package presidio.input.core;

import fortscale.common.shell.PresidioExecutionService;
import fortscale.common.shell.command.PresidioCommands;
import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
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
import presidio.monitoring.elastic.allindexrepo.MetricsAllIndexesRepository;
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
    private MetricRepository metricRepository;
    @MockBean
    private MetricsAllIndexesRepository metricsAllIndexesRepository;
    @MockBean
    private PresidioElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void contextLoads() {
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
            OutputDataServiceConfig.class
    })
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
            properties.put("dataPipeline.startTime", "2019-01-01T00:00:00Z");
            properties.put("transformers.file.path", "classpath:descriptors/");
            properties.put("folder.operation.types", "LOCAL_SHARE_FOLDER_PATH_CHANGED,FOLDER_RENAMED,FOLDER_OWNERSHIP_CHANGED,FOLDER_OPENED,FOLDER_MOVED,FOLDER_DELETED,FOLDER_CREATED,FOLDER_CLASSIFICATION_CHANGED,FOLDER_CENTRAL_ACCESS_POLICY_CHANGED,FOLDER_AUDITING_CHANGED,FOLDER_ATTRIBUTE_CHANGED,FOLDER_ACCESS_RIGHTS_CHANGED,FAILED_FOLDER_ACCESS,NETAPP_FOLDER_RENAMED,NETAPP_FOLDER_OWNERSHIP_CHANGED,NETAPP_FOLDER_MOVED,NETAPP_FOLDER_DELETED,NETAPP_FOLDER_CREATED,NETAPP_FOLDER_ACCESS_RIGHTS_CHANGED,EMC_FOLDER_RENAMED,EMC_FOLDER_OWNERSHIP_CHANGED,EMC_FLDER_MOVED,EMC_FOLDER_DELETED,EMC_FOLDER_CREATED,EMC_FOLDER_ACCESS_RIGHTS_CHANGED,FOLDER_RENAMED,FLUIDFS_FOLDER_OWNERSHIP_CHANGED,FLUIDFS_FOLDER_MOVED,FLUIDFS_FOLDER_DELETED,FLUIDFS_FOLDER_CREATED,FLUIDFS_FOLDER_AUDITING_CHANGED,FLUIDFS_FOLDER_ACCESS_RIGHTS_CHANGED");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }
}
