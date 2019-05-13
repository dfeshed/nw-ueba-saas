package presidio.output.manager.config;

import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.spring.EventPersistencyServiceConfig;
import presidio.output.manager.services.OutputManagerService;
import presidio.output.manager.OutputManagerShellCommands;

@Configuration
@Import({
        MongoConfig.class,
        EventPersistencyServiceConfig.class,
        // CLI commands related configurations
        OutputManagerShellCommands.class
})
public class OutputManagerServiceConfig extends OutputManagerBaseConfig {
}
