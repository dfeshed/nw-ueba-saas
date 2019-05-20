package presidio.output.processor.services.alert.supportinginformation;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.time.TimeRange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.output.domain.records.alerts.*;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.repositories.EventMongoPageIterator;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.processor.config.IndicatorConfig;
import presidio.output.processor.config.SupportingInformationConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.HistoricalDataPopulator;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.HistoricalDataPopulatorFactory;

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

    private HistoricalDataPopulatorFactory historicalDataPopulatorFactory;

    private ObjectMapper objectMapper;

    private SupportingInformationUtils supportingInfoUtils;


    public SupportingInformationForFeatureAggr(SupportingInformationConfig config, EventPersistencyService eventPersistencyService, HistoricalDataPopulatorFactory historicalDataPopulatorFactory, SupportingInformationUtils supportingInfoUtils) {
        this.config = config;
        this.eventPersistencyService = eventPersistencyService;
        this.historicalDataPopulatorFactory = historicalDataPopulatorFactory;
        this.objectMapper = ObjectMapperProvider.getInstance().getNoModulesObjectMapper();
        this.supportingInfoUtils = supportingInfoUtils;
    }

    @Override
    public List<IndicatorEvent> generateEvents(AdeAggregationRecord adeAggregationRecord, Indicator indicator, int eventsLimit, int eventsPageSize) throws Exception {

        IndicatorConfig indicatorConfig = config.getIndicatorConfig(adeAggregationRecord.getFeatureName());
        String userId = adeAggregationRecord.getContext().get(CommonStrings.CONTEXT_USERID);
        TimeRange timeRange = new TimeRange(adeAggregationRecord.getStartInstant(), adeAggregationRecord.getEndInstant());
        List<Pair<String, Object>> features = supportingInfoUtils.buildAnomalyFeatures(indicatorConfig,indicator.getContexts());
        
        EventMongoPageIterator eventMongoPageIterator = new EventMongoPageIterator(eventPersistencyService, eventsPageSize, indicatorConfig.getSchema(), userId, timeRange, features, eventsLimit);
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
    public HistoricalData generateHistoricalData(AdeAggregationRecord adeAggregationRecord, Indicator indicator) throws
            Exception {
        IndicatorConfig indicatorConfig = config.getIndicatorConfig(adeAggregationRecord.getFeatureName());

        HistoricalDataPopulator historicalDataPopulator;

        // create populator
        try {
            String anomalyField = indicatorConfig.getAnomalyDescriptior().getAnomalyField();
            String historicalDataType = indicatorConfig.getHistoricalData().getType();
            historicalDataPopulator = historicalDataPopulatorFactory.createHistoricalDataPopulation(CommonStrings.CONTEXT_USERID, anomalyField, historicalDataType);
        } catch (IllegalArgumentException ex) {
            //TODO logger
            return null;
        }

        // populate historical data
        Instant startInstant = adeAggregationRecord.getStartInstant().minus(historicalPeriodInDays, ChronoUnit.DAYS);
        TimeRange timeRange = new TimeRange(startInstant, adeAggregationRecord.getEndInstant());
        Map<String, String> modelContexts = indicatorConfig.getModelContextFields().stream().collect(Collectors.toMap(
                Function.identity(),
                field -> indicator.getContexts().get(field),
                (oldValue, newValue) -> oldValue,
                LinkedHashMap::new));
        Schema schema = indicatorConfig.getSchema();
        String featureName = indicatorConfig.getHistoricalData().getFeatureName() == null? adeAggregationRecord.getFeatureName():indicatorConfig.getHistoricalData().getFeatureName() ;
        String anomalyValue = indicator.getAnomalyValue();

        HistoricalData historicalData = historicalDataPopulator.createHistoricalData(timeRange, modelContexts, schema, featureName, anomalyValue, indicatorConfig.getHistoricalData());
        historicalData.setIndicatorId(indicator.getId());
        historicalData.setSchema(indicator.getSchema());
        return historicalData;
    }


    @Override
    public String getType() {
        return AggregatedFeatureType.FEATURE_AGGREGATION.name();
    }
}
