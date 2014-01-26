package fortscale.web.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import fortscale.global.configuration.GlobalConfiguration;





@Configuration
@PropertySource("classpath:META-INF/application-config.properties")
public class ApplicationConfiguration {
	
	@Autowired
	private GlobalConfiguration globalConfiguration;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
