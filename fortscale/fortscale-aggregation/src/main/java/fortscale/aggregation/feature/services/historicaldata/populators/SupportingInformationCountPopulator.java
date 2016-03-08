package fortscale.aggregation.feature.services.historicaldata.populators;

import fortscale.common.event.NotificationAnomalyType;
import fortscale.common.feature.Feature;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationException;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.core.Evidence;
import fortscale.domain.historical.data.SupportingInformationKey;
import fortscale.domain.historical.data.SupportingInformationSingleKey;
import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Supporting information populator class for count-based aggregations
 *
 * @author gils
 *         Date: 05/08/2015
 */

@Component
@Scope("prototype")
public class SupportingInformationCountPopulator extends SupportingInformationHistogramBySingleEventsPopulator {

	private static Logger logger = Logger.getLogger(SupportingInformationCountPopulator.class);

	private static final String FEATURE_HISTOGRAM_SUFFIX = "histogram";

	public SupportingInformationCountPopulator(String contextType, String dataEntity, String featureName) {
		super(contextType, dataEntity, featureName);
	}

	@Override protected Map<SupportingInformationKey, Double> createSupportingInformationHistogram(String contextValue,
			long endTime, Integer timePeriodInDays) {
		List<FeatureBucket> featureBuckets = fetchRelevantFeatureBuckets(contextValue, endTime, timePeriodInDays);

        Map<SupportingInformationKey, Double> lastDayMap = createLastDayBucket(getNormalizedContextType(contextType), contextValue, endTime, dataEntity);

        if ((CollectionUtils.isEmpty(featureBuckets)) && (MapUtils.isEmpty(lastDayMap))) {
            throw new SupportingInformationException("Could not find any relevant bucket for histogram creation");
        }

        return createSupportingInformationHistogram(featureBuckets, lastDayMap);

    }

	protected Map<SupportingInformationKey, Double> createSupportingInformationHistogram(
			List<FeatureBucket> featureBuckets, Map<SupportingInformationKey, Double> lastDayMap) {
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

				for (Map.Entry<String, Double> histogramEntry : histogramMap.entrySet()) {
					SupportingInformationKey supportingInformationKey = new SupportingInformationSingleKey(histogramEntry.getKey());
					updateHistoricalDataEntry(histogramKeyObjectMap, supportingInformationKey, histogramEntry.getValue());
				}
			} else {
				logger.error("Cannot find histogram data for feature {} in bucket id {}", normalizedFeatureName, featureBucket.getBucketId());
			}
		}

		//Merge last days map into histogramKeyObjectMap
		if (lastDayMap != null) {
			for (Map.Entry<SupportingInformationKey, Double> lastDayEntry : lastDayMap.entrySet()) {
				updateHistoricalDataEntry(histogramKeyObjectMap, lastDayEntry.getKey(), lastDayEntry.getValue());
			}
		}

		return histogramKeyObjectMap;
	}

	/**
	 * Check if key exists.
	 * If key exists, add current value to old value.
	 * If not - add new entry with the current value
	 *
	 * @param histogramKeyObjectMap
	 * @param supportingInformationKey
	 * @param currValue
	 */
	private void updateHistoricalDataEntry(Map<SupportingInformationKey, Double> histogramKeyObjectMap,
			SupportingInformationKey supportingInformationKey, Double currValue) {

		Double currHistogramValue = histogramKeyObjectMap.get(supportingInformationKey);
		if (currHistogramValue == null) {
			currHistogramValue = new Double(0);
		}

		histogramKeyObjectMap.put(supportingInformationKey, currHistogramValue + currValue);
	}

	protected Map<SupportingInformationKey, Double> buildLastDayMap(List<Map<String, Object>> queryList) {
		Map<SupportingInformationKey, Double> lastDayMap = new HashMap<>();
		for (Map<String, Object> bucketMap : queryList) {
			lastDayMap.put(new SupportingInformationSingleKey((String) bucketMap.get(featureName)), ((Long) bucketMap.get("countField")).doubleValue());
		}
		return lastDayMap;
	}

	@Override protected String getNormalizedContextType(String contextType) {
		return contextType;
	}

	@Override String getNormalizedFeatureName(String featureName) {
		return String.format("%s_%s", featureName, FEATURE_HISTOGRAM_SUFFIX);
	}

	@Override SupportingInformationKey createAnomalyHistogramKey(Evidence evidence, String featureName) {
		String anomalyValue = extractAnomalyValue(evidence, featureName);

		return new SupportingInformationSingleKey(anomalyValue);
	}

	@Override protected boolean isAnomalyIndicationRequired(Evidence evidence) {
		return !NotificationAnomalyType.VPN_GEO_HOPPING.getType().equals(evidence.getAnomalyTypeFieldName());

	}
}
