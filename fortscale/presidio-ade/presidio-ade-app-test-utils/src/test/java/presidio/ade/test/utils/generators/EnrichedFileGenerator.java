package presidio.ade.test.utils.generators;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeService;
import presidio.ade.domain.record.enriched.dlpfile.EnrichedDlpFileRecord;
import presidio.ade.domain.record.enriched.file.EnrichedFileRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.ade.test.utils.EnrichedEventsGenerator;
import presidio.ade.test.utils.converters.DLPFileRaw2EnrichedConverter;

import presidio.ade.test.utils.converters.FileRaw2EnrichedConverter;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.generators.dlpfileop.OperationTypeCyclicGenerator;
import presidio.data.generators.event.file.FileEventsGenerator;
import presidio.data.generators.fileop.FileOperationGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

public class EnrichedFileGenerator implements EnrichedEventsGenerator {

    private static final int DAYS_BACK_FROM = 3;
    private static final int DAYS_BACK_TO = 1;

    private static final Schema ADE_EVENT_TYPE = Schema.DLPFILE;
    private static final Duration DURATION = Duration.ofDays(1);
    private static final Instant START_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_FROM)), DURATION);
    private static final Instant END_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_TO)), DURATION);
    private final FileRaw2EnrichedConverter converter;

    private EnrichedDataStore enrichedDataStore;

    public EnrichedFileGenerator(EnrichedDataStore enrichedDataStore) {
        this.enrichedDataStore = enrichedDataStore;
        this.converter  = new FileRaw2EnrichedConverter();
    }

    /**
     * Generate events with "open" operation Type.
     *
     * @throws GeneratorException
     */
    public void generateAndPersist(int interval) throws GeneratorException {
        String testUser = "testUser";
        FileEventsGenerator generator = new FileEventsGenerator();
        generator.setTimeGenerator(new TimeGenerator(LocalTime.of(0, 0), LocalTime.of(23, 59), interval, DAYS_BACK_FROM, DAYS_BACK_TO));
        generator.setUserGenerator(new SingleUserGenerator(testUser));

        OperationTypeCyclicGenerator operationTypeCyclicGenerator = new OperationTypeCyclicGenerator();
        FileOperationGenerator fileOperationGenerator = new FileOperationGenerator();

        List<FileEvent> event = generator.generate();
        List<EnrichedFileRecord> enrichedDlpFileRecords = converter.convert(event);

        EnrichedRecordsMetadata enrichedRecordsMetadata = new EnrichedRecordsMetadata(ADE_EVENT_TYPE.getName(), START_DATE, END_DATE);
        enrichedDataStore.store(enrichedRecordsMetadata, enrichedDlpFileRecords);
    }

    @Override
    public void generateAndPersistSanityData() throws GeneratorException {
        generateAndPersist(30);
    }
}
