package org.flume.sink.mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.MongoException;
import fortscale.domain.core.AbstractDocument;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.persistency.mongo.MongoUtils;
import org.apache.flume.persistency.mongo.PresidioFilteredEventsMongoRepository;
import org.apache.flume.persistency.mongo.SinkMongoRepository;
import org.apache.flume.persistency.mongo.SinkMongoRepositoryImpl;
import org.flume.sink.base.AbstractPresidioSink;
import org.flume.utils.ConnectorSharedPresidioMonitorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.ReflectionUtils;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.sdk.api.domain.AbstractInputDocument;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;

import static org.apache.coyote.http11.Constants.a;
import static org.apache.flume.CommonStrings.BATCH_SIZE;
import static org.apache.flume.CommonStrings.COLLECTION_NAME;
import static org.apache.flume.CommonStrings.DB_NAME;
import static org.apache.flume.CommonStrings.HAS_AUTHENTICATION;
import static org.apache.flume.CommonStrings.HOST;
import static org.apache.flume.CommonStrings.PASSWORD;
import static org.apache.flume.CommonStrings.PORT;
import static org.apache.flume.CommonStrings.USERNAME;


/**
 * an AbstractPresidioSink that writes events to MongoDB
 */
public class PresidioMongoSink<T extends AbstractDocument> extends AbstractPresidioSink<T> {

    private static Logger logger = LoggerFactory.getLogger(PresidioMongoSink.class);


    private static ObjectMapper mapper;
    private static final String RECORD_TYPE = "recordType";
    private static final String INDEX_FIELD_NAME = "indexFieldName";


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
    private String indexFieldName;
    private String timeFieldName;




    public PresidioMongoSink() {
        this(null);
    }

    public PresidioMongoSink(SinkMongoRepository sinkMongoRepository) {
        this.sinkMongoRepository = sinkMongoRepository;

    }

    @Override
    public synchronized String getName() {
        return "mongo-" + super.getName();
    }

    @Override
    public void doPresidioConfigure(Context context) {
        logger.debug("context is: {}", context);
        try {
            for (String mandatoryParam : mandatoryParams) {
                if (!context.containsKey(mandatoryParam)) {
                    throw new Exception(String.format("Missing mandatory param %s for %s. Mandatory params are: %s", mandatoryParam, getName(), Arrays.toString(mandatoryParams)));
                }
            }

            hasAuthentication = Boolean.parseBoolean(context.getString(HAS_AUTHENTICATION));
            if (hasAuthentication) {
                if (!context.containsKey(USERNAME) || !context.containsKey(PASSWORD)) {
                    throw new Exception(String.format("Missing %s and/or %s for authentication for %s (since %s = true).", USERNAME, PASSWORD, getName(), HAS_AUTHENTICATION));
                }
            }

            final String recordTypeAsString = context.getString(RECORD_TYPE);
            if (Class.forName(recordTypeAsString) == null) {
                throw new Exception(String.format("%s:[%s] is not a valid type for %s.", RECORD_TYPE, recordTypeAsString, getName()));
            } else {
                recordType = getRecordType(recordTypeAsString);
            }

            batchSize = context.getInteger(BATCH_SIZE, 1000);
            collectionName = context.getString(COLLECTION_NAME);

            initRepository(context);

        } catch (Exception e) {
            final String errorMessage = "Failed to configure " + getName();
            logger.error(errorMessage, e);
            throw new FlumeException(errorMessage, e);
        }
    }


    @Override
    protected List<T> getEvents() throws Exception {
        Event flumeEvent;
        List<T> eventsToSave = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            flumeEvent = this.getChannel().take();
            if (flumeEvent == null) {
                logger.trace("No events to sink...");
                break;
            }
            final boolean gotControlDoneMessage = isControlDoneMessage(flumeEvent);
            if (gotControlDoneMessage) {
                logger.debug("Got control message. Finishing batch and closing.");
            }
            isDone = isBatch && gotControlDoneMessage;
//
//            sinkCounter.incrementEventDrainAttemptCount();


            T parsedEvent;
            final String eventBody = new String(flumeEvent.getBody());
            try {
                final Class<T> recordType = this.recordType;
                parsedEvent = mapper.readValue(eventBody, recordType);
            } catch (Exception e) {
                final Map<String, String> eventHeaders = flumeEvent.getHeaders();
                if (!e.getClass().isAssignableFrom(MongoException.class)) {
                    PresidioFilteredEventsMongoRepository.saveFailedFlumeEvent(getApplicationName() + "-" + this.getClass().getSimpleName(), e.getMessage(), flumeEvent);
                }
                final String errorMessage = String.format("PresidioMongoSink failed to sink event. Can't get event since event is not of correct type. expected type:%s, actual event: body:[ %s ], headers:[ %s ].", recordType, eventBody, eventHeaders);
                logger.error(errorMessage);
                throw e;
            }

            eventsToSave.add(parsedEvent);
        }

        if (isDone && this.getChannel().take() != null) {
            logger.warn("Got a control message DONE while there are still more records to process. This is not a valid state!");
        }

        return eventsToSave;
    }

    @Override
    protected void monitorNumberOfReadEvents(int number, Instant logicalHour) {
            logger.warn(this.getClass().getName()+" is not supporting monitoring");
    }

    @Override
    protected void monitorNumberOfSavedEvents(int number, Instant logicalHour) {
        logger.warn(this.getClass().getName()+" is not supporting monitoring");
    }

    @Override
    protected void monitorNumberOfUnassignableEvents(int number, String schema, Instant logicalHour) {
        logger.warn(this.getClass().getName()+" is not supporting monitoring");
    }

    @Override
    protected void monitorUnknownError(int number, Instant logicalHour) {
        logger.warn(this.getClass().getName()+" is not supporting monitoring");
    }

    @Override
    protected Instant getLogicalHour(T event){
        logger.warn(this.getClass().getName()+" is not supporting monitoring");
      return null;
    }

    @Override
    protected void stopMonitoring(){
        logger.warn(this.getClass().getName()+" is not supporting monitoring");
    }




    @Override
    @SuppressWarnings("unchecked")
    protected int saveEvents(List<T> eventsToSave) throws Exception {
        final int numOfEventsToSave = eventsToSave.size();
        int numOfSavedEvents;
        if (numOfEventsToSave == 1) { // or in other words if batchSize == 1
            sinkMongoRepository.save(eventsToSave.get(0), collectionName);
            numOfSavedEvents = 1;
//                sinkCounter.incrementEventDrainSuccessCount();
        } else {
            numOfSavedEvents = sinkMongoRepository.bulkSave(eventsToSave, collectionName);
//                sinkCounter.addToEventDrainSuccessCount(numOfSavedEvents);
        }

        if (StringUtils.isNotEmpty(indexFieldName)) {
            sinkMongoRepository.ensureIndex(collectionName, indexFieldName);
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


    private void initRepository(Context context) throws UnknownHostException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException {
        dbName = context.getString(DB_NAME);
        host = context.getString(HOST);
        port = context.getInteger(PORT, 27017);
        username       = context.getString(USERNAME, "");
        indexFieldName = context.getString(INDEX_FIELD_NAME, "");
        final String password = context.getString(PASSWORD, "");
        if (sinkMongoRepository == null) {
            sinkMongoRepository = createRepository(dbName, host, port, username, password);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("sinkMongoRepository", sinkMongoRepository)
                .append("hasAuthentication", hasAuthentication)
                .append("dbName", dbName)
                .append("host", host)
                .append("port", port)
                .append("collectionName", collectionName)
                .append("username", username)
                .append("batchSize", batchSize)
                .append("recordType", recordType)
                .append("indexFieldName", indexFieldName)
                .append("minBackoffSleep", minBackoffSleep)
                .append("maxBackoffSleep", maxBackoffSleep)
                .append("backoffSleepIncrement", backoffSleepIncrement)
                .append("isBatch", isBatch)
                .append("applicationName", applicationName)
                .append("isDone", isDone)
                .toString();
    }
}
