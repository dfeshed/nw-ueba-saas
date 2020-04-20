package com.rsa.netwitness.presidio.automation.test.ade;


import com.rsa.netwitness.presidio.automation.common.helpers.DateTimeHelperUtils;
import com.rsa.netwitness.presidio.automation.common.scenarios.process.ProcessOperationAnomalies;
import com.rsa.netwitness.presidio.automation.domain.ade.AdeRawEnrichedTestData;
import com.rsa.netwitness.presidio.automation.domain.ade.AdeScoredTestData;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.process.ProcessEnrichStoredData;
import com.rsa.netwitness.presidio.automation.domain.repository.ProcessEnrichStoredDataRepository;
import com.rsa.netwitness.presidio.automation.data.processing.mongo_core.ADETestManager;
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
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.generators.common.GeneratorException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, ADETestManagerConfig.class})
public class Process_Scored_Test extends AbstractTestNGSpringContextTests {
    private static final String SCHEMA = "process";
    private static final String unexpectedDocsCountMsg = "Number of collection \"%s\" documents do not match expected for user %s (scores between %d-%d)\n";
    Instant firstEventTime;
    Instant lastEventTime;
    @Autowired
    private ADETestManager adeTestManager;
    @Autowired
    private ProcessEnrichStoredDataRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @DataProvider(name = "testParams")
    public Object[][] getData() {
        return new Object[][]{
                {new AdeScoredTestData("ade_testuser1", 90, 100, "scored_enriched_process_startInstant_userId_process_score", 0, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_process_recon_1", 1, 10, "scored_enriched_process_dstProcessFileName_userId_process_score", 96, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_process_recon_2", 0, 100, "scored_enriched_process_dstProcessFileName_userId_process_score", 48, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_process_win_1", 80, 100, "scored_enriched_process_srcProcessFilePath_windowsDstProcessFileName_process_score", 19, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_process_lsass_1", 80, 100, "scored_enriched_process_srcProcessFilePath_lsass_process_score", 18, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_process_script_1", 80, 100, "scored_enriched_process_dstProcessFilePathOpened_scriptingSrcProcessFileName_process_score", 48, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_process_script_2", 80, 100, "scored_enriched_process_dstProcessFilePathCreated_scriptingSrcProcessFileName_process_score", 48, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_process_script_3", 80, 100, "scored_enriched_process_srcProcessFilePath_scriptingDstProcessFileName_process_score", 24, unexpectedDocsCountMsg)},
        };
    }

    @BeforeClass
    public void prepare() throws GeneratorException {
        adeTestManager.clearAllCollections();

        // Generate events
        List<ProcessEvent> events = new ArrayList<>();

        // StartInstant scenarios
        events.addAll(ProcessOperationAnomalies.getAbnormalProcessInjectedIntoLSASS("ade_process_lsass_1", 2));
        events.addAll(ProcessOperationAnomalies.getAbnormalProcessInjectedIntoWindowsProcess("ade_process_win_1", 2));
        events.addAll(ProcessOperationAnomalies.getAbnormalReconnaissanceTool("ade_process_recon_1", 2));
        events.addAll(ProcessOperationAnomalies.getReconnaissanceToolExecutedFirstTime("ade_process_recon_2", 2));
        events.addAll(ProcessOperationAnomalies.getAbnormalProcessOpenedByScript("ade_process_script_1", 2));
        events.addAll(ProcessOperationAnomalies.getAbnormalAppTriggeredByScript("ade_process_script_2", 2));
        events.addAll(ProcessOperationAnomalies.getAbnormalProcessExecutesScript("ade_process_script_3", 2));

        // find time interval for "process" ADE commands
        firstEventTime = DateTimeHelperUtils.getFirstEventTime(events);
        lastEventTime = DateTimeHelperUtils.getLastEventTime(events);

        // Store all events
        adeTestManager.insert(events);

        // Process all events
        adeTestManager.processEnriched2Scored(firstEventTime, SCHEMA);
    }

    /***
     * Verification parameters provider for input (enriched) data amounts per user
     */
    @DataProvider(name = "preconditionTestParams")
    public Object[][] getPreconditionTestParams() {
        return new Object[][]{
                {new AdeRawEnrichedTestData("ade_process_lsass_1", firstEventTime, lastEventTime, 325)},
                {new AdeRawEnrichedTestData("ade_process_win_1", firstEventTime, lastEventTime, 331)},
                {new AdeRawEnrichedTestData("ade_process_recon_1", firstEventTime, lastEventTime, 402)},
                {new AdeRawEnrichedTestData("ade_process_recon_2", firstEventTime, lastEventTime, 367)},
                {new AdeRawEnrichedTestData("ade_process_script_1", firstEventTime, lastEventTime, 299)},
                {new AdeRawEnrichedTestData("ade_process_script_2", firstEventTime, lastEventTime, 355)},
                {new AdeRawEnrichedTestData("ade_process_script_3", firstEventTime, lastEventTime, 331)},
        };
    }

    @Test(dataProvider = "preconditionTestParams")
    public void preconditionTestDataValidation(AdeRawEnrichedTestData data) {
        List<ProcessEnrichStoredData> actualEvents = repository.findByTimeAndUser(data.getStartInstant(), data.getEndInstant(), data.getTestUser());
        System.out.println("Actual events count: " + actualEvents.size());
        Assert.assertTrue(actualEvents.size() >= data.getExpectedCount(), String
                .format("Unexpected events count on enriched collection: enrich_%s for user: %s\n", "process", data.getTestUser()));
    }

    @Test(dataProvider = "testParams")
    public void scoredDataVerifications(AdeScoredTestData data) {
        Assert.assertEquals(adeTestManager.getNumberOfScoredEnrichedDocuments(data.getTestUser(),
                data.getLowestScore(), data.getHighestScore(), data.getCollection()), data.getExpectedCount(),
                String.format(data.getTestFailedMessage(), data.getCollection(), data.getTestUser(), data.getLowestScore(), data.getHighestScore()));
    }
}
