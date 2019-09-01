package presidio.output.processor.services.alert.supportinginformation;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.general.Schema;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.time.TimeRange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.output.domain.records.alerts.Aggregation;
import presidio.output.domain.records.alerts.HistoricalData;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.alerts.IndicatorEvent;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.repositories.EventMongoPageIterator;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.processor.config.HistoricalDataConfig;
import presidio.output.processor.config.IndicatorConfig;
import presidio.output.processor.config.SupportingInformationConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.AggregationDataPopulator;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.AggregationDataPopulatorFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Supporting information (events and historical data) for FEATURE_AGGREGATION events (AKA 'F')
 */
public class SupportingInformationForFeatureAggr implements SupportingInformationGenerator {

    @Value("${output.feature.historical.period.days: #{30}}")
    private int historicalPeriodInDays;

    private SupportingInformationConfig config;

    private EventPersistencyService eventPersistencyService;

    private AggregationDataPopulatorFactory aggregationDataPopulatorFactory;

    private ObjectMapper objectMapper;

    private SupportingInformationUtils supportingInfoUtils;


    public SupportingInformationForFeatureAggr(SupportingInformationConfig config, EventPersistencyService eventPersistencyService, AggregationDataPopulatorFactory aggregationDataPopulatorFactory, SupportingInformationUtils supportingInfoUtils) {
        this.config = config;
        this.eventPersistencyService = eventPersistencyService;
        this.aggregationDataPopulatorFactory = aggregationDataPopulatorFactory;
        this.objectMapper = ObjectMapperProvider.getInstance().getNoModulesObjectMapper();
        this.supportingInfoUtils = supportingInfoUtils;
    }

    @Override
    public List<IndicatorEvent> generateEvents(AdeAggregationRecord adeAggregationRecord, Indicator indicator, int eventsLimit, int eventsPageSize, String entityType, String entityId) {

        IndicatorConfig indicatorConfig = config.getIndicatorConfig(adeAggregationRecord.getFeatureName());
        TimeRange timeRange = new TimeRange(adeAggregationRecord.getStartInstant(), adeAggregationRecord.getEndInstant());
        List<Pair<String, Object>> features = supportingInfoUtils.buildAnomalyFeatures(indicatorConfig,indicator.getContexts());
        
        EventMongoPageIterator eventMongoPageIterator = new EventMongoPageIterator(eventPersistencyService, eventsPageSize, indicatorConfig.getSchema(), entityId, timeRange, features, eventsLimit, entityType);
        List<IndicatorEvent> events = new ArrayList<>();

        while (eventMongoPageIterator.hasNext()) {
            List<? extends EnrichedEvent> rawEvents = eventMongoPageIterator.next();


            for (EnrichedEvent rawEvent : rawEvents) {

                Map<String, Object> rawEventFeatures = objectMapper.convertValue(rawEvent, Map.class);
                IndicatorEvent event = new IndicatorEvent();
                event.setFeatures(rawEventFeatures);
                event.setIndicatorId(indicator.getId());
                event.setEventTime(Date.from(rawEvent.getEventDate()));
                event.setSchema(indicatorConfig.getSchema());
                events.add(event);

            }
        }
        return events;
    }

    @Override
    public HistoricalData generateHistoricalData(AdeAggregationRecord adeAggregationRecord, Indicator indicator) {
        IndicatorConfig indicatorConfig = config.getIndicatorConfig(adeAggregationRecord.getFeatureName());

        AggregationDataPopulator aggregationDataPopulator;

        List<HistoricalDataConfig> historicalDataConfigList = indicatorConfig.getHistoricalData();

        List<Aggregation> aggregations = new ArrayList<>();

        Instant startInstant = adeAggregationRecord.getStartInstant().minus(historicalPeriodInDays, ChronoUnit.DAYS);
        TimeRange timeRange = new TimeRange(startInstant, adeAggregationRecord.getEndInstant());
        Map<String, String> modelContexts = indicatorConfig.getModelContextFields().stream().collect(Collectors.toMap(
                Function.identity(),
                field -> indicator.getContexts().get(field),
                (oldValue, newValue) -> oldValue,
                LinkedHashMap::new));
        Schema schema = indicatorConfig.getSchema();
        String anomalyValue = indicator.getAnomalyValue();

        for(HistoricalDataConfig historicalDataConfig : historicalDataConfigList){
            Aggregation aggregation;

            // create aggregation populator
            try {
                aggregationDataPopulator = aggregationDataPopulatorFactory.createAggregationDataPopulation(historicalDataConfig.getType());
            } catch (IllegalArgumentException ex) {
                //TODO logger
                return null;
            }

            // populate historical data
            String featureName = historicalDataConfig.getFeatureName() == null? adeAggregationRecord.getFeatureName():historicalDataConfig.getFeatureName() ;
            aggregation = aggregationDataPopulator.createAggregationData(timeRange, modelContexts, schema, featureName, anomalyValue, historicalDataConfig);
            aggregations.add(aggregation);
        }

        HistoricalData historicalData = new HistoricalData(aggregations);
        historicalData.setIndicatorId(indicator.getId());
        historicalData.setSchema(indicator.getSchema());

        return historicalData;
    }


    @Override
    public String getType() {
        return AggregatedFeatureType.FEATURE_AGGREGATION.name();
    }
}
