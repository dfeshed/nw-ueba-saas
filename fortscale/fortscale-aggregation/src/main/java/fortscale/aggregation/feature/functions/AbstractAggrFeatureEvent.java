package fortscale.aggregation.feature.functions;

import java.util.List;
import java.util.Map;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;

public abstract class AbstractAggrFeatureEvent implements IAggrFeatureEventFunction {
	protected final static String AGGR_FEATURE_TOTAL_NUMBER_OF_EVENTS = "total";
	
    @Override
    public Feature calculateAggrFeature(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
    	if (aggrFeatureEventConf == null || multipleBucketsAggrFeaturesMapList == null) {
            return null;
        }
    	
    	AggrFeatureValue aggrFeatureValue = calculateAggrFeatureValue(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList);
        Feature resFeature = new Feature(aggrFeatureEventConf.getName(), aggrFeatureValue);

        return resFeature;
    }
    
    protected abstract AggrFeatureValue calculateAggrFeatureValue(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList);    	
}
