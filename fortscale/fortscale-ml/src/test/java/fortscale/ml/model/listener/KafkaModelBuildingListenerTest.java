package fortscale.ml.model.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import fortscale.ml.model.message.ModelBuildingStatusMessage;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.hadoop.config.common.annotation.EnableAnnotationConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Date;
import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class KafkaModelBuildingListenerTest {
	@Configuration
	@EnableSpringConfigured
	@EnableAnnotationConfiguration
	static class ContextConfiguration {
		@Bean
		public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
			Properties properties = new Properties();
			PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
			configurer.setProperties(properties);
			return configurer;
		}
	}

	@Test
	public void should_create_and_send_the_correct_model_building_status() throws JsonProcessingException {
		String outputTopicName = "testTopic";
		KafkaModelBuildingListener kafkaModelBuildingListener = new KafkaModelBuildingListener(outputTopicName);

		MessageCollector collector = Mockito.mock(MessageCollector.class);
		kafkaModelBuildingListener.setMessageCollector(collector);

		String modelConfName = "testModelConf";
		String sessionId = "mySession";
		String contextId = "testContextId";
		Date endTime = new Date(1287576000000L);


		JSONObject expectedJson = new JSONObject();
		expectedJson.put("modelConfName", modelConfName);
		expectedJson.put("sessionId", sessionId);
		expectedJson.put("contextId", contextId);
		expectedJson.put("endTimeInSeconds", 1287576000);
		expectedJson.put("isSuccessful", true);
		expectedJson.put("details", ModelBuildingStatus.SUCCESS.getMessage());

		kafkaModelBuildingListener.modelBuildingStatus(modelConfName, sessionId, contextId, endTime, ModelBuildingStatus.SUCCESS);
		ArgumentCaptor<OutgoingMessageEnvelope> argumentCaptor = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);
		Mockito.verify(collector, Mockito.times(1)).send(argumentCaptor.capture());

		OutgoingMessageEnvelope envelope = argumentCaptor.getValue();
		SystemStream systemStream = envelope.getSystemStream();
		ModelBuildingStatusMessage message = (ModelBuildingStatusMessage)envelope.getMessage();
		ObjectMapper objectMapper = new ObjectMapper().registerModule(new JsonOrgModule());
		JSONObject messageJson = objectMapper.convertValue(message,JSONObject.class);
		Assert.assertEquals(outputTopicName, systemStream.getStream());
		Assert.assertEquals(expectedJson.toString(), messageJson.toString());
	}
}
