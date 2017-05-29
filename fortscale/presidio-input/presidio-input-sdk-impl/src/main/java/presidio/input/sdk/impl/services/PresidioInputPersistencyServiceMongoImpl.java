package presidio.input.sdk.impl.services;

import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import presidio.sdk.api.domain.DlpFileDataService;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.util.List;

public class PresidioInputPersistencyServiceMongoImpl implements PresidioInputPersistencyService {
    private final Logger logger = Logger.getLogger(PresidioInputPersistencyServiceMongoImpl.class);

    private final DlpFileDataService dlpFileDataService;

    public PresidioInputPersistencyServiceMongoImpl(DlpFileDataService dlpFileDataService) {
        this.dlpFileDataService = dlpFileDataService;
    }

    @Override
    public boolean store(List<AbstractAuditableDocument> events) {
        //TODO: add metrics number of events stored
        logger.info("Staring to insert data, number of events = {}", events.isEmpty() ? 0 : events.size());
        return dlpFileDataService.store(events);
    }
}
