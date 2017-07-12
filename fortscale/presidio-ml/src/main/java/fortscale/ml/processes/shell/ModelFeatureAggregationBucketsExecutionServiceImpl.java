package fortscale.ml.processes.shell;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketStore;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.common.general.DataSource;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.ml.processes.shell.model.aggregation.ModelFeatureAggregationBucketsService;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.store.enriched.EnrichedDataStore;

import java.time.Instant;

public class ModelFeatureAggregationBucketsExecutionServiceImpl implements PresidioExecutionService {
	private BucketConfigurationService bucketConfigurationService;
	private EnrichedDataStore enrichedDataStore;
	private InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator;
	private FeatureBucketStore featureBucketStore;

	public ModelFeatureAggregationBucketsExecutionServiceImpl(
			BucketConfigurationService bucketConfigurationService,
			EnrichedDataStore enrichedDataStore,
			InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator,
			FeatureBucketStore featureBucketStore) {

		this.bucketConfigurationService = bucketConfigurationService;
		this.enrichedDataStore = enrichedDataStore;
		this.inMemoryFeatureBucketAggregator = inMemoryFeatureBucketAggregator;
		this.featureBucketStore = featureBucketStore;
	}

	@Override
	public void run(DataSource dataSource, Instant startInstant, Instant endInstant, Double fixedDurationStrategyInSeconds) throws Exception {
		ModelFeatureAggregationBucketsService service = new ModelFeatureAggregationBucketsService(
				bucketConfigurationService, enrichedDataStore, inMemoryFeatureBucketAggregator, featureBucketStore);
		service.execute(new TimeRange(startInstant, endInstant), dataSource.getName());
	}

	@Override
	public void clean(DataSource dataSource, Instant startInstant, Instant endInstant) throws Exception {
		// TODO: Implement
	}

	@Override
	public void cleanAll(DataSource dataSource) throws Exception {
		// TODO: Implement
	}
}
