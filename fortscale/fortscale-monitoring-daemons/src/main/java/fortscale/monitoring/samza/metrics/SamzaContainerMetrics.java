package fortscale.monitoring.samza.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;

/**
 * Created by cloudera on 5/8/16.
 */
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

    public void setNumberOfProcessEnvelopes(long numberOfProcessEnvelopes) {
        this.numberOfProcessEnvelopes = numberOfProcessEnvelopes;
    }

    public void setNumberOfProcessNullEnvelopes(long numberOfProcessNullEnvelopes) {
        this.numberOfProcessNullEnvelopes = numberOfProcessNullEnvelopes;
    }

    public void setNumberOfChooseMillis(long numberOfChooseMillis) {
        this.numberOfChooseMillis = numberOfChooseMillis;
    }

    public void setNumberOfWindowMillis(long numberOfWindowMillis) {
        this.numberOfWindowMillis = numberOfWindowMillis;
    }

    public void setNumberOfCommitMillis(long numberOfCommitMillis) {
        this.numberOfCommitMillis = numberOfCommitMillis;
    }

    public void setNumberOfProcessMillis(long numberOfProcessMillis) {
        this.numberOfProcessMillis = numberOfProcessMillis;
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
    long numberOfProcessEnvelopes;
    @StatsLongMetricParams
    long numberOfProcessNullEnvelopes;
    @StatsLongMetricParams
    long numberOfChooseMillis;
    @StatsLongMetricParams
    long numberOfWindowMillis;
    @StatsLongMetricParams
    long numberOfProcessMillis;
    @StatsLongMetricParams
    long numberOfCommitMillis;

    public enum JobContainerOperation {
        COMMITS("commit-calls"), //numberOfCommitCalls
        WINDOWS("window-calls"),//numberOfWindowCalls
        PROCESSES("process-calls"),//numberOfProcessCalls
        SENDS("send-calls"),//numberOfSendCalls
        ENVELOPES("process-envelopes"),//numberOfProcessEnvelopes
        NULL_ENVELOPES("process-null-envelopes"),//numberOfProcessNullEnvelopes
        CHOOSE_MS("choose-ms"), //numberOfChooseMillis
        WINDOW_MS("window-ms"),//numberOfWindowMillis
        PROCESS_MS("process-ms"),//numberOfProcessMillis
        COMMIT_MS("commit-ms"); //numberOfCommitMillis

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
