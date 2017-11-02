package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractAggrFeatureEventHistogram extends AbstractAggrFeatureEvent {
    private boolean removeNA = false;

    private List<String> additionalNAValues = Collections.emptyList();
	
    @Override
    protected AggrFeatureValue calculateAggrFeatureValue(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
    	GenericHistogram histogram = AggrFeatureHistogramFunc.calculateHistogramFromBucketAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList);
    	removeNaValues(histogram);
        AggrFeatureValue aggrFeatureValue = null;
        if(!histogram.getHistogramMap().isEmpty()) {
            aggrFeatureValue = calculateHistogramAggrFeatureValue(histogram);
            fillAggrFeatureValueWithAdditionalInfo(aggrFeatureValue, histogram);
        }
        return aggrFeatureValue;
    }

    private void removeNaValues(GenericHistogram histogram){
        if(removeNA){
            AggGenericNAFeatureValues.getNAValues().forEach(naValue -> histogram.remove(naValue));
            if(additionalNAValues.size() > 0){
                additionalNAValues.forEach(naValue -> histogram.remove(naValue));
            }
        }
    }
    
    protected abstract AggrFeatureValue calculateHistogramAggrFeatureValue(GenericHistogram histogram);
    
    protected void fillAggrFeatureValueWithAdditionalInfo(AggrFeatureValue aggrFeatureValue, GenericHistogram histogram){}

    public boolean getRemoveNA() {
        return this.removeNA;
    }

    public void setRemoveNA(boolean removeNA) {
        this.removeNA = removeNA;
    }

    public List<String> getAdditionalNAValues() {
        return additionalNAValues;
    }

    public void setAdditionalNAValues(List<String> additionalNAValues) {
        this.additionalNAValues = additionalNAValues;
    }
}
