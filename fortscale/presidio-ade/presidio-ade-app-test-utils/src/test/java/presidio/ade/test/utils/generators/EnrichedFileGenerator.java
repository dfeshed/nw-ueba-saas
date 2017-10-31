package presidio.ade.test.utils.generators;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeService;
import presidio.ade.domain.record.enriched.file.EnrichedFileRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.ade.test.utils.EventsGenerator;
import presidio.ade.test.utils.converters.FileRaw2EnrichedConverter;
import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.FixedOperationTypeGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.generators.event.file.FileEventsGenerator;
import presidio.data.generators.fileop.FileOperationGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EnrichedFileGenerator implements EventsGenerator<EnrichedFileRecord> {

    protected static final int DAYS_BACK_FROM = 3;
    protected static final int DAYS_BACK_TO = 1;

    protected static final Schema ADE_EVENT_TYPE = Schema.FILE;
    private static final Duration DURATION = Duration.ofDays(1);
    protected static final Instant START_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_FROM)), DURATION);
    protected static final Instant END_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_TO)), DURATION);
    protected final FileRaw2EnrichedConverter converter;

    protected EnrichedDataStore enrichedDataStore;
    private IUserGenerator userGenerator = new SingleUserGenerator("testUser");

    public EnrichedFileGenerator(EnrichedDataStore enrichedDataStore) throws GeneratorException {
        this.enrichedDataStore = enrichedDataStore;
        this.converter  = new FileRaw2EnrichedConverter();
    }

    /**
     * Generate events.
     *
     * @throws GeneratorException
     */
    public List<EnrichedFileRecord> generateAndPersist(int interval) throws GeneratorException {

        FileEventsGenerator filePermissionEventGenerator = new FileEventsGenerator();
        filePermissionEventGenerator.setTimeGenerator(new TimeGenerator(LocalTime.of(0, 0), LocalTime.of(23, 59), interval, DAYS_BACK_FROM, DAYS_BACK_TO));

        filePermissionEventGenerator.setUserGenerator(userGenerator);
        FileOperationGenerator fileOperationGenerator = new FileOperationGenerator();
        ArrayList<String> filePermissionCategories = new ArrayList<>();
        filePermissionCategories.add("FILE_PERMISSION_CHANGE");
        String permissionOperationName = "FILE_ACCESS_RIGHTS_CHANGED";

        OperationType permissionOperationType = new OperationType(permissionOperationName, filePermissionCategories);
        FixedOperationTypeGenerator fileOpTypePremmisionCategoriesGenerator = new FixedOperationTypeGenerator(permissionOperationType);

        fileOperationGenerator.setOperationTypeGenerator(fileOpTypePremmisionCategoriesGenerator);

        filePermissionEventGenerator.setFileOperationGenerator(fileOperationGenerator);
        List<FileEvent> event = filePermissionEventGenerator.generate();
        FileEventsGenerator fileActionEventGenerator = new FileEventsGenerator();
        FileOperationGenerator fileOperationActionGenerator = new FileOperationGenerator();
        fileActionEventGenerator.setTimeGenerator(new TimeGenerator(LocalTime.of(0, 0), LocalTime.of(23, 59), interval, DAYS_BACK_FROM, DAYS_BACK_TO));
        fileActionEventGenerator.setUserGenerator(userGenerator);
        ArrayList<String> fileActionCategories = new ArrayList<>();
        fileActionCategories.add("FILE_ACTION");
        String actionOperationName = "FOLDER_OPENED";
        OperationType fileActionOperationType = new OperationType(actionOperationName, fileActionCategories);
        FixedOperationTypeGenerator fileOpTypeActionCategoriesGenerator = new FixedOperationTypeGenerator(fileActionOperationType);
        fileOperationActionGenerator.setOperationTypeGenerator(fileOpTypeActionCategoriesGenerator);
        fileActionEventGenerator.setFileOperationGenerator(fileOperationActionGenerator);
        event.addAll(fileActionEventGenerator.generate());
        List<EnrichedFileRecord> enrichedRecords = converter.convert(event);
        EnrichedRecordsMetadata enrichedRecordsMetadata = new EnrichedRecordsMetadata(ADE_EVENT_TYPE.getName(), START_DATE, END_DATE);

        enrichedDataStore.store(enrichedRecordsMetadata, enrichedRecords);
        return enrichedRecords;
    }

    @Override
    public List<EnrichedFileRecord> generateAndPersistSanityData(int interval) throws GeneratorException {
        return generateAndPersist(interval);
    }

    public void setUserGenerator(IUserGenerator userGenerator) {
        this.userGenerator = userGenerator;
    }

    protected int getInterval() {
        return 10;
    }
}
