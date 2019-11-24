package com.rsa.netwitness.presidio.automation.test.ade;


import com.rsa.netwitness.presidio.automation.common.helpers.DateTimeHelperUtils;
import com.rsa.netwitness.presidio.automation.common.scenarios.file.FileDateTimeAnomalies;
import com.rsa.netwitness.presidio.automation.common.scenarios.file.FileOperationTypeAnomalies;
import com.rsa.netwitness.presidio.automation.domain.ade.AdeRawEnrichedTestData;
import com.rsa.netwitness.presidio.automation.domain.ade.AdeScoredTestData;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.file.FileEnrichStoredData;
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
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.GeneratorException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, ADETestManagerConfig.class})
public class File_Scored_Test extends AbstractTestNGSpringContextTests {
    private static final String SCHEMA = "file";
    private static final String unexpectedDocMsg = "Unexpected collection \"%s\" document have been created for user %s (scores between %d-%d)\n";
    private static final String unexpectedDocsCountMsg = "Number of collection \"%s\" documents do not match expected for user %s (scores between %d-%d)\n";
    private static final String fileStartInstantCollection = "scored_enriched_file_startInstant_userId_file_score";
    private static final String fileActionCollection = "scored_enriched_file_operationType_userIdFileAction_file_score";
    private static final String filePermissionChangeCollection = "scored_enriched_file_operationType_userIdFilePermissionChange_file_score";
    Instant firstEventTime;
    Instant lastEventTime;
    @Autowired
    private ADETestManager adeTestManager;
    @Autowired
    private FileEnrichStoredDataRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    /***
     * Verification parameters provider for scenario:
     *      presidio.integration.common.scenarios.file.FileDateTimeAnomalies
     *      presidio.integration.common.scenarios.file.FileOperationTypeAnomalies
     *
     * Per each row in the Object[][], separate test will run.
     * AdeScoredTestData object will be passed to the test as input parameter.
     *
     * @return AdeScoredTestData objects
     */
    @DataProvider(name = "testParams")
    public Object[][] getData() {
        return new Object[][]{
                {new AdeScoredTestData("ade_testuser1", 50, 100, fileStartInstantCollection, 2, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_testuser2", 1, 69, fileStartInstantCollection, 5, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_testuser2", 70, 100, fileStartInstantCollection, 12, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_testuser3", 70, 100, fileStartInstantCollection, 10, unexpectedDocsCountMsg)},

                {new AdeScoredTestData("ade_testuser3", 10, 60, fileActionCollection, 9, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_testuser3", 10, 60, filePermissionChangeCollection, 18, unexpectedDocsCountMsg)},
        };
    }

    @BeforeClass
    public void prepare() throws GeneratorException {
        adeTestManager.clearAllCollections();

        // Generate events
        List<FileEvent> events = new ArrayList<>();

        // StartInstant scenarios
        events.addAll(FileDateTimeAnomalies.getAbnormalTimeActivity("ade_testuser1", 2));
        events.addAll(FileDateTimeAnomalies.getAbnormalNearborderTimeDeviation("ade_testuser2", 2));

        // OPERATION TYPE scenarios
        events.addAll(FileOperationTypeAnomalies.createFilePermissionChangeAnomalyAndActionAnomaly("ade_testuser3", 2));

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
                {new AdeRawEnrichedTestData("ade_testuser1", firstEventTime, lastEventTime, 466)},
                {new AdeRawEnrichedTestData("ade_testuser2", firstEventTime, lastEventTime, 528)},
                {new AdeRawEnrichedTestData("ade_testuser3", firstEventTime, lastEventTime, 339)},
        };
    }

    @Test(dataProvider = "preconditionTestParams")
    public void preconditionTestDataValidation(AdeRawEnrichedTestData data) {
        List<FileEnrichStoredData> actualEvents = repository.findByTimeAndUser(data.getStartInstant(), data.getEndInstant(), data.getTestUser());
        Assert.assertTrue(actualEvents.size() >= data.getExpectedCount(), String
                .format("Unexpected events count on enriched collection: enrich_%s for user: %s\n", "file", data.getTestUser()));
    }

    @Test(dataProvider = "testParams")
    public void scoredDataVerifications(AdeScoredTestData data) {
        Assert.assertEquals(adeTestManager.getNumberOfScoredEnrichedDocuments(data.getTestUser(),
                data.getLowestScore(), data.getHighestScore(), data.getCollection()), data.getExpectedCount(),
                String.format(data.getTestFailedMessage(), data.getCollection(), data.getTestUser(), data.getLowestScore(), data.getHighestScore()));
    }
}
