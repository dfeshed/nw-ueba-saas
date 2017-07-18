package org.flume.utils;




import com.mongodb.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.util.StringUtils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MongoUtils {


    public static MongoTemplate createMongoTemplate(String dbName, String host, int port, String username, String password) throws UnknownHostException {
        final MongoClient mongoClient = createMongoClient(dbName, host, port, username, password);
        return new MongoTemplate(new SimpleMongoDbFactory(mongoClient, dbName));
    }

    private static MongoClient createMongoClient(String dbName, String host, int port, String username, String password) throws UnknownHostException {
        MongoClient client;
        MongoClientOptions writeOptions = MongoClientOptions.builder()
                .writeConcern(WriteConcern.ACKNOWLEDGED)
                .build();
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            ServerAddress address = new ServerAddress(host, port);
            List<MongoCredential> credentials = new ArrayList<>();
            credentials.add(
                    MongoCredential.createCredential(
                            username,
                            dbName,
                            password.toCharArray()
                    )
            );

            client = new MongoClient(address, credentials, writeOptions);
        } else {
            client = new MongoClient(host, port);
        }
        return client;
    }


}
