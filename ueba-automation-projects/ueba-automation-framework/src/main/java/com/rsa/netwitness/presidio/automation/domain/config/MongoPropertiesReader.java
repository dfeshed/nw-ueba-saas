package com.rsa.netwitness.presidio.automation.domain.config;


import presidio.config.server.client.ConfigurationServerClientService;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MongoPropertiesReader {

    private String mongoHostName;
    private int mongoHostPort;
    private String mongoDBName;
    private String mongoUserName;
    private String mongoPassword;
    private String configServerUri;
    private ConfigurationServerClientService configurationServerClientService;


    public MongoPropertiesReader(ConfigurationServerClientService configurationServerClientService, String configServerUri) {
        this.configurationServerClientService = configurationServerClientService;
        this.configServerUri = configServerUri;
    }


    public void initMongoPropeties() throws Exception {

        if (mongoHostName != null) {
//            skip if init already occurred
            return;
        }


        Properties applicationProperties = configurationServerClientService.readConfigurationAsProperties("application");
        mongoHostName = applicationProperties.getProperty("mongo.host.name");

//        if configserver is self pointing on mongo host, then it's a remote one :)
        if (mongoHostName.equals("localhost")) {
            Pattern pattern = Pattern.compile("http\\:\\/\\/(.*)\\:[0-9][0-9][0-9][0-9]");
            Matcher matcher = pattern.matcher(configServerUri);
            if (matcher.find()) {
                mongoHostName = matcher.group(1);
            }
        }
        mongoHostPort = Integer.parseInt(applicationProperties.getProperty("mongo.host.port"));
        mongoDBName = applicationProperties.getProperty("mongo.db.name");
        mongoPassword = applicationProperties.getProperty("mongo.db.password");
        mongoUserName = applicationProperties.getProperty("mongo.db.user");
    }

    public String getMongoHostName() {
        return mongoHostName;
    }

    public int getMongoHostPort() {
        return mongoHostPort;
    }

    public String getMongoDBName() {
        return mongoDBName;
    }

    public String getMongoPassword() {
        return mongoPassword;
    }

    public String getMongoUserName() {
        return mongoUserName;
    }
}

