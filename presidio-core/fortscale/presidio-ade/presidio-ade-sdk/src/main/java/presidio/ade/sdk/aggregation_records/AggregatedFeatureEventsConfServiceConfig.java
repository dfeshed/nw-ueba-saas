package presidio.ade.sdk.aggregation_records;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Barak Schuster
 */
@Configuration
@Import(BucketConfigurationServiceConfig.class)
public class AggregatedFeatureEventsConfServiceConfig {
    @Value("${fortscale.ademanager.aggregation.feature.event.conf.json.file.name:classpath:config/asl/aggregation-records/**/*.json}")
    private String aggregatedFeatureEventsBaseConfigurationPath;
    @Value("${fortscale.ademanager.aggregation.feature.event.conf.json.overriding.files.path:#{null}}")
    private String aggregatedFeatureEventsOverridingConfigurationPath;
    @Value("${fortscale.ademanager.aggregation.feature.event.conf.json.additional.files.path:#{null}}")
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
