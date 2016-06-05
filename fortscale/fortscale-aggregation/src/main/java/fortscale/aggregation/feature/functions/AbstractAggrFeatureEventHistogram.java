package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.datastructures.GenericHistogram;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;

import java.util.List;
import java.util.Map;

public abstract class AbstractAggrFeatureEventHistogram extends AbstractAggrFeatureEvent {
	
	
    @Override
    protected AggrFeatureValue calculateAggrFeatureValue(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
    	GenericHistogram histogram = AggrFeatureHistogramFunc.calculateHistogramFromBucketAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList);
        AggrFeatureValue aggrFeatureValue = calculateHistogramAggrFeatureValue(histogram);
        fillAggrFeatureValueWithAdditionalInfo(aggrFeatureValue, histogram);
        return aggrFeatureValue;
    }
    
    protected abstract AggrFeatureValue calculateHistogramAggrFeatureValue(GenericHistogram histogram);
    
    protected void fillAggrFeatureValueWithAdditionalInfo(AggrFeatureValue aggrFeatureValue, GenericHistogram histogram){}
}
