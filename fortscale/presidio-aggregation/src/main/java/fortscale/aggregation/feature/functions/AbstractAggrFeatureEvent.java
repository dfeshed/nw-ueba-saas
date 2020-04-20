package fortscale.aggregation.feature.functions;

import java.util.List;
import java.util.Map;

import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;

public abstract class AbstractAggrFeatureEvent implements IAggrFeatureEventFunction {
	
    @Override
    public Feature calculateAggrFeature(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
    	if (aggrFeatureEventConf == null || multipleBucketsAggrFeaturesMapList == null) {
            return null;
        }
    	
    	AggrFeatureValue aggrFeatureValue = calculateAggrFeatureValue(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList);
    	Feature resFeature = null;
    	if(aggrFeatureValue != null) {
            resFeature = new Feature(aggrFeatureEventConf.getName(), aggrFeatureValue);
        }

        return resFeature;
    }
    
    protected abstract AggrFeatureValue calculateAggrFeatureValue(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList);    	
}
