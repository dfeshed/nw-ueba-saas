package presidio.output.domain.records.alerts;

import fortscale.common.general.Schema;

/**
 * Projection for indicators without historical data
 */
public interface IndicatorSummary {

    String getId();

    String getName();

    String getAnomalyValue();

    String getAlertId();

    long getStartDate();

    long getEndDate();

    Schema getSchema();

}
