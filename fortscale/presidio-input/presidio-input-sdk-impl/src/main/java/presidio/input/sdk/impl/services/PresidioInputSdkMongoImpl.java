package presidio.input.sdk.impl.services;

import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import presidio.sdk.api.domain.DlpFileDataService;
import presidio.sdk.api.services.PresidioInputSdk;

import java.util.List;

public class PresidioInputSdkMongoImpl implements PresidioInputSdk {
    private final Logger logger = Logger.getLogger(PresidioInputSdkMongoImpl.class);

    private final DlpFileDataService dlpFileDataService;

    public PresidioInputSdkMongoImpl(DlpFileDataService dlpFileDataService){
        this.dlpFileDataService = dlpFileDataService;
    }

    @Override
    public boolean store(List<AbstractAuditableDocument> events) {
        //TODO: change this when we have the new service and repo
        logger.info("Staring to insert data");
        return dlpFileDataService.store(events);
    }
}
