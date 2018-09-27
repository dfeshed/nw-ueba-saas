package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureStringValue;
import fortscale.common.feature.MultiKeyHistogram;
import fortscale.common.util.GenericHistogram;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractAggrFeatureEventHistogram extends AbstractAggrFeatureEvent {
    private boolean removeNA = true;
    private List<String> additionalNAValues = Collections.emptyList();

    @Override
    protected AggrFeatureValue calculateAggrFeatureValue(AggregatedFeatureEventConf aggrFeatureEventConf, List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList) {
        MultiKeyHistogram multiKeyHistogram = AggrFeatureMultiKeyHistogramFunc.calculateHistogramFromBucketAggrFeature(aggrFeatureEventConf, multipleBucketsAggrFeaturesMapList);
        removeNaValues(multiKeyHistogram);
        AggrFeatureValue aggrFeatureValue = null;
        if (!multiKeyHistogram.getHistogram().isEmpty()) {
            aggrFeatureValue = calculateHistogramAggrFeatureValue(multiKeyHistogram);
        }
        return aggrFeatureValue;
    }

    private void removeNaValues(MultiKeyHistogram histogram) {
        if (removeNA) {
            AggGenericNAFeatureValues.getNAValues().forEach(value -> {
                histogram.remove(new FeatureStringValue(value));
            });

            if (additionalNAValues.size() > 0) {
                additionalNAValues.forEach(value -> {
                    histogram.remove(new FeatureStringValue(value));
                });
            }
        }
    }

    protected abstract AggrFeatureValue calculateHistogramAggrFeatureValue(MultiKeyHistogram multiKeyHistogram);

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
