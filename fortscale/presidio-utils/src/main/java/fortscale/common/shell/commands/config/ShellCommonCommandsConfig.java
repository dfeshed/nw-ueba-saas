package fortscale.common.shell.commands.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(value = {"fortscale.utils.shell.commands.common"})
public class ShellCommonCommandsConfig {
}
