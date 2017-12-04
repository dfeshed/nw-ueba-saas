package fortscale.ml.scorer.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.monitoring.services.MetricCollectingService;

/**
 * Created by barak_schuster on 11/27/17.
 */
@Configuration
public class ScoringServiceMetricsContainerConfig
{
    @Autowired
    private MetricCollectingService metricCollectingService;

    @Bean
    public ScoringServiceMetricsContainer scoringServiceMetricsContainer()
    {
        return new ScoringServiceMetricsContainer(metricCollectingService);
    }
}
