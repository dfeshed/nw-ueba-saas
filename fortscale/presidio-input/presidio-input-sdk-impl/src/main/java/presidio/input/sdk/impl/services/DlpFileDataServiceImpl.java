package presidio.input.sdk.impl.services;

import fortscale.common.general.CommonStrings;
import fortscale.domain.core.AbstractAuditableDocument;
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
        logger.debug("Finding dlpfile records between "+ CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME+":{} and "+
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME+":{}.", startTime, endTime);
        return dlpFileDataRepository.find(startTime, endTime);
    }

    @Override
    public int clean(long startTime, long endTime) {
        long startTimeBegingOfTime = 0;
        long endTimeCorentSystemTime = System.currentTimeMillis() / 1000;  //todo: at the moment we just want to delete all the documents in the collection, in the future we will use values that we recive from user or airflow
        logger.debug("Deleting dlpfile records between "+ CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME+":{} and "+
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME+":{}.", startTime, endTime);
        return dlpFileDataRepository.clean(startTimeBegingOfTime, endTimeCorentSystemTime);
    }
}

