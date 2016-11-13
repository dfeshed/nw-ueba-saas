package fortscale.streaming.task.metrics;

/**
 * Created by gaashh on 5/29/16.
 */

import fortscale.streaming.task.HDFSWriterStreamTask;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for HDFSWriterStreamTask
 * Note: StreamingTaskCommonMetrics provides the common stream task metrics
 */
@StatsMetricsGroupParams(name = "streaming.HDFS-writer.task")
public class HDFSWriterStreamingTaskMetrics extends StatsMetricsGroup {

    public HDFSWriterStreamingTaskMetrics(StatsService statsService) {
        // Call parent ctor
        super(statsService, HDFSWriterStreamTask.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        //addTag("foo", fooName);
                    }
                }
        );

    }

    // Number of event messages with unknown data source
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long unknownDataSourceEventMessages;

    // Number of messages without HDFS writer (e.g. bad config key)
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long HDFSWriterNotFoundMessages;

    // Number of task coordinate calls
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long coordinate;

    // Number of task coordinate calls exceptions
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long coordinateExceptions;

    // Number of retries to write an event
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long writeRetries;

    // Number of failed retries to write an event
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long failedWriteRetries;

    // Number of discarded events that were not written
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long eventsDiscarded;

    // Number of write exceptions thrown
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long writeExceptionsThrown;
}
