package presidio.output.processor.services.alert.supportinginformation;

import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeRange;
import org.apache.commons.collections.CollectionUtils;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartAggregationRecord;
import presidio.output.domain.records.alerts.*;
import presidio.output.processor.config.HistoricalDataConfig;
import presidio.output.processor.config.IndicatorConfig;
import presidio.output.processor.services.alert.AlertServiceImpl;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.AggregationDataPopulator;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.AggregationDataPopulatorFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface SupportingInformationGenerator {

    Logger logger = Logger.getLogger(AlertServiceImpl.class);

    default List<Indicator> generateSupportingInformation(SmartAggregationRecord smartAggregationRecord, Alert alert, List<Indicator> indicators, int eventsLimit, int eventsPageSize) {

        AdeAggregationRecord adeAggregationRecord = smartAggregationRecord.getAggregationRecord();
        logger.debug("starting building supporting info for feature {}, indicator with ID {}", adeAggregationRecord.getFeatureName(), adeAggregationRecord.getId());

        try {

            for (Indicator indicator : indicators) {
                indicator.setAlertId(alert.getId());
                // generate events
                List<IndicatorEvent> events = null;
                Optional<Map.Entry<String, String>> optionalEntry = adeAggregationRecord.getContext().entrySet().stream().findAny();
                if (optionalEntry.isPresent()) {
                    events = generateEvents(adeAggregationRecord, indicator, eventsLimit, eventsPageSize, optionalEntry.get().getKey(),
                            optionalEntry.get().getValue());
                }
                if (CollectionUtils.isNotEmpty(events)) {
                    indicator.setEvents(events);
                    indicator.setEventsNum(events.size());
                } else {
                    logger.warn("failed to generate events for indicator ID {}, feature {}", adeAggregationRecord.getId(), adeAggregationRecord.getFeatureName());
                }

                // generate historical data
                HistoricalData historicalData = generateHistoricalData(adeAggregationRecord, indicator);
                if (historicalData == null) {
                    logger.warn("failed to generate historical data to indicator ID {}, feature {}", adeAggregationRecord.getId(), adeAggregationRecord.getFeatureName());
                }
                indicator.setHistoricalData(historicalData);
            }
        } catch (Exception ex) {
            logger.error("failed to build supporting info for feature {}, indicator ID {}", adeAggregationRecord.getFeatureName(), adeAggregationRecord.getId(), ex);
        }

        logger.debug("building supporting info for feature {}, indicator ID {} has been completed", adeAggregationRecord.getFeatureName(), adeAggregationRecord.getId());
        return indicators;
    }

    default Map<String, String> getHistoricalDataContexts(List<String> contexts, Indicator indicator) {
        return contexts.stream().collect(Collectors.toMap(
                Function.identity(),
                contextFieldName -> indicator.getContexts().get(contextFieldName),
                (oldValue, newValue) -> oldValue,
                LinkedHashMap::new));

    }

    default List<Aggregation> generateAggregations(AggregationDataPopulatorFactory aggregationDataPopulatorFactory, List<HistoricalDataConfig> historicalDataConfigList,
                                                   AdeAggregationRecord adeAggregationRecord, IndicatorConfig indicatorConfig, Indicator indicator, TimeRange timeRange, Schema schema, String anomalyValue) {

        List<Aggregation> aggregations = new ArrayList<>();

        for (HistoricalDataConfig historicalDataConfig : historicalDataConfigList) {

            // create aggregation populator
            AggregationDataPopulator aggregationDataPopulator;

            try {
                aggregationDataPopulator = aggregationDataPopulatorFactory.createAggregationDataPopulation(historicalDataConfig.getType());
            } catch (IllegalArgumentException ex) {
                logger.error("failed to create aggregation populator for {} historical data type", historicalDataConfig.getType());
                return null;
            }

            String featureName = getFeatureName(historicalDataConfig, adeAggregationRecord);
            Map<String, String> contexts = historicalDataConfig.getContexts() == null ? getHistoricalDataContexts(indicatorConfig.getModelContextFields(), indicator) : getHistoricalDataContexts(historicalDataConfig.getContexts(), indicator);
            boolean skipAnomaly = historicalDataConfig.getSkipAnomaly() == null ? false : historicalDataConfig.getSkipAnomaly();
            Aggregation aggregation = aggregationDataPopulator.createAggregationData(timeRange, contexts, schema, featureName, anomalyValue, historicalDataConfig, skipAnomaly, indicator.getStartDate());

            aggregations.add(aggregation);
        }

        return aggregations;
    }

    String getFeatureName(HistoricalDataConfig historicalDataConfig, AdeAggregationRecord adeAggregationRecord);

    List<IndicatorEvent> generateEvents(AdeAggregationRecord adeAggregationRecord, Indicator indicator, int eventsLimit, int eventsPageSize, String entityType, String entityId) throws Exception;

    HistoricalData generateHistoricalData(AdeAggregationRecord adeAggregationRecord, Indicator indicator) throws Exception;

    String getType();

}
