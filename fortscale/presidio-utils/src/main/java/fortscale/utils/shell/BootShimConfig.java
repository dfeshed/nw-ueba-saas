package fortscale.utils.shell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Barak Schuster
 * @author Lior Govrin
 */
@Configuration
public class BootShimConfig {
    @Autowired
    private ConfigurableApplicationContext context;

    @Bean
    public BootShim bootShim() {
        return new BootShim(CommandLineArgsHolder.args, context);
    }

    @Bean
    public InstantConverter instantConverter() {
        return new InstantConverter();
    }

    @Bean
    public FixedDurationStrategyConverter fixedDurationStrategyConverter() {
        return new FixedDurationStrategyConverter();
    }
}
