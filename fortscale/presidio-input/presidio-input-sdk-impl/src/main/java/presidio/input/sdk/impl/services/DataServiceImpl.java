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
    public static final String INVALID_DOCUMENTS_COLLECTION_NAME = "input_filtered_raw_events";

    private final DataSourceRepository dataSourceRepository;
    private final ToCollectionNameTranslator<Schema> toCollectionNameTranslator;
    private final ValidationManager validationManager;

    public DataServiceImpl(DataSourceRepository dataSourceRepository, ToCollectionNameTranslator<Schema> toCollectionNameTranslator, ValidationManager validationManager) {
        this.dataSourceRepository = dataSourceRepository;
        this.toCollectionNameTranslator = toCollectionNameTranslator;
        this.validationManager = validationManager;
    }

    @Override
    public boolean store(List<? extends AbstractAuditableDocument> documents, Schema schema) {
        logger.trace("Storing {} documents.", documents.isEmpty() ? 0 : documents.size());
        final ValidationManager.ValidationResults validationResults = validationManager.validate(documents);
        dataSourceRepository.insertDataSource(toCollectionNameTranslator.toCollectionName(schema), validationResults.validDocuments);
        dataSourceRepository.insertDataSource(INVALID_DOCUMENTS_COLLECTION_NAME, validationResults.invalidDocuments);
        return true;
    }

    @Override
    public List<? extends AbstractAuditableDocument> find(Instant startDate, Instant endDate, Schema schema) {
        logger.debug("Finding records between {}:{} and {}:{}.",
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        return dataSourceRepository.getDataSourceDataBetweenDates(toCollectionNameTranslator.toCollectionName(schema), startDate, endDate);
    }

    @Override
    public int clean(Instant startDate, Instant endDate, Schema schema) {
        logger.debug("Deleting records between {}:{} and {}:{}.",
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        return dataSourceRepository.cleanDataSourceDataBetweenDates(toCollectionNameTranslator.toCollectionName(schema), startDate, endDate);
    }

    @Override
    public int cleanUntil(Instant endDate, Schema schema) {
        logger.debug("Deleting records until {}:{}.",
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        return dataSourceRepository.cleanDataSourceDataUntilDate(toCollectionNameTranslator.toCollectionName(schema), endDate);
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

