package fortscale.ml.processes.shell;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.ml.processes.shell.model.aggregation.InMemoryFeatureBucketAggregatorConfig;
import fortscale.ml.processes.shell.model.aggregation.ModelAggregationBucketConfigurationServiceConfig;
import fortscale.ml.processes.shell.model.aggregation.ModelFeatureAggregationBucketsService;
import fortscale.services.config.ParametersValidationServiceConfig;
import fortscale.services.parameters.ParametersValidationService;
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

import static fortscale.common.general.CommonStrings.COMMAND_LINE_DATA_SOURCE_FIELD_NAME;
import static fortscale.common.general.CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME;
import static fortscale.common.general.CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME;

/**
 * Created by YaronDL on 7/2/2017.
 */
@Configuration
@EnableSpringConfigured
@Import({MongoConfig.class,
        ModelAggregationBucketConfigurationServiceConfig.class,
        EnrichedDataStoreConfig.class,
        InMemoryFeatureBucketAggregatorConfig.class,
        ParametersValidationServiceConfig.class,// todo: remove this
})
public class ModelFeatureAggregationBucketsServiceConfiguration {

    @Autowired
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    private EnrichedDataStore enrichedDataStore;
    @Autowired
    private InMemoryFeatureBucketAggregator featureBucketAggregator;
    @Autowired
    private ParametersValidationService parametersValidationService;

    @Bean
    public CommandLineRunner commandLineRunner() {

        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                // todo: all of this will be change when using spring shell
                String dataSourceParam = parametersValidationService.getMandatoryParamAsString(COMMAND_LINE_DATA_SOURCE_FIELD_NAME, strings);
                Instant startTimeParam = Instant.parse(parametersValidationService.getMandatoryParamAsString(COMMAND_LINE_START_DATE_FIELD_NAME, strings));
                Instant endTimeParam = Instant.parse(parametersValidationService.getMandatoryParamAsString(COMMAND_LINE_END_DATE_FIELD_NAME, strings));
                ModelFeatureAggregationBucketsService modelFeatureAggregationBucketsService = new ModelFeatureAggregationBucketsService(bucketConfigurationService, enrichedDataStore, featureBucketAggregator);
                TimeRange timeRange = new TimeRange(startTimeParam, endTimeParam);
                modelFeatureAggregationBucketsService.execute(timeRange, dataSourceParam);
            }
        };
    }
}
