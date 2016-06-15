package fortscale.monitoring.external.stats.samza.collector.service.impl.converter;

import fortscale.monitoring.external.stats.Util.CollectorsUtil;
import fortscale.utils.samza.metricMessageModels.Header;
import fortscale.utils.samza.metricMessageModels.MetricMessage;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static fortscale.monitoring.external.stats.samza.collector.service.impl.converter.SamzaMetricsConversionUtil.*;


public class SamzaMetricsConversionUtilTest {

    @Test
    public void entryValueToLongTest()
    {
        long longValue = 1;
        Integer intValue=1;
        Double doubleValue=1.1;
        Assert.assertEquals(longValue, CollectorsUtil.entryValueToLong(longValue));
        Assert.assertEquals(longValue, CollectorsUtil.entryValueToLong(intValue));
        Assert.assertEquals(longValue, CollectorsUtil.entryValueToLong(doubleValue));
        Assert.assertEquals(longValue, CollectorsUtil.entryValueToLong(true));
        Assert.assertEquals(0, CollectorsUtil.entryValueToLong(false));
    }

    @Test(expected = ClassCastException.class)
    public void entryValueToLongTestConversionException()
    {
        CollectorsUtil.entryValueToLong("testString");
    }

    @Test
    public void getStoreNameTest()
    {
        String convertedStoreName = getStoreName(" rawStoreName-OperationName", Collections.singletonList("OperationName"));
        Assert.assertEquals("rawStoreName",convertedStoreName);
    }

    @Test
    public void getTopicNameTest()
    {
        List<String> topicOperations = new LinkedList<>();
        topicOperations.add("bytes-read");
        topicOperations.add("blocking-poll-timeout-count");
        Assert.assertEquals("topic",getTopicName("kafka-topic",null));
        Assert.assertEquals("topic",getTopicName("kafka-topic-0",topicOperations));
        Assert.assertEquals("topic",getTopicName("kafka-topic-0-offset",topicOperations));
        Assert.assertEquals("topic",getTopicName("kafka-topic-bytes-read",topicOperations));
        Assert.assertEquals("sensitive-machine-service-cache-updates",getTopicName("blocking-poll-timeout-count-SystemStreamPartition [kafka, sensitive-machine-service-cache-updates, 0]",topicOperations));
    }

    @Test
    public void getMetricMessageTimeTest()
    {
        long time= 1464074790047L;
        Header header = new Header("jobId","samzaVersion","BestJobname","GreatesHostname123",123,"SomeCOntainerName","SourceName",time,"version");
        MetricMessage message = new MetricMessage(header,null);
        Assert.assertEquals(time/1000,getMetricMessageTime(message));
    }

    @Test
    public void getOperationNameTest()
    {
        String convertedOperationName = getOperationName(" rawStoreName-OperationName", "rawStoreName");
        Assert.assertEquals("OperationName",convertedOperationName);
    }
}
