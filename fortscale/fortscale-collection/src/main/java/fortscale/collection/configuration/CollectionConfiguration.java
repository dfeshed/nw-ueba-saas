package fortscale.collection.configuration;

import fortscale.domain.core.dao.UserActivityFeaturesExtractiionsRepositoryUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:META-INF/fortscale-collection.properties")
public class CollectionConfiguration {

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public UserActivityFeaturesExtractiionsRepositoryUtil getUserActivityFeaturesExtractiionsRepositoryUtil(){
		return new UserActivityFeaturesExtractiionsRepositoryUtil();
	}
}