package fortscale.monitoring.config;

import fortscale.monitoring.metricAdapter.init.InfluxDBStatsInit;
import fortscale.utils.influxdb.InfluxdbClient;
import fortscale.utils.kafka.KafkaTopicSyncReader;
import fortscale.utils.standardProcess.StandardProcessConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

/**
 * Created by baraks on 4/25/2016.
 */
@Configuration
@EnableSpringConfigured
@EnableAspectJAutoProxy
@Import({StandardProcessConfig.class})
public class MonitoringProcessGroupCommonConfig {


}
