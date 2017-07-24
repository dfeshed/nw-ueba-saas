package presidio.ade.processes.shell;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.common.general.DataSource;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.ml.scorer.enriched_events.EnrichedEventsScoringService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.processes.shell.scoring.aggregation.ScoreAggregationsBucketService;
import presidio.ade.processes.shell.scoring.aggregation.ScoreAggregationsService;

import java.time.Instant;

public class ScoreAggregationsExecutionServiceImpl implements PresidioExecutionService {
	private EnrichedEventsScoringService enrichedEventsScoringService;
	private EnrichedDataStore enrichedDataStore;
	private ScoreAggregationsBucketService scoreAggregationsBucketService;
	private AggregationRecordsCreator aggregationRecordsCreator;
	private AggregatedDataStore aggregatedDataStore;

	public ScoreAggregationsExecutionServiceImpl(
            EnrichedEventsScoringService enrichedEventsScoringService,
            EnrichedDataStore enrichedDataStore,
            ScoreAggregationsBucketService scoreAggregationsBucketService,
            AggregationRecordsCreator aggregationRecordsCreator, AggregatedDataStore aggregatedDataStore) {


		this.enrichedEventsScoringService = enrichedEventsScoringService;
		this.enrichedDataStore = enrichedDataStore;
		this.scoreAggregationsBucketService = scoreAggregationsBucketService;
		this.aggregationRecordsCreator = aggregationRecordsCreator;
		this.aggregatedDataStore = aggregatedDataStore;
	}

	@Override
	public void run(DataSource dataSource, Instant startInstant, Instant endInstant, Double fixedDurationStrategyInSeconds) throws Exception {
		FixedDurationStrategy strategy = FixedDurationStrategy.fromSeconds(fixedDurationStrategyInSeconds.longValue());
		ScoreAggregationsService service = new ScoreAggregationsService(
				strategy, enrichedDataStore, enrichedEventsScoringService,
				scoreAggregationsBucketService, aggregationRecordsCreator, aggregatedDataStore);

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
