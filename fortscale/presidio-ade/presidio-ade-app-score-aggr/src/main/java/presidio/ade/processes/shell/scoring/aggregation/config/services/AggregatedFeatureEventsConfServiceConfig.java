package presidio.ade.processes.shell.scoring.aggregation.config.services;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfUtilService;
import fortscale.aggregation.feature.event.config.AggregatedFeatureEventsConfUtilServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 7/25/17.
 */
@Configuration
@Import({
//        application-specific confs
        ScoreAggregationBucketConfigurationServiceConfig.class,
//        common application confs
        AggregatedFeatureEventsConfUtilServiceConfig.class})
public class AggregatedFeatureEventsConfServiceConfig {
    @Value("${fortscale.aggregation.feature.event.conf.json.file.name}")
    private String aggregatedFeatureEventsBaseConfigurationPath;
    @Value("${fortscale.aggregation.feature.event.conf.json.overriding.files.path:#{null}}")
    private String aggregatedFeatureEventsOverridingConfigurationPath;
    @Value("${fortscale.aggregation.feature.event.conf.json.additional.files.path:#{null}}")
    private String aggregatedFeatureEventsAdditionalConfigurationPath;

    @Autowired
    @Qualifier("scoreAggregationBucketConfigService")
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private AggregatedFeatureEventsConfUtilService aggregatedFeatureEventsConfUtilService;

    @Bean
    public AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService() {
        return new AggregatedFeatureEventsConfService(
                aggregatedFeatureEventsBaseConfigurationPath,
                aggregatedFeatureEventsOverridingConfigurationPath,
                aggregatedFeatureEventsAdditionalConfigurationPath,
                bucketConfigurationService,
                aggregatedFeatureEventsConfUtilService);
    }
}
