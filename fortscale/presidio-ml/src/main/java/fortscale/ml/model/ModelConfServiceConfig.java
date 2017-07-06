package fortscale.ml.model;

import fortscale.aggregation.configuration.AslConfigurationPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelConfServiceConfig {
	@Autowired
	@Qualifier("modelConfigurationPaths")
	private AslConfigurationPaths modelConfigurationPaths;

	@Bean
	public ModelConfService modelConfService() {
		return new ModelConfService(modelConfigurationPaths);
	}
}
