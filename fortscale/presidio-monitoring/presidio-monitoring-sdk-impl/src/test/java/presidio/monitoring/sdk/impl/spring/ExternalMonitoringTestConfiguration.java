package presidio.monitoring.sdk.impl.spring;

import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import presidio.monitoring.datadog.PresidioMetricDataDogService;
import presidio.monitoring.elastic.allindexrepo.MetricsAllIndexesRepository;
import presidio.monitoring.elastic.allindexrepo.MetricsAllIndexesRepositoryConfig;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyServiceImpl;
import presidio.monitoring.endPoint.PresidioMetricBucket;
import presidio.monitoring.endPoint.PresidioSystemMetricsFactory;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;
import presidio.monitoring.sdk.impl.services.PresidioExternalMonitoringServiceImpl;
import presidio.monitoring.services.MetricCollectingServiceImpl;
import presidio.monitoring.services.MetricConventionApplyer;
import presidio.monitoring.services.PresidioMetricConventionApplyer;
import presidio.monitoring.services.export.MetricExportingServiceImpl;
import presidio.monitoring.services.export.MetricsExporter;
import presidio.monitoring.services.export.MetricsExporterImpl;

import java.util.List;


@Configuration
@EnableScheduling
@EnableElasticsearchRepositories(basePackages = "presidio.monitoring.elastic.repositories")
@Import({ElasticsearchTestConfig.class, TestConfig.class,MetricsAllIndexesRepositoryConfig.class})
public class ExternalMonitoringTestConfiguration {

    public static final int AWAIT_TERMINATION_SECONDS = 120;


    private final String APPLICATION_NAME = "External-monitoring";

    @Value("${datadog.port}")
    int dataDogPort;

    @Value("${datadog.host}")
    String dataDogHostName;

    @Value("${datadog.metrics}")
    List<String> dataDogMetricNames;

    @Autowired
    public MetricRepository metricRepository;

    @Autowired
    private MetricsAllIndexesRepository metricsAllIndexesRepository;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public PresidioMetricPersistencyService metricExportService() {
        return new PresidioMetricPersistencyServiceImpl(metricRepository, metricsAllIndexesRepository);
    }

    @Bean
    public PresidioMetricDataDogService presidioMetricDataDogService() {
        return new PresidioMetricDataDogService(dataDogHostName, dataDogPort , dataDogMetricNames);
    }

    public MetricsExporter metricsExporter() {
        return new MetricsExporterImpl(presidioMetricBucket(), metricExportService(), presidioMetricDataDogService(), taskScheduler());
    }


    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
        ts.setWaitForTasksToCompleteOnShutdown(true);
        ts.setAwaitTerminationSeconds(AWAIT_TERMINATION_SECONDS);
        return ts;
    }

    @Bean
    public PresidioExternalMonitoringService PresidioExternalMonitoringService() {
        return new PresidioExternalMonitoringServiceImpl(new MetricCollectingServiceImpl(presidioMetricBucket())
                ,
                new MetricExportingServiceImpl(metricsExporter()));
    }

    @Bean
    public MetricConventionApplyer metricConventionApplyer() {
        return new PresidioMetricConventionApplyer(APPLICATION_NAME);
    }


    @Bean
    public PresidioMetricBucket presidioMetricBucket() {
        return new PresidioMetricBucket(presidioSystemMetrics(), metricConventionApplyer());
    }


    @Bean
    public PresidioSystemMetricsFactory presidioSystemMetrics() {
        return new PresidioSystemMetricsFactory(APPLICATION_NAME);
    }

}
