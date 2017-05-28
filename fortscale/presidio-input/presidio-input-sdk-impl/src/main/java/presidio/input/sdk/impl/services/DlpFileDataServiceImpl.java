package presidio.input.sdk.impl.services;

import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import presidio.input.sdk.impl.repositories.DlpFileDataRepository;
import presidio.sdk.api.domain.DlpFileDataService;

import java.util.List;

public class DlpFileDataServiceImpl implements DlpFileDataService {

    private static final Logger logger = Logger.getLogger(DlpFileDataServiceImpl.class);

    private final DlpFileDataRepository dlpFileDataRepository;

    public DlpFileDataServiceImpl(DlpFileDataRepository dlpFileDataRepository) {
        this.dlpFileDataRepository = dlpFileDataRepository;
    }

    @Override
    public boolean store(List<? extends AbstractAuditableDocument> documents) {
        logger.debug("Storing {} documents.", documents.size());
        return documents.size() == dlpFileDataRepository.save(documents).size();
    }
}

