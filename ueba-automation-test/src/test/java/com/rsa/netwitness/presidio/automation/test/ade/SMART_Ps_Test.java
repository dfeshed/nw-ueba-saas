package com.rsa.netwitness.presidio.automation.test.ade;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.common.scenarios.activedirectory.AdOperationTypeAnomalies;
import com.rsa.netwitness.presidio.automation.common.scenarios.file.FileDateTimeAnomalies;
import com.rsa.netwitness.presidio.automation.common.scenarios.file.FileOperationTypeAnomalies;
import com.rsa.netwitness.presidio.automation.common.scenarios.process.ProcessOperationAnomalies;
import com.rsa.netwitness.presidio.automation.data.processing.mongo_core.ADETestManager;
import com.rsa.netwitness.presidio.automation.data.processing.mongo_core.AdeDataProcessingManager;
import com.rsa.netwitness.presidio.automation.domain.ade.AdeScoredTestData;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.repository.FileEnrichStoredDataRepository;
import com.rsa.netwitness.presidio.automation.utils.ade.config.ADETestManagerConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;

import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;


@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, ADETestManagerConfig.class})
public class SMART_Ps_Test extends AbstractTestNGSpringContextTests {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(SMART_Ps_Test.class);

    private static final String unexpectedDocsCountMsg = "Number of collection \"%s\" documents do not match expected for user %s (scores between %d-%d)\n";
    private static final String testUser1 = "ade_smart1";
    private static final String testUser2 = "ade_smart2";
    private static final String testUser3 = "ade_smart3";
    private static final int anomalyDay = 4;
    @Autowired
    private ADETestManager adeTestManager;
    @Autowired
    private FileEnrichStoredDataRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @DataProvider(name = "testParams")
    public Object[][] getData() {
        return new Object[][]{
                {new AdeScoredTestData(testUser1, 90, 100, "smart_userId_hourly", 1, unexpectedDocsCountMsg)},
                {new AdeScoredTestData(testUser2, 90, 100, "smart_userId_hourly", 1, unexpectedDocsCountMsg)},
                {new AdeScoredTestData(testUser3, 90, 100, "smart_userId_hourly", 1, unexpectedDocsCountMsg)},
        };
    }

    @BeforeClass
    public void prepare() throws GeneratorException {
        adeTestManager.clearAllCollections();

        // One event at 10:00-11:00 every day
        ITimeGenerator normalTimeGenerator = new MinutesIncrementTimeGenerator(LocalTime.of(13, 0), LocalTime.of(21, 0), 60, anomalyDay + 30, anomalyDay + 1);

        // All static ops at 10:00-11:00, day -5
        ITimeGenerator abnormalTimeGenerator = new MinutesIncrementTimeGenerator(LocalTime.of(10, 0), LocalTime.of(11, 0), 5, anomalyDay + 1, anomalyDay);

        prepareAndProcessEvents(normalTimeGenerator, abnormalTimeGenerator);
    }

    private void prepareAndProcessEvents(ITimeGenerator normalTimeGenerator, ITimeGenerator abnormalTimeGenerator) throws GeneratorException {
        Instant normalPeriodStart = normalTimeGenerator.getFirst().truncatedTo(ChronoUnit.DAYS);
        Instant abnormalPeriodStart = abnormalTimeGenerator.getFirst().truncatedTo(ChronoUnit.DAYS);
        Instant abnormalPeriodEnd = abnormalTimeGenerator.getLast().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS);

        /** Generate active directory events - static Ps only*/
        List<List<? extends Event>> allEvents = new ArrayList<>();

        List<ActiveDirectoryEvent> events = new ArrayList<>();
        events.addAll(AdOperationTypeAnomalies.getNormalOperation4StaticPs(testUser1, normalTimeGenerator));
        events.addAll(AdOperationTypeAnomalies.getAllOperation4StaticPs(testUser1, normalTimeGenerator));
        events.addAll(AdOperationTypeAnomalies.getAllOperation4StaticPs(testUser1, abnormalTimeGenerator));

        events.addAll(AdOperationTypeAnomalies.getAdminChangedHisPassword(testUser3, normalTimeGenerator));
        events.addAll(AdOperationTypeAnomalies.getAllOperation4StaticPs(testUser3,
                new MinutesIncrementTimeGenerator(LocalTime.of(0, 0), LocalTime.of(1, 0), 5, anomalyDay + 1, anomalyDay)));

        allEvents.add(events);

        /** Generate file events
         * File, P indicator
         * 1. Time anomaly
         * 2. One P with abnormal action FILE_OPENED at day -5
         *
         * Smart on:
         * 3. One P with abnormal action FILE_DELETED at dayOfAnomaly
         * 4. One P with successful permission changed at dayOfAnomaly
         * 5. One P with failed permission changed at dayOfAnomaly
         * */
        List<FileEvent> fileEvents = new ArrayList<>();
        fileEvents.addAll(FileDateTimeAnomalies.getTimeActivity(testUser2, normalTimeGenerator));
        fileEvents.addAll(FileDateTimeAnomalies.getTimeActivity(testUser2, abnormalTimeGenerator));
        fileEvents.addAll(FileOperationTypeAnomalies.createFileDeleteAnomaly(testUser2, abnormalTimeGenerator));
        allEvents.add(fileEvents);


        /** Generate some process events
         * Process schema, P indicator
         * */
        List<ProcessEvent> processEvents = ProcessOperationAnomalies.getAbnormalProcessInjectedIntoLSASS("ade_process_lsass_1", 2);
        allEvents.add(processEvents);

        adeTestManager.insertAllEvents(allEvents);

        runAde(allEvents, normalPeriodStart, abnormalPeriodStart, abnormalPeriodEnd);
    }

    /** Process events by running ADE applications*/
    private void runAde(List<List<? extends Event>> allEvents, Instant normalPeriodStart, Instant abnormalPeriodStart, Instant abnormalPeriodEnd) {
        List<String> SCHEMAS_TO_PROCESS = List.of("file", "active_directory", "process");
        AdeDataProcessingManager adeTestManagerPar = new AdeDataProcessingManager();
        adeTestManager.insertAllEvents(allEvents);

        Stream<Callable<Integer>> cycle1 = Stream.of(
                SCHEMAS_TO_PROCESS.stream().map(schema -> adeTestManagerPar.processFeatureAggr(normalPeriodStart, abnormalPeriodStart, "hourly", schema)),
                SCHEMAS_TO_PROCESS.stream().map(schema -> adeTestManagerPar.processModelFeatureBuckets(normalPeriodStart, abnormalPeriodStart, "hourly", schema))
        ).flatMap(e -> e);

        Stream<Callable<Integer>> cycle2 =  SCHEMAS_TO_PROCESS.stream().map(schema -> adeTestManagerPar.processScoreAggr(normalPeriodStart, abnormalPeriodStart, "hourly", schema));

        Stream<Callable<Integer>> cycle3 = SCHEMAS_TO_PROCESS.stream().map(schema -> adeTestManagerPar.processScoreAggr(abnormalPeriodStart, abnormalPeriodEnd, "hourly", schema));

        //Ps for normal
        runParallelTasks(cycle1);
        adeTestManager.processModeling("enriched-record-models", "test-run", abnormalPeriodStart);
        runParallelTasks(cycle2);

        //SMART
        adeTestManager.processSmart(normalPeriodStart, abnormalPeriodStart);
        adeTestManager.processAccumulateSmart(normalPeriodStart, abnormalPeriodStart);
        adeTestManager.processModeling("smart-record-models", "test-run", abnormalPeriodStart);

        // new Ps again, for SMART anomaly
        runParallelTasks(cycle3);

        // New Smarts
        adeTestManager.processSmart(abnormalPeriodStart, abnormalPeriodEnd);
    }

    @Test(dataProvider = "testParams")
    public void smartDataVerifications(AdeScoredTestData data) {
        Assert.assertEquals(adeTestManager.getNumberOfSmartDocuments(data.getTestUser(),
                data.getLowestScore(), data.getHighestScore(), data.getCollection()), data.getExpectedCount(),
                String.format(data.getTestFailedMessage(), data.getCollection(), data.getTestUser(), data.getLowestScore(), data.getHighestScore()));
    }


    private void runParallelTasks(Stream<Callable<Integer>> parallelTasks) {
        try {
            ExecutorService executor = Executors.newWorkStealingPool();
            List<Future<Integer>> futures = executor.invokeAll(parallelTasks.collect(toList()), 30, TimeUnit.MINUTES);
            List<Integer> results = futures.parallelStream().map(toIntResult).collect(toList());
            assertThat(results).containsOnly(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Function<Future<Integer>, Integer> toIntResult = future -> {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return -100;
    };

}
