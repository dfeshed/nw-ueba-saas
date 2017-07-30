package fortscale.common.shell.config;

import fortscale.common.shell.config.ShellCommonCommandsConfig;
import fortscale.utils.shell.BootShimConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by efratn on 30/07/2017.
 */
@Configuration
@Import({ShellCommonCommandsConfig.class, BootShimConfig.class})
public class ShellableApplicationConfig {
}
