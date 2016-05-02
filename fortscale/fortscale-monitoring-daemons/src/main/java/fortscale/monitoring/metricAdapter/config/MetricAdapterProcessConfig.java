//package fortscale.monitoring.metricAdapter.config;
//
//import fortscale.monitoring.config.MonitoringProcessGroupCommonConfig;
//import fortscale.monitoring.metricAdapter.init.InfluxDBStatsInit;
//import fortscale.monitoring.metricAdapter.stats.MetricAdapterStats;
//import fortscale.utils.influxdb.InfluxdbClient;
//import fortscale.utils.kafka.KafkaTopicSyncReader;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//
///**
// * Created by baraks on 4/25/2016.
// */
//@Configuration
//@Import({MonitoringProcessGroupCommonConfig.class})
//public class MetricAdapterProcessConfig {
//    @Value("${influxdb.ip}")
//    private String ip;
//    @Value("${influxdb.port}")
//    private String port;
//    @Value("${influxdb.rest.loglevel}")
//    private String logLevel;
//    @Value("${influxdb.db.readTimeout.seconds}")
//    private long readTimeout;
//    @Value("${influxdb.db.writeTimeout.seconds}")
//    private long writeTimeout;
//    @Value("${influxdb.db.connectTimeout.seconds}")
//    private long connectTimeout;
//
//    @Bean
//    InfluxDBStatsInit influxDBStatsInit(){
//        return new InfluxDBStatsInit();
//    }
//    @Bean
//    InfluxdbClient influxdbClient(){
//        return new InfluxdbClient(ip,port,logLevel,readTimeout,writeTimeout,connectTimeout);
//    }
//    @Bean
//    KafkaTopicSyncReader kafkaTopicSyncReader(){return new KafkaTopicSyncReader("MetricAdapterClientId","metrics",0);}
//
//    @Bean
//    MetricAdapterStats metricAdapterStats(){return new MetricAdapterStats();}
//}
