package source.presidioMongoSource;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import domain.AbstractDocument;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.FlumeException;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.source.AbstractEventDrivenSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import source.presidioMongoSource.persistency.SourceMongoRepository;
import source.presidioMongoSource.persistency.SourceMongoRepositoryImpl;
import utils.DateUtils;
import utils.MongoUtils;

import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.List;


public class PresidioMongoSource extends AbstractEventDrivenSource implements Configurable, EventDrivenSource {

    private static Logger logger = LoggerFactory.getLogger(PresidioMongoSource.class);

    private final SourceCounter sourceCounter = new SourceCounter("mongo-source-counter");
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private SourceMongoRepository sourceMongoRepository;

    private static String[] mandatoryParams = {"collectionName", "dbName", "host", "hasAuthentication", "endDate", "startDate"};

    private boolean hasAuthentication;
    private Instant startDate;
    private Instant endDate;
    private int batchSize;
    private String collectionName;
    private String dbName;
    private String host;
    private int port;
    private String username;


    @Override
    protected void doConfigure(Context context) throws FlumeException {
        logger.debug("context is: {}", context);
        try {
            for (String mandatoryParam : mandatoryParams) {
                if (!context.containsKey(mandatoryParam)) {
                    throw new Exception(String.format("Missing mandatory param %s for Mongo sink. Mandatory params are: %s", mandatoryParam, mandatoryParams));
                }
            }
            hasAuthentication = Boolean.parseBoolean(context.getString("hasAuthentication"));
            if (hasAuthentication) {
                if (!context.containsKey("username") || !context.containsKey("password")) {
                    throw new Exception("Missing username and/or password for authentication for Mongo sink (since hasAuthentication = true).");
                }

            }

            final String dateFormat = context.getString("dateFormat", DEFAULT_DATE_FORMAT);
            final String endDateAsString = context.getString("endDate");
            final String startDateAsString = context.getString("startDate");
            startDate = DateUtils.getDateFromText(startDateAsString, dateFormat);
            endDate = DateUtils.getDateFromText(endDateAsString, dateFormat);

            /* configure mongo */
            batchSize = Integer.parseInt(context.getString("batchSize", "1"));
            collectionName = context.getString("collectionName");
            dbName = context.getString("dbName");
            host = context.getString("host");
            port = Integer.parseInt(context.getString("port", "27017"));
            username = context.getString("username", "");
            final String password = context.getString("password", "");
            sourceMongoRepository = createRepository(dbName, host, port, username, password);
        } catch (Exception e) {
            final String errorMessage = "Failed to configure Presidio Mongo Source.";
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
        logger.debug("PresidioMongoSource is processing events for startDate: {}, endDate: {}.");
        sourceCounter.start();

        try {
            int pageNum = 0;// first page
            List<AbstractDocument> currentPage = sourceMongoRepository.findByDateTimeBetween(collectionName, startDate.minusMillis(1), endDate, pageNum, batchSize);
            if (currentPage.size() == 0) {
                logger.error("Failed to process events for startDate: {}, endDate: {}. There were no events to process", startDate, endDate);
            } else {
                processPage(currentPage); //handle first event
                pageNum++;
                while (currentPage.size() == batchSize) { //kind of (maybe)hasNext()
                    currentPage = sourceMongoRepository.findByDateTimeBetween(collectionName, startDate.minusMillis(1), endDate, pageNum, batchSize);
                    processPage(currentPage);
                    pageNum++;
                }
            }
            logger.debug("PresidioMongoSource has finished processing events for startDate: {}, endDate: {}.");
            startDate = endDate; // advance the cursor
            this.stop();

        } catch (Exception e) {
            logger.error("PresidioMongoSource has failed to process events for startDate: {}, endDate: {}.", startDate, endDate);
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
        logger.info("PresidioMongoSource is stopping...");
    }

    private SourceMongoRepositoryImpl createRepository(String dbName, String host, int port, String username, String password) throws UnknownHostException {
        final MongoTemplate mongoTemplate = MongoUtils.createMongoTemplate(dbName, host, port, username, password);
        return new SourceMongoRepositoryImpl(mongoTemplate);
    }

    private void processEvent(AbstractDocument event) throws JsonProcessingException {
        sourceCounter.incrementEventAcceptedCount();
        final Event flumeEvent = EventBuilder.withBody(new Gson().toJson(event), Charset.defaultCharset());
        logger.trace("PresidioMongoSource has finished processing event {}. Sending event to channel", flumeEvent);
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
