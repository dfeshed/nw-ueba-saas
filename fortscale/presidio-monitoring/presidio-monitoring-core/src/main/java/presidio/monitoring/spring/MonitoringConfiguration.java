package presidio.monitoring.spring;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyServiceImpl;
import presidio.monitoring.endPoint.PresidioMetricBucket;
import presidio.monitoring.services.export.MetricsExporter;
import presidio.monitoring.services.export.MetricsExporterElasticImpl;
import presidio.monitoring.services.export.NullMetricsExporter;

@Configuration
@EnableScheduling

@ComponentScan(basePackages = {"presidio.monitoring.aspect"})
@EnableElasticsearchRepositories(basePackages = "presidio.monitoring.elastic.repositories")
public class MonitoringConfiguration {

    public static final int AWAIT_TERMINATION_SECONDS = 120;

    @Value("${enable.metrics.export}")
    boolean enableMetricsExport;

    @Autowired
    public PresidioMetricBucket presidioMetricBucket;

    @Bean
    public MetricsExporter metricsExporter() {
        if(enableMetricsExport) {
            return new MetricsExporterElasticImpl(presidioMetricBucket, presidioMetricPersistencyService(), taskScheduler());
        }
        else
        {
            return new NullMetricsExporter(presidioMetricBucket, taskScheduler());
        }
    }

    @Autowired
    private MetricRepository metricRepository;

    @Bean
    public PresidioMetricPersistencyService presidioMetricPersistencyService() {
        return new PresidioMetricPersistencyServiceImpl(metricRepository);
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        if(enableMetricsExport) {
            ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
            ts.setWaitForTasksToCompleteOnShutdown(true);
            ts.setAwaitTerminationSeconds(AWAIT_TERMINATION_SECONDS);
            return ts;
        }
        return null;
    }
}
