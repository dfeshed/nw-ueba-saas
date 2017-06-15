package presidio.input.sdk.impl.services;

import fortscale.common.general.CommonStrings;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import presidio.input.sdk.impl.repositories.DlpFileDataRepository;
import presidio.sdk.api.domain.DataService;
import presidio.sdk.api.domain.DlpFileDataDocument;

import java.time.Instant;
import java.util.List;

public class DlpFileDataService implements DataService {

    private static final Logger logger = Logger.getLogger(DlpFileDataService.class);

    private final DlpFileDataRepository dlpFileDataRepository;

    public DlpFileDataService(DlpFileDataRepository dlpFileDataRepository) {
        this.dlpFileDataRepository = dlpFileDataRepository;
    }

    @Override
    public boolean store(List<? extends AbstractAuditableDocument> documents) {
        logger.debug("Storing {} documents.", documents.isEmpty() ? 0 : documents.size());
        return documents.size() == dlpFileDataRepository.save(documents).size();
    }

    @Override
    public List<DlpFileDataDocument> find(Instant startDate, Instant endDate) {
        logger.debug("Finding dlpfile records between {}:{} and {}:{}.",
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        return dlpFileDataRepository.find(startDate, endDate);
    }

    @Override
    public int clean(Instant startDate, Instant endDate) {
        Instant startTimeBegingOfTime = Instant.ofEpochSecond(0);
        Instant endTimeCurrentSystemTime = Instant.ofEpochSecond(System.currentTimeMillis() / 1000);  //todo: at the moment we just want to delete all the documents in the collection, in the future we will use values that we receive from user or airflow
        logger.debug("Deleting dlpfile records between {}:{} and {}:{}.",
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        return dlpFileDataRepository.clean(startTimeBegingOfTime, endTimeCurrentSystemTime);
    }

    @Override
    public void cleanAll() {
        logger.info("Cleaning entire collection {}", DlpFileDataDocument.COLLECTION_NAME);
        dlpFileDataRepository.deleteAll();
    }
}

