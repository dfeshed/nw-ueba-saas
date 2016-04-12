package fortscale.ml.model.listener;

import net.minidev.json.JSONObject;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
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
			properties.put("fortscale.model.build.message.field.session.id", "sessionId");
			properties.put("fortscale.model.build.message.field.model.conf.name", "modelConfName");
			properties.put("fortscale.model.build.message.field.end.time.in.seconds", "endTimeInSeconds");
			properties.put("fortscale.model.build.message.field.context.id", "contextId");
			properties.put("fortscale.model.build.message.field.is.successful", "isSuccessful");
			properties.put("fortscale.model.build.message.field.details", "details");
			properties.put("fortscale.model.build.message.field.num.of.successes", "numOfSuccesses");
			properties.put("fortscale.model.build.message.field.num.of.failures", "numOfFailures");
			PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
			configurer.setProperties(properties);
			return configurer;
		}
	}

	@Test
	public void should_create_and_send_the_correct_model_building_status() {
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
		String message = (String)envelope.getMessage();
		Assert.assertEquals(outputTopicName, systemStream.getStream());
		Assert.assertEquals(expectedJson.toJSONString(), message);
	}
}
