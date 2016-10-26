package fortscale.ml.model.retriever;

import fortscale.accumulator.entityEvent.event.AccumulatedEntityEvent;
import fortscale.accumulator.entityEvent.store.AccumulatedEntityEventStore;
import fortscale.entity.event.EntityEventConf;
import fortscale.entity.event.JokerEntityEventData;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Configurable(preConstruction = true)
public class AccumulatedEntityEventValueRetriever extends AbstractEntityEventValueRetriever {
	@Autowired
	private AccumulatedEntityEventStore store;

	public AccumulatedEntityEventValueRetriever(AccumulatedEntityEventValueRetrieverConf config) {
		super(config, false);
	}

	@Override
	protected Stream<JokerEntityEventData> readJokerEntityEventData(EntityEventConf entityEventConf, String contextId, Date startTime, Date endTime) {
		List<AccumulatedEntityEvent> accumulatedEntityEvents = store.findAccumulatedEventsByContextIdAndStartTimeRange(
				entityEventConf,
				contextId,
				getStartTime(endTime).toInstant(),
				endTime.toInstant()
		);
		return accumulatedEntityEvents.stream()
				// create multiple JokerEntityEventData from each accumulatedEntityEvent
				.flatMap(accumulatedEntityEvent -> IntStream.range(
						0,
						// assuming all aggregated features have the same length, take the first one and deduce the
						// number of JokerEntityEventData that should be created based on the length
						accumulatedEntityEvent.getAggregated_feature_events_values_map()
								.values()
								.stream()
								.findFirst()
								.map(aggregatedFeatureEventValues -> aggregatedFeatureEventValues.length)
								.orElseGet(() -> 0)
				).mapToObj(aggrFeatureEventIndex -> {
					Map<String, Double> fullAggregatedFeatureEventNameToScore = accumulatedEntityEvent
							.getAggregated_feature_events_values_map()
							.entrySet()
							.stream()
							// from each aggregated feature's values list extract the aggrFeatureEventIndex's cell
							.map(fullAggregatedFeatureEventNameAndValues -> new ImmutablePair<>(
									fullAggregatedFeatureEventNameAndValues.getKey(),
									fullAggregatedFeatureEventNameAndValues.getValue()[aggrFeatureEventIndex]
							))
							// discard cells that don't have data
							.filter(fullAggregatedFeatureEventNameAndValue -> fullAggregatedFeatureEventNameAndValue.getRight() != null)
							// combine the cells into a map from feature name to its score
							.collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
					return new JokerEntityEventData(
							accumulatedEntityEvent.getStart_time().getEpochSecond(),
							fullAggregatedFeatureEventNameToScore);
				}));
	}
}
