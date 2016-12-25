package fortscale.streaming.task.metrics;

import fortscale.streaming.task.AbstractStreamTask;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Created by gaashh on 5/25/16.
 */
@StatsMetricsGroupParams(name = "streaming.task.common")
public class StreamingTaskCommonMetrics extends StatsMetricsGroup {

    public StreamingTaskCommonMetrics(StatsService statsService) {
        // Call parent ctor
        super(statsService, AbstractStreamTask.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                       // addTag("foo", fooName);
                    }
                }
        );

    }

    // Number of process() task function calls.
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long processedMessages;

    // Number of process() task function calls that threw an exception
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long processedMessagesExceptions;

    // Number of handled unfiltered message
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long handledUnfilteredMessage;


    // Number of message parsed to JSON
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long parseMessageToJson;


    // Number of exceptions thrown while parsing a message to JSON
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long parseMessageToJsonExceptions;

    // Number of message without data source name in their JSON object
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long messagesWithoutDataSourceName;

    // Number of windows() task function calls
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long windows;

    // Number of windows() task function calls that threw an exception
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long windowsExceptions;

}
