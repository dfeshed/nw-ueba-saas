package presidio.input.pre.processing.pre.processor;

import fortscale.common.general.Schema;
import fortscale.domain.lastoccurrenceinstant.writer.LastOccurrenceInstantWriter;
import fortscale.utils.logging.Logger;
import fortscale.utils.reflection.PresidioReflectionUtils;
import org.apache.commons.lang3.Validate;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LastOccurrenceInstantPreProcessor extends PreProcessor<LastOccurrenceInstantPreProcessorArguments> {
    private static final Logger logger = Logger.getLogger(LastOccurrenceInstantPreProcessor.class);
    private static PresidioReflectionUtils reflection = new PresidioReflectionUtils();
    private final PresidioInputPersistencyService presidioInputPersistencyService;
    private final int aggregationPageSize;
    private final LastOccurrenceInstantWriter lastOccurrenceInstantWriter;


    public LastOccurrenceInstantPreProcessor(
            String name,
            PresidioInputPersistencyService presidioInputPersistencyService,
            int aggregationPageSize,
            LastOccurrenceInstantWriter lastOccurrenceInstantWriter) {

        super(name, LastOccurrenceInstantPreProcessorArguments.class);
        Validate.notNull(presidioInputPersistencyService, "presidioInputPersistencyService cannot be null.");
        Validate.isTrue(aggregationPageSize > 0, "aggregationPageSize must be greater than zero.");
        Validate.notNull(lastOccurrenceInstantWriter, "lastOccurrenceInstantWriter cannot be null.");
        this.presidioInputPersistencyService = presidioInputPersistencyService;
        this.aggregationPageSize = aggregationPageSize;
        this.lastOccurrenceInstantWriter = lastOccurrenceInstantWriter;
    }

    @Override
    void preProcess(LastOccurrenceInstantPreProcessorArguments arguments) {
        Schema schema = arguments.getSchema();
        List<String> entityTypes = arguments.getEntityTypes();

        for (String entityType: entityTypes){
            int skip = 0;
            Map<String, Instant> entityTypeToLastOccurrenceInstantMap = Collections.emptyMap();
            logger.info("start processing {}", entityType);
            do {
                entityTypeToLastOccurrenceInstantMap = presidioInputPersistencyService.aggregateKeysMaxTime(
                        arguments.getStartInstant(),
                        arguments.getEndInstant(),
                        entityType, skip, aggregationPageSize, schema, true);
                entityTypeToLastOccurrenceInstantMap.forEach((entityId, instant) -> lastOccurrenceInstantWriter.write(schema, entityType, entityId, instant));
                skip += aggregationPageSize;
            } while (entityTypeToLastOccurrenceInstantMap.size() == aggregationPageSize);
            logger.info("finished processing {}", entityType);
        }

        logger.info("finished processing all entity types.");
        lastOccurrenceInstantWriter.close();
    }
}
