package fortscale.aggregation.feature.event.config;

import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfUtilService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 10/8/16.
 */
@Configuration
public class AggregatedFeatureEventsConfUtilServiceConfig {
    @Bean
    public AggregatedFeatureEventsConfUtilService aggregatedFeatureEventsConfUtilService()
    {
        return new AggregatedFeatureEventsConfUtilService();
    }
}
