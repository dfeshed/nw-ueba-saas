package fortscale.ml.model.cache.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

/**
 * Created by barak_schuster on 12/10/17.
 */
@Configuration
public class ModelCacheMetricsContainerConfig {
    @Autowired
    private MetricCollectingService metricCollectingService;
    @Autowired
    private MetricsExporter metricsExporter;

    @Bean
    public ModelCacheMetricsContainer modelCacheMetricsContainer()
    {
        return new ModelCacheMetricsContainer(metricCollectingService,metricsExporter);
    }
}
