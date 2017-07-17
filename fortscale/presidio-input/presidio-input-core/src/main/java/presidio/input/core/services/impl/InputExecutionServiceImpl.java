package presidio.input.core.services.impl;


import fortscale.common.exporter.FileMetricsExporter;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.common.general.CommonStrings;
import fortscale.common.general.DataSource;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.aspect.annotations.End;
import fortscale.utils.monitoring.aspect.annotations.RunTime;
import fortscale.utils.monitoring.aspect.annotations.Start;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.ade.sdk.executions.common.ADEManagerSDK;
import presidio.input.core.services.converters.*;
import presidio.sdk.api.domain.DlpFileDataDocument;
import presidio.sdk.api.domain.DlpFileEnrichedDocument;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class InputExecutionServiceImpl implements PresidioExecutionService {

    private static final Logger logger = Logger.getLogger(InputExecutionServiceImpl.class);

    private final PresidioInputPersistencyService presidioInputPersistencyService;
    private final ADEManagerSDK adeManagerSDK;

    public InputExecutionServiceImpl(PresidioInputPersistencyService presidioInputPersistencyService, ADEManagerSDK adeManagerSDK) {
        this.presidioInputPersistencyService = presidioInputPersistencyService;
        this.adeManagerSDK = adeManagerSDK;
    }

    @Override
    @RunTime
    @Start
    @End
    public void run(DataSource dataSource, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {
        logger.info("Started input processing with params: data source:{}, from {}:{}, until {}:{}.", dataSource, CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);

        final List<? extends AbstractAuditableDocument> dataRecords = find(dataSource, startDate, endDate);
        logger.info("Found {} dataRecords for datasource:{}, startTime:{}, endTime:{}.", dataSource, startDate, endDate);
        List<? extends AbstractAuditableDocument> enrichedRecords;
        if (dataSource.equals(DataSource.DLPFILE)) {
            enrichedRecords = enrich(dataRecords);
        } else {
            enrichedRecords = dataRecords;
        }

        InputAdeConverter converter = getConverter(dataSource);


        if (!storeForAde(enrichedRecords, startDate, endDate, dataSource, converter)) {
            logger.error("Failed to save input enriched records into ADE!!!");
            //todo: how to handle?
        }

        logger.info("Finished input run with params : data source:{}, from {}:{}, until {}:{}.", dataSource, CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
    }

    private boolean storeForAde(List<? extends AbstractAuditableDocument> enrichedDocuments, Instant startDate, Instant endDate, DataSource dataSource, InputAdeConverter converter) {
        logger.debug("Storing {} records.", enrichedDocuments.size());


        List<? extends EnrichedRecord> records = convert(enrichedDocuments, converter);

        EnrichedRecordsMetadata metaData = new EnrichedRecordsMetadata(dataSource.getName(), startDate, endDate);
        adeManagerSDK.store(metaData, records);

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

    private List<? extends AbstractAuditableDocument> find(DataSource dataSource, Instant startDate, Instant endDate) throws Exception {
        logger.debug("Finding records for data source:{}, from {}:{}, until {}:{}."
                , dataSource,
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        return presidioInputPersistencyService.find(dataSource, startDate, endDate);
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

    private InputAdeConverter getConverter(DataSource dataSource) {
        switch (dataSource) {
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
    public void cleanAll(DataSource dataSource) throws Exception {
        logger.info("Started clean processing for data source:{}.", dataSource);
        presidioInputPersistencyService.cleanAll(dataSource);
    }

    @Override
    public void clean(DataSource dataSource, Instant startDate, Instant endDate) throws Exception {
        logger.info("Started clean processing for data source:{}, from {}:{}, until {}:{}."
                , dataSource,
                CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate,
                CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        presidioInputPersistencyService.clean(dataSource, startDate, endDate);
        logger.info("Finished enrich processing .");
    }
}
