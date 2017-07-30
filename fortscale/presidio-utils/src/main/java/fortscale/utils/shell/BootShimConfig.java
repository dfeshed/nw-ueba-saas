package fortscale.utils.shell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 7/25/17.
 */
@Configuration
public class BootShimConfig {
    @Autowired
    private ConfigurableApplicationContext context;
    @Bean
    public BootShim bootShim()
    {
        return new BootShim(CommandLineArgsHolder.args,context);
    }
}
