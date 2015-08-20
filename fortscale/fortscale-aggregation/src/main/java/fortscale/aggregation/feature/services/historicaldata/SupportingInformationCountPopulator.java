package fortscale.aggregation.feature.services.historicaldata;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.domain.core.Evidence;
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
public class SupportingInformationCountPopulator extends SupportingInformationBasePopulator {

    private static Logger logger = Logger.getLogger(SupportingInformationCountPopulator.class);

    private static final String FEATURE_HISTOGRAM_SUFFIX = "histogram";

    public SupportingInformationCountPopulator(String contextType, String dataEntity, String featureName) {
        super(contextType, dataEntity, featureName);
    }

    @Override
    protected Map<HistogramKey, Double> createSupportingInformationHistogram(List<FeatureBucket> featureBuckets) {
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

                    HistogramKey histogramKey = new HistogramSingleKey(histogramEntry.getKey());

                    Double currHistogramValue = (histogramKeyObjectMap.get(histogramKey) != null ? histogramKeyObjectMap.get(histogramKey) : 0);

                    histogramKeyObjectMap.put(histogramKey, currHistogramValue + currValue);
                }
            } else {
                logger.error("Cannot find histogram data for feature {} in bucket id {}", normalizedFeatureName, featureBucket.getBucketId());
            }
        }
        return histogramKeyObjectMap;
    }

    @Override
    protected String getNormalizedContextType(String contextType) {
        return contextType;
    }

    @Override
    String getNormalizedFeatureName(String featureName) {
        return String.format("%s_%s", featureName, FEATURE_HISTOGRAM_SUFFIX);
    }

    @Override
    HistogramKey createAnomalyHistogramKey(Evidence evidence, String featureName) {
        String anomalyValue = extractAnomalyValue(evidence, featureName);

        return new HistogramSingleKey(anomalyValue);
    }
}
