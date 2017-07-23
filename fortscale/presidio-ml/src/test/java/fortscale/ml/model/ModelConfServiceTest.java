package fortscale.ml.model;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ModelConfServiceTest {
	@Configuration
	@Import(ModelConfServiceConfig.class)
	public static class ContextConfiguration {
		@Bean
		public TestPropertiesPlaceholderConfigurer modelConfServiceTestPropertiesPlaceholderConfigurer() {
			Properties properties = new Properties();
			properties.put("presidio.modeling.base.configurations.path", "classpath:model-conf-service-test/*.json");
			return new TestPropertiesPlaceholderConfigurer(properties);
		}
	}

	@Autowired
	private ModelConfService modelConfService;

	@Test
	public void shouldDeserializeJsonFile() throws Exception {
		modelConfService.loadAslConfigurations();
		List<ModelConf> modelConfs = modelConfService.getModelConfs();
		Assert.assertNotNull(modelConfs);
		Assert.assertEquals(2, modelConfs.size());
		Assert.assertEquals("name1", modelConfs.get(0).getName());
		Assert.assertEquals("name2", modelConfs.get(1).getName());
	}
}
