package presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import presidio.output.processor.config.HistoricalDataConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.DailyHistogram;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface for Historical data fetching
 * Fetcher should provide historical data of specified features, context values, time ranges etc.
 */
public interface HistoricalDataFetcher {

    /**
     * Fetches the historical feature usage of the context (i.e. user) during the specified time period grouped by day
     *
     * @param timeRange           the time period
     * @param contexts            map of contexts (context id (i.e userId) to context value (i.e the user name))
     * @param schema              the schema for which to populate historical behavior
     * @param featureName         the feature for which to populate historical behavior (e.g: login time)
     * @param includeOnlyBaseline true if only the baseline time range should be included,
     *                            false if the rest of the time range should be completed
     * @return List of feature histogram for each day in the range
     *         e.g:   Feature: operationType, Date: 01/01/2017, Histogram {FILE_MOVED:5, FILE_COPY:9, ACCESS_RIGHTS_CHANGED:1}
     *                Feature: operationType, Date: 01/02/2017, Histogram {FILE_OPENED:10, ACCESS_RIGHTS_CHANGED:1}
     */
    List<DailyHistogram<String, Number>> getDailyHistogramsForFeature(
            TimeRange timeRange, Map<String, String> contexts, Schema schema, String featureName,
            HistoricalDataConfig historicalDataConfig, boolean includeOnlyBaseline);


    /**
     * Fetches the historical aggregated feature usage of the context (i.e. user) during the specified time period grouped by day
     *
     * @param timeRange   the time period
     * @param contexts    map of contexts (context id (i.e userId) to context value (i.e the user name))
     * @param schema      the schema for which to populate historical behavior
     * @param featureName the feature for which to populate historical behavior (e.g: login time)
     * @return List of aggregated feature histogram for each day in the range. The histogram key is hour of the day (0 .. 23)
     *         e.g:   Aggregated Feature: numberOfFailedAuthentications, Date: 01/01/2017, histogram {0:1, 2:0, 3:0 ...}
     *                Aggregated Feature: numberOfFailedAuthentications, Date: 01/02/2017, histogram {0:0, 2:1, 3:0 ...}
     */
    List<DailyHistogram<Integer, Double>> getDailyHistogramsForAggregatedFeature(
            TimeRange timeRange, Map<String, String> contexts, Schema schema, String featureName);


    /**
     * Fetches the historical aggregated feature usage of the context (i.e. user) during the specified time period (last day) grouped by day
     *
     * @param timeRange   the time period
     * @param contexts    map of contexts (context id (i.e userId) to context value (i.e the user name))
     * @param schema      the schema for which to populate historical behavior
     * @param featureName the feature for which to populate historical behavior (e.g: login time)
     * @return List of aggregated feature histogram for each day in the range. The histogram key is hour of the day (0 .. 23)
     *         e.g:   Aggregated Feature: numberOfFailedAuthentications, Date: 01/01/2017, histogram {0:1, 2:0, 3:0 ...}
     *                Aggregated Feature: numberOfFailedAuthentications, Date: 01/02/2017, histogram {0:0, 2:1, 3:0 ...}
     */
    List<DailyHistogram<Integer, Double>> getLastDayHistogramsForAggregatedFeature(
            TimeRange timeRange, Map<String, String> contexts, Schema schema, String featureName);

    /**
     * Fetches the organization historical max aggregated feature usage during the specified time period grouped by day
     *
     * @param timeRange   the time period
     * @param schema      the schema for which to populate historical behavior
     * @param featureName the feature for which to populate historical behavior (e.g: login time)
     * @return List of aggregated feature histogram for each day in the range. The histogram key is hour of the day (0 .. 23)
     *         e.g:   Aggregated Feature: numberOfFailedAuthentications, Date: 01/01/2017, histogram {0:1, 2:0, 3:0 ...}
     *                Aggregated Feature: numberOfFailedAuthentications, Date: 01/02/2017, histogram {0:0, 2:1, 3:0 ...}
     */
    List<DailyHistogram<Integer, Double>> getGlobalMaxDailyHistogramsForAggregatedFeature(
            TimeRange timeRange, Schema schema, String featureName);
}
