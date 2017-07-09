package fortscale.ml.processes.shell;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketStore;
import fortscale.aggregation.feature.bucket.FeatureBucketStoreMongoConfig;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.common.general.DataSource;
import fortscale.ml.processes.shell.model.aggregation.InMemoryFeatureBucketAggregatorConfig;
import fortscale.ml.processes.shell.model.aggregation.ModelAggregationBucketConfigurationServiceConfig;
import fortscale.ml.processes.shell.model.aggregation.ModelFeatureAggregationBucketsService;
import fortscale.utils.mongodb.config.MongoConfig;
import fortscale.utils.time.TimeRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;

import java.time.Instant;

/**
 * Created by YaronDL on 7/2/2017.
 */
@Configuration
@EnableSpringConfigured
@Import({MongoConfig.class,
        ModelAggregationBucketConfigurationServiceConfig.class,
        EnrichedDataStoreConfig.class,
        InMemoryFeatureBucketAggregatorConfig.class,
        FeatureBucketStoreMongoConfig.class,
})
public class ModelFeatureAggregationBucketsServiceConfiguration {

    @Autowired
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private EnrichedDataStore enrichedDataStore;
    @Autowired
    private InMemoryFeatureBucketAggregator featureBucketAggregator;
    @Autowired
    FeatureBucketStore featureBucketStore;

    @Bean
    public CommandLineRunner commandLineRunner() {

        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                // todo- hardcoded values till we will adopt presidio shell
                // todo: all of this will be change when using spring shell
                String dataSourceParam = DataSource.DLPFILE.name();
                Instant startTimeParam = Instant.parse(strings[1]);
                Instant endTimeParam = Instant.parse(strings[2]);
                ModelFeatureAggregationBucketsService modelFeatureAggregationBucketsService = new ModelFeatureAggregationBucketsService(bucketConfigurationService, enrichedDataStore, featureBucketAggregator,featureBucketStore);
                TimeRange timeRange = new TimeRange(startTimeParam, endTimeParam);
                modelFeatureAggregationBucketsService.execute(timeRange, dataSourceParam);
            }
        };
    }
}
