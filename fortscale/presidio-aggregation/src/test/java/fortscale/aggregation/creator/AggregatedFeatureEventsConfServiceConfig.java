package fortscale.aggregation.creator;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.config.BucketConfigurationServiceConfig;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 10/8/16.
 */
@Configuration
@Import(BucketConfigurationServiceConfig.class)
public class AggregatedFeatureEventsConfServiceConfig {
    @Value("${fortscale.aggregation.feature.event.conf.json.file.name}")
    private String aggregatedFeatureEventsBaseConfigurationPath;
    @Value("${fortscale.aggregation.feature.event.conf.json.overriding.files.path:#{null}}")
    private String aggregatedFeatureEventsOverridingConfigurationPath;
    @Value("${fortscale.aggregation.feature.event.conf.json.additional.files.path:#{null}}")
    private String aggregatedFeatureEventsAdditionalConfigurationPath;

    @Autowired
    private BucketConfigurationService bucketConfigurationService;

    @Bean
    public AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService() {
        return new AggregatedFeatureEventsConfService(
                aggregatedFeatureEventsBaseConfigurationPath,
                aggregatedFeatureEventsOverridingConfigurationPath,
                aggregatedFeatureEventsAdditionalConfigurationPath,
                bucketConfigurationService);
    }
}
