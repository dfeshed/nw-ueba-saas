package fortscale.collection.configuration;

import fortscale.domain.core.dao.UserActivityFeaturesExtractionsRepositoryUtil;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan(basePackageClasses = UserActivityFeaturesExtractionsRepositoryUtil.class,
		includeFilters = @ComponentScan.Filter(classes = UserActivityFeaturesExtractionsRepositoryUtil.class,type = FilterType.ASSIGNABLE_TYPE))
@PropertySource("classpath:META-INF/fortscale-collection.properties")
public class CollectionConfiguration {

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}


}