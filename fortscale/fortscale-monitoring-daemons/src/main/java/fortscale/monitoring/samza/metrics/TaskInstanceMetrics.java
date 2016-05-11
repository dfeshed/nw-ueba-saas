package fortscale.monitoring.samza.metrics;

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

    public void setNumberOfCommitCalls(long numberOfCommitCalls) {
        this.numberOfCommitCalls = numberOfCommitCalls;
    }

    public void setNumberOfWindowCalls(long numberOfWindowCalls) {
        this.numberOfWindowCalls = numberOfWindowCalls;
    }

    public void setNumberOfProcessCalls(long numberOfProcessCalls) {
        this.numberOfProcessCalls = numberOfProcessCalls;
    }

    public void setNumberOfSendCalls(long numberOfSendCalls) {
        this.numberOfSendCalls = numberOfSendCalls;
    }

    public void setNumberOfFlushCalls(long numberOfFlushCalls) {
        this.numberOfFlushCalls = numberOfFlushCalls;
    }

    public void setNumberOfMessagesSent(long numberOfMessagesSent) {
        this.numberOfMessagesSent = numberOfMessagesSent;
    }



    @StatsLongMetricParams
    long numberOfCommitCalls;
    @StatsLongMetricParams
    long numberOfWindowCalls;
    @StatsLongMetricParams
    long numberOfProcessCalls;
    @StatsLongMetricParams
    long numberOfSendCalls;
    @StatsLongMetricParams
    long numberOfFlushCalls;
    @StatsLongMetricParams
    long numberOfMessagesSent;


    public enum TaskOperation {
        COMMITS("commit-calls"), //numberOfCommitCalls
        WINDOWS("window-calls"),//numberOfWindowCalls
        PROCESSES("process-calls"),//numberOfProcessCalls
        SENDS("send-calls"),//numberOfSendCalls
        FLUSH_CALLS("flush-calls"),//numberOfFlushCalls
        MESSAGES_SENT("messages-sent");//numberOfMessagesSent

        private final String name;

        private TaskOperation(String s) {
            name = s;
        }

        public String value() {
            return name;
        }
    }

    public static final String METRIC_NAME = "org.apache.samza.container.TaskInstanceMetrics";

}
