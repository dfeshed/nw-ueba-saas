package presidio.monitoring.spring;



import presidio.monitoring.aspect.metrics.CustomMetricEndpoint;
import presidio.monitoring.aspect.metrics.PresidioDefaultMetrics;
import presidio.monitoring.export.MetricsExporterElasticImpl;
import presidio.monitoring.export.MetricsExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"presidio.monitoring.aspect"})
public class MonitoringConfiguration {

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
    public MetricsExporter fileMetricsExporter() {
        return new MetricsExporterElasticImpl(metricsEndpoint(),processName);
    }
}
