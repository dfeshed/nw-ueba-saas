package com.rsa.netwitness.presidio.automation.test.ade;

import com.rsa.netwitness.presidio.automation.common.helpers.DateTimeHelperUtils;
import com.rsa.netwitness.presidio.automation.common.scenarios.activedirectory.AdDateTimeAnomalies;
import com.rsa.netwitness.presidio.automation.common.scenarios.activedirectory.AdOperationTypeAnomalies;
import com.rsa.netwitness.presidio.automation.domain.activedirectory.ActiveDirectoryEnrichStoredData;
import com.rsa.netwitness.presidio.automation.domain.ade.AdeRawEnrichedTestData;
import com.rsa.netwitness.presidio.automation.domain.ade.AdeScoredTestData;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.repository.ActiveDirectoryEnrichStoredDataRepository;
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
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;

import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, ADETestManagerConfig.class})
public class ActiveDirectory_Scored_Test extends AbstractTestNGSpringContextTests {
    private static final String SCHEMA = "active_directory";
    private static final String unexpectedDocMsg = "Unexpected collection \"%s\" document have been created for user %s (scores between %d-%d)\n";
    private static final String unexpectedDocsCountMsg = "Number of collection \"%s\" documents do not match expected for user %s (scores between %d-%d)\n";
    Instant firstEventTime;
    Instant lastEventTime;
    @Autowired
    private ADETestManager adeTestManager;
    @Autowired
    private ActiveDirectoryEnrichStoredDataRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeClass
    public void prepare() throws GeneratorException {
        adeTestManager.clearAllCollections();

        // Generate events
        List<ActiveDirectoryEvent> events = new ArrayList<>();

        // StartInstant scenarios
        events.addAll(AdDateTimeAnomalies.getAbnormalClose2NormalActivity("ade_testuser11", 2));
        events.addAll(AdDateTimeAnomalies.getAbnormalFarFromNormalActivity("ade_testuser12", 2));

        // OPERATION TYPE scenarios
        // All events until last day - will include all operation types
        ITimeGenerator myTimeGenerator = new MinutesIncrementTimeGenerator(LocalTime.of(8, 0), LocalTime.of(16, 0), 30, 30, 1);

        events.addAll(AdOperationTypeAnomalies.getAllActiveDirOperations("ade_testuser13", myTimeGenerator));
        events.addAll(AdOperationTypeAnomalies.getAdminChangedHisPassword("ade_testuser14", myTimeGenerator));

        // find time interval for "process" ADE commands
        firstEventTime = DateTimeHelperUtils.getFirstEventTime(events);
        lastEventTime = DateTimeHelperUtils.getLastEventTime(events);

        // Store all events
        adeTestManager.insert(events);

        // Process all events
        adeTestManager.processEnriched2Scored(firstEventTime, SCHEMA);
    }

    /***
     * This is test verification parameters provider for scenario:
     *      presidio.integration.common.scenarios.activedirectory.FileDateTimeAnomalies
     *
     * Per each row in the Object[][], separate test will run.
     * AdeScoredTestData object will be passed to the test as input parameter.
     *
     * @return AdeScoredTestData objects
     */
    @DataProvider(name = "dp")
    public Object[][] getData() {
        return new Object[][]{
                // startInstant - some events happen close to normal activity time
                {new AdeScoredTestData("ade_testuser11", 1, 60, "scored_enriched_active_directory_startInstant_userId_activeDirectory_score", 10, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_testuser11", 69, 100, "scored_enriched_active_directory_startInstant_userId_activeDirectory_score", 9, unexpectedDocsCountMsg)},

                // startInstant - 5% of normal events happen during all 24h
                {new AdeScoredTestData("ade_testuser12", 0, 0, "scored_enriched_active_directory_startInstant_userId_activeDirectory_score", 16, unexpectedDocMsg)},
                {new AdeScoredTestData("ade_testuser12", 1, 50, "scored_enriched_active_directory_startInstant_userId_activeDirectory_score", 3, unexpectedDocMsg)},
        };
    }

    /***
     * Verification parameters provider for input (enriched) data amounts per user
     */
    @DataProvider(name = "preconditionTestParams")
    public Object[][] getPreconditionTestParams() {
        return new Object[][]{
                {new AdeRawEnrichedTestData("ade_testuser11", firstEventTime, lastEventTime, 498)},
                {new AdeRawEnrichedTestData("ade_testuser12", firstEventTime, lastEventTime, 490)},
        };
    }

    @Test(dataProvider = "preconditionTestParams")
    public void preconditionTestDataValidation(AdeRawEnrichedTestData data) {
        List<ActiveDirectoryEnrichStoredData> actualEvents = repository.findByTimeAndUser(data.getStartInstant(), data.getEndInstant(), data.getTestUser());
        Assert.assertTrue(actualEvents.size() >= data.getExpectedCount(), String
                .format("Unexpected events count on enriched collection: enrich_%s for user: %s\n", "file", data.getTestUser()));
    }

    @Test(dataProvider = "dp")
    public void scoredDataVerifications(AdeScoredTestData data) {
        Assert.assertEquals(adeTestManager.getNumberOfScoredEnrichedDocuments(data.getTestUser(),
                data.getLowestScore(), data.getHighestScore(), data.getCollection()), data.getExpectedCount(),
                String.format(data.getTestFailedMessage(), data.getCollection(), data.getTestUser(), data.getLowestScore(), data.getHighestScore()));
    }
}
