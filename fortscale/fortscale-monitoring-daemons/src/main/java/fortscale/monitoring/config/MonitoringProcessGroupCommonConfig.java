package fortscale.monitoring.config;

import fortscale.utils.standardProcess.StandardProcessBaseConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

//import fortscale.utils.kafka.KafkaTopicSyncReader;

/**
 * Created by baraks on 4/25/2016.
 */
@Configuration
@Import({StandardProcessBaseConfig.class})
public class MonitoringProcessGroupCommonConfig {

}
