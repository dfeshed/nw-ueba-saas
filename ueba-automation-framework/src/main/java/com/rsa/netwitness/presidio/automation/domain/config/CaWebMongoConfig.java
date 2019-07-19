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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@PropertySource("classpath:mongo_caweb.properties")
@EnableMongoRepositories(basePackages = {"presidio/integration/domain/repository"})
@EnableMongoAuditing
public class CaWebMongoConfig extends AbstractMongoConfiguration {

        private String mongoHostName;

        @Value("${mongo.host.port}")
        private int mongoHostPort;

        @Value("${mongo.db.name}")
        protected String mongoDBName;

        @Value("${mongo.db.user}")
        private String mongoUserName;

        @Value("${mongo.db.password}")
        private String mongoPassword;

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
            mongoHostName = HostConf.getServerHostname();

            if (!StringUtils.isEmpty(mongoUserName) && !StringUtils.isEmpty(mongoPassword)) {
                ServerAddress address = new ServerAddress(mongoHostName, mongoHostPort);
                List<MongoCredential> credentials = new ArrayList<>();
                credentials.add(
                        MongoCredential.createCredential(
                                mongoUserName,
                                mongoDBName,
                                EncryptionUtils.decrypt(mongoPassword).toCharArray()
                        )
                );

                client = new MongoClient(address, credentials);
            } else {
                client = new MongoClient(mongoHostName,mongoHostPort);
            }
            return client;
        }

        @Bean
        public com.mongodb.async.client.MongoClient asyncClient() throws Exception {

            String connectionString = String.format("mongodb://%s:%s",mongoHostName, mongoHostPort);
            ClusterSettings clusterSettings = ClusterSettings.builder()
                    .applyConnectionString(new ConnectionString(connectionString)).build();
            List<MongoCredential> credentials = new ArrayList<>();
            if (StringUtils.isNotBlank(mongoUserName) && StringUtils.isNotBlank(mongoPassword)) {
                credentials.add(
                        MongoCredential.createCredential(
                                mongoUserName,
                                mongoDBName,
                                EncryptionUtils.decrypt(mongoPassword).toCharArray()
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
        mongoHostName = HostConf.getServerHostname();

        if (!StringUtils.isEmpty(mongoUserName) && !StringUtils.isEmpty(mongoPassword)) {
            ServerAddress address = new ServerAddress(mongoHostName, mongoHostPort);
            List<MongoCredential> credentials = new ArrayList<>();
            try {
                credentials.add(
                        MongoCredential.createCredential(
                                mongoUserName,
                                mongoDBName,
                                EncryptionUtils.decrypt(mongoPassword).toCharArray()
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


