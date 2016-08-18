package fortscale.utils.monitoring.stats.engine.topic;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.samza.metricMessageModels.Header;
import fortscale.utils.samza.metricMessageModels.MetricMessage;
import fortscale.utils.samza.metricMessageModels.Metrics;
import fortscale.utils.monitoring.stats.engine.StatsEngineExceptions;
import fortscale.utils.monitoring.stats.engine.StatsEngineLongMetricData;
import fortscale.utils.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.monitoring.stats.engine.StatsEngineTestingUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by gaashh on 5/2/16.
 */
public class StatsTopicEngineTest {


    @Test
    public void testMessageContent() throws Exception {

        KafkaEventsWriter kafkaEventsWriterMock = Mockito.mock(KafkaEventsWriter.class);

        StatsTopicEngine engine = new StatsTopicEngine(kafkaEventsWriterMock);

        String[] expectedMessageStrings = new String[3];

        for (int  i = 0 ; i < 3 ; i++) {
            long setIndex = i + 1 ;
            List<StatsEngineMetricsGroupData> metricGroupDataList = StatsEngineTestingUtils.createdStatsMetricsGroupsList(setIndex);

            for (StatsEngineMetricsGroupData metricsGroupData : metricGroupDataList) {
                engine.writeMetricsGroupData(metricsGroupData);
            }
            engine.flushMetricsGroupData();

            expectedMessageStrings[i] = StatsEngineTestingUtils.getExpectedMetricsGroupListJsonString(setIndex);
        }

        // Verify the sent messages
        for (String expected : expectedMessageStrings) {
            InOrder inOrder = inOrder(kafkaEventsWriterMock);
            //System.out.println(expected);
            inOrder.verify(kafkaEventsWriterMock).send("", expected);
        }

    }

    @Test // Note: The test is a bit lame because it checks only the write count but the the actual content.
    public void testMessageSplitExactBatchSize() throws Exception {

        KafkaEventsWriter kafkaEventsWriterMock = Mockito.mock(KafkaEventsWriter.class);

        final long BATCH_SIZE = 3;
        StatsTopicEngine engine = new StatsTopicEngine(kafkaEventsWriterMock, BATCH_SIZE, 100 * 1024);

        // Write 9 items to the engine
        for (long i = 0 ; i < 9 ; i++) {
            StatsEngineMetricsGroupData metricsGroupData = new StatsEngineMetricsGroupData();
            metricsGroupData.setInstrumentedClass(StatsTopicEngineTest.class);
            metricsGroupData.addLongMetricData( new StatsEngineLongMetricData("index", i) );
            engine.writeMetricsGroupData(metricsGroupData);
        }

        // Flush the engine data
        engine.flushMetricsGroupData();


        // Verify the sent messages: write should be 3,3,3 => 3 times
        InOrder inOrder = inOrder(kafkaEventsWriterMock);
        inOrder.verify(kafkaEventsWriterMock, times(3)).send(any(),any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test // Note: The test is a bit lame because it checks only the write count but the the actual content.
    public void testMessageSplitNonExactBatchSize() throws Exception {

        KafkaEventsWriter kafkaEventsWriterMock = Mockito.mock(KafkaEventsWriter.class);

        final long BATCH_SIZE = 3;
        StatsTopicEngine engine = new StatsTopicEngine(kafkaEventsWriterMock, BATCH_SIZE, 100 * 1024);

        // Write 10 items to the engine
        for (long i = 0 ; i < 10 ; i++) {
            StatsEngineMetricsGroupData metricsGroupData = new StatsEngineMetricsGroupData();
            metricsGroupData.setInstrumentedClass(StatsTopicEngineTest.class);
            metricsGroupData.addLongMetricData( new StatsEngineLongMetricData("index", i) );
            engine.writeMetricsGroupData(metricsGroupData);
        }

        // Flush the engine data
        engine.flushMetricsGroupData();


        // Verify the sent messages: write should be 3,3,3,1 => 4 times
        InOrder inOrder = inOrder(kafkaEventsWriterMock);
        inOrder.verify(kafkaEventsWriterMock, times(4)).send(any(),any());
        inOrder.verifyNoMoreInteractions();
    }

}
