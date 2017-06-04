package presidio.input.sdk.impl.services;

import fortscale.common.general.Datasource;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import presidio.sdk.api.domain.DlpFileDataDocument;
import presidio.sdk.api.domain.DlpFileDataService;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.util.List;
import java.util.stream.Collectors;

public class PresidioInputPersistencyServiceMongoImpl implements PresidioInputPersistencyService {
    private final Logger logger = Logger.getLogger(PresidioInputPersistencyServiceMongoImpl.class);

    private final DlpFileDataService dlpFileDataService;

    public PresidioInputPersistencyServiceMongoImpl(DlpFileDataService dlpFileDataService) {
        this.dlpFileDataService = dlpFileDataService;
    }

    @Override
    public boolean store(Datasource datasource, List<AbstractAuditableDocument> records) {
        //TODO: change this when we have the new service and repo
        logger.info("Storing {} records for datasource {}", records.size(), datasource);

        List<DlpFileDataDocument> dlpFileDataDocuments = records // todo: this is very ad-hoc. we need to design a mechanism for resolving the right repo and casting
                .stream()
                .map(e -> (DlpFileDataDocument) e)
                .collect(Collectors.toList());
        return dlpFileDataService.store(dlpFileDataDocuments);
    }

    @Override
    public List<? extends AbstractAuditableDocument> find(Datasource dataSource, long startTime, long endTime) {
        logger.info("Finding records for datasource {}, startTime {}, endTime {}", dataSource, startTime, endTime);
        return dlpFileDataService.find(startTime, endTime);
    }
}
