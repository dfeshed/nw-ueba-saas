package fortscale.entity.event;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfUtilService;

public class JokerAggrEventData {
	String fullAggregatedFeatureEventName;
	Double score;

	public JokerAggrEventData(String fullAggregatedFeatureEventName, Double score) {
		this.fullAggregatedFeatureEventName = fullAggregatedFeatureEventName;
		this.score = score;
	}

	public JokerAggrEventData(AggrEvent aggrFeatureEvent) {
		this(
				AggregatedFeatureEventsConfUtilService.buildFullAggregatedFeatureEventName(
						aggrFeatureEvent.getBucketConfName(),
						aggrFeatureEvent.getAggregatedFeatureName()
				),
				aggrFeatureEvent.isOfTypeF() ? aggrFeatureEvent.getScore() : aggrFeatureEvent.getAggregatedFeatureValue()
		);
	}

	public Double getScore() {
		return score;
	}

	public String getFullAggregatedFeatureEventName() {
		return fullAggregatedFeatureEventName;
	}
}
