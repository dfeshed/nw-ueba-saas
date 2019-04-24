package presidio.output.processor.services.alert.supportinginformation;

import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartAggregationRecord;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.HistoricalData;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.alerts.IndicatorEvent;
import presidio.output.processor.services.alert.AlertServiceImpl;

import java.util.ArrayList;
import java.util.List;

public interface SupportingInformationGenerator {

    Logger logger = Logger.getLogger(AlertServiceImpl.class);

    default List<Indicator> generateSupportingInformation(SmartAggregationRecord smartAggregationRecord, Alert alert, List<Indicator> indicators, int eventsLimit, int eventsPageSize) {

        AdeAggregationRecord adeAggregationRecord = smartAggregationRecord.getAggregationRecord();
        logger.debug("starting building supporting info for feature {}, indicator with ID {}", adeAggregationRecord.getFeatureName(), adeAggregationRecord.getId());

        try {

            for (Indicator indicator : indicators) {
                indicator.setAlertId(alert.getId());
                // generate events
                List<IndicatorEvent> events = generateEvents(adeAggregationRecord, indicator, eventsLimit, eventsPageSize, alert.getEntityType());
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

    List<IndicatorEvent> generateEvents(AdeAggregationRecord adeAggregationRecord, Indicator indicator, int eventsLimit, int eventsPageSize, String entityType) throws Exception;

    HistoricalData generateHistoricalData(AdeAggregationRecord adeAggregationRecord, Indicator indicator) throws Exception;

    String getType();

}
