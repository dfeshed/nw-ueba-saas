package fortscale.monitoring.samza;

import fortscale.monitoring.samza.topicReader.SamzaMetricsTopicSyncReader;

import fortscale.utils.samza.metricMessageModels.MetricMessage;
import kafka.message.Message;
import kafka.message.MessageAndOffset;
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


/**
 * Created by cloudera on 5/16/16.
 */
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
    public void ShouldConvertMessageAndOffsetToMetricMessage() throws IOException {
        File originalMessagesFile = new File("src/test/resources/samza/metricMessageTest.json");
        String originalMessage = Files.readAllLines(Paths.get("src/test/resources/samza/metricMessageTest.json")).get(0);

        Message message = new Message(originalMessage.getBytes());

        MessageAndOffset messageAndOffset = new MessageAndOffset(message, 0);
        MetricMessage convertedMessage = remoteService.convertMessageAndOffsetToMetricMessage(messageAndOffset);
        assertThat(convertedMessage, is(originalMessage));

    }

}