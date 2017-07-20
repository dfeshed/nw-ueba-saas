package presidio.monitoring.exporter;


import presidio.monitoring.aspect.CustomMetric;
import presidio.monitoring.aspect.MonitoringAspects;
import presidio.monitoring.exporter.exporters.FileMetricsExporter;
import presidio.monitoring.exporter.exporters.MetricsExporter;
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
public class ExporterConfiguration {

    @Bean
    public PublicMetrics publicMetrics(){
        return new DefaultPublicMetrics();
    }

    @Bean
    public MetricsEndpoint metricsEndpoint(){
        return  new MetricsEndpoint(publicMetrics());
    }

    @Value("${spring.application.name}")
    String processName;


    @Bean
    public MetricsExporter fileMetricsExporter() {
        return new FileMetricsExporter(metricsEndpoint(),processName);
    }
}
