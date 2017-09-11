package org.apache.flume.persistency.mongo;


import fortscale.utils.logging.Logger;
import org.apache.flume.Event;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class PresidioFilteredEventsMongoRepository {

    private static final Logger logger = Logger.getLogger(PresidioFilteredEventsMongoRepository.class);
    private static final String COLLECTION_NAME = "filtered_events";

    private static SinkMongoRepository sinkMongoRepository = null;

    static {
        try {
            //todo: take from config server
            sinkMongoRepository = createRepository("presidio", "localhost", 27017, "presidio", "iYTLjyA0VryKhpkvBrMMLQ==");
        } catch (Exception e) {
            logger.error("Failed to create PresidioFilteredEventsMongoRepository!!!");
        }
    }


    private static SinkMongoRepository createRepository(String dbName, String host, int port, String username, String password) throws UnknownHostException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException {
        final MongoTemplate mongoTemplate = MongoUtils.createMongoTemplate(dbName, host, port, username, password);
        return new SinkMongoRepositoryImpl(mongoTemplate);
    }

    public static void saveFailedEvents(Event filteredEvent) {
        sinkMongoRepository.saveFlumeEvent(filteredEvent, COLLECTION_NAME);
    }


}
