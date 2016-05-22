package fortscale.monitoring.external.stats.samza.collector.samzaMetrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Created by cloudera on 5/8/16.
 */
@StatsMetricsGroupParams(name = "samza.task")
public class TaskInstanceMetrics extends StatsMetricsGroup {
    public static final String METRIC_NAME = "org.apache.samza.container.TaskInstanceMetrics";

    public enum TaskOperation {
        COMMITS("commit-calls"), //commitsCalls
        WINDOWS("window-calls"),//windowCalls
        PROCESSES("process-calls"),//processCalls
        SENDS("send-calls"),//sendCalls
        FLUSH_CALLS("flush-calls"),//flushCalls
        MESSAGES_SENT("messages-sent");//messagesSent

        private final String name;

        private TaskOperation(String s) {
            name = s;
        }

        public String value() {
            return name;
        }
    }


    @StatsLongMetricParams(rateSeconds = 1)
    long commitsCalls;
    @StatsLongMetricParams(rateSeconds = 1)
    long windowCalls;
    @StatsLongMetricParams(rateSeconds = 1)
    long processCalls;
    @StatsLongMetricParams(rateSeconds = 1)
    long sendCalls;
    @StatsLongMetricParams(rateSeconds = 1)
    long flushCalls;
    @StatsLongMetricParams(rateSeconds = 1)
    long messagesSent;

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService                - The stats service to register to. Typically it is obtained via @Autowired
     *                                    of the specific service configuration class. If stats service is unavailable,
     *                                    as in most unit tests, pass a null.
     * @param statsMetricsGroupAttributes - metrics group attributes (e.g. tag list). Might be null.
     */
    public TaskInstanceMetrics(StatsService statsService, StatsMetricsGroupAttributes statsMetricsGroupAttributes) {
        super(statsService, TaskInstanceMetrics.class, statsMetricsGroupAttributes);
    }

    public void setCommitsCalls(long commitsCalls) {
        this.commitsCalls = commitsCalls;
    }

    public void setWindowCalls(long windowCalls) {
        this.windowCalls = windowCalls;
    }

    public void setProcessCalls(long processCalls) {
        this.processCalls = processCalls;
    }

    public void setSendCalls(long sendCalls) {
        this.sendCalls = sendCalls;
    }

    public void setFlushCalls(long flushCalls) {
        this.flushCalls = flushCalls;
    }

    public void setMessagesSent(long messagesSent) {
        this.messagesSent = messagesSent;
    }



}
