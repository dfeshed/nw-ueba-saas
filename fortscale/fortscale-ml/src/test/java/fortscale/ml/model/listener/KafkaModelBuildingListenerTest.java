package fortscale.ml.model.listener;

import net.minidev.json.JSONObject;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

public class KafkaModelBuildingListenerTest {
	@Test
	public void should_create_and_send_the_correct_model_building_status() {
		String outputTopicName = "testTopic";
		KafkaModelBuildingListener kafkaModelBuildingListener = new KafkaModelBuildingListener(outputTopicName);

		MessageCollector collector = Mockito.mock(MessageCollector.class);
		kafkaModelBuildingListener.setMessageCollector(collector);

		String modelConfName = "testModelConf";
		Map<String, String> context = new HashMap<>();
		context.put("normalized_username", "testUser");

		JSONObject expectedJson = new JSONObject();
		expectedJson.put("modelConfName", modelConfName);
		expectedJson.put("context", context);
		expectedJson.put("success", true);

		kafkaModelBuildingListener.modelBuildingStatus(modelConfName, context, true);
		ArgumentCaptor<OutgoingMessageEnvelope> argumentCaptor = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);
		Mockito.verify(collector, Mockito.times(1)).send(argumentCaptor.capture());

		OutgoingMessageEnvelope envelope = argumentCaptor.getValue();
		SystemStream systemStream = envelope.getSystemStream();
		String message = (String)envelope.getMessage();
		Assert.assertEquals(outputTopicName, systemStream.getStream());
		Assert.assertEquals(expectedJson.toJSONString(), message);
	}
}
