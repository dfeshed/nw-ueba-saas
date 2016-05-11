package fortscale.monitoring.samza.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Created by cloudera on 5/8/16.
 */
@StatsMetricsGroupParams(name = "samza.taskoffsets")
public class TaskInstanceOffsetsMetrics extends StatsMetricsGroup {
    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService                - The stats service to register to. Typically it is obtained via @Autowired
     *                                    of the specific service configuration class. If stats service is unavailable,
     *                                    as in most unit tests, pass a null.
     * @param statsMetricsGroupAttributes - metrics group attributes (e.g. tag list). Might be null.
     */
    public TaskInstanceOffsetsMetrics(StatsService statsService, StatsMetricsGroupAttributes statsMetricsGroupAttributes) {
        super(statsService, TaskInstanceOffsetsMetrics.class, statsMetricsGroupAttributes);
    }

    public void setTopicOffset(long topicOffset) {
        this.topicOffset = topicOffset;
    }


    @StatsLongMetricParams
    long topicOffset;


    public enum TaskOperation {
        OFFSET("offset"); //topicOffset


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
