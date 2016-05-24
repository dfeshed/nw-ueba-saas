package fortscale.monitoring.external.stats.samza.collector.service.impl.converter;

import fortscale.monitoring.external.stats.samza.collector.samzaMetrics.KafkaSystemProducerMetrics;
import fortscale.monitoring.external.stats.samza.collector.samzaMetrics.SamzaContainerMetrics;
import fortscale.monitoring.external.stats.samza.collector.service.stats.SamzaMetricCollectorMetrics;
import fortscale.utils.samza.metricMessageModels.MetricMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static fortscale.monitoring.external.stats.samza.collector.service.impl.converter.SamzaMetricsConversionUtil.entryValueToLong;

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
    public void KafkaSystemConsumerToStatsConverterTest() throws IOException {
        SamzaContainerToStatsConverter converter = new SamzaContainerToStatsConverter(null, samzaMetricCollectorMetrics);

        Map<String, Object> metricEntries = metricMessage.getMetrics().getAdditionalProperties().get(converter.METRIC_NAME);
        converter.convert(metricEntries, metricMessage.getHeader().getJobName(), metricMessage.getHeader().getTime(), metricMessage.getHeader().getHost());

        SamzaContainerMetrics statsMetrics = ((SamzaContainerMetrics) (converter.metricsMap.get("vpnsession-events-filter")));
        Assert.assertEquals(statsMetrics.getCommit(), entryValueToLong(metricEntries.get("commit-calls")));
        Assert.assertEquals(statsMetrics.getCommitSeconds(), (double)metricEntries.get("commit-ms")/1000,0);
        Assert.assertEquals(statsMetrics.getProcessNullEnvelopes(), entryValueToLong(metricEntries.get("process-null-envelopes")));
        Assert.assertEquals(statsMetrics.getWindowSeconds(), (double)metricEntries.get("window-ms")/1000,0);
        Assert.assertEquals(statsMetrics.getProcessEnvelopes(), entryValueToLong(metricEntries.get("process-envelopes")));
        Assert.assertEquals(statsMetrics.getWindow(), entryValueToLong(metricEntries.get("window-calls")));
        Assert.assertEquals(statsMetrics.getSend(), entryValueToLong(metricEntries.get("send-calls")));
        Assert.assertEquals(statsMetrics.getProcess(), entryValueToLong(metricEntries.get("process-calls")));
        Assert.assertEquals(statsMetrics.getChooseSeconds(), (double)metricEntries.get("choose-ms")/1000,0);
        Assert.assertEquals(statsMetrics.getProcessSeconds(), (double)metricEntries.get("process-ms")/1000,0);
    }

    @Test
    public void KafkaSystemProducerToStatsConverterTest() throws IOException {
        KafkaSystemProducerToStatsConverter converter = new KafkaSystemProducerToStatsConverter(null, samzaMetricCollectorMetrics);

        Map<String, Object> metricEntries = metricMessage.getMetrics().getAdditionalProperties().get(converter.METRIC_NAME);
        converter.convert(metricEntries, metricMessage.getHeader().getJobName(), metricMessage.getHeader().getTime(), metricMessage.getHeader().getHost());

        KafkaSystemProducerMetrics statsMetrics = ((KafkaSystemProducerMetrics) (converter.metricsMap.get("vpnsession-events-filter" )));

        Assert.assertEquals(statsMetrics.getFlushes(), entryValueToLong(metricEntries.get("kafka-flushes")),0);
        Assert.assertEquals(statsMetrics.getFlushesFailures(),entryValueToLong("kafka-flush-failed"));
        Assert.assertEquals(statsMetrics.getFlushSeconds(), (double)(metricEntries.get("kafka-flush-ms"))/1000,0);
        Assert.assertEquals(statsMetrics.getMessagesSent(), entryValueToLong(metricEntries.get("kafka-producer-send-success")));
        Assert.assertEquals(statsMetrics.getMessagesSentFailures(), entryValueToLong(metricEntries.get("kafka-producer-send-failed")));
        Assert.assertEquals(statsMetrics.getRetries(), entryValueToLong(metricEntries.get("kafka-producer-retries")));
    }

}
