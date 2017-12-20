package presidio.ade.processes.shell.feature.aggregation.buckets;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketStore;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.common.general.Schema;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.utils.store.StoreManager;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.store.enriched.EnrichedDataStore;

import java.time.Instant;

public class ModelFeatureAggregationBucketsExecutionServiceImpl implements PresidioExecutionService {
	private final int maxGroupSize;
	private final int pageSize;
	private BucketConfigurationService bucketConfigurationService;
	private EnrichedDataStore enrichedDataStore;
	private InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator;
	private FeatureBucketStore featureBucketStore;
	private StoreManager storeManager;

	public ModelFeatureAggregationBucketsExecutionServiceImpl(
            BucketConfigurationService bucketConfigurationService,
            EnrichedDataStore enrichedDataStore,
            InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator,
            FeatureBucketStore featureBucketStore, StoreManager storeManager, int pageSize, int maxGroupSize) {

		this.bucketConfigurationService = bucketConfigurationService;
		this.enrichedDataStore = enrichedDataStore;
		this.inMemoryFeatureBucketAggregator = inMemoryFeatureBucketAggregator;
		this.featureBucketStore = featureBucketStore;
		this.storeManager = storeManager;
		this.pageSize = pageSize;
		this.maxGroupSize = maxGroupSize;
	}

	@Override
	public void run(Schema schema, Instant startInstant, Instant endInstant, Double fixedDurationStrategyInSeconds) throws Exception {
		ModelFeatureAggregationBucketsService service = new ModelFeatureAggregationBucketsService(
				bucketConfigurationService, enrichedDataStore, inMemoryFeatureBucketAggregator, featureBucketStore, pageSize, maxGroupSize);
		service.execute(new TimeRange(startInstant, endInstant), schema.getName());
		storeManager.cleanupCollections(startInstant);
	}

    @Override
    public void cleanRetention(Schema schema, Instant startInstant, Instant endInstant) throws Exception {
        // TODO: Implement
    }

	@Override
	public void cleanAll(Schema schema) throws Exception {
		// TODO: Implement
	}

	@Override
	public void cleanup(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {
		storeManager.cleanupCollections(startDate, endDate);
	}
}
