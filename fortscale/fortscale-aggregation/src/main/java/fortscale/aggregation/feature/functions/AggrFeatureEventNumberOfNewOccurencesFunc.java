package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.datastructures.GenericHistogram;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JsonTypeName(AggrFeatureEventNumberOfNewOccurencesFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureEventNumberOfNewOccurencesFunc extends AbstractAggrFeatureEvent {
	public final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_number_of_new_occurences_func";
	public final static String NEW_OCCURENCES_VALUES = "new_occurences_values";

	private boolean includeValues = false;

	
	@Override
    protected AggrFeatureValue calculateAggrFeatureValue(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
		int prevNumberOfBuckets = multipleBucketsAggrFeaturesMapList.size() >= aggrFeatureEventConf.getBucketsLeap() ? 
				multipleBucketsAggrFeaturesMapList.size() - aggrFeatureEventConf.getBucketsLeap() : 0;
		GenericHistogram previousGenericHistogram = AggrFeatureHistogramFunc.calculateHistogramFromBucketAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList.subList(0, prevNumberOfBuckets));
		GenericHistogram lastGenericHistogram = AggrFeatureHistogramFunc.calculateHistogramFromBucketAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList.subList(prevNumberOfBuckets, multipleBucketsAggrFeaturesMapList.size()));

		Set<String> previousFeaturesSet = previousGenericHistogram.getObjects();
		Set<String> currentFeaturesSet = lastGenericHistogram.getObjects();

		Set<String> newOccurencesSet = substractSets(currentFeaturesSet, previousFeaturesSet);
		AggrFeatureValue aggrFeatureValue = new AggrFeatureValue(newOccurencesSet.size(), (long)lastGenericHistogram.getTotalCount());
		if (includeValues) {
			aggrFeatureValue.putAdditionalInformation(NEW_OCCURENCES_VALUES, newOccurencesSet);
		}

		return aggrFeatureValue;
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
