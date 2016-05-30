package fortscale.monitoring.processes.group.config;

import fortscale.utils.process.standardProcess.config.StandardProcessBaseConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({StandardProcessBaseConfig.class})
public class MonitoringProcessGroupCommonConfig {

}
