package fortscale.utils.kafka;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class SimpleMetricsReaderTest {
	@Configuration
	@EnableSpringConfigured
	static class ContextConfiguration {
		@Bean
		public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
			Properties properties = new Properties();
			properties.put("kafka.broker.list", "tc-agent3:9092");
			PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
			configurer.setProperties(properties);
			return configurer;
		}
	}

	// @Test
	public void testSimpleMetricsReader() {
		String jobName = "aggregation-events-streaming";
		String className = "fortscale.streaming.task.AggregationEventsStreamTask";
		Collection<String> metricsToCapture = new ArrayList<>();
		metricsToCapture.add("aggregation-message-count");
		metricsToCapture.add("aggregation-events-streaming-last-message-epochtime");

		SimpleMetricsReader reader = new SimpleMetricsReader(
				getClass().getSimpleName(), 0, jobName, className, metricsToCapture);
		reader.start();

		for (int i = 0; i < 100; i++) {
			try {
				Long count = reader.getLong("aggregation-message-count");
				Long epochtime = reader.getLong("aggregation-events-streaming-last-message-epochtime");
				System.out.println(String.format("[%d] count=%d, epochtime=%d", i, count, epochtime));
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}

		reader.end();
	}
}
