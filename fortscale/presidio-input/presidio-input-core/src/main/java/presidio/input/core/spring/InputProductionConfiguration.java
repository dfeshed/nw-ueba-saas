package presidio.input.core.spring;

import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by shays on 21/06/2017.
 */

@Configuration
@Import({MongoConfig.class, InputCoreConfiguration.class})
public class InputProductionConfiguration {
}
