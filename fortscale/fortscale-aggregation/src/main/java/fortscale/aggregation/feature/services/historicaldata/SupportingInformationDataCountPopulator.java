package fortscale.aggregation.feature.services.historicaldata;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.domain.core.SupportingInformationData;
import fortscale.domain.histogram.HistogramKey;
import fortscale.domain.histogram.HistogramSingleKey;
import fortscale.utils.logging.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Supporting information populator class for count-based aggregations
 *
 * @author gils
 * Date: 05/08/2015
 */

@Component
@Scope("prototype")
public class SupportingInformationDataCountPopulator extends SupportingInformationDataBasePopulator {

    private static Logger logger = Logger.getLogger(SupportingInformationDataCountPopulator.class);

    private static final String FEATURE_HISTOGRAM_SUFFIX = "_histogram";

    public SupportingInformationDataCountPopulator(String contextType,  String dataEntity, String featureName) {
        super(contextType, dataEntity, featureName);
    }

    @Override
    public SupportingInformationData createSupportingInformationData(String contextValue, long evidenceEndTime, int timePeriodInDays, String anomalyValue) {
        List<FeatureBucket> featureBuckets = fetchRelevantFeatureBuckets(contextValue, evidenceEndTime, timePeriodInDays);

        Map<HistogramKey, Double> histogramKeyObjectMap = new HashMap<>();

        for (FeatureBucket featureBucket : featureBuckets) {
            String normalizedFeatureName = getNormalizedFeatureName(featureName);

            Feature feature = featureBucket.getAggregatedFeatures().get(normalizedFeatureName);

            if (feature == null) {
                logger.warn("Could not find feature with name {} in bucket with ID {}", normalizedFeatureName, featureBucket.getBucketId());
                continue;
            }

            Object featureValue = feature.getValue();

            if (featureValue instanceof GenericHistogram) {
                Map<String, Double> histogramMap = ((GenericHistogram) featureValue).getHistogramMap();

                for (Map.Entry<String, Double> histogramEntry : histogramMap.entrySet()) {
                    Double currValue = histogramEntry.getValue();

                    HistogramKey histogramKey = createAnomalyHistogramKey(histogramEntry.getKey());

                    Double currHistogramValue = (histogramKeyObjectMap.get(histogramKey) != null ? histogramKeyObjectMap.get(histogramKey) : 0);

                    histogramKeyObjectMap.put(histogramKey, currHistogramValue + currValue);
                }
            } else {
                // TODO is this considered illegal state? for now don't use the value and continue;
                logger.warn("Cannot find histogram data for feature {} in bucket id {}", normalizedFeatureName, featureBucket.getBucketId());
            }
        }

        HistogramKey anomalyHistogramKey = createAnomalyHistogramKey(anomalyValue);

        return new SupportingInformationData(histogramKeyObjectMap, anomalyHistogramKey);
    }

    @Override
    String getNormalizedFeatureName(String featureName) {
        return featureName + FEATURE_HISTOGRAM_SUFFIX;
    }

    @Override
    HistogramKey createAnomalyHistogramKey(String anomalyValue) {
        return new HistogramSingleKey(anomalyValue);
    }
}
