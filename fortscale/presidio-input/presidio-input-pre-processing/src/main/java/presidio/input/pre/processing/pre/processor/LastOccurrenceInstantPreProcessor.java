package presidio.input.pre.processing.pre.processor;

import fortscale.common.general.Schema;
import fortscale.domain.lastoccurrenceinstant.LastOccurrenceInstantWriter;
import org.apache.commons.lang3.Validate;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.RawEventsPageIterator;
import presidio.sdk.api.services.PresidioInputPersistencyService;
import presidio.sdk.api.utils.ReflectionUtils;

import java.time.Instant;
import java.util.List;

public class LastOccurrenceInstantPreProcessor extends PreProcessor<LastOccurrenceInstantPreProcessorArguments> {
    private final PresidioInputPersistencyService presidioInputPersistencyService;
    private final int rawEventsPageSize;
    private final LastOccurrenceInstantWriter lastOccurrenceInstantWriter;

    public LastOccurrenceInstantPreProcessor(
            String name,
            PresidioInputPersistencyService presidioInputPersistencyService,
            int rawEventsPageSize,
            LastOccurrenceInstantWriter lastOccurrenceInstantWriter) {

        super(name, LastOccurrenceInstantPreProcessorArguments.class);
        Validate.notNull(presidioInputPersistencyService, "presidioInputPersistencyService cannot be null.");
        Validate.isTrue(rawEventsPageSize > 0, "rawEventsPageSize must be greater than zero.");
        Validate.notNull(lastOccurrenceInstantWriter, "lastOccurrenceInstantWriter cannot be null.");
        this.presidioInputPersistencyService = presidioInputPersistencyService;
        this.rawEventsPageSize = rawEventsPageSize;
        this.lastOccurrenceInstantWriter = lastOccurrenceInstantWriter;
    }

    @Override
    void preProcess(LastOccurrenceInstantPreProcessorArguments arguments) {
        Schema schema = arguments.getSchema();
        String instantFieldName = arguments.getInstantFieldName();
        List<String> entityTypes = arguments.getEntityTypes();
        RawEventsPageIterator<AbstractInputDocument> rawEventsPageIterator = new RawEventsPageIterator<>(
                arguments.getStartInstant(),
                arguments.getEndInstant(),
                presidioInputPersistencyService,
                schema,
                rawEventsPageSize);

        while (rawEventsPageIterator.hasNext()) {
            for (AbstractInputDocument rawEvent : rawEventsPageIterator.next()) {
                Instant instant = (Instant)ReflectionUtils.getFieldValue(rawEvent, instantFieldName);

                for (String entityType : entityTypes) {
                    String entityId = (String)ReflectionUtils.getFieldValue(rawEvent, entityType);
                    lastOccurrenceInstantWriter.write(schema, entityType, entityId, instant);
                }
            }
        }
    }
}
