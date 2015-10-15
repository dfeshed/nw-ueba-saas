package fortscale.aggregation.feature.functions;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;

/**
 * Created by orend on 13/08/2015.
 */

@JsonTypeName(AggrFeatureEventHasEventsFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureEventHasEventsFunc extends AbstractAggrFeatureEvent
{
	public final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_has_events_func";

	@Override
	protected AggrFeatureValue calculateAggrFeatureValue(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList){
		long eventsCounter = AggrFeatureEventsCounterFunc.calculateNumberOfEventsFromBucketAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList);
		if (eventsCounter > 0) {
			return new AggrFeatureValue(1, eventsCounter);
		} else {
			return new AggrFeatureValue(0, eventsCounter);
		}		
	}
}
