package presidio.ade.test.utils.generators;

import fortscale.common.general.Schema;
import presidio.ade.domain.record.enriched.file.AdeScoredFileRecord;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStore;
import presidio.ade.test.utils.EventsGenerator;
import presidio.ade.test.utils.converters.FileRaw2ScoredEnrichedConverter;
import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.FixedOperationTypeGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.file.FileEventsGenerator;
import presidio.data.generators.fileop.FileOperationGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by barak_schuster on 15/08/2017.
 */
public class ScoredEnrichedFileGenerator implements EventsGenerator<AdeScoredFileRecord> {
    public static final String FEATURE_NAME = "operationType.userIdFilePermissionChange.file.score";
    public static final String GENERATED_USER = "testUser";
    public static final double GENERATED_SCORE = 88D;
    private ScoredEnrichedDataStore scoredEnrichedDataStore;
    private FileRaw2ScoredEnrichedConverter converter;

    protected static final int DAYS_BACK_FROM = 3;
    protected static final int DAYS_BACK_TO = 1;

    protected static final Schema ADE_EVENT_TYPE = Schema.FILE;
    private static final Duration DURATION = Duration.ofDays(1);

    public ScoredEnrichedFileGenerator(ScoredEnrichedDataStore scoredEnrichedDataStore, FileRaw2ScoredEnrichedConverter converter) {
        this.scoredEnrichedDataStore = scoredEnrichedDataStore;
        this.converter = converter;
    }

    @Override
    public List<AdeScoredFileRecord> generateAndPersistSanityData(int interval) throws GeneratorException {
        FileEventsGenerator filePermissionEventGenerator = new FileEventsGenerator();
        filePermissionEventGenerator.setTimeGenerator(new MinutesIncrementTimeGenerator(LocalTime.of(0, 0), LocalTime.of(23, 59), 30, DAYS_BACK_FROM, DAYS_BACK_TO));
        filePermissionEventGenerator.setUserGenerator(new SingleUserGenerator(GENERATED_USER));
        FileOperationGenerator fileOperationGenerator = new FileOperationGenerator();
        ArrayList<String> filePermissionCategories = new ArrayList<>();
        filePermissionCategories.add("FILE_PERMISSION_CHANGE");
        String permissionOperationName = "FILE_ACCESS_RIGHTS_CHANGED";

        OperationType permissionOperationType = new OperationType(permissionOperationName, filePermissionCategories);
        FixedOperationTypeGenerator fileOpTypePremmisionCategoriesGenerator = new FixedOperationTypeGenerator(permissionOperationType);

        fileOperationGenerator.setOperationTypeGenerator(fileOpTypePremmisionCategoriesGenerator);

        filePermissionEventGenerator.setFileOperationGenerator(fileOperationGenerator);
        List<FileEvent> events = filePermissionEventGenerator.generate();
        converter.setScore(GENERATED_SCORE);
        converter.setFeatureName(FEATURE_NAME);
        List<AdeScoredFileRecord> scoredFileRecords = converter.convert(events);
        scoredEnrichedDataStore.store(scoredFileRecords);
        return scoredFileRecords;
    }
}
