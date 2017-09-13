package presidio.ade.processes.shell;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.common.general.Schema;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.ml.scorer.enriched_events.EnrichedEventsScoringService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.time.TimeRange;
import fortscale.utils.ttl.TtlService;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.processes.shell.scoring.aggregation.ScoreAggregationsBucketService;
import presidio.ade.processes.shell.scoring.aggregation.ScoreAggregationsService;

import java.time.Instant;

public class ScoreAggregationsExecutionServiceImpl implements PresidioExecutionService {
	private final AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	private EnrichedEventsScoringService enrichedEventsScoringService;
	private EnrichedDataStore enrichedDataStore;
	private ScoreAggregationsBucketService scoreAggregationsBucketService;
	private AggregationRecordsCreator aggregationRecordsCreator;
	private AggregatedDataStore aggregatedDataStore;
	private TtlService ttlService;

	public ScoreAggregationsExecutionServiceImpl(
			EnrichedEventsScoringService enrichedEventsScoringService,
			EnrichedDataStore enrichedDataStore,
			ScoreAggregationsBucketService scoreAggregationsBucketService,
			AggregationRecordsCreator aggregationRecordsCreator, AggregatedDataStore aggregatedDataStore,
			AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService, TtlService ttlService) {


		this.enrichedEventsScoringService = enrichedEventsScoringService;
		this.enrichedDataStore = enrichedDataStore;
		this.scoreAggregationsBucketService = scoreAggregationsBucketService;
		this.aggregationRecordsCreator = aggregationRecordsCreator;
		this.aggregatedDataStore = aggregatedDataStore;
		this.aggregatedFeatureEventsConfService = aggregatedFeatureEventsConfService;
		this.ttlService = ttlService;
	}

	@Override
	public void run(Schema schema, Instant startInstant, Instant endInstant, Double fixedDurationStrategyInSeconds) throws Exception {
		FixedDurationStrategy strategy = FixedDurationStrategy.fromSeconds(fixedDurationStrategyInSeconds.longValue());
		ScoreAggregationsService service = new ScoreAggregationsService(
				strategy, enrichedDataStore, enrichedEventsScoringService,
				scoreAggregationsBucketService, aggregationRecordsCreator, aggregatedDataStore, aggregatedFeatureEventsConfService, ttlService);

		service.execute(new TimeRange(startInstant, endInstant), schema.getName());
	}

	@Override
	public void clean(Schema schema, Instant startInstant, Instant endInstant) throws Exception {
		// TODO: Implement
	}

	@Override
	public void cleanAll(Schema schema) throws Exception {
		// TODO: Implement
	}
}
