package fortscale.streaming.stats.metrics;

import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;

/**
 *
 * An helper class for stats metrics in streaming
 *
 * Created by gaashh on 6/2/16.
 */
public class StreamingStatsMetricsUtils {

    /**
     *
     * An helper function that adds "name", "dataSource", "lastStep" tags to stats metric group attributes. Value are
     * extracted from StreamingTaskDataSourceConfigKey
     *
     * @param attributes
     * @param dataSourceConfigKey
     */
    static public void addTagsFromDataSourceConfig(StatsMetricsGroupAttributes attributes,
                                                     StreamingTaskDataSourceConfigKey dataSourceConfigKey) {

        attributes.addTag("name",       dataSourceConfigKey.getMetricsName());
        attributes.addTag("dataSource", dataSourceConfigKey.getDataSource());
        attributes.addTag("lastStep",   dataSourceConfigKey.getLastState());

    }


}
