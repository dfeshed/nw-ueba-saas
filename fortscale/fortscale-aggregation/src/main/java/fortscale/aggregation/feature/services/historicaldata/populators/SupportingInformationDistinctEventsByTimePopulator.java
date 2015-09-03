package fortscale.aggregation.feature.services.historicaldata.populators;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationHistogramData;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationTimeGranularity;
import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.domain.core.Evidence;
import fortscale.domain.historical.data.SupportingInformationKey;
import fortscale.domain.historical.data.SupportingInformationSingleKey;
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
 * Date: 16/08/2015
 */
@Component
@Scope("prototype")
public class SupportingInformationDistinctEventsByTimePopulator extends SupportingInformationHistogramPopulator {

    private static Logger logger = Logger.getLogger(SupportingInformationDistinctEventsByTimePopulator.class);

    private static final String DOT = ".";
    private static final String CONTEXT_PREFIX = "context";
    private static final String ESCAPED_DOT_DELIMITER = "#dot#";
    private static final String FEATURE_HISTOGRAM_SUFFIX = "histogram";

    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    public SupportingInformationDistinctEventsByTimePopulator(String contextType, String dataEntity, String featureName) {
        super(contextType, dataEntity, featureName);
    }

    /*
     * Use same logic as in the base populator and set the time granularity
     */
    @Override
    public SupportingInformationHistogramData createSupportingInformationData(Evidence evidence, String contextValue, long evidenceEndTime, int timePeriodInDays) {

        SupportingInformationHistogramData supportingInformationHistogramData = super.createSupportingInformationData(evidence, contextValue, evidenceEndTime, timePeriodInDays);

        SupportingInformationTimeGranularity timeGranularity = determineTimeGranularity();

        supportingInformationHistogramData.setTimeGranularity(timeGranularity);

        return supportingInformationHistogramData;
    }

    @Override
    protected Map<SupportingInformationKey, Double> createSupportingInformationHistogram(List<FeatureBucket> featureBuckets) {
        Map<SupportingInformationKey, Double> histogramKeyObjectMap = new HashMap<>();

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

                // until FV-8398 is fixed..
                if (histogramMap.isEmpty() || histogramMap.size() > 1) {
                    logger.warn("Histogram map contains {} entries, expecting exactly one", histogramMap.size());
                }

                for (Map.Entry<String, Double> histogramEntry : histogramMap.entrySet()) {
                    String numOfEvents = histogramEntry.getKey();

                    // workaround for bug FV-8398
                    if (Integer.parseInt(numOfEvents) == 0) {
                        logger.warn("Ignoring zero value of histogram entry (" + histogramEntry + ")");
                        continue;
                    }

                    SupportingInformationKey supportingInformationKey = new SupportingInformationSingleKey(Long.toString(TimestampUtils.convertToMilliSeconds(featureBucket.getStartTime())));

                    histogramKeyObjectMap.put(supportingInformationKey, new Double(numOfEvents));
                }
            } else {
                logger.error("Cannot find histogram data for feature {} in bucket id {}", normalizedFeatureName, featureBucket.getBucketId());
            }
        }
        return histogramKeyObjectMap;
    }

    protected SupportingInformationTimeGranularity determineTimeGranularity() {
        AggregatedFeatureEventConf aggregatedFeatureEventConf = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConf(featureName);

        String strategyName = aggregatedFeatureEventConf.getOutputBucketStrategy();

        // TODO need to use the feature bucket strategy service, currently it's in the streaming project
        if (FIXED_DURATION_DAILY_STRATEGY.equals(strategyName)) {
            return SupportingInformationTimeGranularity.Daily;
        }
        else if (FIXED_DURATION_HOURLY_STRATEGY.equals(strategyName)) {
            return SupportingInformationTimeGranularity.Hourly;
        }

        logger.warn("Could not find strategy with name {}", strategyName);

        return null;
    }

    @Override
    String getNormalizedFeatureName(String featureName) {
        return String.format("%s_%s", featureName, FEATURE_HISTOGRAM_SUFFIX);
    }

    @Override
    protected String getNormalizedContextType(String contextType) {
        return contextType.replace(DOT, ESCAPED_DOT_DELIMITER); // must escape dot character in mongo fields
    }

    protected String getBucketConfigurationName(String contextType, String dataEntity) {
        if (featureName.endsWith(BUCKET_CONF_HOURLY_STRATEGY_SUFFIX)) {
            return String.format("%s_%s_%s_%s", removeContextTypePrefix(contextType), dataEntity, BUCKET_CONF_HOURLY_STRATEGY_SUFFIX, featureName);
        }
        else { // default case is daily
            return String.format("%s_%s_%s_%s", removeContextTypePrefix(contextType), dataEntity, BUCKET_CONF_DAILY_STRATEGY_SUFFIX, featureName);
        }
    }

    private String removeContextTypePrefix(String contextType) {
        int lengthToTrim = (CONTEXT_PREFIX + DOT).length(); // e.g. context.normalized_username
        return contextType.substring(lengthToTrim);
    }

    @Override
    SupportingInformationKey createAnomalyHistogramKey(Evidence evidence, String featureName) {
        // TODO need to check if this correct
        return new SupportingInformationSingleKey(String.valueOf(TimestampUtils.convertToMilliSeconds(evidence.getStartDate())));
    }

    @Override
    protected boolean isAnomalyIndicationRequired(Evidence evidence) {
        return true;
    }
}

