package com.rsa.netwitness.presidio.automation.mongo;

import ch.qos.logback.classic.Logger;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;
import org.slf4j.LoggerFactory;
import java.util.function.Supplier;
import static com.rsa.netwitness.presidio.automation.config.EnvironmentProperties.ENVIRONMENT_PROPERTIES;
import static org.assertj.core.api.Assertions.assertThat;

class MongoClientEsaServer {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(MongoClientEsaServer.class);

    static final String DB_USERNAME = "deploy_admin";
    static final String DB_PASSWORD = "netwitness";
    static final String DB_NAME = "admin";

    private static final Lazy<MongoClient> ds = new Lazy<>();

    private static Supplier<MongoClient> initDataSource = () -> {
        LOGGER.info("Going to connect to ESA server MongoDB.");
        MongoCredential credential = MongoCredential.createCredential(DB_USERNAME, DB_NAME, DB_PASSWORD.toCharArray());
        MongoClientOptions clientOptions = MongoClientOptions.builder().connectionsPerHost(2).build();
        String esaServer = ENVIRONMENT_PROPERTIES.esaAnalyticsServerIp();
        assertThat(esaServer).as("Hostname is missing from properties").isNotBlank();
        LOGGER.info("Connection established to " + esaServer);
        return new MongoClient(new ServerAddress(esaServer, 27017), credential, clientOptions);
    };

    private MongoClientEsaServer() { }

    static MongoClient getConnection() {
        return ds.getOrCompute(initDataSource);
    }
}
