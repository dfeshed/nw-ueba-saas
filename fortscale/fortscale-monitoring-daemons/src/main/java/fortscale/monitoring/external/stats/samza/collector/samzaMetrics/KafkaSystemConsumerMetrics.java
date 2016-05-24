package fortscale.monitoring.external.stats.samza.collector.samzaMetrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * consumer metrics
 */
@StatsMetricsGroupParams(name = "samza.task.topic.consumer")
public class KafkaSystemConsumerMetrics extends StatsMetricsGroup {

    @StatsLongMetricParams(rateSeconds = 1)
    long reconnects;
    @StatsLongMetricParams(rateSeconds = 1)
    long skippedFetchRequests;
    @StatsLongMetricParams(rateSeconds = 1)
    long messagesRead;
    @StatsLongMetricParams(rateSeconds = 1)
    long bytesRead;
    @StatsLongMetricParams(rateSeconds = 1)
    long messagesBehindWatermark;
    @StatsLongMetricParams
    long noMoreMessages;
    @StatsLongMetricParams(rateSeconds = 1)
    long blockingPoll;
    @StatsLongMetricParams(rateSeconds = 1)
    long blockingPollTimeout;
    @StatsLongMetricParams(rateSeconds = 1)
    long bufferedMessages;
    @StatsLongMetricParams(rateSeconds = 1)
    long highWaterMark;

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService - The stats service to register to. Typically it is obtained via @Autowired
     *                     of the specific service configuration class. If stats service is unavailable,
     *                     as in most unit tests, pass a null.
     * @param job          - Samza job name
     * @param topic        - topic name
     */
    public KafkaSystemConsumerMetrics(StatsService statsService, String job, String topic) {
        super(statsService, KafkaSystemConsumerMetrics.class, new StatsMetricsGroupAttributes() {{
            addTag("job", job);
            addTag("topic", topic);
        }});
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

    public void setBufferedMessages(long bufferedMessages) {
        this.bufferedMessages = bufferedMessages;
    }


    public void setHighWaterMark(long highWaterMark) {
        this.highWaterMark = highWaterMark;
    }

    public long getReconnects() {
        return reconnects;
    }

    public long getSkippedFetchRequests() {
        return skippedFetchRequests;
    }

    public long getMessagesRead() {
        return messagesRead;
    }

    public long getBytesRead() {
        return bytesRead;
    }

    public long getMessagesBehindWatermark() {
        return messagesBehindWatermark;
    }

    public long getNoMoreMessages() {
        return noMoreMessages;
    }

    public long getBlockingPoll() {
        return blockingPoll;
    }

    public long getBlockingPollTimeout() {
        return blockingPollTimeout;
    }

    public long getBufferedMessages() {
        return bufferedMessages;
    }

    public long getHighWaterMark() {
        return highWaterMark;
    }
}
