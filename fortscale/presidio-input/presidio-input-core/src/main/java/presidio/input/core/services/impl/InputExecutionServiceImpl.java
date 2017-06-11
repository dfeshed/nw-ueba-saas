package presidio.input.core.services.impl;


import fortscale.common.general.DataSource;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.services.parameters.ParametersValidationService;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.input.core.services.api.InputExecutionService;
import presidio.input.core.services.converters.DlpFileConverter;
import presidio.sdk.api.domain.DlpFileDataDocument;
import presidio.sdk.api.domain.DlpFileEnrichedDocument;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fortscale.common.general.CommonStrings.*;

public class InputExecutionServiceImpl implements InputExecutionService {

    private static final Logger logger = Logger.getLogger(InputExecutionServiceImpl.class);

    private final ParametersValidationService parameterValidationService;
    private final PresidioInputPersistencyService presidioInputPersistencyService;
    private final EnrichedDataStore enrichedDataStore;

    private DataSource dataSource;
    private Instant startDate;
    private Instant endDate;

    public InputExecutionServiceImpl(ParametersValidationService parameterValidationService,
                                     PresidioInputPersistencyService presidioInputPersistencyService,
                                     EnrichedDataStore enrichedDataStore) {
        this.parameterValidationService = parameterValidationService;
        this.presidioInputPersistencyService = presidioInputPersistencyService;
        this.enrichedDataStore = enrichedDataStore;
    }

    public void run(String... params) throws Exception {
        logger.info("Started collector processing with params: ." + Arrays.toString(params));

        if (params.length < 3) {
            logger.error("Invalid input[{}]. Need at least {}, {} and {}. Example input: {}=some_{} {}=some_{}_as_long {}=some_{}_as_long.", params, COMMAND_LINE_DATA_SOURCE_FIELD_NAME, COMMAND_LINE_START_DATE_FIELD_NAME, COMMAND_LINE_END_DATE_FIELD_NAME, COMMAND_LINE_DATA_SOURCE_FIELD_NAME, COMMAND_LINE_DATA_SOURCE_FIELD_NAME, COMMAND_LINE_START_DATE_FIELD_NAME, COMMAND_LINE_START_DATE_FIELD_NAME, COMMAND_LINE_END_DATE_FIELD_NAME, COMMAND_LINE_END_DATE_FIELD_NAME);
            return;
        }

        final String dataSourceParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_DATA_SOURCE_FIELD_NAME, params);
        final String startTimeParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_START_DATE_FIELD_NAME, params);
        final String endTimeParam = parameterValidationService.getMandatoryParamAsString(COMMAND_LINE_END_DATE_FIELD_NAME, params);
        try {
            parameterValidationService.validateDatasourceParam(dataSourceParam);
//            parameterValidationService.validateTimeParams(startTimeParam, endTimeParam);
        } catch (Exception e) {
            logger.error("Invalid input[{}].", params, e);
            return;
        }

        dataSource = DataSource.valueOf(dataSourceParam);
        final long startTimeSeconds = TimestampUtils.convertToSeconds(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(startTimeParam));
        final long endTimeSeconds = TimestampUtils.convertToSeconds(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(endTimeParam));
        startDate = Instant.ofEpochSecond(startTimeSeconds);
        endDate = Instant.ofEpochSecond(startTimeSeconds);

        final List<? extends AbstractAuditableDocument> dataRecords = find(dataSource, startTimeSeconds, endTimeSeconds);
        logger.info("Found {} dataRecords for dataSource:{}, startTimeSeconds:{}, endTimeSeconds:{}.", dataSource, startTimeSeconds, endTimeSeconds);

        final List<DlpFileEnrichedDocument> enrichedRecords = enrich(dataRecords);

        if (!storeForAde(enrichedRecords)) {
            logger.error("Failed to save!!!");
            //todo: how to handle?
        }

        logger.info("Finished collector processing with params: ." + Arrays.toString(params));
    }

    private List<? extends AbstractAuditableDocument> find(DataSource dataSource, long startTime, long endTime) {
        logger.debug("Finding {} records for dataSource:{}, startTime:{}, endTime:{}.", dataSource, startTime, endTime);
        return presidioInputPersistencyService.find(dataSource, startTime, endTime);
    }

    private List<DlpFileEnrichedDocument> enrich(List<? extends AbstractAuditableDocument> dataRecords) { //THIS IS A TEMP IMPLEMENTATION!!!!!!!!!!
        //todo: again, very ad-hoc. maybe we should create an enrichment service
        List<DlpFileEnrichedDocument> enrichedRecords = new ArrayList<>();
        for (AbstractAuditableDocument dataRecord : dataRecords) {
            final DlpFileDataDocument dlpfileDataRecord = (DlpFileDataDocument) dataRecord;
            enrichedRecords.add(new DlpFileEnrichedDocument(dlpfileDataRecord, dlpfileDataRecord.getUsername(), dlpfileDataRecord.getHostname()));
        }

        return enrichedRecords;
    }

    private boolean storeForAde(List<? extends AbstractAuditableDocument> enrichedDocuments) {
        logger.debug("Storing {} records.", enrichedDocuments.size());


        EnrichedRecordsMetadata recordsMetaData = new EnrichedRecordsMetadata(dataSource.toString(), startDate, endDate);
        List<? extends EnrichedRecord> records = convert((List<AbstractAuditableDocument>) enrichedDocuments, new DlpFileConverter());

        enrichedDataStore.store(recordsMetaData, records);

        logger.info("*************input logic comes here***********");
        logger.info("enriched documents: \n{}", enrichedDocuments);
        logger.info("**********************************************");
        final boolean storeSuccessful = true;
        /*temp*/
        return storeSuccessful;
    }

    protected List<EnrichedRecord> convert(List<AbstractAuditableDocument> enrichedDocuments,
                                           DlpFileConverter converter) {
        List<EnrichedRecord> records = new ArrayList<>();
        enrichedDocuments.forEach(doc -> records.add(converter.convert(doc)));
        return records;
    }
}
