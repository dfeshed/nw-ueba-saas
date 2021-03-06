package presidio.monitoring.spring;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import presidio.monitoring.datadog.PresidioMetricDataDogService;
import presidio.monitoring.elastic.allindexrepo.MetricsAllIndexesRepositoryConfig;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.elastic.allindexrepo.MetricsAllIndexesRepository;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyServiceImpl;
import presidio.monitoring.endPoint.PresidioMetricBucket;
import presidio.monitoring.services.export.MetricsExporter;
import presidio.monitoring.services.export.MetricsExporterImpl;
import presidio.monitoring.services.export.NullMetricsExporter;

import java.util.List;

@Configuration
@EnableScheduling

@ComponentScan(basePackages = {"presidio.monitoring.aspect"})
@EnableElasticsearchRepositories(basePackages = "presidio.monitoring.elastic.repositories")
@Import(MetricsAllIndexesRepositoryConfig.class)
public class MonitoringConfiguration {

    public static final int AWAIT_TERMINATION_SECONDS = 120;

    @Value("${enable.metrics.export}")
    private boolean enableMetricsExport;

    @Value("${datadog.port}")
    private int dataDogPort;

    @Value("${datadog.host}")
    private String dataDogHostName;

    @Value("#{'${datadog.metrics}'.split(',')}")
    private List<String> dataDogMetricNames;

    @Autowired
    public PresidioMetricBucket presidioMetricBucket;

    @Bean
    public MetricsExporter metricsExporter() {
        if(enableMetricsExport) {
            return new MetricsExporterImpl(presidioMetricBucket, presidioMetricPersistencyService(), presidioMetricDataDogService(), taskScheduler());
        }
        else
        {
            return new NullMetricsExporter(presidioMetricBucket, taskScheduler());
        }
    }

    @Autowired
    private MetricRepository metricRepository;

    @Autowired
    private MetricsAllIndexesRepository metricsAllIndexesRepository;

    @Bean
    public PresidioMetricPersistencyService presidioMetricPersistencyService() {
        return new PresidioMetricPersistencyServiceImpl(metricRepository, metricsAllIndexesRepository);
    }

    @Bean
    public PresidioMetricDataDogService presidioMetricDataDogService() {
        return new PresidioMetricDataDogService(dataDogHostName, dataDogPort, dataDogMetricNames);
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
