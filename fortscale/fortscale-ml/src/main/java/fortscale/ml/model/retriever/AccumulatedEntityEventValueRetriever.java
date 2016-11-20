package fortscale.ml.model.retriever;

import fortscale.accumulator.entityEvent.event.AccumulatedEntityEvent;
import fortscale.accumulator.entityEvent.store.AccumulatedEntityEventStore;
import fortscale.entity.event.EntityEventConf;
import fortscale.entity.event.JokerEntityEventData;
import fortscale.utils.logging.Logger;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Configurable(preConstruction = true)
public class AccumulatedEntityEventValueRetriever extends AbstractEntityEventValueRetriever {
	private static final Logger logger = Logger.getLogger(AccumulatedEntityEventValueRetriever.class);

	@Autowired
	private AccumulatedEntityEventStore store;

	public AccumulatedEntityEventValueRetriever(AccumulatedEntityEventValueRetrieverConf config) {
		super(config, false);
	}

	public AccumulatedEntityEventValueRetriever(AccumulatedEntityEventValueRetrieverConf config,EntityEventConf entityEventConf) {
		super(config, entityEventConf, false);
	}

	@Override
	protected Stream<JokerEntityEventData> readJokerEntityEventData(EntityEventConf entityEventConf, String contextId, Date startTime, Date endTime) {
		List<AccumulatedEntityEvent> accumulatedEntityEvents = store.findAccumulatedEventsByContextIdAndStartTimeRange(
				entityEventConf,
				contextId,
				getStartTime(endTime).toInstant(),
				endTime.toInstant()
		);
		List<JokerEntityEventData> jokerEntityEventDataList = new ArrayList<>();
		for (AccumulatedEntityEvent accumulatedEntityEvent : accumulatedEntityEvents) {
			for (Integer activityTime : accumulatedEntityEvent.getActivityTime()) {
				Map<String, Double> fullAggregatedFeatureEventNameToScore = new HashedMap();
				for (Map.Entry<String, Map<Integer, Double>> aggrFeature : accumulatedEntityEvent.getAggregated_feature_events_values_map().entrySet()) {
					Double activityTimeScore = aggrFeature.getValue().get(activityTime);
					String featureName = aggrFeature.getKey();
					if (activityTimeScore == null) {
						logger.debug("score does not exists for aggrFeature={} at activityTime={} setting to 0", featureName,activityTime);
					}
					else {
						logger.debug("score={} for aggrFeature={} at activityTime={}", activityTimeScore, featureName, activityTime);
						fullAggregatedFeatureEventNameToScore.put(featureName, activityTimeScore);
					}

				}
				jokerEntityEventDataList.add(new JokerEntityEventData(accumulatedEntityEvent.getStart_time().getEpochSecond(), fullAggregatedFeatureEventNameToScore));
			}
		}
		return jokerEntityEventDataList.stream();
	}
//		return accumulatedEntityEvents.stream()
//				// create multiple JokerEntityEventData from each accumulatedEntityEvent
//				.flatMap(accumulatedEntityEvent -> IntStream.range(
//						0,
//						// assuming all aggregated features have the same length, take the first one and deduce the
//						// number of JokerEntityEventData that should be created based on the length
//						accumulatedEntityEvent.getAggregated_feature_events_values_map()
//								.values()
//								.stream()
//								.findFirst()
//								.map(aggregatedFeatureEventValues -> aggregatedFeatureEventValues.length)
//								.orElseGet(() -> 0)
//				).mapToObj(aggrFeatureEventIndex -> {
//					Map<String, Double> fullAggregatedFeatureEventNameToScore = accumulatedEntityEvent
//							.getAggregated_feature_events_values_map()
//							.entrySet()
//							.stream()
//							// from each aggregated feature's values list extract the aggrFeatureEventIndex's cell
//							.map(fullAggregatedFeatureEventNameAndValues -> new ImmutablePair<>(
//									fullAggregatedFeatureEventNameAndValues.getKey(),
//									fullAggregatedFeatureEventNameAndValues.getValue()[aggrFeatureEventIndex]
//							))
//							// discard cells that don't have data
//							.filter(fullAggregatedFeatureEventNameAndValue -> fullAggregatedFeatureEventNameAndValue.getRight() != null)
//							// combine the cells into a map from feature name to its score
//							.collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
//					return new JokerEntityEventData(
//							accumulatedEntityEvent.getStart_time().getEpochSecond(),
//							fullAggregatedFeatureEventNameToScore);
//				}));


	public void setStore(AccumulatedEntityEventStore store){
		this.store = store;
	}
}
