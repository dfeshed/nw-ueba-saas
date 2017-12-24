package fortscale.ml.model.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

@Configuration
public class TimeModelRetrieverMetricsContainerConfig {
    @Autowired
    private MetricCollectingService metricCollectingService;
    @Autowired
    private MetricsExporter metricsExporter;

    @Bean
    public TimeModelRetrieverMetricsContainer timeModelRetrieverMetricsContainer() {
        return new TimeModelRetrieverMetricsContainer(metricCollectingService, metricsExporter);
    }
}
