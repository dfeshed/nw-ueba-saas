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
    public TimeRange generateAndPersistFileEventData(List<IFileOperationGenerator> fileOperationGeneratorList, int startHourOfDay, int endHourOfDay, int daysBackFrom, int daysBackTo, String contextIdPattern) throws GeneratorException {

        StringRegexCyclicValuesGenerator contextIdGenerator = new StringRegexCyclicValuesGenerator(contextIdPattern);
        ITimeGeneratorFactory timeGeneratorFactory = new SingleTimeGeneratorFactory(startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo);

        FileEventGeneratorTemplateFactory fileEventGeneratorTemplateFactory = new FileEventGeneratorTemplateFactory();
        MultiFileEventGenerator multiFileEventGenerator = fileEventGeneratorTemplateFactory.createMultiFileEventGenerator(timeGeneratorFactory, contextIdGenerator, fileOperationGeneratorList);
        List<EnrichedFileRecord> enrichedFileRecords = multiFileEventGenerator.generate();

        TimeRange dataTimeRange = getEnrichedFileRecordsTimeRange(enrichedFileRecords);

        storeEnrichedData(enrichedFileRecords, dataTimeRange);

        return dataTimeRange;
    }

    /**
     * Generate every interval an event s.
     * Operation type of all the events is "open"
     *
     * @throws GeneratorException
     */
    public List<EnrichedFileRecord> generateAndPersistFileEventData(int interval, int daysBackFrom, int daysBackTo) throws GeneratorException {

        FileEventsGenerator filePermissionEventGenerator = new FileEventsGenerator();
        filePermissionEventGenerator.setTimeGenerator(new TimeGenerator(LocalTime.of(0, 0), LocalTime.of(23, 59), interval, daysBackFrom, daysBackTo));

        IUserGenerator userGenerator = new SingleUserGenerator("testUser");
        filePermissionEventGenerator.setUserGenerator(userGenerator);
        FileOperationGenerator fileOperationGenerator = new FileOperationGenerator();

        OperationType permissionOperationType = new OperationType("FILE_ACCESS_RIGHTS_CHANGED", Collections.singletonList("FILE_PERMISSION_CHANGE"));
        FixedFileOperationTypeGenerator fileOpTypePremmisionCategoriesGenerator = new FixedFileOperationTypeGenerator(permissionOperationType);

        fileOperationGenerator.setOperationTypeGenerator(fileOpTypePremmisionCategoriesGenerator);

        filePermissionEventGenerator.setFileOperationGenerator(fileOperationGenerator);
        List<FileEvent> event = filePermissionEventGenerator.generate();
        FileEventsGenerator fileActionEventGenerator = new FileEventsGenerator();
        FileOperationGenerator fileOperationActionGenerator = new FileOperationGenerator();
        fileActionEventGenerator.setTimeGenerator(new TimeGenerator(LocalTime.of(0, 0), LocalTime.of(23, 59), interval, daysBackFrom, daysBackTo));
        fileActionEventGenerator.setUserGenerator(userGenerator);
        ArrayList<String> fileActionCategories = new ArrayList<>();
        fileActionCategories.add("FILE_ACTION");
        String actionOperationName = "FOLDER_OPENED";
        OperationType fileActionOperationType = new OperationType(actionOperationName, fileActionCategories);
        FixedFileOperationTypeGenerator fileOpTypeActionCategoriesGenerator = new FixedFileOperationTypeGenerator(fileActionOperationType);
        fileOperationActionGenerator.setOperationTypeGenerator(fileOpTypeActionCategoriesGenerator);
        fileActionEventGenerator.setFileOperationGenerator(fileOperationActionGenerator);
        event.addAll(fileActionEventGenerator.generate());


        FileRaw2EnrichedConverter converter = new FileRaw2EnrichedConverter();
        List<EnrichedFileRecord> enrichedRecords = converter.convert(event);
        storeEnrichedData(enrichedRecords, getEnrichedFileRecordsTimeRange(enrichedRecords));

        return enrichedRecords;
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
