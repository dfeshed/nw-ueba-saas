package fortscale.common.shell.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(value = {"fortscale.common.shell.command"})
public class ShellCommonCommandsConfig {
}
