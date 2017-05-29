package presidio.input.sdk.impl.services;

import fortscale.utils.logging.Logger;
import presidio.input.sdk.impl.repositories.DlpFileDataRepository;
import presidio.sdk.api.domain.DlpFileDataDocument;
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
        logger.debug("Storing {} documents.", documents.isEmpty() ? 0 : documents.size());
        return documents.size() == dlpFileDataRepository.save(documents).size();
    }

    @Override
    public List<DlpFileDataDocument> find(long startTime, long endTime) {
        logger.debug("finding dlpfile records {} between startTime:{} and endTime:{}.", startTime, endTime);
        return dlpFileDataRepository.find(startTime, endTime);
    }
}

