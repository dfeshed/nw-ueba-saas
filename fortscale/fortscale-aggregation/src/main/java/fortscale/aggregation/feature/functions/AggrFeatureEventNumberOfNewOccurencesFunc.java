package fortscale.aggregation.feature.functions;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.util.GenericHistogram;

@JsonTypeName(AggrFeatureEventNumberOfNewOccurencesFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureEventNumberOfNewOccurencesFunc extends AggrFeatureHistogramFunc {
	public final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_number_of_new_occurences_func";
	public final static String NEW_OCCURENCES_VALUES = "new_occurences_values";

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

		Set<String> previousFeaturesSet;
		Set<String> currentFeaturesSet;
		if(lastFeaturesAggr==null || lastFeaturesAggr.getValue()==null) {
			return null;
		}
		if(previousFeaturesAggr!=null && previousFeaturesAggr.getValue() != null) {
			previousFeaturesSet = ((GenericHistogram)previousFeaturesAggr.getValue()).getObjects();
		}
		else {
			previousFeaturesSet = Collections.emptySet();
		}
		currentFeaturesSet = ((GenericHistogram)lastFeaturesAggr.getValue()).getObjects();

		Set<String> newOccurencesSet = substractSets(currentFeaturesSet, previousFeaturesSet);
		AggrFeatureValue aggrFeatureValue = new AggrFeatureValue(newOccurencesSet.size());
		if (includeValues) {
			aggrFeatureValue.putAdditionalInformation(NEW_OCCURENCES_VALUES, newOccurencesSet);
		}
		Feature resFeature = new Feature(aggrFeatureEventConf.getName(), aggrFeatureValue);

		return resFeature;
	}

	// Getter and Setter are only for unit tests
	public boolean getIncludeValues() { return this.includeValues; }
	public void setIncludeValues(boolean includeValues) { this.includeValues = includeValues; }

	private Set<String> substractSets(Set<String> setA, Set<String> setB) {
		Set<String> ret = new HashSet<>();
		for(String a: setA){
			if(!setB.contains(a)){
				ret.add(a);
			}
		}

		return ret;
	}
}
