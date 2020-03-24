package com.rsa.netwitness.presidio.automation.domain.config;

import com.google.common.collect.Lists;
import com.mongodb.*;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.netty.NettyStreamFactoryFactory;
import com.rsa.netwitness.presidio.automation.utils.encription.EncryptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.rsa.netwitness.presidio.automation.config.AutomationConf.IS_MONGO_PASSWORD_ENCRYPTED;
import static com.rsa.netwitness.presidio.automation.config.AutomationConf.USE_JAR_DECRYPT;

@Configuration
@EnableMongoAuditing
@Import({MongoPropertiesReaderConfig.class})
@SpringBootConfiguration
@EnableMongoRepositories(basePackages = {"com.rsa.netwitness.presidio.automation.domain.repository"})
public class MongoConfig extends AbstractMongoConfiguration {

    private String mongoHostName;

    private int mongoHostPort;

    protected String mongoDBName;

    private String mongoUserName;

    private String mongoPassword;

    @Autowired
    private MongoPropertiesReader mongoPropertiesReader;

    @Override
    protected String getDatabaseName() {
        return mongoDBName;
    }

    public String getMongoUserName() {
        return mongoUserName;
    }

    public void setMongoUserName(String mongoUserName) {
        this.mongoUserName = mongoUserName;
    }

    @Bean
    public Mongo mongo() throws Exception {
        MongoClient client;
        initMongoProperties();
        if (!StringUtils.isEmpty(mongoUserName) && !StringUtils.isEmpty(mongoPassword)) {
            ServerAddress address = new ServerAddress(mongoHostName, mongoHostPort);
            List<MongoCredential> credentials = new ArrayList<>();
            credentials.add(
                    MongoCredential.createCredential(
                            mongoUserName,
                            mongoDBName,
                            EncryptionUtils.decrypt(mongoPassword, USE_JAR_DECRYPT).toCharArray()
                    )
            );

            client = new MongoClient(address, credentials);
        } else {
            client = new MongoClient(mongoHostName,mongoHostPort);
        }
        return client;
    }

    private void initMongoProperties() throws Exception {
        mongoPropertiesReader.initMongoPropeties();
        mongoHostName = mongoPropertiesReader.getMongoHostName();
        mongoHostPort = mongoPropertiesReader.getMongoHostPort();
        mongoDBName = mongoPropertiesReader.getMongoDBName();
        mongoUserName = mongoPropertiesReader.getMongoUserName();

        String password = mongoPropertiesReader.getMongoPassword();
        System.out.println("IS_MONGO_PASSWORD_ENCRYPTED=" + IS_MONGO_PASSWORD_ENCRYPTED);
        if (IS_MONGO_PASSWORD_ENCRYPTED) {
            mongoPassword = password;
        } else {
            mongoPassword = EncryptionUtils.encrypt(password);
        }
    }

    @Bean
    public com.mongodb.async.client.MongoClient asyncClient() throws Exception {
        initMongoProperties();
        String connectionString = String.format("mongodb://%s:%s",mongoHostName, mongoHostPort);
        ClusterSettings clusterSettings = ClusterSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString)).build();
        List<MongoCredential> credentials = new ArrayList<>();
        if (StringUtils.isNotBlank(mongoUserName) && StringUtils.isNotBlank(mongoPassword)) {
            credentials.add(
                    MongoCredential.createCredential(
                            mongoUserName,
                            mongoDBName,
                            EncryptionUtils.decrypt(mongoPassword, USE_JAR_DECRYPT).toCharArray()
                    )
            );
        }
        MongoClientSettings settings = MongoClientSettings.builder()
                .credentialList(credentials)
                .clusterSettings(clusterSettings)
                .streamFactoryFactory(new NettyStreamFactoryFactory())
                .writeConcern(WriteConcern.ACKNOWLEDGED)
                .build();
        com.mongodb.async.client.MongoClient mongoClient = MongoClients.create(settings);
        return mongoClient;
    }

    @Bean
    public MongoDatabase asyncClientDb() throws Exception {
        return  asyncClient().getDatabase(getDatabaseName());
    }

    @Override
    public MongoClient mongoClient() {
        MongoClient client;
        try {
            initMongoProperties();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!StringUtils.isEmpty(mongoUserName) && !StringUtils.isEmpty(mongoPassword)) {
            ServerAddress address = new ServerAddress(mongoHostName, mongoHostPort);
            List<MongoCredential> credentials = new ArrayList<>();
            try {
                credentials.add(
                        MongoCredential.createCredential(
                                mongoUserName,
                                mongoDBName,
                                EncryptionUtils.decrypt(mongoPassword, USE_JAR_DECRYPT).toCharArray()
                        )
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

            client = new MongoClient(address, credentials);
        } else {
            client = new MongoClient(mongoHostName,mongoHostPort);
        }
        return client;
    }

    public @Bean
    MongoTemplate mongoTemplate() throws Exception {

        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());

        return mongoTemplate;

    }

    @Override
    protected Collection<String> getMappingBasePackages() {
        return Lists.newArrayList("fortscale", "presidio");
    }

}