package presidio.ade.processes.shell;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.common.general.Schema;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.ml.scorer.enriched_events.EnrichedEventsScoringService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.processes.shell.scoring.aggregation.ScoreAggregationsBucketService;
import presidio.ade.processes.shell.scoring.aggregation.ScoreAggregationsService;
import presidio.monitoring.flush.MetricContainerFlusher;

import java.time.Instant;

public class ScoreAggregationsExecutionServiceImpl implements PresidioExecutionService {
    private static final String SCHEMA = "schema";
    private static final String FIXED_DURATION_STRATEGY = "fixed_duration_strategy";

    private final AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    private final int maxGroupSize;
    private final int pageSize;
    private final MetricContainerFlusher metricContainerFlusher;
    private EnrichedEventsScoringService enrichedEventsScoringService;
    private EnrichedDataStore enrichedDataStore;
    private ScoreAggregationsBucketService scoreAggregationsBucketService;
    private AggregationRecordsCreator aggregationRecordsCreator;
    private AggregatedDataStore aggregatedDataStore;
    private StoreManager storeManager;

    public ScoreAggregationsExecutionServiceImpl(
            EnrichedEventsScoringService enrichedEventsScoringService,
            EnrichedDataStore enrichedDataStore,
            ScoreAggregationsBucketService scoreAggregationsBucketService,
            AggregationRecordsCreator aggregationRecordsCreator, AggregatedDataStore aggregatedDataStore,
            AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService, StoreManager storeManager, int pageSize, int maxGroupSize,
            MetricContainerFlusher metricContainerFlusher) {


        this.enrichedEventsScoringService = enrichedEventsScoringService;
        this.enrichedDataStore = enrichedDataStore;
        this.scoreAggregationsBucketService = scoreAggregationsBucketService;
        this.aggregationRecordsCreator = aggregationRecordsCreator;
        this.aggregatedDataStore = aggregatedDataStore;
        this.aggregatedFeatureEventsConfService = aggregatedFeatureEventsConfService;
        this.storeManager = storeManager;
        this.pageSize = pageSize;
        this.maxGroupSize = maxGroupSize;
        this.metricContainerFlusher = metricContainerFlusher;
    }

    @Override
    public void run(Schema schema, Instant startInstant, Instant endInstant, Double fixedDurationStrategyInSeconds) throws Exception {
        FixedDurationStrategy strategy = FixedDurationStrategy.fromSeconds(fixedDurationStrategyInSeconds.longValue());
        ScoreAggregationsService service = new ScoreAggregationsService(
                strategy, enrichedDataStore, enrichedEventsScoringService,
                scoreAggregationsBucketService, aggregationRecordsCreator, aggregatedDataStore, aggregatedFeatureEventsConfService, pageSize, maxGroupSize, metricContainerFlusher);

        StoreMetadataProperties storeMetadataProperties = createStoreMetadataProperties(schema, strategy);
        service.execute(new TimeRange(startInstant, endInstant), schema.getName(), storeMetadataProperties);
        storeManager.cleanupCollections(storeMetadataProperties, startInstant);
    }

    @Override
    public void cleanup(Schema schema, Instant startInstant, Instant endInstant, Double fixedDurationStrategyInSeconds) throws Exception {
        FixedDurationStrategy fixedDurationStrategy = FixedDurationStrategy.fromSeconds(fixedDurationStrategyInSeconds.longValue());
        StoreMetadataProperties storeMetadataProperties = createStoreMetadataProperties(schema, fixedDurationStrategy);
        storeManager.cleanupCollections(storeMetadataProperties, startInstant, endInstant);
    }

    @Override
    public void applyRetentionPolicy(Schema schema, Instant startInstant, Instant endInstant) throws Exception {
        // TODO: Implement
    }

    @Override
    public void cleanAll(Schema schema) throws Exception {
        // TODO: Implement
    }

    private StoreMetadataProperties createStoreMetadataProperties(Schema schema, FixedDurationStrategy fixedDurationStrategy){
        StoreMetadataProperties storeMetadataProperties = new StoreMetadataProperties();
        storeMetadataProperties.setProperty(SCHEMA, schema.getName());
        storeMetadataProperties.setProperty(FIXED_DURATION_STRATEGY, fixedDurationStrategy.toStrategyName());
        return storeMetadataProperties;
    }
}
