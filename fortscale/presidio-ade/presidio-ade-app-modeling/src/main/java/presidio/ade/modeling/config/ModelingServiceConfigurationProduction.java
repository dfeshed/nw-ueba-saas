package presidio.ade.modeling.config;

import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Lior Govrin
 */
@Configuration
@Import({ModelingServiceConfiguration.class, MongoConfig.class})
public class ModelingServiceConfigurationProduction {}
