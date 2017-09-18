package presidio.output.processor.services.alert.supportinginformation.historicaldata;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import presidio.output.domain.records.alerts.HistoricalData;
import presidio.output.processor.config.HistoricalDataConfig;

/**
 * Interface for Historical data population. Populator should provide the complete
 * historical data based on the context value, time and anomaly value.
 */
public interface HistoricalDataPopulator {

    /**
     * Populates the historical behaviour of the context (i.e. user) during the specified time period
     *
     * @param timeRange
     * @param contextField the context id (i.e userId)
     * @param contextValue the context value (i.e the user name)
     * @param schema the schema for which to populate historical behavior
     * @param featureName the feature for which to populate historical behavior (e.g: login time)
     * @param anomalyValue the anomaly value
     *
     * @return Historical data with anomaly value indication
     */
    HistoricalData createHistoricalData(TimeRange timeRange, String contextField, String contextValue, Schema schema, String featureName, String anomalyValue, HistoricalDataConfig historicalDataConfig);

    String getType();

}
