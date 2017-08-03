package org.flume.source.mongo;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fortscale.domain.core.AbstractDocument;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.FlumeException;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.source.AbstractEventDrivenSource;
import org.flume.CommonStrings;
import org.flume.source.mongo.persistency.SourceMongoRepository;
import org.flume.source.mongo.persistency.SourceMongoRepositoryImpl;
import org.flume.utils.DateUtils;
import org.flume.utils.MongoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.flume.CommonStrings.*;


public class PresidioMongoSource extends AbstractEventDrivenSource implements Configurable, EventDrivenSource {

    private static Logger logger = LoggerFactory.getLogger(PresidioMongoSource.class);

    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }


    private final SourceCounter sourceCounter = new SourceCounter("mongo-source-counter");

    private SourceMongoRepository sourceMongoRepository;

    private static String[] mandatoryParams = {COLLECTION_NAME, DB_NAME, HOST, HAS_AUTHENTICATION, START_DATE, END_DATE};

    private Instant startDate;
    private Instant endDate;
    private int batchSize;
    private String collectionName;
    private String dbName;
    private String host;
    private int port;
    private String username;
    private String dateTimeField;


    @Override
    protected void doConfigure(Context context) throws FlumeException {
        logger.debug("context is: {}", context);
        try {
            for (String mandatoryParam : mandatoryParams) {
                if (!context.containsKey(mandatoryParam)) {
                    throw new Exception(String.format("Missing mandatory param %s for %s. Mandatory params are: %s",
                            getName(), mandatoryParam, Arrays.toString(mandatoryParams)));
                }
            }
            boolean hasAuthentication = Boolean.parseBoolean(context.getString(HAS_AUTHENTICATION));
            if (hasAuthentication) {
                if (!context.containsKey(CommonStrings.USERNAME) || !context.containsKey(CommonStrings.PASSWORD)) {
                    throw new Exception(String.format("Missing %s and/or %s for authentication for %s (since %s = true).",
                            CommonStrings.USERNAME, CommonStrings.PASSWORD, getName(), CommonStrings.HAS_AUTHENTICATION));
                }

            }

            final String dateFormat = context.getString(DATE_FORMAT, DEFAULT_DATE_FORMAT);
            final String endDateAsString = context.getString(END_DATE);
            final String startDateAsString = context.getString(START_DATE);
            startDate = DateUtils.getDateFromText(startDateAsString, dateFormat);
            endDate = DateUtils.getDateFromText(endDateAsString, dateFormat);

            /* configure mongo */
            batchSize = Integer.parseInt(context.getString(BATCH_SIZE, "1"));
            collectionName = context.getString(COLLECTION_NAME);
            dbName = context.getString(DB_NAME);
            host = context.getString(HOST);
            port = Integer.parseInt(context.getString(PORT, "27017"));
            username = context.getString(USERNAME, "");
            final String password = context.getString(PASSWORD, "");
            dateTimeField = context.getString(DATE_TIME_FIELD, DEFAULT_DATE_TIME_FIELD_NAME);
            sourceMongoRepository = createRepository(dbName, host, port, username, password);
        } catch (Exception e) {
            final String errorMessage = "Failed to configure ." + getName();
            logger.error(errorMessage, e);
            throw new FlumeException(errorMessage, e);
        }
    }

    @Override
    public void start() {
        sourceCounter.start();
        super.start();
    }

    @Override
    protected void doStart() throws FlumeException {
        logger.debug("{} is processing events for {}: {}, {}: {}.",
                getName(), START_DATE, END_DATE, startDate, endDate);
        sourceCounter.start();

        try {
            int pageNum = 0;// first page
            List<AbstractDocument> currentPage = sourceMongoRepository.findByDateTimeBetween(
                    collectionName, startDate.minusMillis(1), endDate, pageNum, batchSize, dateTimeField);
            if (currentPage.size() == 0) {
                logger.warn("Failed to process events for {}: {}, {]: {}. There were no events to process",
                        START_DATE, startDate, END_DATE, endDate);
            } else {
                processPage(currentPage); //handle first event
                pageNum++;
                while (currentPage.size() == batchSize) { //kind of (maybe)hasNext()
                    currentPage = sourceMongoRepository.findByDateTimeBetween(
                            collectionName, startDate.minusMillis(1), endDate, pageNum, batchSize, dateTimeField);
                    processPage(currentPage);
                    pageNum++;
                }
            }
            logger.debug("{} has finished processing events for {}: {}, {}: {}.",
                    getName(), START_DATE, startDate, END_DATE, endDate);
            startDate = endDate; // advance the cursor
            this.stop();

        } catch (Exception e) {
            logger.error("{} has failed to process events for }: {}, {}: {}.",
                    getName(), START_DATE, startDate, END_DATE, endDate);
            logger.error(e.getMessage());
            this.stop();
        }
    }

    @Override
    public void stop() {
        sourceCounter.stop();
        super.stop();
    }


    @Override
    protected void doStop() throws FlumeException {
        logger.info("{} is stopping...", getName());
    }

    private SourceMongoRepository createRepository(String dbName, String host, int port, String username, String password)
            throws UnknownHostException {
        final MongoTemplate mongoTemplate = MongoUtils.createMongoTemplate(dbName, host, port, username, password);
        return new SourceMongoRepositoryImpl(mongoTemplate);
    }

    private void processEvent(AbstractDocument event) throws JsonProcessingException {
        sourceCounter.incrementEventAcceptedCount();
        final String eventAsJson;
        eventAsJson = mapper.writeValueAsString(event);
        final Event flumeEvent = EventBuilder.withBody(eventAsJson, Charset.defaultCharset());
        logger.trace("{} has finished processing event {}. Sending event to channel", getName(), flumeEvent);
        getChannelProcessor().processEvent(flumeEvent); // Store the Event into this Source's associated Channel(s)
    }

    private void processPage(List<AbstractDocument> pageEvents) throws Exception {
        sourceCounter.addToEventReceivedCount(pageEvents.size());
        if (!validateEvents(pageEvents)) { //todo
            final String errorMessage = "event validation failed!";
            logger.error(errorMessage);
            throw new Exception(errorMessage);
        } else {
            for (AbstractDocument pageEvent : pageEvents) {
                processEvent(pageEvent);
            }
        }
    }


    private boolean validateEvents(List<AbstractDocument> events) {
        return events != null; //todo
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("startDate", startDate)
                .append("endDate", endDate)
                .append("batchSize", batchSize)
                .append("collectionName", collectionName)
                .append("dbName", dbName)
                .append("host", host)
                .append("port", port)
                .append("username", username)
                .toString();
    }
}
