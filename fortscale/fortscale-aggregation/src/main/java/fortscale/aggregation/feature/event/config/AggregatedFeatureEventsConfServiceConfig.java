package fortscale.aggregation.feature.event.config;

import fortscale.aggregation.feature.bucket.config.BucketConfigurationServiceConfig;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 10/8/16.
 */
@Configuration
@Import({BucketConfigurationServiceConfig.class,
        AggregatedFeatureEventsConfUtilServiceConfig.class,
        RetentionStrategiesConfServiceConfig.class})
public class AggregatedFeatureEventsConfServiceConfig {
    @Bean
    public AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService()
    {
        return new AggregatedFeatureEventsConfService();
    }
}
