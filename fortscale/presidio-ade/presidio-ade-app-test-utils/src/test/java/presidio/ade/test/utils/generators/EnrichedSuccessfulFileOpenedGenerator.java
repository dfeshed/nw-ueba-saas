package presidio.ade.test.utils.generators;

import presidio.ade.domain.record.enriched.file.EnrichedFileRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.generators.event.OPERATION_RESULT;
import presidio.data.generators.event.file.FileEventsGenerator;
import presidio.data.generators.fileop.FileOperationGenerator;
import presidio.data.generators.fileop.FixedFileOperationTypeGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

public class EnrichedSuccessfulFileOpenedGenerator extends EnrichedFileGenerator {


    public EnrichedSuccessfulFileOpenedGenerator(EnrichedDataStore enrichedDataStore) {
        super(enrichedDataStore);
    }

    /**
     * Generate events with "open" operation Type ans "SUCCESS" result.
     * @param interval
     * @throws GeneratorException
     */
    @Override
    public void generateAndPersist(int interval) throws GeneratorException {
        String testUser = "testUser";
        FileEventsGenerator generator = new FileEventsGenerator();
        generator.setTimeGenerator(new TimeGenerator(LocalTime.of(0, 0), LocalTime.of(23, 59), interval, DAYS_BACK_FROM, DAYS_BACK_TO));
        generator.setUserGenerator(new SingleUserGenerator(testUser));

        OperationType operationType = new OperationType("FILE_OPENED", Collections.emptyList());
        FixedFileOperationTypeGenerator fixedFileOperationTypeGenerator = new FixedFileOperationTypeGenerator(operationType);
        FileOperationGenerator fileOperationGenerator = new FileOperationGenerator();
        fileOperationGenerator.setOperationTypeGenerator(fixedFileOperationTypeGenerator);

        OperationResultPercentageGenerator operationResultPercentageGenerator = new OperationResultPercentageGenerator(new String[]{OPERATION_RESULT.SUCCESS.value},  new int[]{100});
        fileOperationGenerator.setOperationResultGenerator(operationResultPercentageGenerator);
        generator.setFileOperationGenerator(fileOperationGenerator);

        List<FileEvent> event = generator.generate();
        List<EnrichedFileRecord> enrichedRecords = converter.convert(event);

        EnrichedRecordsMetadata enrichedRecordsMetadata = new EnrichedRecordsMetadata(ADE_EVENT_TYPE.getName(), START_DATE, END_DATE);
        enrichedDataStore.store(enrichedRecordsMetadata, enrichedRecords);
    }
}
