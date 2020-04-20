package fortscale.ml.model;

import fortscale.aggregation.configuration.AslResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelConfServiceConfig {
	@Value("${presidio.modeling.base.configurations.path}")
	private String baseConfigurationsPath;
	@Value("${presidio.modeling.overriding.configurations.path:#{null}}")
	private String overridingConfigurationsPath;
	@Value("${presidio.modeling.additional.configurations.path:#{null}}")
	private String additionalConfigurationsPath;

	@Autowired
	private AslResourceFactory aslResourceFactory;

	@Bean
	public AslResourceFactory aslResourceFactory() {
		return new AslResourceFactory();
	}

	@Bean
	public ModelConfService modelConfService() {
		ModelConfService modelConfService = new ModelConfService(
				aslResourceFactory.getResources(baseConfigurationsPath),
				aslResourceFactory.getResources(overridingConfigurationsPath),
				aslResourceFactory.getResources(additionalConfigurationsPath));
		modelConfService.loadAslConfigurations();
		return modelConfService;
	}
}
