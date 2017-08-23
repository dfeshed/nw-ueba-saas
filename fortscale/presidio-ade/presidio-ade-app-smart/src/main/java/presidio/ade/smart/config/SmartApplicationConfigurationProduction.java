package presidio.ade.smart.config;

import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Lior Govrin
 */
@Configuration
@Import({SmartApplicationConfiguration.class, MongoConfig.class})
public class SmartApplicationConfigurationProduction {}
