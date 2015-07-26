package fortscale.streaming.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.aggregation.feature.util.GenericHistogram;
import fortscale.streaming.service.aggregation.feature.event.AggregatedFeatureEventConf;
import net.minidev.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by orend on 23/07/2015.
 */
@JsonTypeName(AggrFeatureEventNumberOfNewOccurencesFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureEventNumberOfNewOccurencesFunc extends AggrFeatureHistogramFunc {
	public final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_number_of_new_occurences_func";
	public final static String FEATURE_NAME = "number_of_new_occurences";
	public final static String FEATURE_DISTINCT_VALUES = "new_occurences_values";

	private boolean includeValues = false;

	/**
	 * Create new feature by running the associated {@link AggrFeatureFunction} that is configured in the given
	 * {@link AggregatedFeatureEventConf} and using the aggregated features as input to those functions.
	 *
	 * @param aggrFeatureEventConf the specification of the feature to be created
	 * @param multipleBucketsAggrFeaturesMapList list of aggregated feature maps from multiple buckets
	 * @return a new feature created by the relevant function.
	 */
	@Override
	public Feature calculateAggrFeature(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
		if(aggrFeatureEventConf == null) {
			return null;
		}
		Feature previousFeaturesAggr = super.calculateAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList.subList(0, multipleBucketsAggrFeaturesMapList.size()-1));
		Feature lastFeaturesAggr = super.calculateAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList.subList(multipleBucketsAggrFeaturesMapList.size()-1, multipleBucketsAggrFeaturesMapList.size()));

		Set<Object> previousFeaturesSet;
		Set<Object> currentFeaturesSet;
		if(lastFeaturesAggr==null || lastFeaturesAggr.getValue()==null) {
			return null;
		}
		if(previousFeaturesAggr!=null && previousFeaturesAggr.getValue() != null) {
			previousFeaturesSet = ((GenericHistogram)previousFeaturesAggr.getValue()).getObjects();
		}
		else {
			previousFeaturesSet = new HashSet<Object>();
		}
		currentFeaturesSet = ((GenericHistogram)lastFeaturesAggr.getValue()).getObjects();

		Set<Object> newOccurencesSet = substractSets(currentFeaturesSet, previousFeaturesSet);
		JSONObject value = new JSONObject();
		value.put(FEATURE_NAME, newOccurencesSet.size());
		if (includeValues) {
			value.put(FEATURE_DISTINCT_VALUES, newOccurencesSet);
		}
		Feature resFeature = new Feature(FEATURE_NAME, value);

		return resFeature;
	}

	// Getter and Setter are only for unit tests
	public boolean getIncludeValues() { return this.includeValues; }
	public void setIncludeValues(boolean includeValues) { this.includeValues = includeValues; }

	private Set<Object> substractSets(Set<Object> setA, Set<Object> setB) {
		Set<Object> symmetricDiff = new HashSet<Object>(setA);
		Set<Object> intersectionSet = new HashSet<Object>(setA);
		intersectionSet.retainAll(setB);
		symmetricDiff.removeAll(intersectionSet);

		return symmetricDiff;
	}
}
