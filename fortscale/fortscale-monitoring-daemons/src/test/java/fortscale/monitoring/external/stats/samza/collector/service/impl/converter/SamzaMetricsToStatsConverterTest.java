package fortscale.monitoring.external.stats.samza.collector.service.impl.converter;

import fortscale.monitoring.external.stats.samza.collector.samzaMetrics.*;
import fortscale.monitoring.external.stats.samza.collector.service.stats.SamzaMetricCollectorMetrics;
import fortscale.utils.samza.metricMessageModels.MetricMessage;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static fortscale.monitoring.external.stats.Util.CollectorsUtil.entryValueToLong;

public class SamzaMetricsToStatsConverterTest {

    MetricMessageUtil metricMessageUtil;
    MetricMessage metricMessage;
    SamzaMetricCollectorMetrics samzaMetricCollectorMetrics;

    @Before
    public void init() throws IOException {
        metricMessageUtil = new MetricMessageUtil();
        metricMessage = metricMessageUtil.getMetricMessage();
        samzaMetricCollectorMetrics = new SamzaMetricCollectorMetrics(null);
    }

    @Test
    public void SamzaContainerToStatsConverterTest() throws IOException {
        SamzaContainerToStatsConverter converter = new SamzaContainerToStatsConverter(null, samzaMetricCollectorMetrics);

        Map<String, Object> metricEntries = metricMessage.getMetrics().getAdditionalProperties().get(SamzaContainerToStatsConverter.METRIC_NAME);
        converter.convert(metricEntries, metricMessage.getHeader().getJobName(), metricMessage.getHeader().getTime(), metricMessage.getHeader().getHost());

        SamzaContainerMetrics statsMetrics = ((SamzaContainerMetrics) (converter.metricsMap.get("vpnsession-events-filter")));
        Assert.assertEquals(statsMetrics.getCommit(), entryValueToLong(metricEntries.get("commit-calls")));
        Assert.assertEquals(statsMetrics.getCommitSeconds(), (double) metricEntries.get("commit-ms") / 1000, 0);
        Assert.assertEquals(statsMetrics.getProcessNullEnvelopes(), entryValueToLong(metricEntries.get("process-null-envelopes")));
        Assert.assertEquals(statsMetrics.getWindowSeconds(), (double) metricEntries.get("window-ms") / 1000, 0);
        Assert.assertEquals(statsMetrics.getProcessEnvelopes(), entryValueToLong(metricEntries.get("process-envelopes")));
        Assert.assertEquals(statsMetrics.getWindow(), entryValueToLong(metricEntries.get("window-calls")));
        Assert.assertEquals(statsMetrics.getSend(), entryValueToLong(metricEntries.get("send-calls")));
        Assert.assertEquals(statsMetrics.getProcesses(), entryValueToLong(metricEntries.get("process-calls")));
        Assert.assertEquals(statsMetrics.getChooseSeconds(), (double) metricEntries.get("choose-ms") / 1000, 0);
        Assert.assertEquals(statsMetrics.getProcessSeconds(), (double) metricEntries.get("process-ms") / 1000, 0);
    }

    @Test
    public void KafkaSystemProducerToStatsConverterTest() throws IOException {
        KafkaSystemProducerToStatsConverter converter = new KafkaSystemProducerToStatsConverter(null, samzaMetricCollectorMetrics);

        Map<String, Object> metricEntries = metricMessage.getMetrics().getAdditionalProperties().get(KafkaSystemProducerToStatsConverter.METRIC_NAME);
        converter.convert(metricEntries, metricMessage.getHeader().getJobName(), metricMessage.getHeader().getTime(), metricMessage.getHeader().getHost());

        KafkaSystemProducerMetrics statsMetrics = ((KafkaSystemProducerMetrics) (converter.metricsMap.get("vpnsession-events-filter")));

        Assert.assertEquals(statsMetrics.getFlushes(), entryValueToLong(metricEntries.get("kafka-flushes")));
        Assert.assertEquals(statsMetrics.getFlushesFailures(), entryValueToLong(metricEntries.get("kafka-flush-failed")));
        Assert.assertEquals(statsMetrics.getFlushSeconds(), (double) (metricEntries.get("kafka-flush-ms")) / 1000, 0);
        Assert.assertEquals(statsMetrics.getMessagesSent(), entryValueToLong(metricEntries.get("kafka-producer-send-success")));
        Assert.assertEquals(statsMetrics.getMessagesSentFailures(), entryValueToLong(metricEntries.get("kafka-producer-send-failed")));
        Assert.assertEquals(statsMetrics.getRetries(), entryValueToLong(metricEntries.get("kafka-producer-retries")));
    }

    @Test
    public void KafkaSystemConsumerToStatsConverterTest() {
        KafkaSystemConsumerToStatsConverter converter = new KafkaSystemConsumerToStatsConverter(null, samzaMetricCollectorMetrics);

        Map<String, Object> metricEntries = metricMessage.getMetrics().getAdditionalProperties().get(KafkaSystemConsumerToStatsConverter.METRIC_NAME);
        converter.convert(metricEntries, metricMessage.getHeader().getJobName(), metricMessage.getHeader().getTime(), metricMessage.getHeader().getHost());

        MultiKey multiKey = new MultiKey("vpnsession-events-filter", "fortscale-vpn-enriched-after-write");
        KafkaSystemConsumerMetrics statsMetrics = ((KafkaSystemConsumerMetrics) (converter.metricsMap.get(multiKey)));

        Assert.assertEquals(statsMetrics.getBlockingPoll(), entryValueToLong(metricEntries.get("blocking-poll-count-SystemStreamPartition [kafka, fortscale-vpn-enriched-after-write, 0]")));
        Assert.assertEquals(statsMetrics.getBlockingPollTimeout(), entryValueToLong(metricEntries.get("blocking-poll-timeout-count-SystemStreamPartition [kafka, fortscale-vpn-enriched-after-write, 0]")));
        Assert.assertEquals(statsMetrics.getBufferedMessages(), entryValueToLong(metricEntries.get("buffered-message-count-SystemStreamPartition [kafka, fortscale-vpn-enriched-after-write, 0]")));
        Assert.assertEquals(statsMetrics.getBytesRead(), entryValueToLong(metricEntries.get("kafka-fortscale-vpn-enriched-after-write-0-bytes-read")));
        Assert.assertEquals(statsMetrics.getHighWaterMark(), entryValueToLong(metricEntries.get("kafka-fortscale-vpn-enriched-after-write-0-high-watermark")));
        Assert.assertEquals(statsMetrics.getNoMoreMessages(), entryValueToLong(metricEntries.get("no-more-messages-SystemStreamPartition [kafka, fortscale-vpn-enriched-after-write, 0]")));
        Assert.assertEquals(statsMetrics.getMessagesBehindWatermark(), entryValueToLong(metricEntries.get("kafka-fortscale-vpn-enriched-after-write-0-messages-behind-high-watermark")));
        Assert.assertEquals(statsMetrics.getMessagesRead(), entryValueToLong(metricEntries.get("kafka-fortscale-vpn-enriched-after-write-0-messages-read")));
    }

    @Test
    public void KeyValueChangelogTopicToStatsConverterTest() {
        KeyValueChangelogTopicToStatsConverter converter = new KeyValueChangelogTopicToStatsConverter(null, samzaMetricCollectorMetrics);

        Map<String, Object> metricEntries = metricMessage.getMetrics().getAdditionalProperties().get(KeyValueChangelogTopicToStatsConverter.METRIC_NAME);
        converter.convert(metricEntries, metricMessage.getHeader().getJobName(), metricMessage.getHeader().getTime(), metricMessage.getHeader().getHost());

        MultiKey multiKey = new MultiKey("vpnsession-events-filter", "user-tag-service-cache");

        KeyValueChangelogTopicMetrics statsMetrics = ((KeyValueChangelogTopicMetrics) (converter.metricsMap.get(multiKey)));

        Assert.assertEquals(statsMetrics.getDeletes(), entryValueToLong(metricEntries.get("user-tag-service-cache-deletes")));
        Assert.assertEquals(statsMetrics.getFlushes(), entryValueToLong(metricEntries.get("user-tag-service-cache-flushes")));
        Assert.assertEquals(statsMetrics.getQueries(), entryValueToLong(metricEntries.get("user-tag-service-cache-gets")));
        Assert.assertEquals(statsMetrics.getRangeQueries(), entryValueToLong(metricEntries.get("user-tag-service-cache-ranges")));
        Assert.assertEquals(statsMetrics.getRecordsInStore(), entryValueToLong(metricEntries.get("user-tag-service-cache-alls")));
        Assert.assertEquals(statsMetrics.getWrites(), entryValueToLong(metricEntries.get("user-tag-service-cache-puts")));
    }

    @Test
    public void KeyValueStorageMetricsToStatsConverterTest() {
        KeyValueStorageMetricsToStatsConverter converter = new KeyValueStorageMetricsToStatsConverter(null, samzaMetricCollectorMetrics);

        Map<String, Object> metricEntries = metricMessage.getMetrics().getAdditionalProperties().get(KeyValueStorageMetricsToStatsConverter.METRIC_NAME);
        converter.convert(metricEntries, metricMessage.getHeader().getJobName(), metricMessage.getHeader().getTime(), metricMessage.getHeader().getHost());

        MultiKey multiKey = new MultiKey("vpnsession-events-filter", "hdfs-write-wamescore");

        KeyValueStorageMetrics statsMetrics = ((KeyValueStorageMetrics) (converter.metricsMap.get(multiKey)));

        Assert.assertEquals(statsMetrics.getDeletes(), entryValueToLong(metricEntries.get("hdfs-write-wamescore-deletes")));
        Assert.assertEquals(statsMetrics.getFlushes(), entryValueToLong(metricEntries.get("hdfs-write-wamescore-flushes")));
        Assert.assertEquals(statsMetrics.getQueries(), entryValueToLong((metricEntries.get("hdfs-write-wamescore-gets"))));
        Assert.assertEquals(statsMetrics.getRangeQueries(), entryValueToLong(metricEntries.get("hdfs-write-wamescore-ranges")));
        Assert.assertEquals(statsMetrics.getRecordsInStore(), entryValueToLong(metricEntries.get("hdfs-write-wamescore-alls")));
        Assert.assertEquals(statsMetrics.getWrites(), entryValueToLong(metricEntries.get("hdfs-write-wamescore-puts")));
        Assert.assertEquals(statsMetrics.getMessagesRestored(), entryValueToLong(metricEntries.get("hdfs-write-wamescore-messages-restored")));
        Assert.assertEquals(statsMetrics.getMessagesRestored(), entryValueToLong(metricEntries.get("hdfs-write-wamescore-messages-bytes")));
    }

    @Test
    public void KeyValueStoreMetricsToStatsConverterTest() {
        KeyValueStoreMetricsToStatsConverter converter = new KeyValueStoreMetricsToStatsConverter(null, samzaMetricCollectorMetrics);

        Map<String, Object> metricEntries = metricMessage.getMetrics().getAdditionalProperties().get(KeyValueStoreMetricsToStatsConverter.METRIC_NAME);
        converter.convert(metricEntries, metricMessage.getHeader().getJobName(), metricMessage.getHeader().getTime(), metricMessage.getHeader().getHost());

        MultiKey multiKey = new MultiKey("vpnsession-events-filter", "hdfs-write-wamescore");
        KeyValueStoreMetrics statsMetrics = ((KeyValueStoreMetrics) (converter.metricsMap.get(multiKey)));

        Assert.assertEquals(statsMetrics.getDeletes(), entryValueToLong(metricEntries.get("hdfs-write-wamescore-deletes")));
        Assert.assertEquals(statsMetrics.getFlushes(), entryValueToLong(metricEntries.get("hdfs-write-wamescore-flushes")));
        Assert.assertEquals(statsMetrics.getQueries(), entryValueToLong(metricEntries.get("hdfs-write-wamescore-gets")));
        Assert.assertEquals(statsMetrics.getRangeQueries(), entryValueToLong(metricEntries.get("hdfs-write-wamescore-ranges")));
        Assert.assertEquals(statsMetrics.getRecordsInStore(), entryValueToLong(metricEntries.get("hdfs-write-wamescore-alls")));
        Assert.assertEquals(statsMetrics.getWrites(), entryValueToLong(metricEntries.get("hdfs-write-wamescore-puts")));
        Assert.assertEquals(statsMetrics.getBytesWritten(), entryValueToLong(metricEntries.get("hdfs-write-wamescore-bytes-written")));
        Assert.assertEquals(statsMetrics.getBytesRead(), entryValueToLong(metricEntries.get("hdfs-write-wamescore-bytes-read")));
    }
}
