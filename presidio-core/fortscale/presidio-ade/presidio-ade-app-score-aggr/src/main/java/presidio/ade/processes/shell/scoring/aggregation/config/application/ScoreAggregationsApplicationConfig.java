package presidio.ade.processes.shell.scoring.aggregation.config.application;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.bucket.FeatureBucketService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.ml.scorer.enriched_events.EnrichedEventsScoringService;
import fortscale.utils.shell.BootShimConfig;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.StoreManagerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.aggr.AggregatedDataStoreConfig;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;
import presidio.ade.processes.shell.ScoreAggregationsExecutionServiceImpl;
import presidio.ade.processes.shell.scoring.aggregation.config.services.AggregationRecordsCreatorConfig;
import presidio.ade.processes.shell.scoring.aggregation.config.services.EnrichedEventsScoringServiceConfig;
import presidio.ade.processes.shell.scoring.aggregation.config.services.ScoreAggregationsBucketServiceConfiguration;
import presidio.monitoring.flush.MetricContainerFlusher;
import presidio.monitoring.flush.MetricContainerFlusherConfig;

/**
 * @author Barak Schuster
 * @author Lior Govrin
 */
@Configuration
@Import({
        // Application specific configurations
        EnrichedEventsScoringServiceConfig.class,
        AggregationRecordsCreatorConfig.class,
        ScoreAggregationsBucketServiceConfiguration.class,
        // Common application configurations
        EnrichedDataStoreConfig.class,
        AggregatedDataStoreConfig.class,
        BootShimConfig.class,
        StoreManagerConfig.class,
        MetricContainerFlusherConfig.class
})
public class ScoreAggregationsApplicationConfig {
    @Value("${score-aggregation.pageIterator.pageSize}")
    private int pageSize;
    @Value("${score-aggregation.pageIterator.maxGroupSize}")
    private int maxGroupSize;
    @Value("${score-aggregation.filterNullContext:false}")
    private boolean filterNullContext;

    @Autowired
    private EnrichedEventsScoringService enrichedEventsScoringService;
    @Autowired
    private EnrichedDataStore enrichedDataStore;
    @Autowired
    private FeatureBucketService<AdeScoredEnrichedRecord> featureBucketService;
    @Autowired
    private AggregationRecordsCreator aggregationRecordsCreator;
    @Autowired
    private AggregatedDataStore aggregatedDataStore;
    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    @Autowired
    private StoreManager storeManager;
    @Autowired
    private MetricContainerFlusher metricContainerFlusher;

    @Bean
    public PresidioExecutionService presidioExecutionService() {
        return new ScoreAggregationsExecutionServiceImpl(
                enrichedEventsScoringService,
                enrichedDataStore,
                featureBucketService,
                aggregationRecordsCreator,
                aggregatedDataStore,
                aggregatedFeatureEventsConfService,
                storeManager,
                pageSize,
                maxGroupSize,
                metricContainerFlusher,
                filterNullContext);
    }
}
