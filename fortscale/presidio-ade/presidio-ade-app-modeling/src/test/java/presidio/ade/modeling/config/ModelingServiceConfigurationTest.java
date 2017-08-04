package presidio.ade.modeling.config;

import fortscale.utils.shell.BootShimConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author Lior Govrin
 */
@Configuration
@Import({ModelingServiceConfiguration.class, MongodbTestConfig.class, BootShimConfig.class})
public class ModelingServiceConfigurationTest {
	private static final String PROPERTIES_FILE_PATH =
			"/home/presidio/dev-projects/presidio-core/fortscale/presidio-configuration-server/" +
			"src/main/resources/configurations/modeling-service.properties";

	@Bean
	public static TestPropertiesPlaceholderConfigurer modelingServiceConfigurationTestPropertiesPlaceholderConfigurer()
			throws Exception {

		Properties properties = new Properties();
		properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
		return new TestPropertiesPlaceholderConfigurer(properties);
	}
}
