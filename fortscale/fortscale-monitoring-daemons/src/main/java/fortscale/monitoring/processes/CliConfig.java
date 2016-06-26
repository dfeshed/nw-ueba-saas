package fortscale.monitoring.processes;

import fortscale.monitoring.processes.group.config.MonitoringProcessGroupCommonConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MonitoringProcessGroupCommonConfig.class
})
@ComponentScan(basePackages = "fortscale.utils.cli")
public class CliConfig {

}
