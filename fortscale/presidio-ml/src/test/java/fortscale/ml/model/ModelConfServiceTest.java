package fortscale.ml.model;

import fortscale.aggregation.configuration.AslConfigurationPaths;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ModelConfServiceTest {
	@Configuration
	public static class ContextConfiguration {
		@Bean
		public ModelConfService modelConfService() {
			AslConfigurationPaths modelConfigurationPaths = new AslConfigurationPaths(
					"testModelConfs",
					"classpath:model-conf-service-test/*.json",
					"file:home/cloudera/fortscale/config/asl/models/overriding/*.json",
					"file:home/cloudera/fortscale/config/asl/models/additional/*.json");
			return new ModelConfService(modelConfigurationPaths);
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
