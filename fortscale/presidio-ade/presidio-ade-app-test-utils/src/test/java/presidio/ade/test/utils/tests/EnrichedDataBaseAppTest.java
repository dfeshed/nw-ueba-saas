package presidio.ade.test.utils.tests;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.file.EnrichedFileRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.ade.test.utils.converters.FileRaw2EnrichedConverter;
import presidio.ade.test.utils.generators.MultiFileEventGenerator;
import presidio.ade.test.utils.generators.factory.FileEventGeneratorTemplateFactory;
import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.StringRegexCyclicValuesGenerator;
import presidio.data.generators.common.time.ITimeGeneratorFactory;
import presidio.data.generators.common.time.SingleTimeGeneratorFactory;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.generators.event.file.FileEventsGenerator;
import presidio.data.generators.fileop.FileOperationGenerator;
import presidio.data.generators.fileop.FixedFileOperationTypeGenerator;
import presidio.data.generators.fileop.IFileOperationGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class EnrichedDataBaseAppTest extends BaseAppTest{

    @Autowired
    private EnrichedDataStore enrichedDataStore;

    public void prepareTest() {
        mongoTemplate.getCollectionNames().forEach(collection -> mongoTemplate.dropCollection(collection));
    }

    /**
     * Generate records every 10 minutes.
     * create context and time generators.
     *
     * @param fileOperationGeneratorList file operation generator list
     * @param startHourOfDay            start hour of day
     * @param endHourOfDay              end hour of day
     * @param daysBackFrom
     * @param daysBackTo
     * @param contextIdPattern          contextId pattern for contextIdGenerator
     * @return TimeRange of records
     * @throws GeneratorException
     */
    public TimeRange generateAndPersistFileEventData(List<IFileOperationGenerator> fileOperationGeneratorList, int startHourOfDay, int endHourOfDay, int daysBackFrom, int daysBackTo, String contextIdPattern, boolean isAdmin) throws GeneratorException {

        StringRegexCyclicValuesGenerator contextIdGenerator = new StringRegexCyclicValuesGenerator(contextIdPattern);
        ITimeGeneratorFactory timeGeneratorFactory = new SingleTimeGeneratorFactory(startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo);

        return generateAndPersistFileEventData(fileOperationGeneratorList, timeGeneratorFactory, contextIdGenerator, isAdmin);
    }

    /**
     * For each operation generator create file events that belong to specific user
     * The events are spread as define by the time generator that is returned by the time generator factory.
     * create context and time generators.
     *
     * @param fileOperationGeneratorList file operation generator list
     * @param timeGeneratorFactory       time generator factory
     * @param contextIdGenerator         string generator that return context ids.
     * @return TimeRange of records
     * @throws GeneratorException
     */
    public TimeRange generateAndPersistFileEventData(List<IFileOperationGenerator> fileOperationGeneratorList, ITimeGeneratorFactory timeGeneratorFactory, IStringGenerator contextIdGenerator, boolean isAdmin) throws GeneratorException {
        FileEventGeneratorTemplateFactory fileEventGeneratorTemplateFactory = new FileEventGeneratorTemplateFactory();
        MultiFileEventGenerator multiFileEventGenerator = fileEventGeneratorTemplateFactory.createMultiFileEventGenerator(timeGeneratorFactory, isAdmin, contextIdGenerator, fileOperationGeneratorList);
        List<EnrichedFileRecord> enrichedFileRecords = multiFileEventGenerator.generate();

        TimeRange dataTimeRange = getEnrichedFileRecordsTimeRange(enrichedFileRecords);

        storeEnrichedData(enrichedFileRecords, dataTimeRange);

        return dataTimeRange;
    }

    private void storeEnrichedData(List<? extends EnrichedRecord> records, TimeRange dataTimeRange){
        EnrichedRecordsMetadata recordsMetadata = new EnrichedRecordsMetadata("file", dataTimeRange.getStart(), dataTimeRange.getEnd());
        enrichedDataStore.store(recordsMetadata, records);
    }

    private TimeRange getEnrichedFileRecordsTimeRange(List<EnrichedFileRecord> enrichedFileRecords){
        Instant start = enrichedFileRecords.stream().min(Comparator.comparing(EnrichedFileRecord::getStartInstant)).get().getStartInstant();
        Instant end = enrichedFileRecords.stream().max(Comparator.comparing(EnrichedFileRecord::getStartInstant)).get().getStartInstant();

        return new TimeRange(start, end);
    }

    @Import({BaseAppTest.springConfig.class})
    @Configuration
    protected static class EnrichedDataBaseAppSpringConfig {

    }
}
