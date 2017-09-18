package presidio.output.processor.services.alert.supportinginformation;

import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.IndicatorEvent;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.alerts.HistoricalData;
import presidio.output.processor.services.alert.AlertServiceImpl;

import java.util.ArrayList;
import java.util.List;

public interface SupportingInformationGenerator {

    static final Logger logger = Logger.getLogger(AlertServiceImpl.class);

    default List<Indicator> generateSupporingInformation(AdeAggregationRecord adeAggregationRecord, Alert alert) {

        logger.debug("starting building supporting info for feature {}, indicator with ID {}", adeAggregationRecord.getFeatureName(),  adeAggregationRecord.getId());
        List<Indicator> indicators = new ArrayList<Indicator>();

        try {
            // generate indicators
            indicators = generateIndicators(adeAggregationRecord, alert);
            for (Indicator indicator : indicators) {
                indicator.setAlertId(alert.getId());

                // generate events
                List<IndicatorEvent> events = generateEvents(adeAggregationRecord, indicator);
                if(CollectionUtils.isNotEmpty(events)) {
                    indicator.setEvents(events);
                    indicator.setEventsNum(events.size());
                }

                // generate historical data
                HistoricalData historicalData = generateHistoricalData(adeAggregationRecord, indicator);
                indicator.setHistoricalData(historicalData);
            }
        } catch (Exception ex) {
            logger.error("failed to build supporting info for feature {}, indicator ID {}", adeAggregationRecord.getFeatureName(),  adeAggregationRecord.getId(),ex);

        }
        logger.debug("building supporting info for feature {}, indicator ID {} has been completed", adeAggregationRecord.getFeatureName(),  adeAggregationRecord.getId());
        return indicators;
    }

    List<Indicator> generateIndicators(AdeAggregationRecord adeAggregationRecord, Alert alert)  throws Exception;
    List<IndicatorEvent> generateEvents(AdeAggregationRecord adeAggregationRecord, Indicator indicator) throws Exception;
    HistoricalData generateHistoricalData(AdeAggregationRecord adeAggregationRecord, Indicator indicator)  throws Exception;
    String getType();

}
