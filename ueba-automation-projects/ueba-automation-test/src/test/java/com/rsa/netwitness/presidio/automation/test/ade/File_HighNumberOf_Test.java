package com.rsa.netwitness.presidio.automation.test.ade;


import com.rsa.netwitness.presidio.automation.common.helpers.DateTimeHelperUtils;
import com.rsa.netwitness.presidio.automation.common.scenarios.file.FileHighNumberOfOperations;
import com.rsa.netwitness.presidio.automation.domain.ade.AdeRawEnrichedTestData;
import com.rsa.netwitness.presidio.automation.domain.ade.AdeScoredTestData;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.file.FileEnrichStoredData;
import com.rsa.netwitness.presidio.automation.domain.repository.FileEnrichStoredDataRepository;
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
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.GeneratorException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, ADETestManagerConfig.class})
public class File_HighNumberOf_Test extends AbstractTestNGSpringContextTests {
    private static final String SCHEMA = "file";
    private static final String SCORED_FEATURE_AGGR = "scored_feature_aggr__";
    private static final String unexpectedDocsCountMsg = "Number of collection \"%s\" documents do not match expected for user %s (scores between %d-%d)\n";
    @Autowired
    private ADETestManager adeTestManager;
    @Autowired
    private FileEnrichStoredDataRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;
    private Instant firstEventTime;
    private Instant lastEventTime;

    /***
     * Verification parameters provider for scored_feature_aggr (F's) scenarios
     */
    @DataProvider(name = "testParams")
    public Object[][] getTestParams() {
        return new Object[][]{
                {new AdeScoredTestData("ade_testuser_a", 90, 100, SCORED_FEATURE_AGGR + "numberOfSuccessfulFilePermissionChangesUserIdFileHourly", 3, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_testuser_b", 90, 100, SCORED_FEATURE_AGGR + "numberOfFailedFileActionsUserIdFileHourly", 5, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_testuser_c", 90, 100, SCORED_FEATURE_AGGR + "numberOfDistinctFileOpenedUserIdFileHourly", 3, unexpectedDocsCountMsg)},
        };
    }

    /***
     * Verification parameters provider for input (enriched) data amounts per user
     */
    @DataProvider(name = "preconditionTestParams")
    public Object[][] getPreconditionTestParams() {
        return new Object[][]{
                {new AdeRawEnrichedTestData("ade_testuser_a", firstEventTime, lastEventTime, 1164)},
                {new AdeRawEnrichedTestData("ade_testuser_b", firstEventTime, lastEventTime, 548)},
                {new AdeRawEnrichedTestData("ade_testuser_c", firstEventTime, lastEventTime, 344)},
        };
    }

    @BeforeClass
    public void prepare() throws GeneratorException {
        adeTestManager.clearAllCollections();

        // Generate events
        List<FileEvent> events = new ArrayList<>();

        // OPERATION TYPE "High Number Of (Distinct)" scenarios
        events.addAll(FileHighNumberOfOperations.getHighNumSuccessfulPermissionChange("ade_testuser_a", 2));
        events.addAll(FileHighNumberOfOperations.getHighNumFailedFileOperations("ade_testuser_b", 2));
        events.addAll(FileHighNumberOfOperations.getHighNumFileOpenOperations("ade_testuser_c", 2));

        // find time interval for "process" ADE commands
        firstEventTime = DateTimeHelperUtils.getFirstEventTime(events);
        lastEventTime = DateTimeHelperUtils.getLastEventTime(events);

        // Store all events
        adeTestManager.insert(events);

        // Process all events
        adeTestManager.processEnriched2F(firstEventTime, SCHEMA);
    }

    @Test(dataProvider = "preconditionTestParams")
    public void preconditionTestDataValidation(AdeRawEnrichedTestData data) {
        List<FileEnrichStoredData> actualEvents = repository.findByTimeAndUser(data.getStartInstant(), data.getEndInstant(), data.getTestUser());
        System.out.println("Actual events count: " + actualEvents.size());
        Assert.assertTrue(actualEvents.size() >= data.getExpectedCount(), String
                .format("Unexpected events count on enriched collection: enrich_%s for user: %s\n", SCHEMA, data.getTestUser()));
    }

    @Test(dataProvider = "testParams")
    public void scoredDataVerifications(AdeScoredTestData data) {
        Assert.assertEquals(adeTestManager.getNumberOfFDocuments(data.getTestUser(),
                data.getLowestScore(), data.getHighestScore(), data.getCollection()), data.getExpectedCount(),
                String.format(data.getTestFailedMessage(), data.getCollection(), data.getTestUser(), data.getLowestScore(), data.getHighestScore()));
    }
}
