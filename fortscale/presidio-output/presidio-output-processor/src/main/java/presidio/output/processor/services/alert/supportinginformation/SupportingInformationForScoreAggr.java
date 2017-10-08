package presidio.output.processor.services.alert.supportinginformation;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.output.domain.records.alerts.*;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.processor.config.IndicatorConfig;
import presidio.output.processor.config.SupportingInformationConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.HistoricalDataPopulator;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.HistoricalDataPopulatorFactory;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Supporting information (indicators, events and historical data) for SCORE_AGGREGATION events (AKA 'P')
 */
public class SupportingInformationForScoreAggr implements SupportingInformationGenerator {

    public static final String START_INSTANT = "startInstant";

    @Value("${output.aggregated.feature.historical.period.days: #{30}}")
    private int historicalPeriodInDays;


    private AdeManagerSdk adeManagerSdk;

    private SupportingInformationConfig config;

    private EventPersistencyService eventPersistencyService;

    private HistoricalDataPopulatorFactory historicalDataPopulatorFactory;


    public SupportingInformationForScoreAggr(SupportingInformationConfig supportingInformationConfig, AdeManagerSdk adeManagerSdk, EventPersistencyService eventPersistencyService, HistoricalDataPopulatorFactory historicalDataPopulatorFactory) {
        this.config = supportingInformationConfig;
        this.adeManagerSdk = adeManagerSdk;
        this.eventPersistencyService = eventPersistencyService;
        this.historicalDataPopulatorFactory = historicalDataPopulatorFactory;
    }


    @Override
    public List<Indicator> generateIndicators(AdeAggregationRecord adeAggregationRecord, Alert alert) {
        List<Indicator> indicators = new ArrayList<Indicator>();
        IndicatorConfig indicatorConfig = config.getIndicatorConfig(adeAggregationRecord.getFeatureName());
        Pair<String, String> contextFieldAndValue = Pair.of(CommonStrings.CONTEXT_USERID, adeAggregationRecord.getContext().get(CommonStrings.CONTEXT_USERID));
        TimeRange timeRange = new TimeRange(adeAggregationRecord.getStartInstant(), adeAggregationRecord.getEndInstant());
        List<String> distinctFeatureValues = getDistinctFeatureValues(adeAggregationRecord, indicatorConfig, contextFieldAndValue, timeRange);
        for (String featureValue :distinctFeatureValues) {

            Indicator indicator = new Indicator(alert.getId());
            indicator.setName(indicatorConfig.getName());
            indicator.setStartDate(adeAggregationRecord.getStartInstant().getLong(ChronoField.INSTANT_SECONDS));
            indicator.setEndDate(adeAggregationRecord.getEndInstant().getLong(ChronoField.INSTANT_SECONDS));
            indicator.setAnomalyValue(featureValue);
            indicator.setSchema(indicatorConfig.getSchema());
            indicator.setType(AlertEnums.IndicatorTypes.valueOf(indicatorConfig.getType()));
            indicators.add(indicator);
        }
        return indicators;
    }


    @Override
    public List<IndicatorEvent> generateEvents(AdeAggregationRecord adeAggregationRecord, Indicator indicator) throws Exception{

        List<IndicatorEvent> events = new ArrayList<IndicatorEvent>();

        // get raw events from the output (output_ collections)
        IndicatorConfig indicatorConfig = config.getIndicatorConfig(adeAggregationRecord.getFeatureName());
        String userId = adeAggregationRecord.getContext().get(CommonStrings.CONTEXT_USERID);
        TimeRange timeRange = new TimeRange(adeAggregationRecord.getStartInstant(), adeAggregationRecord.getEndInstant());

        Map<String, Object> features = new HashMap<String, Object>();
        String anomalyField = indicatorConfig.getAnomalyDescriptior().getAnomalyField();
        String anomalyValue = getAnomalyValue(indicator, indicatorConfig);
        if (StringUtils.isNoneEmpty(anomalyValue,indicatorConfig.getAnomalyDescriptior().getAnomalyField())) {
            features.put(anomalyField, anomalyValue);
        }
        List<? extends EnrichedEvent> rawEvents = eventPersistencyService.findEvents(indicatorConfig.getSchema(), userId, timeRange, features);

        if (CollectionUtils.isNotEmpty(rawEvents)) {

            // get scored raw events from ade
            List<String> eventsIds = rawEvents.stream().map(e -> e.getEventId()).collect(Collectors.toList());
            List<AdeScoredEnrichedRecord> adeScoredEnrichedRecords = adeManagerSdk.findScoredEnrichedRecords(eventsIds, indicatorConfig.getAdeEventType(), 0d);

            // build Event for each raw event
            for (EnrichedEvent rawEvent : rawEvents) {
                Map<String, Object> rawEventFeatures = new ObjectMapper().convertValue(rawEvent, Map.class);

                IndicatorEvent event = new IndicatorEvent();
                event.setFeatures(rawEventFeatures);
                event.setIndicatorId(indicator.getId());
                event.setEventTime(rawEvent.getEventDate().getLong(ChronoField.INSTANT_SECONDS));
                event.setSchema(indicatorConfig.getSchema());

                if (CollectionUtils.isNotEmpty(adeScoredEnrichedRecords)) {
                    scoreEvent(indicator, anomalyField, adeScoredEnrichedRecords, rawEvent, event);

                }
                events.add(event);

            }
        }
        return events;
    }


    @Override
    public HistoricalData generateHistoricalData(AdeAggregationRecord adeAggregationRecord, Indicator indicator) {

        IndicatorConfig indicatorConfig = config.getIndicatorConfig(adeAggregationRecord.getFeatureName());
        HistoricalDataPopulator historicalDataPopulator = historicalDataPopulatorFactory.createHistoricalDataPopulation(CommonStrings.CONTEXT_USERID,
                                                                                         indicatorConfig.getAnomalyDescriptior().getAnomalyField(),
                                                                                         indicatorConfig.getHistoricalData().getType());

        Instant startInstant = adeAggregationRecord.getStartInstant().minus(historicalPeriodInDays, ChronoUnit.DAYS);
        TimeRange timeRange = new TimeRange(startInstant, adeAggregationRecord.getEndInstant());
        String contextField = CommonStrings.CONTEXT_USERID;
        String contextValue =  adeAggregationRecord.getContext().get(CommonStrings.CONTEXT_USERID);
        Schema schema = indicatorConfig.getSchema();
        String featureName = indicatorConfig.getHistoricalData().getFeatureName();
        String anomalyValue = getAnomalyValue(indicator, indicatorConfig);

        HistoricalData historicalData = historicalDataPopulator.createHistoricalData(timeRange, contextField, contextValue, schema, featureName, anomalyValue, indicatorConfig.getHistoricalData());
        historicalData.setIndicatorId(indicator.getId());
        historicalData.setSchema(indicator.getSchema());
        return historicalData;
    }

    @Override
    public String getType() {
        return AggregatedFeatureType.SCORE_AGGREGATION.name();
    }




    private String getAnomalyValue(Indicator indicator, IndicatorConfig indicatorConfig) {
        // static indicator -> the event value is static and therefore taken from the configuration (e.g: admin_changed_his_own_password => PASSWORD_CHANGED)
        // dynamic indicators -> the event value is taken from the indicator itself (e.g: abnormal_file_action_operation_type => FILE_OPENED, FILE_MOVED ...)
        return StringUtils.isNotEmpty(indicator.getAnomalyValue())? indicator.getAnomalyValue(): indicatorConfig.getAnomalyDescriptior().getAnomalyValue();
    }

    private List<String> getDistinctFeatureValues(AdeAggregationRecord adeAggregationRecord, IndicatorConfig indicatorConfig, Pair<String, String> contextFieldAndValue, TimeRange timeRange) {
        List<String> distinctFeatureValues = new ArrayList<String>();

        // static indicator -> one empty value
        if (AlertEnums.IndicatorTypes.STATIC_INDICATOR.name().equals(indicatorConfig.getType())) {
            distinctFeatureValues.add(StringUtils.EMPTY);
            return distinctFeatureValues;
        }

        // start instance case -> WORKAROUND - start_instant is not part of the ADE context
        if (START_INSTANT.equals(indicatorConfig.getAnomalyDescriptior().getAnomalyField())) {
            distinctFeatureValues.add(adeAggregationRecord.getStartInstant().toString());
        } else {
            // get distinct values of all the scored events from ADE
            distinctFeatureValues.addAll(adeManagerSdk.findScoredEnrichedRecordsDistinctFeatureValues(indicatorConfig.getAdeEventType(), contextFieldAndValue, timeRange, indicatorConfig.getAnomalyDescriptior().getAnomalyField(), 0.0));
        }
        return distinctFeatureValues;
    }

    private void scoreEvent(Indicator indicator, String anomalyField, List<AdeScoredEnrichedRecord> adeScoredEnrichedRecords, EnrichedEvent rawEvent, IndicatorEvent event) {
        // find scored event with the event id
        Optional<AdeScoredEnrichedRecord> scoredEnrichedRecord = adeScoredEnrichedRecords.stream()
                .filter(e -> e.getContext().getEventId().equals(rawEvent.getEventId()))
                .findFirst();
        if (scoredEnrichedRecord.isPresent()) {
            Map<String, Double> scores = new HashMap<String, Double>();
            scores.put(anomalyField, scoredEnrichedRecord.get().getScore());
            event.setScores(scores);
            // TODO: find better way to do it
            indicator.setScore(scoredEnrichedRecord.get().getScore());
        }
    }

}
