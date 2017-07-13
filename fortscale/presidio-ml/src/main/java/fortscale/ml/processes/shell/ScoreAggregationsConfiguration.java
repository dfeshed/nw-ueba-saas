package fortscale.ml.processes.shell;

import fortscale.aggregation.creator.AggregationsCreator;
import fortscale.aggregation.creator.AggregationsCreatorConfig;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.ml.model.cache.EventModelsCacheServiceConfig;
import fortscale.ml.processes.shell.scoring.aggregation.ScoreAggregationsBucketService;
import fortscale.ml.processes.shell.scoring.aggregation.ScoreAggregationsBucketServiceConfiguration;
import fortscale.ml.scorer.enriched_events.EnrichedEventsScoringService;
import fortscale.ml.scorer.enriched_events.EnrichedEventsScoringServiceConfig;
import fortscale.utils.mongodb.config.MongoConfig;
import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import presidio.ade.domain.store.aggr.AggrDataStore;
import presidio.ade.domain.store.aggr.AggrDataStoreConfig;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;

@Configuration
@EnableSpringConfigured
@Import({
        MongoConfig.class,
        EventModelsCacheServiceConfig.class,
        EnrichedEventsScoringServiceConfig.class,
        EnrichedDataStoreConfig.class,
        ScoreAggregationsBucketServiceConfiguration.class,
        AggregationsCreatorConfig.class,
        AggrDataStoreConfig.class,
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
    private AggregationsCreator aggregationsCreator;
    @Autowired
    private AggrDataStore aggrDataStore;

    @Bean
    public PresidioExecutionService presidioExecutionService() {
        return new ScoreAggregationsExecutionServiceImpl(
                enrichedEventsScoringService, enrichedDataStore, scoreAggregationsBucketService,aggregationsCreator,aggrDataStore);
    }
}
