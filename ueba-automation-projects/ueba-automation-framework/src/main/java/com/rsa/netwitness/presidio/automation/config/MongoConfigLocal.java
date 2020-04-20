package com.rsa.netwitness.presidio.automation.config;

import com.rsa.netwitness.presidio.automation.utils.common.Lazy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;

public enum MongoConfigLocal {
    MONGO_CONFIG_LOCAL;

    private Lazy<Properties> mongoPropertiesHolder = new Lazy<>();
    private Supplier<Properties> mongoProperties = () -> mongoPropertiesHolder.getOrCompute(this::getMongoPropertiesResource);

    public String hostname = mongoProperties.get().getOrDefault("mongo.host.name", "localhost").toString();
    public int port = Integer.valueOf(mongoProperties.get().getOrDefault("mongo.host.port", 27017).toString());
    public String mongoDBName = mongoProperties.get().getOrDefault("mongo.db.name", "presidio").toString();
    public String mongoPassword = mongoProperties.get().getOrDefault("mongo.db.password", "netwitness").toString();
    public String mongoUserName = mongoProperties.get().getOrDefault("mongo.db.user", "presidio").toString();

    private Properties getMongoPropertiesResource() {
        String RESOURCE_NAME = "mongo.properties";
        Properties props = new Properties();

        try(InputStream resourceStream = this.getClass().getClassLoader().getResourceAsStream(RESOURCE_NAME)) {
            props.load(Objects.requireNonNull(resourceStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }


}
