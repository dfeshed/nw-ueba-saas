package fortscale.ml.model.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

@Configuration
public class MaxContinuousModelRetrieverMetricsContainerConfig {
    @Autowired
    private MetricCollectingService metricCollectingService;
    @Autowired
    private MetricsExporter metricsExporter;

    @Bean
    public MaxContinuousModelRetrieverMetricsContainer maxContinuousModelRetrieverMetricsContainer() {
        return new MaxContinuousModelRetrieverMetricsContainer(metricCollectingService, metricsExporter);
    }
}
