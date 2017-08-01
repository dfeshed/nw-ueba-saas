package fortscale.common.shell.config;

import fortscale.utils.shell.InstantConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(value = {"fortscale.common.shell.command"})
public class  ShellCommonCommandsConfig {

    @Bean
    public InstantConverter instantConverter() {
        return new InstantConverter();
    }
}