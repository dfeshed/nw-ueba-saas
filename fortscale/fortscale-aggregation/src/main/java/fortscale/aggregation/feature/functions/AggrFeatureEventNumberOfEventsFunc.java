package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.utils.ConversionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by orend on 23/07/2015.
 */
@JsonTypeName(AggrFeatureEventNumberOfEventsFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureEventNumberOfEventsFunc implements AggrFeatureFunction, AggrFeatureEventFunction {
	public final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_number_of_events_func";
	public final static String AGGREGATED_FEATURE_NAME_TO_SUM = "aggregated_feature_name_to_sum";

	/**
	 * Updates the aggrFeatures by running the associated {@link AggrFeatureFunction} that is configured for each
	 * AggrFeature in the given  {@link AggregatedFeatureConf} and using the features as input to those functions.
	 * Creates new map entry <String, Feature> for any AggrFeatureConf for which there is no entry in the aggrFeatures
	 * map.
	 * @param aggregatedFeatureConf
	 * @param aggrFeature
	 * @param features
	 * @return a map with entry for each {@link AggregatedFeatureConf}. Each entry is updated by the relevant function.
	 * If aggrFeatures is null, a new {@link HashMap <String, Feature>} will be created with new Feature object for each
	 * of the {@link AggregatedFeatureConf} in aggrFeatureConfs.
	 */
	@Override
	public Object updateAggrFeature(AggregatedFeatureConf aggregatedFeatureConf, Map<String, Feature> features, Feature aggrFeature) {
		if (aggregatedFeatureConf == null) {
			return null;
		}
		if (aggrFeature == null) {
			aggrFeature = new Feature(aggregatedFeatureConf.getName(), 1);
			return aggrFeature;
		}

		Object value = aggrFeature.getValue();
		if (value == null) {
			value = 0;
			aggrFeature.setValue(value);
		}
		aggrFeature.setValue(ConversionUtils.convertToInteger(aggrFeature.getValue())+1);

		return aggrFeature.getValue();
	}

	/**
	 * Create new feature by running the associated {@link AggrFeatureEventFunction} that is configured in the given
	 * {@link AggregatedFeatureEventConf} and using the aggregated features as input to those functions.
	 *
	 * @param aggrFeatureEventConf the specification of the feature to be created
	 * @param multipleBucketsAggrFeaturesMapList list of aggregated feature maps from multiple buckets
	 * @return a new feature created by the relevant function.
	 */
	@Override
	public Feature calculateAggrFeature(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
		if (aggrFeatureEventConf == null || multipleBucketsAggrFeaturesMapList == null) {
			return null;
		}

		Integer eventsCounter = 0;

		List<String> aggregatedFeatureNamesList = aggrFeatureEventConf.getAggregatedFeatureNamesMap().get(AGGREGATED_FEATURE_NAME_TO_SUM);
		if (aggregatedFeatureNamesList.size() != 1) {
			throw new IllegalArgumentException(String.format("wrong number of parameters: %d", aggregatedFeatureNamesList.size()));
		}
		String aggrFeatureName = aggregatedFeatureNamesList.get(0);
		for (Map<String, Feature> aggrFeatures : multipleBucketsAggrFeaturesMapList) {
			Feature numberOfevents = aggrFeatures.get(aggrFeatureName);
			if (numberOfevents != null) {
				if (numberOfevents.getValue() instanceof Integer) {
					eventsCounter += (Integer) numberOfevents.getValue();
				} else {
					throw new IllegalArgumentException(String.format("Missing aggregated feature named %s of type %s", aggrFeatureName, Integer.class.getSimpleName()));
				}
			}
		}

		Feature resFeature = new Feature(aggrFeatureEventConf.getName(), eventsCounter);
		return resFeature;
	}
}