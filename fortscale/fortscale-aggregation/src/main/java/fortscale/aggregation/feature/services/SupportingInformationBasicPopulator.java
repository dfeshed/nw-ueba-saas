package fortscale.aggregation.feature.services;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.domain.core.HistogramKey;
import fortscale.domain.core.HistogramSingleKey;
import fortscale.domain.core.SupportingInformationData;
import fortscale.utils.logging.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gils
 * Date: 05/08/2015
 */
public class SupportingInformationBasicPopulator implements SupportingInformationPopulator {

    private static Logger logger = Logger.getLogger(SupportingInformationBasicPopulator.class);

    public SupportingInformationData createSupportingInformationData(List<FeatureBucket> featureBuckets, String featureName, String anomalyValue, String aggregationFunction) {

        Map<HistogramKey, Double> histogramKeyObjectMap = new HashMap<>();

        for (FeatureBucket featureBucket : featureBuckets) {
            Feature feature = featureBucket.getAggregatedFeatures().get(featureName);

            if (feature == null) {
                continue;
            }

            Object featureValue = feature.getValue();

            if (featureValue instanceof GenericHistogram) {
                Map<Object, Double> histogramMap = ((GenericHistogram) featureValue).getHistogramMap();

                for (Map.Entry<Object, Double> histogramEntry : histogramMap.entrySet()) {
                    Double currValue = histogramEntry.getValue();

                    HistogramKey histogramKey = new HistogramSingleKey((String) histogramEntry.getKey());

                    Double currHistogramValue = (histogramKeyObjectMap.get(histogramKey) != null ? histogramKeyObjectMap.get(histogramKey) : 0);

                    histogramKeyObjectMap.put(histogramKey, currHistogramValue + currValue);
                }
            } else {
                // TODO is this considered illegal state? for now don't use the value and continue;
                logger.warn("Cannot find histogram data for feature {} in bucket id {}", featureName, featureBucket.getBucketId());
            }
        }

        HistogramSingleKey anomalyValue1 = new HistogramSingleKey(anomalyValue);

        return new SupportingInformationData(histogramKeyObjectMap, anomalyValue1);
    }
}
