package fortscale.monitoring.samza;

import fortscale.monitoring.samza.topicReader.SamzaMetricsTopicSyncReader;

import fortscale.utils.samza.metricMessageModels.MetricMessage;
import kafka.message.Message;
import kafka.message.MessageAndOffset;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SamzaMetricsTopicSyncReaderTest {
    @Configuration
    public static class SamzaMetricsTopicSyncReaderConfig {

        @Bean
        public SamzaMetricsTopicSyncReader reader() throws Exception {
            String[] hostAndPort = {"host", "port"};
            SamzaMetricsTopicSyncReader remoteService = new SamzaMetricsTopicSyncReader(0, 0, 0, hostAndPort, "testClientId", "testTopic", 0);
            return remoteService;
        }

    }

    @Autowired
    SamzaMetricsTopicSyncReader remoteService;

    @Test
    public void ShouldConvertMessageAndOffsetToMetricMessage() throws IOException, JSONException {
        String originalMessage = Files.readAllLines(Paths.get("src/test/resources/samza/metricMessageTest.json")).get(0);
        JSONObject originalMessageJson = new JSONObject(originalMessage);
        originalMessageJson.getJSONObject("header").put("additionalProperties", new JSONObject());

        Message message = new Message(originalMessage.getBytes());

        MessageAndOffset messageAndOffset = new MessageAndOffset(message, 0);
        MetricMessage convertedMessage = remoteService.convertMessageAndOffsetToMetricMessage(messageAndOffset);

        assertEquals(originalMessageJson.getJSONObject("header").get("host"), (convertedMessage.getHeader().getHost()));
        assertEquals(originalMessageJson.getJSONObject("header").get("job-id"), (convertedMessage.getHeader().getJobId()));
        assertEquals(originalMessageJson.getJSONObject("header").get("job-name"), (convertedMessage.getHeader().getJobName()));
        assertEquals(originalMessageJson.getJSONObject("header").get("reset-time"), (convertedMessage.getHeader().getResetTime()));
        assertEquals(originalMessageJson.getJSONObject("header").get("samza-version"), (convertedMessage.getHeader().getSamzaVersion()));
        assertEquals(originalMessageJson.getJSONObject("header").get("source"), (convertedMessage.getHeader().getSource()));
        assertEquals(originalMessageJson.getJSONObject("header").get("time"), (convertedMessage.getHeader().getTime()));
        assertEquals(originalMessageJson.getJSONObject("metrics").getJSONObject("org.apache.samza.container.TaskInstanceMetrics").get("commit-calls"), (convertedMessage.getMetrics().getAdditionalProperties().get("org.apache.samza.container.TaskInstanceMetrics").get("commit-calls")));
        assertEquals(originalMessageJson.getJSONObject("metrics").getJSONObject("org.apache.samza.container.TaskInstanceMetrics").get("window-calls"), (convertedMessage.getMetrics().getAdditionalProperties().get("org.apache.samza.container.TaskInstanceMetrics").get("window-calls")));
        assertEquals(originalMessageJson.getJSONObject("metrics").getJSONObject("org.apache.samza.container.TaskInstanceMetrics").get("flush-calls"), (convertedMessage.getMetrics().getAdditionalProperties().get("org.apache.samza.container.TaskInstanceMetrics").get("flush-calls")));
        assertEquals(originalMessageJson.getJSONObject("metrics").getJSONObject("org.apache.samza.container.TaskInstanceMetrics").get("send-calls"), (convertedMessage.getMetrics().getAdditionalProperties().get("org.apache.samza.container.TaskInstanceMetrics").get("send-calls")));
    }

}