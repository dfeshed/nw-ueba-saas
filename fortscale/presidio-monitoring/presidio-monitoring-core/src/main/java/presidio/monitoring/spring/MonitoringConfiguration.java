package presidio.monitoring.spring;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.endPoint.PresidioMetricEndPoint;
import presidio.monitoring.services.export.MetricsExporter;
import presidio.monitoring.services.export.MetricsExporterElasticImpl;

@Configuration
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ConditionalOnProperty(prefix = "spring.aop",
        name = "proxy.target.class",
        havingValue = "true",
        matchIfMissing = false)
@ComponentScan(basePackages = {"presidio.monitoring.aspect"})
public class MonitoringConfiguration {

    public static final int AWAIT_TERMINATION_SECONDS = 120;

    @Autowired
    public PresidioMetricEndPoint presidioMetricEndPoint;


    @Autowired
    public PresidioMetricPersistencyService presidioMetricPersistencyService;


    @Bean
    public MetricsExporter metricsExporter() {
        return new MetricsExporterElasticImpl(presidioMetricEndPoint, presidioMetricPersistencyService, taskScheduler());
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
        ts.setWaitForTasksToCompleteOnShutdown(true);
        ts.setAwaitTerminationSeconds(AWAIT_TERMINATION_SECONDS);
        return ts;
    }
}
