package org.flume.sink.input;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.MongoException;
import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractAuditableDocument;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.conf.MonitorDetails;
import org.apache.flume.marker.MonitorUses;
import org.apache.flume.persistency.mongo.PresidioFilteredEventsMongoRepository;
import org.flume.sink.base.AbstractPresidioSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.apache.flume.CommonStrings.BATCH_SIZE;

/**
 * an AbstractPresidioSink that uses the InputSDK jar to write events to Presidio-Input's input
 */
public class PresidioInputSdkSink<T extends AbstractAuditableDocument> extends AbstractPresidioSink<T> implements Configurable, Sink, MonitorUses {

    private static Logger logger = LoggerFactory.getLogger(PresidioInputSdkSink.class);

    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private static final String SCHEMA = "schema";
    private static final String RECORD_TYPE = "recordType";

    private static String[] mandatoryParams = {SCHEMA, RECORD_TYPE};

    private PresidioInputPersistencyService presidioInputPersistencyService;
    private ConnectorSharedPresidioExternalMonitoringService monitoringService;
    private Class<T> recordType;
    private Schema schema;
    private int batchSize;

    @Override
    public synchronized void start() {

    }


    @Override
    @SuppressWarnings("unchecked")
    protected void doPresidioConfigure(Context context) {
        logger.debug("context is: {}", context);
        try {
            for (String mandatoryParam : mandatoryParams) {
                if (!context.containsKey(mandatoryParam)) {
                    throw new Exception(String.format("Missing mandatory param %s for %s. Mandatory params are: %s", mandatoryParam, getName(), Arrays.toString(mandatoryParams)));
                }
            }

            recordType = (Class<T>) Class.forName(context.getString(RECORD_TYPE));
            schema = Schema.createSchema(context.getString(SCHEMA));
            batchSize = Integer.parseInt(context.getString(BATCH_SIZE, "1"));
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
            isDone = isBatch && gotControlDoneMessage;
            if (gotControlDoneMessage) {
                logger.debug("Got control message. Finishing batch and closing.");
                continue;
            }

            final T parsedEvent;
            final String eventBody = new String(flumeEvent.getBody());
            try {
                final Class<T> recordType = this.recordType;
                parsedEvent = mapper.readValue(eventBody, recordType);
            } catch (Exception e) {
                final Map<String, String> eventHeaders = flumeEvent.getHeaders();
                if (!e.getClass().isAssignableFrom(MongoException.class)) {
                    PresidioFilteredEventsMongoRepository.saveFailedFlumeEvent(getApplicationName() + "-" + this.getClass().getSimpleName(), e.getMessage(), flumeEvent);
                }
                final String errorMessage = String.format("Failed to sink event. Can't getEvent since event is not of correct type. expected type:%s, actual event: body:[ %s ], headers:[ %s ].", recordType, eventBody, eventHeaders);
                logger.error(errorMessage);
                throw e;
            }
            eventsToSave.add(parsedEvent);
        }

        if (isDone && this.getChannel().take() != null) {
            logger.error("Got a control message DONE while there are still more records to process. This is not a valid state!");
        }

        return eventsToSave;
    }

    @Override
    protected void monitorNumberOfReadEvents(int number, Instant logicalHour) {
        monitoringService.reportTotalEventMetric(number);
    }

    @Override
    protected void monitorNumberOfSavedEvents(int number, Instant logicalHour) {
        monitoringService.reportSuccessEventMetric(number);
    }

    @Override
    protected void monitorNumberOfUnassignableEvents(int number, String schema, Instant logicalHour) {
        monitoringService.reportFailedEventMetric("UNASSIGNABLE_EVENTS",number);
    }

    @Override
    protected void monitorUnknownError(int number, Instant logicalHour) {
        monitoringService.reportFailedEventMetric("UNKNOWN_ERROR_EVENTS",number);
    }



    @Override
    protected int saveEvents(List<T> records) {
        if (records.isEmpty()) {
            logger.trace("0 events were saved successfully.");
            return 0;
        }
        final boolean allSavedSuccessfully = presidioInputPersistencyService.store(schema, records);
        final int size = records.size();
        if (allSavedSuccessfully) {
            logger.debug("{} events were saved successfully.", size);
//            sinkCounter.addToEventDrainSuccessCount(size);
        } else {
            logger.warn("Not all records out of {} total records were saved successfully", size);
        }
        return size;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("schema", schema)
                .append("batchSize", batchSize)
                .toString();
    }

    @Override
    public void setMonitorDetails(MonitorDetails monitorDetails) {
        if (schema!=null){
            monitorDetails.setSchema(schema.getName());
        }
        monitoringService = new ConnectorSharedPresidioExternalMonitoringService(monitorDetails,this.getName());
    }
}
