package presidio.ade.processes.shell;

import fortscale.common.general.Schema;
import fortscale.utils.mongodb.util.ToCollectionNameTranslator;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.shell.BootShim;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.time.TimeService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.shell.core.CommandResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.record.enriched.dlpfile.EnrichedDlpFileRecord;
import presidio.ade.domain.store.aggr.AggrRecordsMetadata;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.data.domain.event.dlpfile.DLPFileEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.generators.dlpfileop.DEFAULT_EVENT_TYPE;
import presidio.data.generators.dlpfileop.DLPFileOperationGenerator;
import presidio.data.generators.dlpfileop.OperationTypeCyclicGenerator;
import presidio.data.generators.event.dlpfile.DLPFileEventsGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@Category(ModuleTestCategory.class)
@ContextConfiguration(classes = AccumulateAggregationsConfigurationTest.class)
public class AccumulateAggregationsApplicationTest {

    private static final int DAYS_BACK_FROM = 3;
    private static final int DAYS_BACK_TO = 1;
    private static final long FIXED_DURATION_STRATEGY = 86400;
    private static final long FEATURE_BUCKET_STRATEGY = 3600;

    private static final Schema ADE_EVENT_TYPE = Schema.DLPFILE;
    private static final Duration DURATION = Duration.ofDays(1);
    private static final Instant START_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_FROM)), DURATION);
    private static final Instant END_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_TO)), DURATION);

    private static final String EXECUTION_COMMAND = String.format("run  --schema %s --start_date %s --end_date %s --fixed_duration_strategy %s --feature_bucket_strategy %s", ADE_EVENT_TYPE, START_DATE, END_DATE, FIXED_DURATION_STRATEGY, FEATURE_BUCKET_STRATEGY);

    @Autowired
    private BootShim bootShim;
    @Autowired
    private EnrichedDataStore enrichedDataStore;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    public ToCollectionNameTranslator toCollectionNameTranslator;


    @Test
    public void contextTest() {
        CommandResult commandResult = bootShim.getShell().executeCommand(EXECUTION_COMMAND);
        Assert.assertTrue(commandResult.isSuccess());
    }

    /**
     * Generate 2 events per hour along 24 hours for 2 days.
     * Operation type of all the events is "open"
     * <p>
     * Expected result:
     * 2 accumulatedRecords - record per each day.
     * 24 aggregatedFeatureValues for each record.
     * value of each aggregatedFeatureValues is 2 (2 opens files in each hour)
     */
    @Test
    public void sanityTest() {
        try {
            int interval = 30;
            generateAndPersist(interval);
            CommandResult commandResult = bootShim.getShell().executeCommand(EXECUTION_COMMAND);
            Assert.assertTrue(commandResult.isSuccess());

            Set<String> collectionNames = mongoTemplate.getCollectionNames();

            AggrRecordsMetadata aggrRecordsMetadata = new AggrRecordsMetadata("number_of_opened_files_normalized_username_hourly_dlpfile");
            List<AccumulatedAggregationFeatureRecord> accumulatedRecords = mongoTemplate.findAll(AccumulatedAggregationFeatureRecord.class, toCollectionNameTranslator.toCollectionName(aggrRecordsMetadata));

            //expected results
            int expectedAccumulatedRecordsSize = DAYS_BACK_FROM - DAYS_BACK_TO;
            int expectedAggregatedFeatureValuesSize = 24;
            int expectedAggregatedFeatureValue = 2;

            Assert.assertTrue(accumulatedRecords.size() == expectedAccumulatedRecordsSize);

            long days = 0;
            for (AccumulatedAggregationFeatureRecord accumulatedRecord : accumulatedRecords) {
                Duration duration = Duration.ofDays(days);
                Instant start = START_DATE.plusSeconds((duration.getSeconds()));
                Instant end = start.plusSeconds((DURATION.getSeconds()));
                days++;

                List<Double> aggregatedFeatureValues = accumulatedRecord.getAggregatedFeatureValues();
                Assert.assertTrue(aggregatedFeatureValues.size() == expectedAggregatedFeatureValuesSize);

                Assert.assertTrue(accumulatedRecord.getStartInstant().equals(start));
                Assert.assertTrue(accumulatedRecord.getEndInstant().equals(end));

                for (Double aggregatedFeatureValue : aggregatedFeatureValues) {
                    Assert.assertTrue(aggregatedFeatureValue == expectedAggregatedFeatureValue);
                }
            }
        } catch (GeneratorException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate events with "open" operation Type.
     *
     * @throws GeneratorException
     */
    public void generateAndPersist(int interval) throws GeneratorException {
        String testUser = "testUser";
        DLPFileEventsGenerator generator = new DLPFileEventsGenerator();
        generator.setTimeGenerator(new TimeGenerator(LocalTime.of(0, 0), LocalTime.of(23, 59), interval, DAYS_BACK_FROM, DAYS_BACK_TO));
        generator.setUserGenerator(new SingleUserGenerator(testUser));

        OperationTypeCyclicGenerator operationTypeCyclicGenerator = new OperationTypeCyclicGenerator(new String[]{DEFAULT_EVENT_TYPE.FILE_OPEN.value});
        DLPFileOperationGenerator dlpFileOperationGenerator = new DLPFileOperationGenerator();
        dlpFileOperationGenerator.setEventTypeGenerator(operationTypeCyclicGenerator);
        generator.setFileOperationGenerator(dlpFileOperationGenerator);

        List<DLPFileEvent> event = generator.generate();
        List<EnrichedDlpFileRecord> enrichedDlpFileRecords = convert(event);

        EnrichedRecordsMetadata enrichedRecordsMetadata = new EnrichedRecordsMetadata(ADE_EVENT_TYPE.getName(), START_DATE, END_DATE);
        enrichedDataStore.store(enrichedRecordsMetadata, enrichedDlpFileRecords);
    }


    /**
     * Convert DLPFileEvent list to EnrichedDlpFileRecord list
     * @param events
     * @return
     */
    private List<EnrichedDlpFileRecord> convert(List<DLPFileEvent> events) {

        List<EnrichedDlpFileRecord> enrichedEventsList = new ArrayList<>();

        for (DLPFileEvent event : events) {
            EnrichedDlpFileRecord enrichedEvent = new EnrichedDlpFileRecord(event.getDateTime());

            enrichedEvent.setUserId(event.getNormalizedUsername());
            enrichedEvent.setSrcMachineId(event.getNormalized_src_machine());
            enrichedEvent.setSourcePath(event.getSourcePath());
            enrichedEvent.setSourceFileName(event.getSourceFileName());
            enrichedEvent.setSourceDriveType(event.getSourceDriveType());
            enrichedEvent.setDestinationPath(event.getDestinationPath());
            enrichedEvent.setDestinationFileName(event.getDestinationFileName());
            enrichedEvent.setDestinationDriveType(event.getDestinationDriveType());
            enrichedEvent.setFileSize(event.getFileSize());
            enrichedEvent.setOperationType(event.getEventType());
            enrichedEvent.setWasBlocked(event.getWasBlocked());
            enrichedEvent.setWasClassified(event.getWasClassified());
            enrichedEvent.setMalwareScanResult(event.getMalwareScanResult());
            enrichedEvent.setExecutingApplication(event.getExecutingApplication());

            enrichedEventsList.add(enrichedEvent);
        }
        return enrichedEventsList;
    }
}