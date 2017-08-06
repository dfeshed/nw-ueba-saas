package presidio.monitoring.spring;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.*;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.quartz.SimpleThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import presidio.monitoring.aspect.MonitoringAspects;
import presidio.monitoring.aspect.metrics.CustomMetricEndpoint;
import presidio.monitoring.aspect.metrics.PresidioCustomMetrics;
import presidio.monitoring.aspect.metrics.PresidioDefaultMetrics;
import presidio.monitoring.aspect.services.MetricCollectingService;
import presidio.monitoring.aspect.services.MetricCollectingServiceImpl;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.elastic.services.MetricExportService;
import presidio.monitoring.elastic.services.MetricExportServiceImpl;
import presidio.monitoring.export.MetricsExporterElasticImpl;
import presidio.monitoring.export.MetricsExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.boot.actuate.endpoint.PublicMetrics;

@Configuration
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ConditionalOnProperty(prefix="spring.aop",
        name="proxy.target.class",
        havingValue="true",
        matchIfMissing=false)
@ComponentScan(basePackages = {"presidio.monitoring.aspect"})
@EnableElasticsearchRepositories(basePackages = "presidio.monitoring.elastic.repositories")
@Import(fortscale.utils.elasticsearch.config.ElasticsearchConfig.class)
public class MonitoringConfiguration {

    public static final int AWAIT_TERMINATION_SECONDS = 120;

    @Bean
    public PublicMetrics publicMetrics(){
        return new PresidioDefaultMetrics();
    }

    @Bean
    public MetricsEndpoint metricsEndpoint(){
        return  new CustomMetricEndpoint(publicMetrics());
    }

    @Value("${spring.application.name}")
    String processName;

    @Bean
    public PresidioCustomMetrics presidioCustomMetrics(){return new PresidioCustomMetrics();}

    @Bean
    public MonitoringAspects monitoringAspects(){return new MonitoringAspects(metricsEndpoint(),presidioCustomMetrics());}

    @Autowired
    private MetricRepository metricRepository;

    @Bean
    public MetricExportService metricExportService(){return new MetricExportServiceImpl(metricRepository);}


    @Bean
    public MetricsExporter fileMetricsExporter() {
        return new MetricsExporterElasticImpl(metricsEndpoint(),processName,metricExportService(),taskScheduler());
    }

    @Bean
    public MetricCollectingService metricCollectingService(){return new MetricCollectingServiceImpl();}



    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
        ts.setWaitForTasksToCompleteOnShutdown(true);
        ts.setAwaitTerminationSeconds(AWAIT_TERMINATION_SECONDS);
        return ts;
    }
}
