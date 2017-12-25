package presidio.output.processor.services.alert.supportinginformation;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import fortscale.utils.ConversionUtils;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.time.TimeRange;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.output.domain.records.alerts.*;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.ScoredEnrichedEvent;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.event.ScoredEventService;
import presidio.output.processor.config.AnomalyFiltersConfig;
import presidio.output.processor.config.IndicatorConfig;
import presidio.output.processor.config.SupportingInformationConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.HistoricalDataPopulator;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.HistoricalDataPopulatorFactory;
import presidio.output.processor.services.alert.supportinginformation.transformer.SupportingInformationTransformer;
import presidio.output.processor.services.alert.supportinginformation.transformer.SupportingInformationTransformerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Supporting information (indicators, events and historical data) for SCORE_AGGREGATION events (AKA 'P')
 */
public class SupportingInformationForScoreAggr implements SupportingInformationGenerator {

    public static final String START_INSTANT = "startInstant";
    private final ObjectMapper objectMapper;

    @Autowired
    private SupportingInformationTransformerFactory transformerFactory;

    @Value("${output.aggregated.feature.historical.period.days: #{30}}")
    private int historicalPeriodInDays;

    @Value("${output.activity.time.historical.period.days: #{90}}")
    private int historicalActivityTimePeriodInDays;

    private AdeManagerSdk adeManagerSdk;

    private SupportingInformationConfig config;

    private EventPersistencyService eventPersistencyService;

    private HistoricalDataPopulatorFactory historicalDataPopulatorFactory;

    private ScoredEventService scoredEventService;


    public SupportingInformationForScoreAggr(SupportingInformationConfig supportingInformationConfig, AdeManagerSdk adeManagerSdk, EventPersistencyService eventPersistencyService, HistoricalDataPopulatorFactory historicalDataPopulatorFactory, ScoredEventService scoredEventService) {
        this.config = supportingInformationConfig;
        this.adeManagerSdk = adeManagerSdk;
        this.eventPersistencyService = eventPersistencyService;
        this.historicalDataPopulatorFactory = historicalDataPopulatorFactory;
        this.scoredEventService = scoredEventService;
        this.objectMapper = ObjectMapperProvider.getInstance().getNoModulesObjectMapper();
    }


    @Override
    public List<Indicator> generateIndicators(AdeAggregationRecord adeAggregationRecord, Alert alert, int eventsLimit, int eventsPageSize) {
        List<Indicator> indicators = new ArrayList<>();
        IndicatorConfig indicatorConfig = config.getIndicatorConfig(adeAggregationRecord.getFeatureName());
        Pair<String, String> contextFieldAndValue = Pair.of(CommonStrings.CONTEXT_USERID, adeAggregationRecord.getContext().get(CommonStrings.CONTEXT_USERID));
        TimeRange timeRange = new TimeRange(adeAggregationRecord.getStartInstant(), adeAggregationRecord.getEndInstant());
        List<String> distinctFeatureValues = getDistinctFeatureValues(adeAggregationRecord, indicatorConfig, contextFieldAndValue, timeRange, eventsLimit, eventsPageSize);
        for (String featureValue : distinctFeatureValues) {

            Indicator indicator = new Indicator(alert.getId());
            indicator.setName(indicatorConfig.getName());
            indicator.setStartDate(Date.from(adeAggregationRecord.getStartInstant()));
            indicator.setEndDate(Date.from(adeAggregationRecord.getEndInstant()));
            indicator.setAnomalyValue(featureValue);
            indicator.setSchema(indicatorConfig.getSchema());
            indicator.setType(AlertEnums.IndicatorTypes.valueOf(indicatorConfig.getType()));
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

        List<Pair<String, Object>> features = new ArrayList<>();
        String anomalyField = indicatorConfig.getAnomalyDescriptior().getAnomalyField();
        String anomalyValue = getAnomalyValue(indicator, indicatorConfig);
        if (StringUtils.isNoneEmpty(anomalyValue, anomalyField)) {
            Object featureValue = ConversionUtils.convertToObject(anomalyValue, eventPersistencyService.findFeatureType(indicatorConfig.getSchema(), anomalyField));
            features.add(Pair.of(anomalyField, featureValue));
        }
        AnomalyFiltersConfig anomalyFiltersConfig = indicatorConfig.getAnomalyDescriptior().getAnomalyFilters();
        if (anomalyFiltersConfig != null && StringUtils.isNoneEmpty(anomalyFiltersConfig.getFieldName(), anomalyFiltersConfig.getFieldValue())) {
            String fieldName = anomalyFiltersConfig.getFieldName();
            String fieldValue = anomalyFiltersConfig.getFieldValue();
            String[] values = StringUtils.split(fieldValue, ",");
            for (String value : values) {
                Object featureValue = ConversionUtils.convertToObject(value, eventPersistencyService.findFeatureType(indicatorConfig.getSchema(), fieldName));
                features.add(Pair.of(fieldName, featureValue));
            }
        }

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
                    scores.put(anomalyField, rawEvent.getScore());
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

        Instant startInstant = EnrichedEvent.START_INSTANT_FIELD.equals(indicatorConfig.getAnomalyDescriptior().getAnomalyField()) ?
                adeAggregationRecord.getStartInstant().minus(historicalActivityTimePeriodInDays, ChronoUnit.DAYS) :
                adeAggregationRecord.getStartInstant().minus(historicalPeriodInDays, ChronoUnit.DAYS);
        TimeRange timeRange = new TimeRange(startInstant, adeAggregationRecord.getEndInstant());
        String contextField = CommonStrings.CONTEXT_USERID;
        String contextValue = adeAggregationRecord.getContext().get(CommonStrings.CONTEXT_USERID);
        Schema schema = indicatorConfig.getSchema();
        String featureName = indicatorConfig.getHistoricalData().getFeatureName();
        String anomalyValue = getAnomalyValue(indicator, indicatorConfig);

        HistoricalData historicalData = historicalDataPopulator.createHistoricalData(timeRange, contextField, contextValue, schema, featureName, anomalyValue, indicatorConfig.getHistoricalData());
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

    private List<String> getDistinctFeatureValues(AdeAggregationRecord adeAggregationRecord, IndicatorConfig indicatorConfig, Pair<String, String> contextFieldAndValue, TimeRange timeRange, int eventsLimit, int eventsPageSize) {
        List<String> distinctFeatureValues = new ArrayList<>();

        // static indicator -> one empty value
        if (AlertEnums.IndicatorTypes.STATIC_INDICATOR.name().equals(indicatorConfig.getType())) {
            distinctFeatureValues.add(StringUtils.EMPTY);
            return distinctFeatureValues;
        }

        // get distinct values of all the scored events
        List<Pair<String, Object>> features = new ArrayList<>();
        AnomalyFiltersConfig anomalyFiltersConfig = indicatorConfig.getAnomalyDescriptior().getAnomalyFilters();
        if (anomalyFiltersConfig != null && StringUtils.isNoneEmpty(anomalyFiltersConfig.getFieldName(), anomalyFiltersConfig.getFieldValue())) {
            String fieldName = anomalyFiltersConfig.getFieldName();
            String fieldValue = anomalyFiltersConfig.getFieldValue();
            Object featureValue = ConversionUtils.convertToObject(fieldValue, eventPersistencyService.findFeatureType(indicatorConfig.getSchema(), fieldName));
            features.add(Pair.of(anomalyFiltersConfig.getFieldName(), featureValue));
        }
        List<Object> featureValues =
                scoredEventService.findDistinctScoredFeatureValue(indicatorConfig.getSchema(),
                        indicatorConfig.getAdeEventType(),
                        contextFieldAndValue,
                        timeRange,
                        indicatorConfig.getAnomalyDescriptior().getAnomalyField(), 0.0, features, eventsLimit, eventsPageSize);

        if (CollectionUtils.isNotEmpty(featureValues)) {
            if (EnrichedEvent.START_INSTANT_FIELD.equals(indicatorConfig.getAnomalyDescriptior().getAnomalyField())) {
                distinctFeatureValues.add(featureValues.get(0).toString());
            } else {
                distinctFeatureValues.addAll(featureValues.stream().map(Object::toString).collect(Collectors.toList()));
            }
        }
        return distinctFeatureValues;
    }

}
