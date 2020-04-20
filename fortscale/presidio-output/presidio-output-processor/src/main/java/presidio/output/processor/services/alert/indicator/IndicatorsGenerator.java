package presidio.output.processor.services.alert.indicator;

import presidio.ade.domain.record.aggregated.SmartAggregationRecord;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.Indicator;

import java.util.List;

public interface IndicatorsGenerator {

    List<Indicator> generateIndicators(SmartAggregationRecord smartAggregationRecord, Alert alert);

    String getType();
}
