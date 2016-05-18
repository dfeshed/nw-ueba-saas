package fortscale.monitoring.samza.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Created by cloudera on 5/8/16.
 */
@StatsMetricsGroupParams(name = "kafka.task.consumer")
public class KafkaSystemConsumerMetrics extends StatsMetricsGroup {
    public static final String METRIC_NAME = "org.apache.samza.system.kafka.KafkaSystemConsumerMetrics";

    @StatsLongMetricParams (rateSeconds = 1)
    long reconnects;
    @StatsLongMetricParams (rateSeconds = 1)
    long skippedFetchRequests;
    @StatsLongMetricParams (rateSeconds = 1)
    long messagesRead;
    @StatsLongMetricParams (rateSeconds = 1)
    long bytesRead;
    @StatsLongMetricParams (rateSeconds = 1) //todo: is offest needed? this vs bytes read
    long offsetChange;
    @StatsLongMetricParams (rateSeconds = 1)
    long messagesBehindWatermark;
    @StatsLongMetricParams (rateSeconds = 1)
    long noMoreMessages;
    @StatsLongMetricParams (rateSeconds = 1)
    long blockingPoll;
    @StatsLongMetricParams (rateSeconds = 1)
    long blockingPollTimeout;
    @StatsLongMetricParams (rateSeconds = 1)
    long bufferedMessage;
    @StatsLongMetricParams (rateSeconds = 1)
    long highWaterMark;

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

    public void setReconnects(long reconnects) {
        this.reconnects = reconnects;
    }

    public void setSkippedFetchRequests(long skippedFetchRequests) {
        this.skippedFetchRequests = skippedFetchRequests;
    }

    public void setMessagesRead(long messagesRead) {
        this.messagesRead = messagesRead;
    }

    public void setBytesRead(long bytesRead) {
        this.bytesRead = bytesRead;
    }


    public void setMessagesBehindWatermark(long messagesBehindWatermark) {
        this.messagesBehindWatermark = messagesBehindWatermark;
    }

    public void setNoMoreMessages(long noMoreMessages) {
        this.noMoreMessages = noMoreMessages;
    }

    public void setBlockingPoll(long blockingPoll) {
        this.blockingPoll = blockingPoll;
    }

    public void setBlockingPollTimeout(long blockingPollTimeout) {
        this.blockingPollTimeout = blockingPollTimeout;
    }

    public void setBufferedMessage(long bufferedMessage) {
        this.bufferedMessage = bufferedMessage;
    }

    public void setOffsetChange(long offsetChange) {

        this.offsetChange = offsetChange;
    }
    public void setHighWaterMark(long highWaterMark) {
        this.highWaterMark = highWaterMark;
    }



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


}
