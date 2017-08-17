package presidio.webapp;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import presidio.webapp.service.RestAlertService;
import presidio.webapp.spring.OutputWebappConfiguration;

import java.util.Properties;

@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore //TODO fix those tests
public class FortscaleOutputWebAppTests {

	private RestAlertService restAlertService;

	@Test
	public void contextLoads() {
		Assert.notNull(restAlertService, "restAlertService cannot be null on spring context");
	}

	@Configuration
	@Import({OutputWebappConfiguration.class})
	@EnableSpringConfigured
	public static class springConfig {
		@Bean
		public static TestPropertiesPlaceholderConfigurer outputWebappTestConfigurer()
		{
			Properties properties = new Properties();
			properties.put("elasticsearch.port", "12345");
			properties.put("elasticsearch.clustername", "presidio");

			return new TestPropertiesPlaceholderConfigurer(properties);
		}

	}

}
