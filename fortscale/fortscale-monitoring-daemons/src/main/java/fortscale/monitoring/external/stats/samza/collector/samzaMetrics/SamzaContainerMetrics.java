package fortscale.monitoring.external.stats.samza.collector.samzaMetrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * samza container stats metrics
 */
@StatsMetricsGroupParams(name = "samza.container")
public class SamzaContainerMetrics extends StatsMetricsGroup {

    @StatsDoubleMetricParams(rateSeconds = 1)
    long commit;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long window;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long process;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long send;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long processEnvelopes;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long processNullEnvelopes;
    @StatsDoubleMetricParams// todo: check: is it a comulative value? - if so rate is needed
    double chooseSeconds;
    @StatsDoubleMetricParams
    double windowSeconds;
    @StatsDoubleMetricParams
    double processSeconds;
    @StatsDoubleMetricParams
    double commitSeconds;

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
            setManualUpdateMode(true);
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

    public long getCommit() {
        return commit;
    }

    public long getWindow() {
        return window;
    }

    public long getProcess() {
        return process;
    }

    public long getSend() {
        return send;
    }

    public long getProcessEnvelopes() {
        return processEnvelopes;
    }

    public long getProcessNullEnvelopes() {
        return processNullEnvelopes;
    }

    public double getChooseSeconds() {
        return chooseSeconds;
    }

    public double getWindowSeconds() {
        return windowSeconds;
    }

    public double getProcessSeconds() {
        return processSeconds;
    }

    public double getCommitSeconds() {
        return commitSeconds;
    }
}
