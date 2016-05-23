package fortscale.monitoring.external.stats.samza.collector.samzaMetrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * samza container stats metircs
 */
@StatsMetricsGroupParams(name = "samza.container")

public class SamzaContainerMetrics extends StatsMetricsGroup {
    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService - The stats service to register to. Typically it is obtained via @Autowired
     *                     of the specific service configuration class. If stats service is unavailable,
     *                     as in most unit tests, pass a null.
     * @param job          - samza job
     */
    public SamzaContainerMetrics(StatsService statsService, String job) {
        super(statsService, SamzaContainerMetrics.class, new StatsMetricsGroupAttributes() {{
            addTag("job", job);
        }});
    }

    public void setCommit(long commit) {
        this.commit = commit;
    }

    public void setWindow(long window) {
        this.window = window;
    }

    public void setProcess(long process) {
        this.process = process;
    }

    public void setSend(long send) {
        this.send = send;
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

    @StatsLongMetricParams(rateSeconds = 1)
    long commit;
    @StatsLongMetricParams(rateSeconds = 1)
    long window;
    @StatsLongMetricParams(rateSeconds = 1)
    long process;
    @StatsLongMetricParams(rateSeconds = 1)
    long send;
    @StatsLongMetricParams(rateSeconds = 1)
    long processEnvelopes;
    @StatsLongMetricParams(rateSeconds = 1)
    long processNullEnvelopes;
    @StatsDoubleMetricParams// todo: check: is it a comulative value? - if so rate is needed
            double chooseSeconds;
    @StatsDoubleMetricParams
    double windowSeconds;
    @StatsDoubleMetricParams
    double processSeconds;
    @StatsDoubleMetricParams
    double commitSeconds;

    public enum JobContainerOperation {
        COMMITS("commit-calls"), //commit
        WINDOWS("window-calls"),//window
        PROCESSES("process-calls"),//process
        SENDS("send-calls"),//send
        ENVELOPES("process-envelopes"),//processEnvelopes
        NULL_ENVELOPES("process-null-envelopes"),//processNullEnvelopes
        CHOOSE_MS("choose-ms"), //chooseSeconds
        WINDOW_MS("window-ms"),//windowSeconds
        PROCESS_MS("process-ms"),//processSeconds
        COMMIT_MS("commit-ms"); //commitSeconds

        private final String name;

        JobContainerOperation(String s) {
            name = s;
        }

        public String value() {
            return name;
        }
    }

    public static final String METRIC_NAME = "org.apache.samza.container.SamzaContainerMetrics";

}
