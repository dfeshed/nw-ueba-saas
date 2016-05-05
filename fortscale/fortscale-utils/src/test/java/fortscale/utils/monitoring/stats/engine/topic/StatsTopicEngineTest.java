package fortscale.utils.monitoring.stats.engine.topic;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.kafka.metricMessageModels.Header;
import fortscale.utils.kafka.metricMessageModels.MetricMessage;
import fortscale.utils.kafka.metricMessageModels.Metrics;
import fortscale.utils.monitoring.stats.engine.StatsEngineExceptions;
import fortscale.utils.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.monitoring.stats.engine.StatsEngineTestingUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by gaashh on 5/2/16.
 */
public class StatsTopicEngineTest {


    static public String getExpectedMessage(long setIndex, String hostName, String jobName, long epochTime, long firstEpochTime) throws Exception {


        String expectedData = StatsEngineTestingUtils.getExpectedMetricsGroupListJsonString(setIndex);

        // Build the metrics topic header
        Header header = new Header();
        header.setHost(hostName);
        header.setJobName(jobName);
        header.setVersion("0.0.1");
        header.setTime(epochTime * 1000);  // in mSec
        header.setResetTime(firstEpochTime * 1000); // in mSec

        // Convert the engine data into a JSON string
        String engineDataInJsonString = expectedData;

        // Creates a metrics entry
        HashMap<String, Object> engineDataMetricsEntry = new HashMap<>();
        engineDataMetricsEntry.put("EngineData", engineDataInJsonString);

        // Create the metrics object and populate it with the entry
        Metrics metrics = new Metrics();
        metrics.setAdditionalProperty("fortscale.utils.monitoring.stats.models.engine", engineDataMetricsEntry);

        // Build the metric message
        MetricMessage metricMessage = new MetricMessage();
        metricMessage.setHeader(header);
        metricMessage.setMetrics(metrics);

        // Encode the metrics message as a JSON string
        // In case of error, just log an error
        ObjectMapper mapper = new ObjectMapper();

        String jsonInString = null;

        jsonInString = mapper.writeValueAsString(metricMessage);

        return jsonInString;

    }

    @Test
    public void test1() throws Exception {

        KafkaEventsWriter kafkaEventsWriterMock = Mockito.mock(KafkaEventsWriter.class);

        StatsTopicEngine engine = new StatsTopicEngine(kafkaEventsWriterMock);

        String[] expectedMessageStrings = new String[3];
        long[]   expectedEpoches        = {1000101122,  1000201122, 1000301122}; // Must much the data
        for (int  i = 0 ; i < 3 ; i++) {
            long setIndex = i + 1 ;
            List<StatsEngineMetricsGroupData> metricGroupDataList = StatsEngineTestingUtils.createdStatsMetricsGroupsList(setIndex);

            for (StatsEngineMetricsGroupData metricsGroupData : metricGroupDataList) {
                engine.writeMetricsGroupData(metricsGroupData);
            }
            engine.flushMetricsGroupData();

            expectedMessageStrings[i] = getExpectedMessage(setIndex, "TODO-hostname", "TODO-jobname",
                                        expectedEpoches[i], expectedEpoches[0]);


        }

        // Verify the sent messages
        for (String expected : expectedMessageStrings) {
            InOrder inOrder = inOrder(kafkaEventsWriterMock);
            System.out.println(expected);
            inOrder.verify(kafkaEventsWriterMock).send("", expected);
        }


    }
}
