package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.common.feature.MultiKeyHistogram;

import java.util.*;

@JsonTypeName(AggrFeatureEventNumberOfNewOccurrencesFunc.AGGR_FEATURE_FUNCTION_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AggrFeatureEventNumberOfNewOccurrencesFunc extends AbstractAggrFeatureEvent {
	public final static String AGGR_FEATURE_FUNCTION_TYPE = "aggr_feature_number_of_new_occurrences_func";

	@Override
	protected AggrFeatureValue calculateAggrFeatureValue(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
		int prevNumberOfBuckets = multipleBucketsAggrFeaturesMapList.size() >= aggrFeatureEventConf.getBucketsLeap() ? multipleBucketsAggrFeaturesMapList.size() - aggrFeatureEventConf.getBucketsLeap() : 0;
		MultiKeyHistogram previousMultiKeyHistogram = AggrFeatureMultiKeyHistogramFunc.calculateHistogramFromBucketAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList.subList(0, prevNumberOfBuckets),true, Collections.emptyList());
		MultiKeyHistogram lastMultiKeyHistogram = AggrFeatureMultiKeyHistogramFunc.calculateHistogramFromBucketAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList.subList(prevNumberOfBuckets, multipleBucketsAggrFeaturesMapList.size()),true, Collections.emptyList());
		Set<MultiKeyFeature> previousFeaturesSet = previousMultiKeyHistogram.getHistogram().keySet();
		Set<MultiKeyFeature> currentFeaturesSet = lastMultiKeyHistogram.getHistogram().keySet();

		Set<MultiKeyFeature> newOccurrencesSet = subtractSets(currentFeaturesSet, previousFeaturesSet);
		return new AggrFeatureValue(newOccurrencesSet.size());
	}

	private Set<MultiKeyFeature> subtractSets(Set<MultiKeyFeature> setA, Set<MultiKeyFeature> setB) {
		Set<MultiKeyFeature> ret = new HashSet<>();
		for (MultiKeyFeature a : setA) {
			if (!setB.contains(a)) {
				ret.add(a);
			}
		}
		return ret;
	}
}
