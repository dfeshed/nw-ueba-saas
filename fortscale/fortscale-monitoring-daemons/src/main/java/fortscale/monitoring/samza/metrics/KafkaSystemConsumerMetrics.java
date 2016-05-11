package fortscale.monitoring.samza.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Created by cloudera on 5/8/16.
 */
@StatsMetricsGroupParams(name = "kafka.consumer")
public class KafkaSystemConsumerMetrics extends StatsMetricsGroup {
    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService                - The stats service to register to. Typically it is obtained via @Autowired
     *                                    of the specific service configuration class. If stats service is unavailable,
     *                                    as in most unit tests, pass a null.
     * @param statsMetricsGroupAttributes - metrics group attributes (e.g. tag list). Might be null.
     */
    public KafkaSystemConsumerMetrics(StatsService statsService, StatsMetricsGroupAttributes statsMetricsGroupAttributes) {
        super(statsService, KafkaSystemConsumerMetrics.class, statsMetricsGroupAttributes);
    }

    public void setNumberOfReconnects(long numberOfReconnects) {
        this.numberOfReconnects = numberOfReconnects;
    }

    public void setNumberOfSkippedFetchRequests(long numberOfSkippedFetchRequests) {
        this.numberOfSkippedFetchRequests = numberOfSkippedFetchRequests;
    }

    public void setNumberOfMessagesRead(long numberOfMessagesRead) {
        this.numberOfMessagesRead = numberOfMessagesRead;
    }

    public void setNumberOfBytesRead(long numberOfBytesRead) {
        this.numberOfBytesRead = numberOfBytesRead;
    }


    public void setNumberOfMessagesBehindWatermark(long numberOfMessagesBehindWatermark) {
        this.numberOfMessagesBehindWatermark = numberOfMessagesBehindWatermark;
    }

    public void setNoMoreMessages(long noMoreMessages) {
        this.noMoreMessages = noMoreMessages;
    }

    public void setBlockingPollCount(long blockingPollCount) {
        this.blockingPollCount = blockingPollCount;
    }

    public void setBlockingPollTimeoutCount(long blockingPollTimeoutCount) {
        this.blockingPollTimeoutCount = blockingPollTimeoutCount;
    }

    public void setBufferedMessageCount(long bufferedMessageCount) {
        this.bufferedMessageCount = bufferedMessageCount;
    }

    public void setOffsetChange(long offsetChange) {

        this.offsetChange = offsetChange;
    }
    public void setNumberOfHighWaterMark(long numberOfHighWaterMark) {
        this.numberOfHighWaterMark = numberOfHighWaterMark;
    }
    @StatsLongMetricParams
    long numberOfReconnects;
    @StatsLongMetricParams
    long numberOfSkippedFetchRequests;
    @StatsLongMetricParams
    long numberOfMessagesRead;
    @StatsLongMetricParams
    long numberOfBytesRead;
    @StatsLongMetricParams
    long offsetChange;
    @StatsLongMetricParams
    long numberOfMessagesBehindWatermark;
    @StatsLongMetricParams
    long noMoreMessages;
    @StatsLongMetricParams
    long blockingPollCount;
    @StatsLongMetricParams
    long blockingPollTimeoutCount;
    @StatsLongMetricParams
    long bufferedMessageCount;
    @StatsLongMetricParams
    long numberOfHighWaterMark;


    public enum TopicStatus {
        NO_MORE_MESSAGES("no-more-messages"),
        BLOCKING_POLL_COUNT("blocking-poll-count"),
        BLOCKING_POLL_TIMEOUT_COUNT("blocking-poll-timeout-count"),
        BUFFERED_MESSAGE_COUNT("buffered-message-count"),
        POLL_COUNT("poll-count");

        private final String name;

        private TopicStatus(String s) {
            name = s;
        }
        public String value(){return name;}

    }

    public enum TopicOperation {
        RECONNECTS("reconnects"),
        SKIPPED_FETCH_REQUESTS("skipped-fetch-requests"),
        MESSAGES_READ("messages-read"),
        OFFSET_CHANGE("offset-change"),
        MESSAGES_BEHIND_HIGH_WATERMARK("messages-behind-high-watermark"),
        HIGH_WATERMARK("high-watermark"),
        BYTES_READ("bytes-read"),
        TOPIC_PARTITIONS("topic-partitions");

        private final String name;

        private TopicOperation(String s) {
            name = s;
        }
        public String value(){return name;}
    }

    public static final String METRIC_NAME = "org.apache.samza.system.kafka.KafkaSystemConsumerMetric";

}
