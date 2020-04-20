package fortscale.ml.model.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

@Configuration
public class WeightModeBuilderMetricsContainerConfig {
    @Autowired
    private MetricCollectingService metricCollectingService;
    @Autowired
    private MetricsExporter metricsExporter;

    @Bean
    public WeightModelBuilderMetricsContainer weightModelBuilderMetricsContainer() {
        return new WeightModelBuilderMetricsContainer(metricCollectingService, metricsExporter);
    }
}
