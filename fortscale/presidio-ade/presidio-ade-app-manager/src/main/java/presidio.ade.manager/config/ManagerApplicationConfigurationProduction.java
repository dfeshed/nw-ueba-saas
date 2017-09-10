package presidio.ade.manager.config;

import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by maria_dorohin on 7/26/17.
 */
@Configuration
@Import({
        MongoConfig.class
})
public class ManagerApplicationConfigurationProduction extends ManagerApplicationConfig {

}

