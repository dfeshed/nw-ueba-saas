package presidio.output.manager.services;

import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import presidio.output.domain.services.event.EventPersistencyService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class OutputManagerService {
    private static final Logger logger = Logger.getLogger(OutputManagerService.class);

    private final EventPersistencyService eventPersistencyService;
    private final long retentionEnrichedEventsDays;

    public OutputManagerService(EventPersistencyService eventPersistencyService, long retentionEnrichedEventsDays){
        this.eventPersistencyService = eventPersistencyService;
        this.retentionEnrichedEventsDays = retentionEnrichedEventsDays;
    }

    public void cleanDocuments(Instant endDate, List<Schema> schemas){
        if(schemas == null){
            schemas = Arrays.asList(Schema.values());
        }
        schemas.forEach(schema -> {
            logger.debug("Start retention clean to mongo for schema {}", schema);
            eventPersistencyService.remove(schema, Instant.EPOCH, endDate.minus(retentionEnrichedEventsDays, ChronoUnit.DAYS));
        });
    }
}
