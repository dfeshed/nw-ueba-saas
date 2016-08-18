package fortscale.monitoring.processes.samza.collector.config;

import fortscale.monitoring.external.stats.samza.collector.service.config.SamzaMetricsCollectorServiceConfig;
import fortscale.monitoring.processes.group.config.MonitoringProcessGroupCommonConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({MonitoringProcessGroupCommonConfig.class, SamzaMetricsCollectorServiceConfig.class})
public class SamzaMetricsCollectorConfig {


}
