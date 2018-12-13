package presidio.output.processor.services.alert.supportinginformation;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.time.TimeRange;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.SmartAggregationRecord;
import presidio.ade.sdk.aggregation_records.splitter.ScoreAggregationRecordContributors;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.output.domain.records.alerts.*;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.ScoredEnrichedEvent;
import presidio.output.domain.services.event.ScoredEventService;
import presidio.output.processor.config.IndicatorConfig;
import presidio.output.processor.config.SupportingInformationConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.HistoricalDataPopulator;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.HistoricalDataPopulatorFactory;
import presidio.output.processor.services.alert.supportinginformation.transformer.SupportingInformationTransformer;
import presidio.output.processor.services.alert.supportinginformation.transformer.SupportingInformationTransformerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Supporting information (indicators, events and historical data) for SCORE_AGGREGATION events (AKA 'P')
 */
public class SupportingInformationForScoreAggr implements SupportingInformationGenerator {
    public static final String CONTEXT_PREFIX = "context.";
    private final ObjectMapper objectMapper;

    @Autowired
    private SupportingInformationTransformerFactory transformerFactory;

    @Value("${output.aggregated.feature.historical.period.days: #{30}}")
    private int historicalPeriodInDays;

    @Value("${output.activity.time.historical.period.days: #{90}}")
    private int historicalActivityTimePeriodInDays;

    private SupportingInformationConfig config;

    private HistoricalDataPopulatorFactory historicalDataPopulatorFactory;

    private ScoredEventService scoredEventService;

    private SupportingInformationUtils supportingInfoUtils;

    private AdeManagerSdk adeManagerSdk;


    public SupportingInformationForScoreAggr(SupportingInformationConfig supportingInformationConfig, HistoricalDataPopulatorFactory historicalDataPopulatorFactory, ScoredEventService scoredEventService, SupportingInformationUtils supportingInfoUtils, AdeManagerSdk adeManagerSdk) {
        this.config = supportingInformationConfig;
        this.historicalDataPopulatorFactory = historicalDataPopulatorFactory;
        this.scoredEventService = scoredEventService;
        this.objectMapper = ObjectMapperProvider.getInstance().getNoModulesObjectMapper();
        this.supportingInfoUtils = supportingInfoUtils;
        this.adeManagerSdk = adeManagerSdk;
    }


    @Override
    public List<Indicator> generateIndicators(SmartAggregationRecord smartAggregationRecord, Alert alert, int eventsLimit, int eventsPageSize) {
        AdeAggregationRecord adeAggregationRecord = smartAggregationRecord.getAggregationRecord();
        List<Indicator> indicators = new ArrayList<>();
        IndicatorConfig indicatorConfig = config.getIndicatorConfig(adeAggregationRecord.getFeatureName());

        List<String> splitFieldNames = new ArrayList<>(indicatorConfig.getSplitFields());
        splitFieldNames.replaceAll(field -> translateOutputToAdeName(field));

        ScoreAggregationRecordContributors scoreAggregationRecordContributors = adeManagerSdk.splitScoreAggregationRecordToContributors(adeAggregationRecord, splitFieldNames);

        for (ScoreAggregationRecordContributors.Contributor scoreAggregationRecordContributor : scoreAggregationRecordContributors.getContributors()) {

            Indicator indicator = new Indicator(alert.getId());
            indicator.setName(indicatorConfig.getName());
//            indicator.setStartDate(Date.from(scoreAggregationRecordContributor.getTimeRange().getStart()));
//            indicator.setEndDate(Date.from(scoreAggregationRecordContributor.getTimeRange().getEnd()));
            indicator.setStartDate(Date.from(adeAggregationRecord.getStartInstant()));
            indicator.setEndDate(Date.from(adeAggregationRecord.getEndInstant()));
            indicator.setSchema(indicatorConfig.getSchema());
            indicator.setType(AlertEnums.IndicatorTypes.valueOf(indicatorConfig.getType()));
            indicator.setScoreContribution(scoreAggregationRecordContributor.getContributionRatio()*smartAggregationRecord.getContribution());
            Map<String, String> adeContexts = scoreAggregationRecordContributor.getContextFieldNameToValueMap().getFeatureNameToValue();
            // add split fields
            Map<String, String> contexts = adeContexts.entrySet().stream()
                                                .filter(entry -> entry.getValue() != null)
                                                .collect(Collectors.toMap(entry -> translateAdeNameToOutput(entry.getKey()),
                                                                          entry -> translateAdeValueToOutput(entry.getKey(), entry.getValue())));
            // add indicator context
            contexts.put(CommonStrings.CONTEXT_USERID, adeAggregationRecord.getContext().get(CommonStrings.CONTEXT_USERID));
            // add model context

            String featureValue  = AlertEnums.IndicatorTypes.STATIC_INDICATOR.name().equals(indicatorConfig.getType())?
                    StringUtils.EMPTY:
                    contexts.get(indicatorConfig.getAnomalyDescriptior().getAnomalyField());
            indicator.setAnomalyValue(featureValue);
            indicator.setContexts(contexts);
            indicators.add(indicator);
        }
        return indicators;
    }

    @Override
    public List<IndicatorEvent> generateEvents(AdeAggregationRecord adeAggregationRecord, Indicator indicator, int eventsLimit, int eventsPageSize) throws Exception {

        List<IndicatorEvent> events = new ArrayList<>();

        // get raw events from the output (output_ collections)
        IndicatorConfig indicatorConfig = config.getIndicatorConfig(adeAggregationRecord.getFeatureName());
        String userId = adeAggregationRecord.getContext().get(CommonStrings.CONTEXT_USERID);
        TimeRange timeRange = new TimeRange(adeAggregationRecord.getStartInstant(), adeAggregationRecord.getEndInstant());

        List<Pair<String, Object>> features = supportingInfoUtils.buildAnomalyFeatures(indicatorConfig,indicator.getContexts());

        List<ScoredEnrichedEvent> rawEvents = scoredEventService.findEventsAndScores(indicatorConfig.getSchema(), indicatorConfig.getAdeEventType(), userId, timeRange, features, eventsLimit, eventsPageSize);

        if (CollectionUtils.isNotEmpty(rawEvents)) {

            // build Event for each raw event
            for (ScoredEnrichedEvent rawEvent : rawEvents) {
                Map<String, Object> rawEventFeatures = objectMapper.convertValue(rawEvent.getEnrichedEvent(), Map.class);

                IndicatorEvent event = new IndicatorEvent();
                event.setFeatures(rawEventFeatures);
                event.setIndicatorId(indicator.getId());
                event.setEventTime(Date.from(rawEvent.getEnrichedEvent().getEventDate()));
                event.setSchema(indicatorConfig.getSchema());
                if (rawEvent.getScore() > 0) {
                    Map<String, Double> scores = new HashMap<>();
                    scores.put(indicatorConfig.getAnomalyDescriptior().getAnomalyField(), rawEvent.getScore());
                    event.setScores(scores);
                    indicator.setScore(rawEvent.getScore());
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

        Instant startInstant = EnrichedEvent.EVENT_DATE_FIELD_NAME.equals(indicatorConfig.getAnomalyDescriptior().getAnomalyField()) ?
                adeAggregationRecord.getStartInstant().minus(historicalActivityTimePeriodInDays, ChronoUnit.DAYS) :
                adeAggregationRecord.getStartInstant().minus(historicalPeriodInDays, ChronoUnit.DAYS);
        TimeRange timeRange = new TimeRange(startInstant, adeAggregationRecord.getEndInstant());

        Schema schema = indicatorConfig.getSchema();
        String featureName = indicatorConfig.getHistoricalData().getFeatureName();
        String anomalyValue = getAnomalyValue(indicator, indicatorConfig);

        Map<String, String> modelContexts = indicatorConfig.getModelContextFields().stream().collect(Collectors.toMap(
                                            Function.identity(), field -> indicator.getContexts().get(field)));
        HistoricalData historicalData = historicalDataPopulator.createHistoricalData(timeRange, modelContexts, schema, featureName, anomalyValue, indicatorConfig.getHistoricalData());
        historicalData.setIndicatorId(indicator.getId());
        historicalData.setSchema(indicator.getSchema());

        if (indicatorConfig.getTransformer() != null) {
            SupportingInformationTransformer transformer = transformerFactory.getTransformer(indicatorConfig.getTransformer());
            transformer.transformHistoricalData(historicalData);
        }

        return historicalData;
    }

    @Override
    public String getType() {
        return AggregatedFeatureType.SCORE_AGGREGATION.name();
    }


    private String getAnomalyValue(Indicator indicator, IndicatorConfig indicatorConfig) {
        // static indicator -> the event value is static and therefore taken from the configuration (e.g: admin_changed_his_own_password => PASSWORD_CHANGED)
        // dynamic indicators -> the event value is taken from the indicator itself (e.g: abnormal_file_action_operation_type => FILE_OPENED, FILE_MOVED ...)
        return StringUtils.isNotEmpty(indicator.getAnomalyValue()) ? indicator.getAnomalyValue() : indicatorConfig.getAnomalyDescriptior().getAnomalyValue();
    }

    private String translateAdeNameToOutput(String adeName) {
        return AdeRecord.START_INSTANT_FIELD.equals(adeName)?
                EnrichedEvent.EVENT_DATE_FIELD_NAME:
                adeName.replace(CONTEXT_PREFIX,"");
    }

    private String translateAdeValueToOutput(String adeName, String adeValue) {
        return AdeRecord.START_INSTANT_FIELD.equals(adeName)?
                Instant.ofEpochMilli(Long.valueOf(adeValue)).toString():
                adeValue;
    }

    private String translateOutputToAdeName(String outputName) {
        return EnrichedEvent.EVENT_DATE_FIELD_NAME.equals(outputName)?
                AdeRecord.START_INSTANT_FIELD:
                CONTEXT_PREFIX + outputName;
    }


}
