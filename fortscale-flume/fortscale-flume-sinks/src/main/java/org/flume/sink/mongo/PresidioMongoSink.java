package org.flume.sink.mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fortscale.domain.core.AbstractDocument;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.flume.sink.base.AbstractPresidioSink;
import org.flume.sink.mongo.persistency.SinkMongoRepository;
import org.flume.sink.mongo.persistency.SinkMongoRepositoryImpl;
import org.flume.utils.MongoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.flume.CommonStrings.BATCH_SIZE;
import static org.apache.flume.CommonStrings.COLLECTION_NAME;
import static org.apache.flume.CommonStrings.DB_NAME;
import static org.apache.flume.CommonStrings.HAS_AUTHENTICATION;
import static org.apache.flume.CommonStrings.HOST;
import static org.apache.flume.CommonStrings.PASSWORD;
import static org.apache.flume.CommonStrings.PORT;
import static org.apache.flume.CommonStrings.USERNAME;

public class PresidioMongoSink<T extends AbstractDocument> extends AbstractPresidioSink<T> {

    private static Logger logger = LoggerFactory.getLogger(PresidioMongoSink.class);

    private static ObjectMapper mapper;
    private static final String RECORD_TYPE = "recordType";

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    private static String[] mandatoryParams = {COLLECTION_NAME, DB_NAME, HOST, HAS_AUTHENTICATION, RECORD_TYPE};


    private SinkMongoRepository sinkMongoRepository;
    private boolean hasAuthentication;
    private String dbName;
    private String host;
    private int port;
    private String collectionName;
    private String username;
    private int batchSize;
    private Class<T> recordType;

    @Override
    public synchronized String getName() {
        return "mongo-" + super.getName();
    }

    @Override
    public void configure(Context context) {
        logger.debug("context is: {}", context);
        try {
            for (String mandatoryParam : mandatoryParams) {
                if (!context.containsKey(mandatoryParam)) {
                    throw new Exception(String.format("Missing mandatory param %s for %s. Mandatory params are: %s", mandatoryParam, getName(), Arrays.toString(mandatoryParams)));
                }
            }

            final String recordTypeAsString = context.getString(RECORD_TYPE);
            if (Class.forName(recordTypeAsString) == null) {
                throw new Exception(String.format("%s:[%s] is not a valid type for %s.", RECORD_TYPE, recordTypeAsString, getName()));
            }

            hasAuthentication = Boolean.parseBoolean(context.getString(HAS_AUTHENTICATION));
            if (hasAuthentication) {
                if (!context.containsKey(USERNAME) || !context.containsKey(PASSWORD)) {
                    throw new Exception(String.format("Missing %s and/or %s for authentication for %s (since %s = true).", USERNAME, PASSWORD, getName(), HAS_AUTHENTICATION));
                }
            }

            /* configure mongo */
            recordType = getRecordType(recordTypeAsString);
            batchSize = Integer.parseInt(context.getString(BATCH_SIZE, "1"));
            collectionName = context.getString(COLLECTION_NAME);
            dbName = context.getString(DB_NAME);
            host = context.getString(HOST);
            port = Integer.parseInt(context.getString(PORT, "27017"));
            username = context.getString(USERNAME, "");
            final String password = context.getString(PASSWORD, "");
            sinkMongoRepository = createRepository(dbName, host, port, username, password);
        } catch (Exception e) {
            final String errorMessage = "Failed to configure " + getName();
            logger.error(errorMessage, e);
            throw new FlumeException(errorMessage, e);
        }
    }

    @Override
    protected List<T> getEvents() throws IOException {
        Event flumeEvent;
        List<T> eventsToSave = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            flumeEvent = this.getChannel().take();
            if (flumeEvent == null) {
                logger.trace("No events to sink...");
                break;
            }
            final boolean isControlDoneMessage = isGotControlDoneMessage(flumeEvent);
            if (isControlDoneMessage) {
                break;
            }

            sinkCounter.incrementEventDrainAttemptCount();

            T parsedEvent;
            final Class<T> recordType = this.recordType;

            parsedEvent = mapper.readValue(new String(flumeEvent.getBody()), recordType);

            eventsToSave.add(parsedEvent);
        }

        return eventsToSave;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected int saveEvents(List<T> eventsToSave) throws Exception {
        final int numOfEventsToSave = eventsToSave.size();
        int numOfSavedEvents = 0;
        if (numOfEventsToSave != 0) {
            if (numOfEventsToSave == 1) { // or in other words if batchSize == 1
                sinkMongoRepository.save(eventsToSave.get(0), collectionName);
                numOfSavedEvents = 1;
                sinkCounter.incrementEventDrainSuccessCount();
            } else {
                numOfSavedEvents = sinkMongoRepository.bulkSave(eventsToSave, collectionName);
                sinkCounter.addToEventDrainSuccessCount(numOfSavedEvents);
            }
        }

        return numOfSavedEvents;
    }

    private static SinkMongoRepository createRepository(String dbName, String host, int port, String username, String password) throws UnknownHostException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException {
        final MongoTemplate mongoTemplate = MongoUtils.createMongoTemplate(dbName, host, port, username, password);
        return new SinkMongoRepositoryImpl(mongoTemplate);
    }

    @SuppressWarnings("unchecked")
    private Class<T> getRecordType(String recordTypeAsString) throws ClassNotFoundException {
        return (Class<T>) Class.forName(recordTypeAsString);
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
