package com.rsa.netwitness.presidio.automation.domain.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import presidio.config.server.client.ConfigurationServerClientService;


@Configuration
@EnableMongoAuditing
@Import(ConfigServerClientServiceConfiguration.class)
public class MongoPropertiesReaderConfig {

    @Autowired
    private ConfigurationServerClientService configurationServerClientService;
    @Value("${spring.cloud.config.uri}")
    private  String configServerUri;

    @Bean
    public MongoPropertiesReader mongoPropertiesReader() {
        return new MongoPropertiesReader(configurationServerClientService, configServerUri);
    }
}

