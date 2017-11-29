package presidio.monitoring.sdk.impl.spring;

import fortscale.utils.elasticsearch.config.ElasticsearchConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
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
import presidio.monitoring.services.export.MetricsExporter;
import presidio.monitoring.services.export.MetricsExporterElasticImpl;


@Configuration
@EnableScheduling
@PropertySource("classpath:monitoring.properties")
@EnableElasticsearchRepositories(basePackages = "presidio.monitoring.elastic.repositories")
@Import({ElasticsearchConfig.class})
public class ExternalMonitoringConfiguration {

    public static final int AWAIT_TERMINATION_SECONDS = 120;
    private final String EMPTY_APPLICATION_NAME = "";

    @Autowired
    public MetricRepository metricRepository;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public PresidioMetricPersistencyService metricExportService() {
        return new PresidioMetricPersistencyServiceImpl(metricRepository);
    }

    public MetricsExporter metricsExporter() {
        return new MetricsExporterElasticImpl(presidioMetricEndPoint(), metricExportService(), taskScheduler());
    }


    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
        ts.setWaitForTasksToCompleteOnShutdown(true);
        ts.setAwaitTerminationSeconds(AWAIT_TERMINATION_SECONDS);
        return ts;
    }

    @Bean
    public PresidioExternalMonitoringService PresidioExternalMonitoringService() {
        return new PresidioExternalMonitoringServiceImpl(new MetricCollectingServiceImpl(presidioMetricEndPoint()));
    }

    @Bean
    public MetricConventionApplyer metricConventionApplyer() {
        return new PresidioMetricConventionApplyer(EMPTY_APPLICATION_NAME);
    }

    @Bean
    public PresidioMetricBucket presidioMetricEndPoint() {
        return new PresidioMetricBucket(presidioSystemMetrics(), metricConventionApplyer());
    }

    @Bean
    public PresidioSystemMetricsFactory presidioSystemMetrics() {
        return new PresidioSystemMetricsFactory(EMPTY_APPLICATION_NAME);
    }


}
