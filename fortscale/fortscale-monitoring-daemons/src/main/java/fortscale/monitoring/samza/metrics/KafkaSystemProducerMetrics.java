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
    // Tracks the number of calls made to send in KafkaSystemProducer
    @StatsLongMetricParams
    long producerSends;
    // Tracks the number of calls made to flush in KafkaSystemProducer
    @StatsLongMetricParams
    long flushes;
    // Tracks how long the flush call takes to complete
    @StatsDoubleMetricParams(rateSeconds = 1, factor = 1.0 / 1000 / 1000 / 1000) //convert nanoseconds to seconds
    double flushSeconds;
    // Tracks the number of times the system producer retries a send request (due to RetriableException)
    @StatsLongMetricParams
    long producerRetries;
    // Tracks the number of times flush operation failed
    @StatsLongMetricParams
    long flushFailed;
    // Tracks the number of send requests that was failed by the KafkaProducer (due to unrecoverable errors)
    @StatsLongMetricParams
    long producerSendFailed;
    // Tracks the number of send requests that was successfully completed by the KafkaProducer
    @StatsLongMetricParams
    long producerSendSuccess;

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService                - The stats service to register to. Typically it is obtained via @Autowired
     *                                    of the specific service configuration class. If stats service is unavailable,
     *                                    as in most unit tests, pass a null.
     * @param statsMetricsGroupAttributes - metrics group attributes (e.g. tag list). Might be null.
     */
    public KafkaSystemProducerMetrics(StatsService statsService, StatsMetricsGroupAttributes statsMetricsGroupAttributes) {
        super(statsService, KafkaSystemProducerMetricsService.class, statsMetricsGroupAttributes);
    }

    /**
     * set the number of calls made to send in KafkaSystemProducer
     * @param producerSends
     */
    public void setProducerSends(long producerSends) {
        this.producerSends = producerSends;
    }

    /**
     * set the number of calls made to flush in KafkaSystemProducer
     * @param flushes
     */
    public void setFlushes(long flushes) {
        this.flushes = flushes;
    }

    /**
     * set how long the flush call takes to complete
     * @param flushSeconds
     */
    public void setFlushSeconds(double flushSeconds) {this.flushSeconds = flushSeconds;}

    /**
     * set the number of times the system producer retries a send request (due to RetriableException)
     * @param producerRetries
     */
    public void setProducerRetries(long producerRetries) {
        this.producerRetries = producerRetries;
    }

    /**
     * set the number of times flush operation failed
     * @param flushFailed
     */
    public void setFlushFailed(long flushFailed) {
        this.flushFailed = flushFailed;
    }

    /**
     * set the number of send requests that was failed by the KafkaProducer (due to unrecoverable errors)
     * @param producerSendFailed
     */
    public void setProducerSendFailed(long producerSendFailed) {
        this.producerSendFailed = producerSendFailed;
    }

    /**
     * set the number of send requests that was successfully completed by the KafkaProducer
     * @param producerSendSuccess
     */
    public void setProducerSendSuccess(long producerSendSuccess) {
        this.producerSendSuccess = producerSendSuccess;
    }


}
