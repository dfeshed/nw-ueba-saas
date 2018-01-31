package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractAggrFeatureEventHistogram extends AbstractAggrFeatureEvent {
    private boolean removeNA = true;
    private List<String> additionalNAValues = Collections.emptyList();

    @Override
    protected AggrFeatureValue calculateAggrFeatureValue(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
        GenericHistogram histogram = AggrFeatureHistogramFunc.calculateHistogramFromBucketAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList);
        removeNaValues(histogram);
        AggrFeatureValue aggrFeatureValue = null;
        if (!histogram.getHistogramMap().isEmpty()) {
            aggrFeatureValue = calculateHistogramAggrFeatureValue(histogram);
        }
        return aggrFeatureValue;
    }

    private void removeNaValues(GenericHistogram histogram) {
        if (removeNA) {
            AggGenericNAFeatureValues.getNAValues().forEach(histogram::remove);
            if (additionalNAValues.size() > 0) {
                additionalNAValues.forEach(histogram::remove);
            }
        }
    }

    protected abstract AggrFeatureValue calculateHistogramAggrFeatureValue(GenericHistogram histogram);

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
