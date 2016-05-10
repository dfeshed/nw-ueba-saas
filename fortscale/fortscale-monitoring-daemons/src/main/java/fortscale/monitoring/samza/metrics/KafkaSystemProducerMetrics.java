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
@StatsMetricsGroupParams(name = "kafka.producer")
public class KafkaSystemProducerMetrics extends StatsMetricsGroup {

    public void setFlushMillis(double flushMillis) {
        this.flushMillis = flushMillis;
    }

    public void setNumberOfSendFailed(long numberOfSendFailed) {
        this.numberOfSendFailed = numberOfSendFailed;
    }

    public void setNumberOfSendSuccess(long numberOfSendSuccess) {
        this.numberOfSendSuccess = numberOfSendSuccess;
    }

    public void setNumberOfFlushes(long numberOfFlushes) {
        this.numberOfFlushes = numberOfFlushes;
    }

    public void setNumberOfFlushFailed(long numberOfFlushFailed) {
        this.numberOfFlushFailed = numberOfFlushFailed;
    }

    public void setNumberOfProducerSends(long numberOfProducerSends) {
        this.numberOfProducerSends = numberOfProducerSends;
    }

    public void setNumberOfProducerRetries(long numberOfProducerRetries) {
        this.numberOfProducerRetries = numberOfProducerRetries;
    }

    @StatsDoubleMetricParams
    private double flushMillis;
    @StatsLongMetricParams
    private long numberOfSendFailed;
    @StatsLongMetricParams
    private long numberOfSendSuccess;
    @StatsLongMetricParams
    private long numberOfFlushes;
    @StatsLongMetricParams
    private long numberOfFlushFailed;
    @StatsLongMetricParams
    private long numberOfProducerSends;
    @StatsLongMetricParams
    private long numberOfProducerRetries;


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

    public static final String METRIC_NAME = "org.apache.samza.system.kafka.KafkaSystemProducerMetrics";

}
