package fortscale.aggregation.feature.services.historicaldata;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.domain.core.Evidence;
import fortscale.domain.histogram.HistogramKey;
import fortscale.domain.histogram.HistogramSingleKey;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gils
 *         Date: 16/08/2015
 */
@Component
@Scope("prototype")
public class SupportingInformationAggrEventPopulator extends SupportingInformationBasePopulator {

    private static Logger logger = Logger.getLogger(SupportingInformationAggrEventPopulator.class);

    private static final String CONTEXT_PREFIX = "context";
    private static final String DOT_DELIMITER = "#dot#";
    private static final String FEATURE_HISTOGRAM_SUFFIX = "histogram";

    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    public SupportingInformationAggrEventPopulator(String contextType, String dataEntity, String featureName) {
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

                if (histogramMap.isEmpty() || histogramMap.size() > 1) {
                    logger.warn("Histogram map contains {} entries, expecting exactly one", histogramMap.size());
                }

                for (Map.Entry<String, Double> histogramEntry : histogramMap.entrySet()) {
                    String numOfEvents = histogramEntry.getKey();

                    HistogramKey histogramKey = new HistogramSingleKey(Long.toString(TimestampUtils.convertToMilliSeconds(featureBucket.getStartTime())));

                    histogramKeyObjectMap.put(histogramKey, Double.parseDouble(numOfEvents));
                }
            } else {
                // TODO is this considered illegal state? for now don't use the value and continue;
                logger.warn("Cannot find histogram data for feature {} in bucket id {}", normalizedFeatureName, featureBucket.getBucketId());
            }
        }
        return histogramKeyObjectMap;
    }

    @Override
    String getNormalizedFeatureName(String featureName) {
        return String.format("%s_%s", featureName, FEATURE_HISTOGRAM_SUFFIX);
    }

    @Override
    String getNormalizedContextType(String contextType) {
        return CONTEXT_PREFIX + DOT_DELIMITER + contextType;
    }

    protected String getBucketConfigurationName(String contextType, String dataEntity) {
        return String.format("%s_%s_%s_%s", contextType, dataEntity, BUCKET_CONF_DAILY_STRATEGY_SUFFIX, featureName);
    }

    @Override
    HistogramKey createAnomalyHistogramKey(Evidence evidence, String featureName) {
        // TODO need to check
        return new HistogramSingleKey(String.valueOf(TimestampUtils.convertToMilliSeconds(evidence.getStartDate())));
    }
}

