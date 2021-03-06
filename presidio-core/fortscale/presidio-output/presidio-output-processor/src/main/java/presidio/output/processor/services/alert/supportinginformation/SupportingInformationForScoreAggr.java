package presidio.output.processor.services.alert.supportinginformation;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.general.Schema;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import fortscale.utils.time.TimeRange;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.output.domain.records.alerts.Aggregation;
import presidio.output.domain.records.alerts.HistoricalData;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.alerts.IndicatorEvent;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.ScoredEnrichedEvent;
import presidio.output.domain.services.event.ScoredEventService;
import presidio.output.processor.config.HistoricalDataConfig;
import presidio.output.processor.config.IndicatorConfig;
import presidio.output.processor.config.SupportingInformationConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.AggregationDataPopulatorFactory;
import presidio.output.processor.services.alert.supportinginformation.transformer.SupportingInformationTransformer;
import presidio.output.processor.services.alert.supportinginformation.transformer.SupportingInformationTransformerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Supporting information (events and historical data) for SCORE_AGGREGATION events (AKA 'P')
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

    private AggregationDataPopulatorFactory aggregationDataPopulatorFactory;

    private ScoredEventService scoredEventService;

    private SupportingInformationUtils supportingInfoUtils;

    private AdeManagerSdk adeManagerSdk;

    private RecordReaderFactoryService recordReaderFactoryService;


    public SupportingInformationForScoreAggr(SupportingInformationConfig supportingInformationConfig, AggregationDataPopulatorFactory aggregationDataPopulatorFactory, ScoredEventService scoredEventService, SupportingInformationUtils supportingInfoUtils, AdeManagerSdk adeManagerSdk, RecordReaderFactoryService recordReaderFactoryService) {
        this.config = supportingInformationConfig;
        this.aggregationDataPopulatorFactory = aggregationDataPopulatorFactory;
        this.scoredEventService = scoredEventService;
        this.objectMapper = ObjectMapperProvider.getInstance().getNoModulesObjectMapper();
        this.supportingInfoUtils = supportingInfoUtils;
        this.adeManagerSdk = adeManagerSdk;
        this.recordReaderFactoryService = recordReaderFactoryService;
    }

    @Override
    public List<IndicatorEvent> generateEvents(AdeAggregationRecord adeAggregationRecord, Indicator indicator, int eventsLimit, int eventsPageSize, String entityType, String entityId) {

        List<IndicatorEvent> events = new ArrayList<>();

        // get raw events from the output (output_ collections)
        IndicatorConfig indicatorConfig = config.getIndicatorConfig(adeAggregationRecord.getFeatureName());
        TimeRange timeRange = new TimeRange(adeAggregationRecord.getStartInstant(), adeAggregationRecord.getEndInstant());

        List<Pair<String, Object>> features = supportingInfoUtils.buildAnomalyFeatures(indicatorConfig, indicator.getContexts());

        List<ScoredEnrichedEvent> rawEvents = scoredEventService.findEventsAndScores(indicatorConfig.getSchema(), indicatorConfig.getAdeEventType(), entityId, timeRange, features, eventsLimit, eventsPageSize, entityType);

        if (CollectionUtils.isNotEmpty(rawEvents)) {

            // build Event for each raw event
            for (ScoredEnrichedEvent rawEvent : rawEvents) {
                Map<String, Object> rawEventFeatures = objectMapper.convertValue(rawEvent.getEnrichedEvent(), Map.class);

                IndicatorEvent event = new IndicatorEvent();
                event.setFeatures(rawEventFeatures);
                event.setIndicatorId(indicator.getId());
                event.setEventTime(Date.from(rawEvent.getEnrichedEvent().getEventDate()));
                event.setSchema(indicatorConfig.getSchema());
                event.setEntityType(indicator.getEntityType());
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
        List<HistoricalDataConfig> historicalDataConfigList = indicatorConfig.getHistoricalData();

        Instant startInstant = EnrichedEvent.EVENT_DATE_FIELD_NAME.equals(indicatorConfig.getAnomalyDescriptior().getAnomalyField()) ?
                adeAggregationRecord.getStartInstant().minus(historicalActivityTimePeriodInDays, ChronoUnit.DAYS) :
                adeAggregationRecord.getStartInstant().minus(historicalPeriodInDays, ChronoUnit.DAYS);
        TimeRange timeRange = new TimeRange(startInstant, adeAggregationRecord.getEndInstant());
        Schema schema = indicatorConfig.getSchema();
        String anomalyValue = getAnomalyValue(indicator, indicatorConfig);

        List<Aggregation> aggregations = generateAggregations(aggregationDataPopulatorFactory, historicalDataConfigList, adeAggregationRecord, indicatorConfig, indicator, timeRange, schema, anomalyValue);

        HistoricalData historicalData = new HistoricalData(aggregations);
        historicalData.setIndicatorId(indicator.getId());
        historicalData.setSchema(indicator.getSchema());

        if (indicatorConfig.getTransformer() != null) {
            SupportingInformationTransformer transformer = transformerFactory.getTransformer(indicatorConfig.getTransformer());
            transformer.transformHistoricalData(historicalData);
        }

        return historicalData;
    }

    @Override
    public String getFeatureName(HistoricalDataConfig historicalDataConfig, AdeAggregationRecord adeAggregationRecord) {
        return historicalDataConfig.getFeatureName();
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


}
