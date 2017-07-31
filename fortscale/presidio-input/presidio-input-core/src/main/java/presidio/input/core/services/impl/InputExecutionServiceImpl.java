package presidio.input.core.services.impl;


import fortscale.common.general.Schema;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.common.general.CommonStrings;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import presidio.monitoring.aspect.annotations.RunTime;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.input.core.services.converters.*;
import presidio.input.core.services.data.AdeDataService;
import presidio.sdk.api.domain.DlpFileDataDocument;
import presidio.sdk.api.domain.DlpFileEnrichedDocument;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class InputExecutionServiceImpl implements PresidioExecutionService {

    private static final Logger logger = Logger.getLogger(InputExecutionServiceImpl.class);

    private final PresidioInputPersistencyService presidioInputPersistencyService;
    private final AdeDataService adeDataService;

    public InputExecutionServiceImpl(PresidioInputPersistencyService presidioInputPersistencyService, AdeDataService adeDataService) {
        this.presidioInputPersistencyService = presidioInputPersistencyService;
        this.adeDataService = adeDataService;
    }

    @Override
    public void run(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {
        logger.info("Started input processing with params: data source:{}, from {}:{}, until {}:{}.", schema, CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);

        final List<? extends AbstractAuditableDocument> dataRecords = find(schema, startDate, endDate);
        logger.info("Found {} dataRecords for datasource:{}, startTime:{}, endTime:{}.", schema, startDate, endDate);
        List<? extends AbstractAuditableDocument> enrichedRecords;
        if (schema.equals(Schema.DLPFILE)) {
            enrichedRecords = enrich(dataRecords);
        } else {
            enrichedRecords = dataRecords;
        }

        InputAdeConverter converter = getConverter(schema);


        if (!storeForAde(enrichedRecords, startDate, endDate, schema, converter)) {
            logger.error("Failed to save input enriched records into ADE!!!");
            //todo: how to handle?
        }

        logger.info("Finished input run with params : data source:{}, from {}:{}, until {}:{}.", schema, CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
    }

    private boolean storeForAde(List<? extends AbstractAuditableDocument> enrichedDocuments, Instant startDate, Instant endDate, Schema schema, InputAdeConverter converter) {
        logger.debug("Storing {} records.", enrichedDocuments.size());


        List<? extends EnrichedRecord> records = convert(enrichedDocuments, converter);

        adeDataService.store(schema, startDate, endDate, records);

        logger.info("*************input logic comes here***********");
        logger.info("enriched documents: \n{}", enrichedDocuments);
        logger.info("**********************************************");
        final boolean storeSuccessful = true;
        /*temp*/
        return storeSuccessful;
    }

    protected List<EnrichedRecord> convert(List<? extends AbstractAuditableDocument> enrichedDocuments,
                                           InputAdeConverter converter) {
        List<EnrichedRecord> records = new ArrayList<>();
        enrichedDocuments.forEach(doc -> records.add(converter.convert(doc)));
        return records;
    }

    private List<? extends AbstractAuditableDocument> find(Schema schema, Instant startDate, Instant endDate) throws Exception {
        logger.debug("Finding records for data source:{}, from {}:{}, until {}:{}."
                , schema,
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        return presidioInputPersistencyService.find(schema, startDate, endDate);
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

    private InputAdeConverter getConverter(Schema schema) {
        switch (schema) {
            case DLPFILE:
                return new DlpFileConverter();
            case DLPMAIL:
                break;
            case PRNLOG:
                break;
            case FILE:
                return new FileConverter();
            case ACTIVE_DIRECTORY:
                return new ActiveDirectoryConverter();
            case AUTHENTICATION:
                return new AuthenticationConverter();
        }
        return null;
    }

    @Override
    public void cleanAll(Schema schema) throws Exception {
        logger.info("Started clean processing for data source:{}.", schema);
        presidioInputPersistencyService.cleanAll(schema);
    }

    @Override
    @RunTime
    public void clean(Schema schema, Instant startDate, Instant endDate) throws Exception {
        logger.info("Started clean processing for data source:{}, from {}:{}, until {}:{}."
                , schema,
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        presidioInputPersistencyService.clean(schema, startDate, endDate);
        logger.info("Finished enrich processing .");
    }
}
