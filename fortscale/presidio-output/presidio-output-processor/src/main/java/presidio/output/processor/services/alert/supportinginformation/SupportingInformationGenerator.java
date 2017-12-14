package presidio.output.processor.services.alert.supportinginformation;

import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.HistoricalData;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.alerts.IndicatorEvent;
import presidio.output.processor.services.alert.AlertServiceImpl;

import java.util.ArrayList;
import java.util.List;

public interface SupportingInformationGenerator {

    Logger logger = Logger.getLogger(AlertServiceImpl.class);

    default List<Indicator> generateSupportingInformation(AdeAggregationRecord adeAggregationRecord, Alert alert, int eventsLimit) {

        logger.debug("starting building supporting info for feature {}, indicator with ID {}", adeAggregationRecord.getFeatureName(), adeAggregationRecord.getId());
        List<Indicator> indicators = new ArrayList<>();

        try {
            // generate indicators
            indicators = generateIndicators(adeAggregationRecord, alert, eventsLimit);
            if (CollectionUtils.isEmpty(indicators)) {
                logger.warn("failed to generate indicators for adeAggregationRecord ID {}, feature {}", adeAggregationRecord.getId(), adeAggregationRecord.getFeatureName());
            }

            for (Indicator indicator : indicators) {
                indicator.setAlertId(alert.getId());
                // generate events
                List<IndicatorEvent> events = generateEvents(adeAggregationRecord, indicator, eventsLimit);
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

    List<Indicator> generateIndicators(AdeAggregationRecord adeAggregationRecord, Alert alert, int eventsLimit) throws Exception;

    List<IndicatorEvent> generateEvents(AdeAggregationRecord adeAggregationRecord, Indicator indicator, int eventsLimit) throws Exception;

    HistoricalData generateHistoricalData(AdeAggregationRecord adeAggregationRecord, Indicator indicator) throws Exception;

    String getType();

}
