package presidio.ade.test.utils.generators;

import presidio.ade.domain.record.enriched.file.EnrichedFileRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.data.ade.AdeFileOperationGeneratorTemplateFactory;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.file.FileEventsGenerator;
import presidio.data.generators.fileop.IFileOperationGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.LocalTime;
import java.util.List;

public class EnrichedSuccessfulFileOpenedGenerator extends EnrichedFileGenerator {

    public EnrichedSuccessfulFileOpenedGenerator(EnrichedDataStore enrichedDataStore) throws GeneratorException {
        super(enrichedDataStore);
    }

    /**
     * Generate events with "open" operation Type ans "SUCCESS" result.
     * @param interval
     * @throws GeneratorException
     */
    @Override
    public List<EnrichedFileRecord> generateAndPersist(int interval) throws GeneratorException {
        String testUser = "testUser";
        FileEventsGenerator generator = new FileEventsGenerator();
        generator.setTimeGenerator(new MinutesIncrementTimeGenerator(LocalTime.of(0, 0), LocalTime.of(23, 59), interval, DAYS_BACK_FROM, DAYS_BACK_TO));
        generator.setUserGenerator(new SingleUserGenerator(testUser));
        addSuccessfulFileOpenedOperationGenerator(generator);

        List<FileEvent> event = generator.generate();
        List<EnrichedFileRecord> enrichedRecords = converter.convert(event);

        EnrichedRecordsMetadata enrichedRecordsMetadata = new EnrichedRecordsMetadata(ADE_EVENT_TYPE.getName(), START_DATE, END_DATE);
        enrichedDataStore.store(enrichedRecordsMetadata, enrichedRecords);
        return enrichedRecords;
    }

    private void addSuccessfulFileOpenedOperationGenerator(FileEventsGenerator generator) throws GeneratorException {
        AdeFileOperationGeneratorTemplateFactory adeFileOperationGeneratorTemplateFactory = new AdeFileOperationGeneratorTemplateFactory();
        IFileOperationGenerator fileOperationGenerator = adeFileOperationGeneratorTemplateFactory.createOpenFileOperationsGenerator();
        generator.setFileOperationGenerator(fileOperationGenerator);
    }
}
