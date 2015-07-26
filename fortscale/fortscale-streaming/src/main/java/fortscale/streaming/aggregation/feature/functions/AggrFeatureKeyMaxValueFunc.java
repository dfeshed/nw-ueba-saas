package fortscale.streaming.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.service.aggregation.AggregatedFeatureConf;
import fortscale.streaming.service.aggregation.feature.event.AggregatedFeatureEventConf;
import net.minidev.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by orend on 26/07/2015.
 */

@JsonTypeName(AggrFeatureKeyMaxValueFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureKeyMaxValueFunc implements AggrFeatureFunction, AggrFeatureEventFunction {
	final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_key_max_value_func";
	final static String GROUP_BY_FIELD_NAME = "groupBy";

	@Override
	public Object updateAggrFeature(AggregatedFeatureConf aggregatedFeatureConf, Map<String, Feature> features, Feature aggrFeature) {
		if (aggregatedFeatureConf == null || aggrFeature == null) {
			return null;
		}

		Object value = aggrFeature.getValue();
		if (value == null) {
			JSONObject jsonObject = new JSONObject();
			aggrFeature.setValue(jsonObject);
			return aggrFeature;
		} else if (!(value instanceof JSONObject)) {
			throw new IllegalArgumentException(String.format("Value of aggregated feature %s must be of type %s",
					aggrFeature.getName(), JSONObject.class.getSimpleName()));
		}

		JSONObject keysMaxValues = (JSONObject)value;
		if (features != null) {
			List<String> featureNames = aggregatedFeatureConf.getFeatureNamesMap().get(GROUP_BY_FIELD_NAME);
			for (String featureName : featureNames) {
				Feature feature = features.get(featureName);
				if (feature != null) {
					Integer currentMax = (Integer)keysMaxValues.get(featureName);
					if (currentMax == null || (Integer)feature.getValue() > currentMax) {
						keysMaxValues.put(feature.getName(), feature.getValue());
					}
				}
			}
		}

		return keysMaxValues;
	}

	@Override
	public Feature calculateAggrFeature(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
		if (aggrFeatureEventConf == null || multipleBucketsAggrFeaturesMapList == null) {
			return null;
		}

		JSONObject keysMaxValues = new JSONObject();
		List<String> aggregatedFeatureNamesList = aggrFeatureEventConf.getAggregatedFeatureNamesMap().get(GROUP_BY_FIELD_NAME);
		for (Map<String, Feature> aggrFeatures : multipleBucketsAggrFeaturesMapList) {
			for (String aggregatedFeatureName : aggregatedFeatureNamesList) {
				Feature aggrFeature = aggrFeatures.get(aggregatedFeatureName);
				if (aggrFeature != null) {
					if(aggrFeature.getValue() instanceof Integer) {
						Integer currentMax = (Integer)keysMaxValues.get(aggrFeature.getName());
						if (currentMax == null || (Integer)aggrFeature.getValue() > currentMax) {
							keysMaxValues.put(aggrFeature.getName(), aggrFeature.getValue());
						}



					} else {
						throw new IllegalArgumentException(String.format("Missing aggregated feature named %s of type %s",
								aggregatedFeatureName, Integer.class.getSimpleName()));
					}
				}
			}
		}
		return getFeatureWithMaxValues(keysMaxValues);
	}

	private Feature getFeatureWithMaxValues(JSONObject keysMaxValues) {
		Feature ret = null;
		Iterator iterator = keysMaxValues.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry pair = (Map.Entry) iterator.next();
			if (ret == null || (Integer) ret.getValue() < (Integer) pair.getValue()) {
				ret.setName((String) pair.getKey());
				ret.setValue(pair.getValue());
			}
		}

		return ret;
	}
}
