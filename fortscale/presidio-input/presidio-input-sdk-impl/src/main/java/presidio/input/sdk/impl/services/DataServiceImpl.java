package presidio.input.sdk.impl.services;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.ToCollectionNameTranslator;
import presidio.input.sdk.impl.repositories.DataSourceRepository;
import presidio.input.sdk.impl.validators.ValidationManager;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.services.DataService;

import java.time.Instant;
import java.util.List;

public class DataServiceImpl implements DataService {

    private static final Logger logger = Logger.getLogger(DataServiceImpl.class);

    private final DataSourceRepository dataSourceRepository;
    private final ToCollectionNameTranslator toCollectionNameTranslator;
    private final ValidationManager validationManager;

    public DataServiceImpl(DataSourceRepository dataSourceRepository, ToCollectionNameTranslator toCollectionNameTranslator, ValidationManager validationManager) {
        this.dataSourceRepository = dataSourceRepository;
        this.toCollectionNameTranslator = toCollectionNameTranslator;
        this.validationManager = validationManager;
    }

    @Override
    public boolean store(List<? extends AbstractAuditableDocument> documents, Schema schema) {
        logger.debug("Storing {} documents.", documents.isEmpty() ? 0 : documents.size());
        List<? extends AbstractAuditableDocument> validDocuments = validationManager.validate(documents);
        dataSourceRepository.insertDataSource(toCollectionNameTranslator.toCollectionName(schema), validDocuments);
        return true;
    }

    @Override
    public List<AbstractAuditableDocument> find(Instant startDate, Instant endDate, Schema schema) {
        logger.debug("Finding dlpfile records between {}:{} and {}:{}.",
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        return (List<AbstractAuditableDocument>) dataSourceRepository.getDataSourceDataBetweenDates(toCollectionNameTranslator.toCollectionName(schema), startDate, endDate);
    }

    @Override
    public int clean(Instant startDate, Instant endDate, Schema schema) {
        Instant startTimeBegingOfTime = Instant.ofEpochSecond(0);
        Instant endTimeCurrentSystemTime = Instant.ofEpochSecond(System.currentTimeMillis() / 1000);  //todo: at the moment we just want to delete all the documents in the collection, in the future we will use values that we receive from user or airflow
        logger.debug("Deleting dlpfile records between {}:{} and {}:{}.",
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        return dataSourceRepository.cleanDataSourceDataBetweenDates(toCollectionNameTranslator.toCollectionName(schema), startTimeBegingOfTime, endTimeCurrentSystemTime);
    }

    @Override
    public void cleanAll(Schema schema) {
        logger.info("Cleaning entire collection {}", toCollectionNameTranslator.toCollectionName(schema));
        dataSourceRepository.cleanCollection(toCollectionNameTranslator.toCollectionName(schema));
    }

    @Override
    public <U extends AbstractInputDocument> List<U> readRecords(Schema schema, Instant startDate, Instant endDate, int numOfItemsToSkip, int pageSize) {
        return dataSourceRepository.readRecords(toCollectionNameTranslator.toCollectionName(schema), startDate, endDate, numOfItemsToSkip, pageSize);
    }

    @Override
    public long count(Schema schema, Instant startDate, Instant endDate) {
        return dataSourceRepository.count(toCollectionNameTranslator.toCollectionName(schema), startDate, endDate);
    }
}

