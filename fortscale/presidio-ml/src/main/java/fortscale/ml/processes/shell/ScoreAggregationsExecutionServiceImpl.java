package fortscale.ml.processes.shell;

import fortscale.aggregation.creator.AggregationsCreator;
import fortscale.common.general.DataSource;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.ml.processes.shell.scoring.aggregation.ScoreAggregationsBucketService;
import fortscale.ml.processes.shell.scoring.aggregation.ScoreAggregationsService;
import fortscale.ml.scorer.enriched_events.EnrichedEventsScoringService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.store.aggr.AggrDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStore;

import java.time.Instant;

public class ScoreAggregationsExecutionServiceImpl implements PresidioExecutionService {
	private EnrichedEventsScoringService enrichedEventsScoringService;
	private EnrichedDataStore enrichedDataStore;
	private ScoreAggregationsBucketService scoreAggregationsBucketService;
	private AggregationsCreator aggregationsCreator;
	private AggrDataStore aggrDataStore;

	public ScoreAggregationsExecutionServiceImpl(
			EnrichedEventsScoringService enrichedEventsScoringService,
			EnrichedDataStore enrichedDataStore,
			ScoreAggregationsBucketService scoreAggregationsBucketService,
			AggregationsCreator aggregationsCreator, AggrDataStore aggrDataStore) {

		this.enrichedEventsScoringService = enrichedEventsScoringService;
		this.enrichedDataStore = enrichedDataStore;
		this.scoreAggregationsBucketService = scoreAggregationsBucketService;
		this.aggregationsCreator = aggregationsCreator;
		this.aggrDataStore = aggrDataStore;
	}

	@Override
	public void run(DataSource dataSource, Instant startInstant, Instant endInstant, Double fixedDurationStrategyInSeconds) throws Exception {
		FixedDurationStrategy strategy = FixedDurationStrategy.fromSeconds(fixedDurationStrategyInSeconds.longValue());
		ScoreAggregationsService service = new ScoreAggregationsService(
				strategy, enrichedDataStore, enrichedEventsScoringService,
				scoreAggregationsBucketService,aggregationsCreator,aggrDataStore);
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
