package fortscale.aggregation.feature.bucket.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

/**
 * Created by barak_schuster on 12/12/17.
 */
@Configuration
public class FeatureBucketAggregatorMetricsContainerConfig {
    @Autowired
    private MetricCollectingService metricCollectingService;
    @Autowired
    private MetricsExporter metricsExporter;

    @Bean
    public FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer()
    {
        return new FeatureBucketAggregatorMetricsContainer(metricCollectingService,metricsExporter);
    }
}
