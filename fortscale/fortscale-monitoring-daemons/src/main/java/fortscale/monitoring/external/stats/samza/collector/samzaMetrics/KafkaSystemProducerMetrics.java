package fortscale.monitoring.external.stats.samza.collector.samzaMetrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Created by cloudera on 5/8/16.
 */
@StatsMetricsGroupParams(name = "samza.task.producer")
public class KafkaSystemProducerMetrics extends StatsMetricsGroup {
    public static final String METRIC_NAME = "org.apache.samza.system.kafka.KafkaSystemProducerMetrics";

    @StatsDoubleMetricParams (factor = 1.0/1000)
    private double flushSeconds;
    @StatsLongMetricParams (rateSeconds = 1)
    private long messagesSentFailures;
    @StatsLongMetricParams (rateSeconds = 1)
    private long flushes;
    @StatsLongMetricParams (rateSeconds = 1)
    private long flushesFailures;
    @StatsLongMetricParams (rateSeconds = 1)
    private long messagesSent;
    @StatsLongMetricParams (rateSeconds = 1)
    private long retries;

    public void setFlushSeconds(double flushSeconds) {
        this.flushSeconds = flushSeconds;
    }

    public void setMessagesSentFailures(long messagesSentFailures) {
        this.messagesSentFailures = messagesSentFailures;
    }

    public void setFlushes(long flushes) {
        this.flushes = flushes;
    }

    public void setFlushesFailures(long flushesFailures) {
        this.flushesFailures = flushesFailures;
    }

    public void setMessagesSent(long messagesSent) {
        this.messagesSent = messagesSent;
    }

    public void setRetries(long retries) {
        this.retries = retries;
    }




    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService                - The stats service to register to. Typically it is obtained via @Autowired
     *                                    of the specific service configuration class. If stats service is unavailable,
     *                                    as in most unit tests, pass a null.
     * @param statsMetricsGroupAttributes - metrics group attributes (e.g. tag list). Might be null.
     */
    public KafkaSystemProducerMetrics(StatsService statsService, StatsMetricsGroupAttributes statsMetricsGroupAttributes) {
        super(statsService, KafkaSystemProducerMetrics.class, statsMetricsGroupAttributes);
    }

    public enum Operation {
        FLUSH_MS("flush-ms"),
        SEND_FAILED("send-failed"),
        SEND_SUCCESS("send-success"),
        FLUSH_FAILED("flush-failed"),
        FLUSHES("flushes"),
        PRODUCER_SENDS("producer-sends"),
        PRODUCER_RETRIES("producer-retries");

        private final String name;
        private Operation(String s) {
            name = s;
        }
        public String value() {
            return name;
        }
    }


}
