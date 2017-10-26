package presidio.monitoring.sdk.impl.spring;

import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import presidio.monitoring.aspect.MonitoringAspects;
import presidio.monitoring.aspect.metrics.CustomMetricEndpoint;
import presidio.monitoring.aspect.metrics.PresidioCustomMetrics;
import presidio.monitoring.aspect.metrics.PresidioDefaultMetrics;
import presidio.monitoring.aspect.services.MetricCollectingServiceImpl;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.elastic.services.MetricExportService;
import presidio.monitoring.elastic.services.MetricExportServiceImpl;
import presidio.monitoring.export.MetricsExporter;
import presidio.monitoring.export.MetricsExporterElasticImpl;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;
import presidio.monitoring.sdk.impl.services.PresidioExternalMonitoringServiceImpl;

import java.net.InetAddress;

@Configuration
@EnableScheduling
@PropertySource("classpath:monitoring.properties")
@EnableElasticsearchRepositories(basePackages = "presidio.monitoring.elastic.repositories")
public class ExternalMonitoringConfiguration {

    public static final int AWAIT_TERMINATION_SECONDS = 120;


    @Value("${elasticsearch.host}")
    private String EsHost; //todo get from config server

    @Value("${elasticsearch.port}") //todo get from config server
    private int EsPort;

    @Value("${elasticsearch.clustername}") //todo get from config server
    private String EsClusterName;

    @Value("${spring.application.name}") //todo get from config server
    private String processName;

    @Autowired
    private MetricRepository metricRepository;

    @Bean
    public Client client() throws Exception {
        Settings esSettings = Settings.builder().put("cluster.name", EsClusterName).build();
        return new PreBuiltTransportClient(esSettings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(EsHost), EsPort));
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() throws Exception {
        return new PresidioElasticsearchTemplate(client());
    }


    @Bean
    public PresidioCustomMetrics presidioCustomMetrics() {
        return new PresidioCustomMetrics();
    }

    @Bean
    public MonitoringAspects monitoringAspects() {
        return new MonitoringAspects(metricsEndpoint(), presidioCustomMetrics());
    }

    @Bean
    public MetricExportService metricExportService() {
        return new MetricExportServiceImpl(metricRepository);
    }

    @Bean
    public PublicMetrics publicMetrics() {
        return new PresidioDefaultMetrics();
    }

    @Bean
    public MetricsEndpoint metricsEndpoint() {
        return new CustomMetricEndpoint(publicMetrics());
    }

    @Bean
    public MetricsExporter fileMetricsExporter() {
        return new MetricsExporterElasticImpl(metricsEndpoint(), null, metricExportService(), taskScheduler());
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
        ts.setWaitForTasksToCompleteOnShutdown(true);
        ts.setAwaitTerminationSeconds(AWAIT_TERMINATION_SECONDS);
        return ts;
    }

    @Bean
    public PresidioExternalMonitoringService PresidioExternalMonitoringService() {
        return new PresidioExternalMonitoringServiceImpl(new MetricCollectingServiceImpl(presidioCustomMetrics()));
    }
}
