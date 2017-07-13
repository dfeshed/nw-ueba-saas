package fortscale.aggregation.creator;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.utils.ConversionUtils;
import presidio.ade.domain.record.aggregated.AdeAggrRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by barak_schuster on 6/14/17.
 */
public class AggregationsCreatorImpl implements AggregationsCreator {
    private final IAggrFeatureEventFunctionsService aggrFeatureEventFunctionsService;
    private final AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    public AggregationsCreatorImpl(IAggrFeatureEventFunctionsService aggrFeatureEventFunctionsService, AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService) {
        this.aggrFeatureEventFunctionsService = aggrFeatureEventFunctionsService;
        this.aggregatedFeatureEventsConfService = aggregatedFeatureEventsConfService;
    }

    @Override
    public List<AdeAggrRecord> createAggregations(List<FeatureBucket> featureBuckets) {
        List<AdeAggrRecord> aggrRecords = new ArrayList<>();
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
                    Map<String, Object> aggregatedFeatureInfo = featureValue.getAdditionalInformationMap();
                    String aggregatedFeatureName = aggregatedFeatureEventConf.getName();

                    AggregatedFeatureType aggregatedFeatureType = AggregatedFeatureType.fromConfStringType(aggregatedFeatureEventConf.getType());
                    AdeAggrRecord adeAggrRecord = new AdeAggrRecord(startTime, endTime, aggregatedFeatureInfo, aggregatedFeatureName, aggregatedFeatureValue, featureBucketConfName, contextFieldNameToValueMap, aggregatedFeatureType);
                    aggrRecords.add(adeAggrRecord);
                }
            }
        }
        return aggrRecords;
    }
}
