package org.flume.sink.mongo;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.flume.sink.base.AbstractPresidioSink;
import org.flume.sink.mongo.persistency.SinkMongoRepository;
import org.flume.sink.mongo.persistency.SinkMongoRepositoryImpl;

import org.flume.utils.MongoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;


import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PresidioMongoSink extends AbstractPresidioSink<DBObject> implements Configurable, Sink {

    private static Logger logger = LoggerFactory.getLogger(PresidioMongoSink.class);

    private static String[] mandatoryParams = {"collectionName", "dbName", "host", "hasAuthentication"};


    private SinkMongoRepository sinkMongoRepository;
    private boolean hasAuthentication;
    private String dbName;
    private String host;
    private int port;
    private String collectionName;
    private String username;
    private int batchSize;

    @Override
    public synchronized String getName() {
        return "mongo-sink";
    }

    @Override
    public void configure(Context context) {
        logger.debug("context is: {}", context);
        try {
            for (String mandatoryParam : mandatoryParams) {
                if (!context.containsKey(mandatoryParam)) {
                    throw new Exception(String.format("Missing mandatory param %s for Mongo sink. Mandatory params are: %s", mandatoryParam, Arrays.toString(mandatoryParams)));
                }
            }
            hasAuthentication = Boolean.parseBoolean(context.getString("hasAuthentication"));
            if (hasAuthentication) {
                if (!context.containsKey("username") || !context.containsKey("password")) {
                    throw new Exception("Missing username and/or password for authentication for Mongo sink (since hasAuthentication = true).");
                }

            }

            /* configure mongo */
            batchSize = Integer.parseInt(context.getString("batchSize", "1"));
            collectionName = context.getString("collectionName");
            dbName = context.getString("dbName");
            host = context.getString("host");
            port = Integer.parseInt(context.getString("port", "27017"));
            username = context.getString("username", "");
            final String password = context.getString("password", "");
            sinkMongoRepository = createRepository(dbName, host, port, username, password);
        } catch (Exception e) {
            final String errorMessage = "Failed to configure Presidio Mongo Source.";
            logger.error(errorMessage, e);
            throw new FlumeException(errorMessage, e);
        }
    }

    @Override
    protected List<DBObject> parseEvents(Channel channel) {
        Event flumeEvent;
        List<DBObject> eventsToSave = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            flumeEvent = channel.take();
            if (flumeEvent == null) {
                logger.trace("No events to sink...");
                break;
            }
            sinkCounter.incrementEventDrainAttemptCount();
            final DBObject parsedEvent = (DBObject) JSON.parse(new String(flumeEvent.getBody()));
            eventsToSave.add(parsedEvent);
        }

        return eventsToSave;
    }

    @Override
    protected int saveEvents(List<DBObject> eventsToSave) {
        final int numOfEventsToSave = eventsToSave.size();
        if (numOfEventsToSave != 0) {
            if (numOfEventsToSave == 1) { // or in other words if batchSize == 1
                sinkMongoRepository.save(eventsToSave.get(0), collectionName);
                sinkCounter.incrementEventDrainSuccessCount();
            } else {
                final int numOfSavedEvents = sinkMongoRepository.bulkSave(eventsToSave, collectionName);
                sinkCounter.addToEventDrainSuccessCount(numOfSavedEvents);
            }
        }
        return numOfEventsToSave;
    }

    private static SinkMongoRepository createRepository(String dbName, String host, int port, String username, String password) throws UnknownHostException {
        final MongoTemplate mongoTemplate = MongoUtils.createMongoTemplate(dbName, host, port, username, password);
        return new SinkMongoRepositoryImpl(mongoTemplate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("hasAuthentication", hasAuthentication)
                .append("dbName", dbName)
                .append("host", host)
                .append("port", port)
                .append("collectionName", collectionName)
                .append("username", username)
                .toString();
    }
}
