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
 * @author gils
 * Date: 16/08/2015
 */
@Component
@Scope("prototype")
public class SupportingInformationDistinctEventsByTimePopulator extends SupportingInformationBaseHistogramPopulator {

    private static Logger logger = Logger.getLogger(SupportingInformationDistinctEventsByTimePopulator.class);

    private static final String DOT = ".";
    private static final String CONTEXT_PREFIX = "context";
    private static final String FEATURE_HISTOGRAM_SUFFIX = "histogram";


    public SupportingInformationDistinctEventsByTimePopulator(String contextType, String dataEntity, String featureName) {
        super(contextType, dataEntity, featureName);
    }

    @Override
    public SupportingInformationGenericData<Double> createSupportingInformationData(Evidence evidence, String contextValue, long evidenceEndTime, Integer timePeriodInDays) {

        Map<SupportingInformationKey, Double> histogramMap = createSupportingInformationHistogram(contextValue, evidenceEndTime, timePeriodInDays, evidence);

        SupportingInformationGenericData<Double> supportingInformationData;

        if (isAnomalyIndicationRequired(evidence)) {
            SupportingInformationKey anomalySupportingInformationKey = createAnomalyHistogramKey(evidence, featureName);

            validateHistogramDataConsistency(histogramMap, anomalySupportingInformationKey);

            supportingInformationData = new SupportingInformationGenericData<>(histogramMap, anomalySupportingInformationKey);
        }
        else {
            supportingInformationData = new SupportingInformationGenericData<>(histogramMap);
        }


        SupportingInformationTimeGranularity supportingInformationTimeGranularity = determineTimeGranularity(evidence);

        supportingInformationData.setTimeGranularity(supportingInformationTimeGranularity);

        return supportingInformationData;
    }

    protected Map<SupportingInformationKey, Double> createSupportingInformationHistogram(String contextValue, long evidenceEndTime, Integer timePeriodInDays,Evidence evidence) {

//        String normalizedContextType = getNormalizedContextType(contextType);
//
//        Long startTime = TimeUtils.calculateStartingTime(evidenceEndTime, timePeriodInDays);
//        List<AggrEvent> aggregatedEventsByContextIdAndTimeRange = aggregatedEventQueryService.getAggregatedEventsByContextIdAndTimeRange(featureName, normalizedContextType, contextValue, startTime, evidenceEndTime);
//
//        if (aggregatedEventsByContextIdAndTimeRange.isEmpty()) {
//            throw new SupportingInformationException("Could not find any relevant scored aggregated events for supporting information creation");
//        }
//
//        Map<SupportingInformationKey, Double> supportingInformationHistogram = new HashMap<>();
//
//        for (AggrEvent aggrEvent : aggregatedEventsByContextIdAndTimeRange) {
//            Double numOfEvents = aggrEvent.getAggregatedFeatureValue();
//
//            SupportingInformationKey supportingInformationKey = new SupportingInformationTimestampKey(Long.toString(TimestampUtils.convertToMilliSeconds(aggrEvent.getStartTimeUnix())));
//
//            supportingInformationHistogram.put(supportingInformationKey, numOfEvents);
//        }
//
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
    protected String getNormalizedFeatureName(String featureName) {
        return String.format("%s_%s", featureName, FEATURE_HISTOGRAM_SUFFIX);
    }

    @Override
    protected String getNormalizedContextType(String contextType) {
        return removeContextTypePrefix(contextType);
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
        // TODO need to check if this correct
        return new SupportingInformationTimestampKey(String.valueOf(TimestampUtils.convertToMilliSeconds(evidence.getStartDate())));
    }

    @Override
    protected boolean isAnomalyIndicationRequired(Evidence evidence) {
        return true;
    }


}
