package fortscale.aggregation.feature.services.historicaldata.populators;

import fortscale.aggregation.feature.services.historicaldata.SupportingInformationException;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationGenericData;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationTimeGranularity;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.EvidenceTimeframe;
import fortscale.domain.historical.data.SupportingInformationKey;
import fortscale.domain.historical.data.SupportingInformationTimestampKey;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of supporting information populator for aggregated events mapped by time
 *
 * @author Amir Keren
 * Date: 15/11/2015
 */
@Component
@Scope("prototype")
public class SupportingInformationCountByTimePopulator extends SupportingInformationBaseHistogramPopulator {

    private static Logger logger = Logger.getLogger(SupportingInformationCountByTimePopulator.class);

    private static final String DOT = ".";
    private static final String CONTEXT_PREFIX = "context";
    private static final String EVENT_COUNTER_BUCKET = "events_counter";


    public SupportingInformationCountByTimePopulator(String contextType, String dataEntity, String featureName) {
        super(contextType, dataEntity, featureName);
    }

    @Override
    public SupportingInformationGenericData<Double> createSupportingInformationData(Evidence evidence,
                                                                                    String contextValue,
                                                                                    long evidenceEndTime,
                                                                                    Integer timePeriodInDays) {
        Map<SupportingInformationKey, Double> histogramMap = createSupportingInformationHistogram(contextValue,
                evidenceEndTime, timePeriodInDays,evidence);
        SupportingInformationGenericData<Double> supportingInformationData;
        if (isAnomalyIndicationRequired(evidence)) {
            SupportingInformationKey anomalySupportingInformationKey = createAnomalyHistogramKey(evidence, featureName);
            validateHistogramDataConsistency(histogramMap, anomalySupportingInformationKey);
            supportingInformationData = new SupportingInformationGenericData<>(histogramMap,
                    anomalySupportingInformationKey);
        }
        else {
            supportingInformationData = new SupportingInformationGenericData<>(histogramMap);
        }
        SupportingInformationTimeGranularity supportingInformationTimeGranularity = determineTimeGranularity(evidence);
        supportingInformationData.setTimeGranularity(supportingInformationTimeGranularity);
        return supportingInformationData;
    }

    protected Map<SupportingInformationKey, Double> createSupportingInformationHistogram(String contextValue,
                                                                                         long evidenceEndTime,
                                                                                         Integer timePeriodInDays,
                                                                                         Evidence evidence) {
//        String normalizedContextType = getNormalizedContextType(contextType);
//        Long startTime = TimeUtils.calculateStartingTime(evidenceEndTime, timePeriodInDays);
//        List featureBucketsByContextAndTimeRange = featureBucketQueryService.
//                getFeatureBucketsByContextAndTimeRange(featureName, normalizedContextType, contextValue, startTime,
//                        evidenceEndTime);
//        if (featureBucketsByContextAndTimeRange.isEmpty()) {
//            throw new SupportingInformationException("Could not find any relevant supporting information creation");
//        }
//        Map<SupportingInformationKey, Double> supportingInformationHistogram = new HashMap<>();
//        for (FeatureBucket featureBucket : featureBucketsByContextAndTimeRange) {
//            FeatureNumericValue numericValue = (FeatureNumericValue)featureBucket.getAggregatedFeatures().
//                    get(EVENT_COUNTER_BUCKET).getValue();
//            Double numOfEvents = numericValue.getValue().doubleValue();
//            SupportingInformationKey supportingInformationKey = new SupportingInformationTimestampKey(Long.
//                    toString(TimestampUtils.convertToMilliSeconds(featureBucket.getStartTime())));
//            supportingInformationHistogram.put(supportingInformationKey, numOfEvents);
//        }
//        return supportingInformationHistogram;
        return MapUtils.EMPTY_MAP;
    }

    protected SupportingInformationTimeGranularity determineTimeGranularity(Evidence evidence) {
        EvidenceTimeframe evidenceTimeframe = evidence.getTimeframe();
        if (evidenceTimeframe != null) {
            if (evidenceTimeframe == EvidenceTimeframe.Hourly) {
                return SupportingInformationTimeGranularity.Hourly;
            }
            else if (evidenceTimeframe == EvidenceTimeframe.Daily) {
                return SupportingInformationTimeGranularity.Daily;
            }
            else {
                logger.error("Could not determine supporting information time granularity for evidence ID {} with timeframe {}", evidence.getId(), evidence.getTimeframe());
            }
        }
        else {
            logger.error("Could not determine supporting information time granularity for evidence ID {} : evidence timeframe field is not set", evidence.getId());
        }
        return null;
    }

    @Override
    protected String getNormalizedContextType(String contextType) {
        return removeContextTypePrefix(contextType);
    }

    @Override
    String getNormalizedFeatureName(String featureName) {
        return null;
    }

    private String removeContextTypePrefix(String contextType) {
        if (contextType.startsWith(CONTEXT_PREFIX + DOT)) {
            int lengthToTrim = (CONTEXT_PREFIX + DOT).length(); // e.g. context.normalized_username
            return contextType.substring(lengthToTrim);
        } else {
            return contextType;
        }
    }

    @Override
    SupportingInformationKey createAnomalyHistogramKey(Evidence evidence, String featureName) {
        return new SupportingInformationTimestampKey(String.valueOf(TimestampUtils.convertToMilliSeconds(evidence.
                getStartDate())));
    }

    @Override
    protected boolean isAnomalyIndicationRequired(Evidence evidence) {
        return true;
    }

//    public void setFeatureBucketQueryService(FeatureBucketQueryService featureBucketQueryService) {
//        this.featureBucketQueryService = featureBucketQueryService;
//    }

}