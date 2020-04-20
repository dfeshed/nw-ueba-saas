package presidio.output.manager.config;

import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.manager.OutputManagerShellCommands;

@Configuration
@Import({
        MongoConfig.class,
        // CLI commands related configurations
        OutputManagerShellCommands.class
})
public class OutputManagerServiceConfig extends OutputManagerBaseConfig {
}
