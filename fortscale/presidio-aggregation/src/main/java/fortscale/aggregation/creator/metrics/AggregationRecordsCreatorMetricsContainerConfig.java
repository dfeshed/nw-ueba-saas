package fortscale.aggregation.creator.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

/**
 * Created by barak_schuster on 12/24/17.
 */
@Configuration
public class AggregationRecordsCreatorMetricsContainerConfig {
    @Autowired
    private MetricCollectingService metricCollectingService;
    @Autowired
    private MetricsExporter metricsExporter;

    @Bean
    public AggregationRecordsCreatorMetricsContainer aggregationRecordsCreatorMetricsContainer()
    {
        return new AggregationRecordsCreatorMetricsContainer(metricCollectingService,metricsExporter);
    }
}
