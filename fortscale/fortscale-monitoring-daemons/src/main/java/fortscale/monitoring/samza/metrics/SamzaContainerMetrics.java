package fortscale.monitoring.samza.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Created by cloudera on 5/8/16.
 */
@StatsMetricsGroupParams(name = "samza.container")

public class SamzaContainerMetrics extends StatsMetricsGroup {
    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService                - The stats service to register to. Typically it is obtained via @Autowired
     *                                    of the specific service configuration class. If stats service is unavailable,
     *                                    as in most unit tests, pass a null.
     * @param statsMetricsGroupAttributes - metrics group attributes (e.g. tag list). Might be null.
     */
    public SamzaContainerMetrics(StatsService statsService, StatsMetricsGroupAttributes statsMetricsGroupAttributes) {
        super(statsService, SamzaContainerMetrics.class, statsMetricsGroupAttributes);
    }

    public void setCommitCalls(long commitCalls) {
        this.commitCalls = commitCalls;
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

    public void setProcessEnvelopes(long processEnvelopes) {
        this.processEnvelopes = processEnvelopes;
    }

    public void setProcessNullEnvelopes(long processNullEnvelopes) {
        this.processNullEnvelopes = processNullEnvelopes;
    }

    public void setChooseSeconds(double chooseSeconds) {
        this.chooseSeconds = chooseSeconds;
    }

    public void setWindowSeconds(double windowSeconds) {
        this.windowSeconds = windowSeconds;
    }

    public void setCommitSeconds(double commitSeconds) {
        this.commitSeconds = commitSeconds;
    }

    public void setProcessSeconds(double processSeconds) {
        this.processSeconds = processSeconds;
    }

    @StatsLongMetricParams (rateSeconds = 1)
    long commitCalls;
    @StatsLongMetricParams (rateSeconds = 1)
    long windowCalls;
    @StatsLongMetricParams (rateSeconds = 1)
    long processCalls;
    @StatsLongMetricParams (rateSeconds = 1)
    long sendCalls;
    @StatsLongMetricParams (rateSeconds = 1)
    long processEnvelopes;
    @StatsLongMetricParams (rateSeconds = 1)
    long processNullEnvelopes;
    @StatsDoubleMetricParams (factor = 1.0/1000)
    double chooseSeconds;
    @StatsDoubleMetricParams (factor = 1.0/1000)
    double windowSeconds;
    @StatsDoubleMetricParams (factor = 1.0/1000)
    double processSeconds;
    @StatsDoubleMetricParams (factor = 1.0/1000)
    double commitSeconds;

    public enum JobContainerOperation {
        COMMITS("commit-calls"), //commitsCalls
        WINDOWS("window-calls"),//windowCalls
        PROCESSES("process-calls"),//processCalls
        SENDS("send-calls"),//sendCalls
        ENVELOPES("process-envelopes"),//processEnvelopes
        NULL_ENVELOPES("process-null-envelopes"),//processNullEnvelopes
        CHOOSE_MS("choose-ms"), //chooseSeconds
        WINDOW_MS("window-ms"),//windowSeconds
        PROCESS_MS("process-ms"),//processSeconds
        COMMIT_MS("commit-ms"); //commitSeconds

        private final String name;

        private JobContainerOperation(String s) {
            name = s;
        }

        public String value() {
            return name;
        }
    }

    public static final String METRIC_NAME = "org.apache.samza.container.SamzaContainerMetrics";

}
