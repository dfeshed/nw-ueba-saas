package fortscale.ml.model;

import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.data.hadoop.config.common.annotation.EnableAnnotationConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;
import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class ModelConfServiceTest {
	@Configuration
	@EnableSpringConfigured
	@EnableAnnotationConfiguration
	@Import(NullStatsServiceConfig.class)
	static class ContextConfiguration {
		@Bean
		public ModelConfService modelConfService() {
			return new ModelConfService();
		}

		@Bean
		public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
			Properties properties = new Properties();
			properties.put("fortscale.model.configurations.location.path", "classpath:model-conf-service-test/*.json");
			properties.put("fortscale.model.configurations.overriding.location.path", "file:home/cloudera/fortscale/config/asl/models/overriding/*.json");
			properties.put("fortscale.model.configurations.additional.location.path", "file:home/cloudera/fortscale/config/asl/models/additional/*.json");
			PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
			configurer.setProperties(properties);
			configurer.setOrder(Ordered.HIGHEST_PRECEDENCE);
			configurer.setIgnoreUnresolvablePlaceholders(true);
			configurer.setLocalOverride(true);
			return configurer;
		}
	}

	@Autowired
	private ModelConfService modelConfService;

	@Test
	public void shouldDeserializeJsonFile() throws Exception {
		List<ModelConf> modelConfs = modelConfService.getModelConfs();
		Assert.assertNotNull(modelConfs);
		Assert.assertEquals(2, modelConfs.size());
		Assert.assertEquals("name1", modelConfs.get(0).getName());
		Assert.assertEquals("name2", modelConfs.get(1).getName());
	}
}
