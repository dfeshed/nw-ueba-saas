package presidio.ade.processes.shell;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.creator.AggregationRecordsCreatorConfig;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.ml.model.cache.EventModelsCacheServiceConfig;
import fortscale.ml.processes.shell.model.aggregation.ModelAggregationBucketConfigurationServiceConfig;
import fortscale.ml.scorer.enriched_events.EnrichedEventsScoringService;
import fortscale.ml.scorer.enriched_events.EnrichedEventsScoringServiceConfig;
import fortscale.utils.mongodb.config.MongoConfig;
import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.aggr.AggregatedDataStoreConfig;

import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.ade.processes.shell.scoring.aggregation.ScoreAggregationsBucketService;
import presidio.ade.processes.shell.scoring.aggregation.ScoreAggregationsBucketServiceConfiguration;

@Configuration
@EnableSpringConfigured
@Import({
        MongoConfig.class,
        EventModelsCacheServiceConfig.class,
        EnrichedEventsScoringServiceConfig.class,
        EnrichedDataStoreConfig.class,
        ScoreAggregationsBucketServiceConfiguration.class,
        AggregationRecordsCreatorConfig.class,
        AggregatedDataStoreConfig.class,
        ModelAggregationBucketConfigurationServiceConfig.class,
        NullStatsServiceConfig.class // TODO: Remove this
})
public class ScoreAggregationsConfiguration {
    @Autowired
    private EnrichedEventsScoringService enrichedEventsScoringService;
    @Autowired
    private EnrichedDataStore enrichedDataStore;
    @Autowired
    private ScoreAggregationsBucketService scoreAggregationsBucketService;
    @Autowired
    private AggregationRecordsCreator aggregationRecordsCreator;
    @Autowired
    private AggregatedDataStore aggregatedDataStore;

    @Bean
    public PresidioExecutionService presidioExecutionService() {
        return new ScoreAggregationsExecutionServiceImpl(
                enrichedEventsScoringService, enrichedDataStore, scoreAggregationsBucketService, aggregationRecordsCreator, aggregatedDataStore);
    }
}
