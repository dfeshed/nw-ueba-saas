package presidio.output.processor.services.alert.supportinginformation;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import fortscale.utils.ConversionUtils;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.time.TimeRange;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartAggregationRecord;
import presidio.output.domain.records.alerts.*;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.repositories.EventMongoPageIterator;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.processor.config.AnomalyFiltersConfig;
import presidio.output.processor.config.IndicatorConfig;
import presidio.output.processor.config.SupportingInformationConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.HistoricalDataPopulator;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.HistoricalDataPopulatorFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Supporting information (indicators, events and historical data) for FEATURE_AGGREGATION events (AKA 'F')
 */
public class SupportingInformationForFeatureAggr implements SupportingInformationGenerator {

    @Value("${output.feature.historical.period.days: #{30}}")
    private int historicalPeriodInDays;

    private SupportingInformationConfig config;

    private EventPersistencyService eventPersistencyService;

    private HistoricalDataPopulatorFactory historicalDataPopulatorFactory;
    private ObjectMapper objectMapper;


    public SupportingInformationForFeatureAggr(SupportingInformationConfig config, EventPersistencyService eventPersistencyService, HistoricalDataPopulatorFactory historicalDataPopulatorFactory) {
        this.config = config;
        this.eventPersistencyService = eventPersistencyService;
        this.historicalDataPopulatorFactory = historicalDataPopulatorFactory;
        this.objectMapper = ObjectMapperProvider.getInstance().getNoModulesObjectMapper();
    }

    @Override
    public List<Indicator> generateIndicators(SmartAggregationRecord smartAggregationRecord, Alert alert, int eventsLimit, int eventsPageSize) {
        AdeAggregationRecord adeAggregationRecord = smartAggregationRecord.getAggregationRecord();
        List<Indicator> indicators = new ArrayList<>();
        IndicatorConfig indicatorConfig = config.getIndicatorConfig(adeAggregationRecord.getFeatureName());

        Indicator indicator = new Indicator(alert.getId());
        indicator.setName(indicatorConfig.getName());
        indicator.setStartDate(Date.from(adeAggregationRecord.getStartInstant()));
        indicator.setEndDate(Date.from(adeAggregationRecord.getEndInstant()));
        indicator.setAnomalyValue(String.valueOf(adeAggregationRecord.getFeatureValue()));
        indicator.setSchema(indicatorConfig.getSchema());
        indicator.setType(AlertEnums.IndicatorTypes.valueOf(indicatorConfig.getType()));
        indicator.setScore(((ScoredFeatureAggregationRecord) adeAggregationRecord).getScore());
        indicator.setScoreContribution(smartAggregationRecord.getContribution());
        indicators.add(indicator);

        return indicators;
    }

    @Override
    public List<IndicatorEvent> generateEvents(AdeAggregationRecord adeAggregationRecord, Indicator indicator, int eventsLimit, int eventsPageSize) throws Exception {

        IndicatorConfig indicatorConfig = config.getIndicatorConfig(adeAggregationRecord.getFeatureName());
        String userId = adeAggregationRecord.getContext().get(CommonStrings.CONTEXT_USERID);
        TimeRange timeRange = new TimeRange(adeAggregationRecord.getStartInstant(), adeAggregationRecord.getEndInstant());
        List<Pair<String, Object>> features = new ArrayList<Pair<String, Object>>();
        if (indicatorConfig.getAnomalyDescriptior() != null &&
                StringUtils.isNoneEmpty(indicatorConfig.getAnomalyDescriptior().getAnomalyField(),
                        indicatorConfig.getAnomalyDescriptior().getAnomalyValue())) {
            String[] values = StringUtils.split(indicatorConfig.getAnomalyDescriptior().getAnomalyValue(), ",");
            for (String value : values) {
                features.add(Pair.of(indicatorConfig.getAnomalyDescriptior().getAnomalyField(), value));
            }
        }
        AnomalyFiltersConfig anomalyFiltersConfig = indicatorConfig.getAnomalyDescriptior().getAnomalyFilters();
        if (anomalyFiltersConfig != null && StringUtils.isNoneEmpty(anomalyFiltersConfig.getFieldName(), anomalyFiltersConfig.getFieldValue())) {
            String fieldName = anomalyFiltersConfig.getFieldName();
            String fieldValue = anomalyFiltersConfig.getFieldValue();
            Object featureValue = ConversionUtils.convertToObject(fieldValue, eventPersistencyService.findFeatureType(indicatorConfig.getSchema(), fieldName));
            features.add(Pair.of(fieldName, featureValue));
        }

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
        String contextField = CommonStrings.CONTEXT_USERID;
        String contextValue = adeAggregationRecord.getContext().get(CommonStrings.CONTEXT_USERID);
        Schema schema = indicatorConfig.getSchema();
        String featureName = adeAggregationRecord.getFeatureName();
        String anomalyValue = adeAggregationRecord.getFeatureValue().toString();

        HistoricalData historicalData = historicalDataPopulator.createHistoricalData(timeRange, contextField, contextValue, schema, featureName, anomalyValue, indicatorConfig.getHistoricalData());
        historicalData.setIndicatorId(indicator.getId());
        historicalData.setSchema(indicator.getSchema());
        return historicalData;
    }


    @Override
    public String getType() {
        return AggregatedFeatureType.FEATURE_AGGREGATION.name();
    }
}
