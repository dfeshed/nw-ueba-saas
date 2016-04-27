package fortscale.utils.influxdb.config;

import fortscale.utils.influxdb.InfluxdbClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
@EnableAspectJAutoProxy(proxyTargetClass = false)
@PropertySource("classpath:META-INF/influxdb/config/InfluxdbClient.properties")
public class InfluxdbClientConfig {
    @Value("${influxdb.ip}")
    private String ip;
    @Value("${influxdb.port}")
    private String port;
    @Value("${influxdb.rest.loglevel}")
    private String logLevel;
    @Value("${influxdb.db.readTimeout.seconds}")
    private long readTimeout;
    @Value("${influxdb.db.writeTimeout.seconds}")
    private long writeTimeout;
    @Value("${influxdb.db.connectTimeout.seconds}")
    private long connectTimeout;
    @Value("${influxdb.db.batch.actions}")
    private int batchActions;
    @Value("${influxdb.db.batch.flushInterval}")
    private int flushInterval;

    @Bean
    InfluxdbClient influxdbClient(){
        return new InfluxdbClient(ip,port,logLevel,readTimeout,writeTimeout,connectTimeout,batchActions,flushInterval);
    }
}
