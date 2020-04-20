package fortscale.aggregation.creator;

import fortscale.aggregation.creator.metrics.AggregationRecordsCreatorMetricsContainer;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.utils.ConversionUtils;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by barak_schuster on 6/14/17.
 */
public class AggregationRecordsCreatorImpl implements AggregationRecordsCreator {
    private final IAggrFeatureEventFunctionsService aggrFeatureEventFunctionsService;
    private final AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    private final AggregationRecordsCreatorMetricsContainer metricsContainer;

    public AggregationRecordsCreatorImpl(IAggrFeatureEventFunctionsService aggrFeatureEventFunctionsService, AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService, AggregationRecordsCreatorMetricsContainer aggregationRecordsCreatorMetricsContainer) {
        this.aggrFeatureEventFunctionsService = aggrFeatureEventFunctionsService;
        this.aggregatedFeatureEventsConfService = aggregatedFeatureEventsConfService;
        this.metricsContainer = aggregationRecordsCreatorMetricsContainer;
    }

    @Override
    public List<AdeAggregationRecord> createAggregationRecords(List<FeatureBucket> featureBuckets) {
        List<AdeAggregationRecord> aggrRecords = new ArrayList<>();
        if (featureBuckets != null) {
            for (FeatureBucket featureBucket : featureBuckets) {
                Map<String, Feature> aggregatedFeatures = featureBucket.getAggregatedFeatures();
                String featureBucketConfName = featureBucket.getFeatureBucketConfName();
                List<AggregatedFeatureEventConf> aggregatedFeatureEventConfList = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList(featureBucketConfName);
                List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList = Collections.singletonList(aggregatedFeatures);
                Map<String, String> contextFieldNameToValueMap = featureBucket.getContextFieldNameToValueMap();
                Instant startTime = featureBucket.getStartTime();
                Instant endTime = featureBucket.getEndTime();
                for (AggregatedFeatureEventConf aggregatedFeatureEventConf : aggregatedFeatureEventConfList) {
                    Feature feature = aggrFeatureEventFunctionsService.calculateAggrFeature(aggregatedFeatureEventConf, multipleBucketsAggrFeaturesMapList);
                    if(feature == null){
                        continue;
                    }
                    AggrFeatureValue featureValue;
                    Double aggregatedFeatureValue;
                    try {
                        featureValue = (AggrFeatureValue) feature.getValue();
                        aggregatedFeatureValue = ConversionUtils.convertToDouble(featureValue.getValue());
                    } catch (Exception ex) {
                        throw new IllegalArgumentException(String.format("Feature is null or value is null or value is not a AggrFeatureValue object: %s", feature), ex);
                    }
                    if (aggregatedFeatureValue == null) {
                        throw new IllegalArgumentException(String.format("Feature value doesn't contain a 'value' element: %s", featureValue));
                    }
                    String aggregatedFeatureName = aggregatedFeatureEventConf.getName();
                    AggregatedFeatureType aggregatedFeatureType = AggregatedFeatureType.fromCodeRepresentation(aggregatedFeatureEventConf.getType());
                    AdeAggregationRecord adeAggregationRecord = new AdeAggregationRecord(startTime, endTime, aggregatedFeatureName, aggregatedFeatureValue, featureBucketConfName, contextFieldNameToValueMap, aggregatedFeatureType);
                    metricsContainer.updateMetrics(adeAggregationRecord);
                    aggrRecords.add(adeAggregationRecord);
                }
            }
        }
        return aggrRecords;
    }
}
