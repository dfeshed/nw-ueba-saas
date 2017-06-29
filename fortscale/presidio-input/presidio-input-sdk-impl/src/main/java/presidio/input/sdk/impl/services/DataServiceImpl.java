package presidio.input.sdk.impl.services;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.DataSource;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.ToCollectionNameTranslator;
import presidio.input.sdk.impl.repositories.DataSourceRepository;
import presidio.sdk.api.domain.DataService;
import presidio.sdk.api.domain.DlpFileDataDocument;
import presidio.input.sdk.impl.validators.ValidationManager;

import java.time.Instant;
import java.util.List;

public class DataServiceImpl implements DataService {

    private static final Logger logger = Logger.getLogger(DataServiceImpl.class);

    private final DataSourceRepository dataSourceRepository;
    private final ToCollectionNameTranslator toCollectionNameTranslator;

    public DataServiceImpl(DataSourceRepository dataSourceRepository, ToCollectionNameTranslator toCollectionNameTranslator) {
        this.dataSourceRepository = dataSourceRepository;
        this.toCollectionNameTranslator = toCollectionNameTranslator;
    }

    @Override
    public boolean store(List<? extends AbstractAuditableDocument> documents, DataSource dataSource) {
        logger.debug("Storing {} documents.", documents.isEmpty() ? 0 : documents.size());
        ValidationManager m = new ValidationManager();
        List <? extends  AbstractAuditableDocument> validDocuments  = m.validate(documents);
        dataSourceRepository.insertDataSource(toCollectionNameTranslator.toCollectionName(dataSource), validDocuments);
        return true;
    }

    @Override
    public List<DlpFileDataDocument> find(Instant startDate, Instant endDate, DataSource dataSource) {
        logger.debug("Finding dlpfile records between {}:{} and {}:{}.",
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        return (List<DlpFileDataDocument>) dataSourceRepository.getDataSourceDataBetweenDates(toCollectionNameTranslator.toCollectionName(dataSource), startDate, endDate);
    }

    @Override
    public int clean(Instant startDate, Instant endDate, DataSource dataSource) {
        Instant startTimeBegingOfTime = Instant.ofEpochSecond(0);
        Instant endTimeCurrentSystemTime = Instant.ofEpochSecond(System.currentTimeMillis() / 1000);  //todo: at the moment we just want to delete all the documents in the collection, in the future we will use values that we receive from user or airflow
        logger.debug("Deleting dlpfile records between {}:{} and {}:{}.",
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        return dataSourceRepository.cleanDataSourceDataBetweenDates(toCollectionNameTranslator.toCollectionName(dataSource), startTimeBegingOfTime, endTimeCurrentSystemTime);
    }

    @Override
    public void cleanAll(DataSource dataSource) {
        logger.info("Cleaning entire collection {}", toCollectionNameTranslator.toCollectionName(dataSource));
        dataSourceRepository.cleanCollection(toCollectionNameTranslator.toCollectionName(dataSource));
    }
}

