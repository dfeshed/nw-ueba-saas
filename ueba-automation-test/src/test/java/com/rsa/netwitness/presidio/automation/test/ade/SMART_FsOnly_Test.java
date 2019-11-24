package com.rsa.netwitness.presidio.automation.test.ade;


import com.rsa.netwitness.presidio.automation.common.scenarios.activedirectory.AdHighNumberOfOperations;
import com.rsa.netwitness.presidio.automation.common.scenarios.authentication.AuthenticationMachineAnomalies;
import com.rsa.netwitness.presidio.automation.common.scenarios.file.FileHighNumberOfOperations;
import com.rsa.netwitness.presidio.automation.common.scenarios.process.ProcessHighNumberOfOperations;
import com.rsa.netwitness.presidio.automation.domain.ade.AdeScoredTestData;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.repository.FileEnrichStoredDataRepository;
import com.rsa.netwitness.presidio.automation.test_managers.ADETestManager;
import com.rsa.netwitness.presidio.automation.utils.ade.config.ADETestManagerConfig;
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
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.common.time.TimeGenerator;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, ADETestManagerConfig.class})
public class SMART_FsOnly_Test extends AbstractTestNGSpringContextTests {
    private static final String unexpectedDocsCountMsg = "Number of collection \"%s\" documents do not match expected for user %s (scores between %d-%d)\n";
    private static final String testUser1 = "ade_smart_Fs1";
    private static final String testUser2 = "ade_smart_Fs2";
    private static final String testUser3 = "ade_smart_Fs3";
    private static final String testUser4 = "ade_smart_Fs4";
    private static final String testUser5 = "ade_smart_Fs5";
    private static final int anomalyDay = 3;
    @Autowired
    private ADETestManager adeTestManager;
    @Autowired
    private FileEnrichStoredDataRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @DataProvider(name = "testParams")
    public Object[][] getData() {
        return new Object[][]{
                {new AdeScoredTestData("ade_smart_Fs1", 90, 100, "smart_userId_hourly", 1, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_smart_Fs2", 90, 100, "smart_userId_hourly", 1, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_smart_Fs3", 90, 100, "smart_userId_hourly", 1, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_smart_Fs5_a", 90, 100, "smart_userId_hourly", 7, unexpectedDocsCountMsg)},
        };
    }

    @BeforeClass
    public void prepare() throws GeneratorException {
        adeTestManager.clearAllCollections();

        // One event at 10:00-11:00 every day
        ITimeGenerator normalTimeGenerator = new MinutesIncrementTimeGenerator(LocalTime.of(13, 0), LocalTime.of(21, 0), 60, anomalyDay + 30, anomalyDay + 1);

        // One event every few seconds at 10:00-11:00 every day
        ITimeGenerator normalFrequentTimeGenerator = new TimeGenerator(LocalTime.of(13, 0), LocalTime.of(21, 0), 5000, anomalyDay + 30, anomalyDay + 1);

        // All static ops at 10:00-11:00 on anomaly day
        ITimeGenerator abnormalTimeGenerator = new MinutesIncrementTimeGenerator(LocalTime.of(10, 0), LocalTime.of(11, 0), 5, anomalyDay + 1, anomalyDay);

        // One event every few seconds at 10:00-11:00 on anomaly day
        ITimeGenerator abnormalFrequentTimeGenerator = new TimeGenerator(LocalTime.of(10, 0), LocalTime.of(11, 0), 1000, anomalyDay + 1, anomalyDay);

        prepareEvents(normalTimeGenerator, abnormalTimeGenerator);
        runAde(normalTimeGenerator, abnormalTimeGenerator);
    }

    private void prepareEvents(ITimeGenerator normalTimeGenerator, ITimeGenerator abnormalTimeGenerator) throws GeneratorException {

        List<FileEvent> fileEvents = new ArrayList<>();
        List<ActiveDirectoryEvent> activeDirectoryEvents = new ArrayList<>();
        List<AuthenticationEvent> authenticationEvents = new ArrayList<>();
        List<ProcessEvent> processEvents = new ArrayList<>();

        Duration anomalyDuration = Duration.between(abnormalTimeGenerator.getFirst().truncatedTo(ChronoUnit.DAYS), Instant.now().truncatedTo(ChronoUnit.DAYS));
        long anomalyDaysback = anomalyDuration.get(ChronoUnit.SECONDS);
        /** Generate events */
        fileEvents.addAll(FileHighNumberOfOperations.getPermissionChangeActionsOneHourAnomaly(testUser1, normalTimeGenerator, abnormalTimeGenerator)); // Fs at 2-1d back
        fileEvents.addAll(FileHighNumberOfOperations.getHighNumFailedFileOperations(testUser1, normalTimeGenerator, abnormalTimeGenerator)); // Fs at 2-1d back
        activeDirectoryEvents.addAll(AdHighNumberOfOperations.getHighNumFailedActiveDirectoryEvents(testUser2, normalTimeGenerator, abnormalTimeGenerator));
        authenticationEvents.addAll(AuthenticationMachineAnomalies.getAbnormalMachineActivity(true, testUser3, normalTimeGenerator, abnormalTimeGenerator));
        authenticationEvents.addAll(AuthenticationMachineAnomalies.sumOfHighestDstMachineNameRegexClusterScoresUserIdInteractiveRemote(testUser4, normalTimeGenerator, abnormalTimeGenerator));
        processEvents.addAll(ProcessHighNumberOfOperations.getHighNumOfReconnaissanceToolsByUserAndTarget(testUser5, (int) anomalyDaysback / 24 / 60 / 60));

        List<List<? extends Event>> allEvents = new ArrayList<>();
        allEvents.add(fileEvents);
        allEvents.add(activeDirectoryEvents);
        allEvents.add(authenticationEvents);
        allEvents.add(processEvents);

        adeTestManager.insertAllEvents(allEvents);
    }

    private void runAde(ITimeGenerator normalTimeGenerator, ITimeGenerator abnormalTimeGenerator) throws GeneratorException {
        /** Process events by running ADE applications*/
        Instant normalPeriodStart = normalTimeGenerator.getFirst().truncatedTo(ChronoUnit.DAYS);
        Instant abnormalPeriodStart = abnormalTimeGenerator.getFirst().truncatedTo(ChronoUnit.DAYS);
        Instant abnormalPeriodEnd = abnormalTimeGenerator.getLast().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS);

        //Fs for normal
        adeTestManager.processAccumulateAggr(normalPeriodStart, abnormalPeriodStart, "file");
        adeTestManager.processAccumulateAggr(normalPeriodStart, abnormalPeriodStart, "active_directory");
        adeTestManager.processAccumulateAggr(normalPeriodStart, abnormalPeriodStart, "authentication");
        adeTestManager.processAccumulateAggr(normalPeriodStart, abnormalPeriodStart, "process");
        adeTestManager.processModeling("feature-aggregation-record-models", "test-run", abnormalPeriodStart);
        adeTestManager.processFeatureAggr(normalPeriodStart, abnormalPeriodStart, "hourly", "file");
        adeTestManager.processFeatureAggr(normalPeriodStart, abnormalPeriodStart, "hourly", "active_directory");
        adeTestManager.processFeatureAggr(normalPeriodStart, abnormalPeriodStart, "hourly", "authentication");
        adeTestManager.processFeatureAggr(normalPeriodStart, abnormalPeriodStart, "hourly", "process");

        //SMART
        adeTestManager.processSmart(normalPeriodStart, abnormalPeriodStart);
        adeTestManager.processAccumulateSmart(normalPeriodStart, abnormalPeriodStart);
        adeTestManager.processModeling("smart-record-models", "test-run", abnormalPeriodStart);

        // new Fs again, for SMART anomaly
        adeTestManager.processFeatureAggr(abnormalPeriodStart, abnormalPeriodEnd, "hourly", "file");
        adeTestManager.processFeatureAggr(abnormalPeriodStart, abnormalPeriodEnd, "hourly", "active_directory");
        adeTestManager.processFeatureAggr(abnormalPeriodStart, abnormalPeriodEnd, "hourly", "authentication");
        adeTestManager.processFeatureAggr(abnormalPeriodStart, abnormalPeriodEnd, "hourly", "process");

        // New Smarts
        adeTestManager.processSmart(abnormalPeriodStart, abnormalPeriodEnd);
    }

    @Test(dataProvider = "testParams")
    public void smartDataVerifications(AdeScoredTestData data) {
        Assert.assertEquals(adeTestManager.getNumberOfSmartDocuments(data.getTestUser(),
                data.getLowestScore(), data.getHighestScore(), data.getCollection()), data.getExpectedCount(),
                String.format(data.getTestFailedMessage(), data.getCollection(), data.getTestUser(), data.getLowestScore(), data.getHighestScore()));
    }
}
