package fortscale.ml.model.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

@Configuration
public class TimeModeBuilderMetricsPartitionsContainerConfig {
    @Autowired
    private MetricCollectingService metricCollectingService;
    @Autowired
    private MetricsExporter metricsExporter;
    @Value("${fortscale.metric.time.model.builder.partition.resolution:3600}")
    private int metricTimePartitionResolution;

    @Bean
    public TimeModelBuilderPartitionsMetricsContainer timeModelBuilderPartitionsMetricsContainer() {
        return new TimeModelBuilderPartitionsMetricsContainer(metricCollectingService, metricsExporter, metricTimePartitionResolution);
    }
}
