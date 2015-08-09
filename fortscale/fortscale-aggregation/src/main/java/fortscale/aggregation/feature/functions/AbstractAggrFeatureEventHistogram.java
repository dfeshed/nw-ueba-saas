package fortscale.aggregation.feature.functions;

import java.util.List;
import java.util.Map;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.util.GenericHistogram;

public abstract class AbstractAggrFeatureEventHistogram extends AbstractAggrFeatureEvent {
	
	
    @Override
    protected AggrFeatureValue calculateAggrFeatureValue(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
    	GenericHistogram histogram = AggrFeatureHistogramFunc.calculateHistogramFromBucketAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList);
        AggrFeatureValue aggrFeatureValue = calculateHistogramAggrFeatureValue(histogram);
        fillAggrFeatureValueWithAdditionalInfo(aggrFeatureValue, histogram);
        return aggrFeatureValue;
    }
    
    protected abstract AggrFeatureValue calculateHistogramAggrFeatureValue(GenericHistogram histogram);
    
    protected void fillAggrFeatureValueWithAdditionalInfo(AggrFeatureValue aggrFeatureValue, GenericHistogram histogram){
    	aggrFeatureValue.putAdditionalInformation(AbstractAggrFeatureEvent.AGGR_FEATURE_TOTAL_NUMBER_OF_EVENTS, histogram.getTotalCount());
    }
}
