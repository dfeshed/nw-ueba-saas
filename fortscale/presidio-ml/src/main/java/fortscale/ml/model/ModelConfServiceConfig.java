package fortscale.ml.model;

import fortscale.aggregation.configuration.AslConfigurationPaths;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelConfServiceConfig {
	@Bean
	public ModelConfService modelConfService() {
		return new ModelConfService(new AslConfigurationPaths(
				"productionModelConfs",
				"classpath:config/asl/models/*.json",
				"file:home/cloudera/fortscale/config/asl/models/overriding/*.json",
				"file:home/cloudera/fortscale/config/asl/models/additional/*.json"));
	}
}
