package fortscale.streaming.task.enrichment;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.service.vpn.VpnEnrichService;
import fortscale.streaming.task.GeneralTaskTest;
import fortscale.streaming.task.monitor.TaskMonitoringHelper;
import net.minidev.json.JSONObject;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskCoordinator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by rans on 02/02/15.
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "classpath:META-INF/spring/vpn-enrich-context-test.xml")
public class VpnEnrichTaskTest extends GeneralTaskTest {

    private static final String DATA_SOURCE_FIELD_NAME = "data_source";
    private static final String LAST_STATE_FIELD_NAME = "last_state";
    final String HOST_NAME = "MY-PC";
    final String INPUT_TOPIC = "topic-1";
    final String DATA_SOURCE = "data-source";
    final String LAST_STATE = "last-state";
    final String MESSAGE_TEMPLATE = "{ \"name\": \"user1\",  \"time\": 1, ,  \"%s\": \"%s\", ,  \"%s\": \"%s\" }";
    final String MESSAGE = String.format(MESSAGE_TEMPLATE, DATA_SOURCE_FIELD_NAME, DATA_SOURCE, LAST_STATE_FIELD_NAME, LAST_STATE);


    @InjectMocks
    VpnEnrichTask task;


	@Mock
	VpnEnrichService vpnEnrichService;


    @Mock
    private SystemStreamPartition systemStreamPartition;
    @Mock
    private SystemStream systemStream;
    @Mock
    private MessageCollector messageCollector;
    @Mock
    TaskCoordinator taskCoordinator;

    @Mock
    TaskMonitoringHelper taskMonitorHelper;

    ObjectMapper mapper = new ObjectMapper();


    /**
     * Tests that task flow works
     * @throws Exception
     */
    @Test
    public void wrappedProcess_normal() throws Exception {

        // Create the task metrics (because init() is not called)
        task.createTaskMetrics();

		Map<StreamingTaskDataSourceConfigKey, VpnEnrichService> topicToServiceMap = new HashMap<>();

        StreamingTaskDataSourceConfigKey configKey = new StreamingTaskDataSourceConfigKey(DATA_SOURCE, LAST_STATE);

		topicToServiceMap.put(configKey,vpnEnrichService);
		VpnEnrichTask.setDataSourceConfigs(topicToServiceMap);

        //stub
        Map map = new HashMap();
        map.put("username", "myUser");

        when(vpnEnrichService.processVpnEvent(any(JSONObject.class), eq(messageCollector))).thenReturn(new JSONObject(map));
        when(systemStreamPartition.getSystemStream()).thenReturn(systemStream);
        when(vpnEnrichService.getUsernameFieldName()).thenReturn("username");

        // prepare envelope
        IncomingMessageEnvelope envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, null,MESSAGE  , INPUT_TOPIC);
        // run the process on the envelope
        task.wrappedProcess(envelope , messageCollector, taskCoordinator);
        task.wrappedClose();
        // validate the services were read
        verify(vpnEnrichService).processVpnEvent(any(JSONObject.class), eq(messageCollector));
        verify(vpnEnrichService).getPartitionKey(any(JSONObject.class));
        verify(messageCollector).send(any(OutgoingMessageEnvelope.class));

        //reset the mocks so it clears counters for the sake of next test
        reset(vpnEnrichService);
        reset(messageCollector);
    }

    /**
     * Tests that proper exception is thrown when kafka cannot send events.
     * @throws Exception
     */
    @Test(expected = KafkaPublisherException.class)
    public void wrappedProcess_kafkaException() throws Exception {



        //stub
        Map map = new HashMap();
        map.put("username", "myUser");
		Map<StreamingTaskDataSourceConfigKey, VpnEnrichService> topicToServiceMap = new HashMap<>();
        StreamingTaskDataSourceConfigKey configKey = new StreamingTaskDataSourceConfigKey(DATA_SOURCE, LAST_STATE);

        topicToServiceMap.put(configKey,vpnEnrichService);
		VpnEnrichTask.setDataSourceConfigs(topicToServiceMap);

        when(vpnEnrichService.processVpnEvent(any(JSONObject.class), eq(messageCollector))).thenReturn(new JSONObject(map));
        when(systemStreamPartition.getSystemStream()).thenReturn(systemStream);
        when(vpnEnrichService.getUsernameFieldName()).thenReturn("username");
        doThrow(new RuntimeException()).when(messageCollector).send(any(OutgoingMessageEnvelope.class));

        // prepare envelope
        IncomingMessageEnvelope envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, null,MESSAGE  , INPUT_TOPIC);
        // run the process on the envelope
        task.wrappedCreateTaskMetrics();
        task.wrappedProcess(envelope , messageCollector, taskCoordinator);
        task.wrappedClose();

        //reset the mocks so it clears counters for the sake of next test
        reset(vpnEnrichService);
        reset(messageCollector);
    }
}
