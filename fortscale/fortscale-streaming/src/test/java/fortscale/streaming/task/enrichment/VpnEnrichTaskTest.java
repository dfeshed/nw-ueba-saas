package fortscale.streaming.task.enrichment;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.vpn.VpnEnrichService;
import fortscale.streaming.task.GeneralTaskTest;
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

import static org.mockito.Mockito.*;

/**
 * Created by rans on 02/02/15.
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "classpath:META-INF/spring/vpn-enrich-context-test.xml")
public class VpnEnrichTaskTest extends GeneralTaskTest {

    final String MESSAGE = "{ \"name\": \"user1\",  \"time\": 1 }";
    final String HOST_NAME = "MY-PC";
    final String INPUT_TOPIC = "topic-1";



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

    ObjectMapper mapper = new ObjectMapper();


    /**
     * Tests that task flow works
     * @throws Exception
     */
    @Test
    public void wrappedProcess_normal() throws Exception {



		Map<String, VpnEnrichService> topicToServiceMap = new HashMap<>();
		topicToServiceMap.put(INPUT_TOPIC,vpnEnrichService);
		VpnEnrichTask.setTopicToServiceMap(topicToServiceMap);

        //stub
        Map map = new HashMap();
        map.put("username", "myUser");

        when(vpnEnrichService.processVpnEvent(any(JSONObject.class))).thenReturn(new JSONObject(map));
        when(systemStreamPartition.getSystemStream()).thenReturn(systemStream);
        when(vpnEnrichService.getUsernameFieldName()).thenReturn("username");

        // prepare envelope
        IncomingMessageEnvelope envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, null,MESSAGE  , INPUT_TOPIC);
        // run the process on the envelope
        task.wrappedProcess(envelope , messageCollector, taskCoordinator);
        task.wrappedClose();
        // validate the services were read
        verify(vpnEnrichService).processVpnEvent(any(JSONObject.class));
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
		Map<String, VpnEnrichService> topicToServiceMap = new HashMap<>();
		topicToServiceMap.put(INPUT_TOPIC,vpnEnrichService);
		VpnEnrichTask.setTopicToServiceMap(topicToServiceMap);

        when(vpnEnrichService.processVpnEvent(any(JSONObject.class))).thenReturn(new JSONObject(map));
        when(systemStreamPartition.getSystemStream()).thenReturn(systemStream);
        when(vpnEnrichService.getUsernameFieldName()).thenReturn("username");
        doThrow(new RuntimeException()).when(messageCollector).send(any(OutgoingMessageEnvelope.class));

        // prepare envelope
        IncomingMessageEnvelope envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, null,MESSAGE  , INPUT_TOPIC);
        // run the process on the envelope
        task.wrappedProcess(envelope , messageCollector, taskCoordinator);
        task.wrappedClose();

        //reset the mocks so it clears counters for the sake of next test
        reset(vpnEnrichService);
        reset(messageCollector);
    }
}
