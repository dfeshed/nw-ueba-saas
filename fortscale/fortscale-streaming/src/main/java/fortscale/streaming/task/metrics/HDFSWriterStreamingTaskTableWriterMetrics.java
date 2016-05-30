package fortscale.streaming.task.metrics;

/**
 * Created by gaashh on 5/29/16.
 */

import fortscale.streaming.task.HDFSWriterStreamTask;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for HDFSWriterStreamTask writer
 * Note: StreamingTaskCommonMetrics provides the common stream task metrics
 */
@StatsMetricsGroupParams(name = "streaming.ip-resolving.table-writer")
public class HDFSWriterStreamingTaskTableWriterMetrics extends StatsMetricsGroup {

    public HDFSWriterStreamingTaskTableWriterMetrics(StatsService statsService, String jobName, String tableName) {
        // Call parent ctor
        super(statsService, HDFSWriterStreamTask.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        addTag("job",   jobName);
                        addTag("table", tableName);
                    }
                }
        );

    }

    // Number of messages written to HDFS
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long writeToHdfsMessages;

    // Number of messages written to output topic
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long writeToOutputTopicMessages;

    // Number of messages written to output topic failure
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long writeToOutputTopicMessagesFailures;

    // Number of messages without HDFS writer
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long invalidTimeFieldMessages;

    // Number of messages filtered messages due to message filter
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long filterFilteredMessages;

    // Number of messages filtered messages due to barrier
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long barrierFilteredMessages;

    // Number of barrier updates
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long barrierUpdates;

    // Number of HDFS and barrier flushes
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long flushes;

    // Last message time stamp
    @StatsDateMetricParams
    public long messageEpoch;


}



