package presidio.output.processor.services.alert.supportinginformation.transformer;

import presidio.output.domain.records.alerts.HistoricalData;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.alerts.IndicatorEvent;

import java.util.List;

public interface SupportingInformationTransformer {

    default void transformIndicator(Indicator indicator) {
        throw new UnsupportedOperationException();
    }

    default void transformEvents(List<IndicatorEvent> events) {
        throw new UnsupportedOperationException();
    }

    default void transformHistoricalData(HistoricalData historicalData) {}

}
