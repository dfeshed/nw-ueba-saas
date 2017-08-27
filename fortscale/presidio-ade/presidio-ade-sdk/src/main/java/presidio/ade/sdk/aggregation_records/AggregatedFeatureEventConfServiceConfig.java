package presidio.ade.sdk.aggregation_records;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 15/08/2017.
 */
@Configuration
@Import(AdeManagerSdkBucketConfigurationServiceConfig.class)
public class AggregatedFeatureEventConfServiceConfig {
    @Value("${fortscale.ademanager.aggregation.feature.event.conf.json.file.name:classpath:config/asl/aggregation-records/**/*.json}")
    private String aggregatedFeatureEventsBaseConfigurationPath;
    @Value("${fortscale.ademanager.aggregation.feature.event.conf.json.overriding.files.path:#{null}}")
    private String aggregatedFeatureEventsOverridingConfigurationPath;
    @Value("${fortscale.ademanager.aggregation.feature.event.conf.json.additional.files.path:#{null}}")
    private String aggregatedFeatureEventsAdditionalConfigurationPath;

    @Autowired
    @Qualifier("adeManagerSdkBucketConfigurationService")
    private BucketConfigurationService bucketConfigurationService;

    @Bean
    public AggregatedFeatureEventsConfService adeManagerSdkAggregatedFeatureEventsConfService() {
        return new AggregatedFeatureEventsConfService(
                aggregatedFeatureEventsBaseConfigurationPath,
                aggregatedFeatureEventsOverridingConfigurationPath,
                aggregatedFeatureEventsAdditionalConfigurationPath,
                bucketConfigurationService);
    }
}
