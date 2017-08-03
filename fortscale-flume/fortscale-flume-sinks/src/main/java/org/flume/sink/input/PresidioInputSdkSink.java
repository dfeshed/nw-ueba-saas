package org.flume.sink.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractAuditableDocument;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.Sink;
import org.apache.flume.conf.Configurable;
import org.flume.sink.base.AbstractPresidioSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.input.sdk.impl.factory.PresidioInputPersistencyServiceFactory;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.flume.CommonStrings.BATCH_SIZE;

public class PresidioInputSdkSink<T extends AbstractAuditableDocument> extends AbstractPresidioSink<T> implements Configurable, Sink {

    private static Logger logger = LoggerFactory.getLogger(PresidioInputSdkSink.class);

    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    private static final String SCHEMA = "schema";
    private static final String RECORD_TYPE = "recordType";

    private static String[] mandatoryParams = {SCHEMA, RECORD_TYPE};

    private PresidioInputPersistencyService presidioInputPersistencyService;
    private Class<T> recordType;
    private Schema schema;
    private int batchSize;

    @Override
    public synchronized void start() {
        PresidioInputPersistencyServiceFactory presidioInputPersistencyServiceFactory = new PresidioInputPersistencyServiceFactory();
        try {
            presidioInputPersistencyService = presidioInputPersistencyServiceFactory.createPresidioInputPersistencyService();
        } catch (Exception e) {
            final String errorMessage = "Failed to start " + getName();
            logger.error(errorMessage, e);
            throw new FlumeException(errorMessage, e);
        }
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
            sinkCounter.incrementEventDrainAttemptCount();
            final T parsedEvent;
            final String eventBody = new String(flumeEvent.getBody());
            try {
                final Class<T> recordType = this.recordType;
                parsedEvent = mapper.readValue(eventBody, recordType);
            } catch (Exception e) {
                final String errorMessage = String.format("Failed to sink event. event is not of correct type. expected type:%s, actual event body:%s.", recordType, eventBody);
                logger.error(errorMessage);
                throw new Exception(errorMessage, e);
            }
            eventsToSave.add(parsedEvent);
        }

        return eventsToSave;
    }

    @Override
    protected void saveEvents(List<T> records) {
        presidioInputPersistencyService.store(schema, records);
        sinkCounter.addToEventDrainSuccessCount(records.size());
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("schema", schema)
                .append("batchSize", batchSize)
                .toString();
    }
}
