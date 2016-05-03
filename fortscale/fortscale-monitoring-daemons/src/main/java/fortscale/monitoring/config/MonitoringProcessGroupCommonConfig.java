package fortscale.monitoring.config;

import fortscale.utils.standardProcess.standardProcessConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

//import fortscale.utils.kafka.KafkaTopicSyncReader;

/**
 * Created by baraks on 4/25/2016.
 */
@Configuration
@EnableSpringConfigured
@EnableAspectJAutoProxy
@Import({standardProcessConfig.class})
public class MonitoringProcessGroupCommonConfig {


}
