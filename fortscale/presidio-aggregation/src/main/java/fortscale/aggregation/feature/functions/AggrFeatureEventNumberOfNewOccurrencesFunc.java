package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JsonTypeName(AggrFeatureEventNumberOfNewOccurrencesFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureEventNumberOfNewOccurrencesFunc extends AbstractAggrFeatureEvent {
	public final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_number_of_new_occurrences_func";

	@Override
	protected AggrFeatureValue calculateAggrFeatureValue(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
		int prevNumberOfBuckets = multipleBucketsAggrFeaturesMapList.size() >= aggrFeatureEventConf.getBucketsLeap() ? multipleBucketsAggrFeaturesMapList.size() - aggrFeatureEventConf.getBucketsLeap() : 0;
		GenericHistogram previousGenericHistogram = AggrFeatureHistogramFunc.calculateHistogramFromBucketAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList.subList(0, prevNumberOfBuckets));
		GenericHistogram lastGenericHistogram = AggrFeatureHistogramFunc.calculateHistogramFromBucketAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList.subList(prevNumberOfBuckets, multipleBucketsAggrFeaturesMapList.size()));
		Set<String> previousFeaturesSet = previousGenericHistogram.getObjects();
		Set<String> currentFeaturesSet = lastGenericHistogram.getObjects();
		Set<String> newOccurrencesSet = subtractSets(currentFeaturesSet, previousFeaturesSet);
		return new AggrFeatureValue(newOccurrencesSet.size(), (long)lastGenericHistogram.getTotalCount());
	}

	private Set<String> subtractSets(Set<String> setA, Set<String> setB) {
		Set<String> ret = new HashSet<>();
		for (String a : setA) {
			if (!setB.contains(a)) {
				ret.add(a);
			}
		}
		return ret;
	}
}
