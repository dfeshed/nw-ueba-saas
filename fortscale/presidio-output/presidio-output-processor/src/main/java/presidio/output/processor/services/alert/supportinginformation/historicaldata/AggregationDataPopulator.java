package presidio.output.processor.services.alert.supportinginformation.historicaldata;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import presidio.output.domain.records.alerts.Aggregation;
import presidio.output.processor.config.HistoricalDataConfig;

import java.util.Map;

/**
 * Interface for Historical data population. Populator should provide the complete
 * historical data based on the context value, time and anomaly value.
 */
public interface AggregationDataPopulator {

    /**
     * Populates the historical behaviour of the context (i.e. user) during the specified time period
     *
     * @param timeRange
     * @param contexts map of contexts (context id (i.e userId) to context value (i.e the user name))
     * @param schema the schema for which to populate historical behavior
     * @param anomalyValue the anomaly value
     *
     * @return Aggregation data with anomaly value indication
     */

    Aggregation createAggregationData(TimeRange timeRange, Map<String, String> contexts, Schema schema, String featureName, String anomalyValue, HistoricalDataConfig historicalDataConfig, boolean skipAnomaly);

    String getType();

}
