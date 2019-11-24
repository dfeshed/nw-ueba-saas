package com.rsa.netwitness.presidio.automation.test.ade;

import com.rsa.netwitness.presidio.automation.common.helpers.DateTimeHelperUtils;
import com.rsa.netwitness.presidio.automation.common.scenarios.activedirectory.AdHighNumberOfOperations;
import com.rsa.netwitness.presidio.automation.domain.activedirectory.ActiveDirectoryEnrichStoredData;
import com.rsa.netwitness.presidio.automation.domain.ade.AdeRawEnrichedTestData;
import com.rsa.netwitness.presidio.automation.domain.ade.AdeScoredTestData;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.repository.ActiveDirectoryEnrichStoredDataRepository;
import com.rsa.netwitness.presidio.automation.test_managers.ADETestManager;
import com.rsa.netwitness.presidio.automation.utils.ade.config.ADETestManagerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.common.GeneratorException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, ADETestManagerConfig.class})
public class ActiveDirectory_HighNumberOf_Test extends AbstractTestNGSpringContextTests {
    private static final String SCHEMA = "active_directory";
    private static final String SCORED_FEATURE_AGGR = "scored_feature_aggr__";
    private static final String unexpectedDocsCountMsg = "Number of collection \"%s\" documents do not match expected for user %s (scores between %d-%d)\n";
    @Autowired
    private ADETestManager adeTestManager;
    @Autowired
    private ActiveDirectoryEnrichStoredDataRepository repository;
    private Instant firstEventTime;
    private Instant lastEventTime;

    /***
     * Verification parameters provider for scored_feature_aggr (F's) scenarios
     */
    @DataProvider(name = "testParams")
    public Object[][] getTestParams() {
        return new Object[][]{
                // All scored feature aggregation records in the collections are from the last anomalous day, therefore all the scores are supposed to be ~100
                {new AdeScoredTestData("ade_testuser_ad_a", 0, 90, SCORED_FEATURE_AGGR + "numberOfSensitiveGroupMembershipOperationUserIdActiveDirectoryHourly", 0, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_testuser_ad_b", 0, 90, SCORED_FEATURE_AGGR + "numberOfGroupMembershipOperationUserIdActiveDirectoryHourly", 0, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_testuser_ad_c", 0, 90, SCORED_FEATURE_AGGR + "numberOfFailedOperationTypeUserIdActiveDirectoryHourly", 0, unexpectedDocsCountMsg)},

                {new AdeScoredTestData("ade_testuser_ad_a", 90, 100, SCORED_FEATURE_AGGR + "numberOfSensitiveGroupMembershipOperationUserIdActiveDirectoryHourly", 5, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_testuser_ad_b", 90, 100, SCORED_FEATURE_AGGR + "numberOfGroupMembershipOperationUserIdActiveDirectoryHourly", 6, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_testuser_ad_c", 90, 100, SCORED_FEATURE_AGGR + "numberOfFailedOperationTypeUserIdActiveDirectoryHourly", 6, unexpectedDocsCountMsg)}
        };
    }

    /***
     * Verification parameters provider for input (enriched) data amounts per user
     */
    @DataProvider(name = "preconditionTestParams")
    public Object[][] getPreconditionTestParams() {
        return new Object[][]{
                {new AdeRawEnrichedTestData("ade_testuser_ad_a", firstEventTime, lastEventTime, 479)},
                {new AdeRawEnrichedTestData("ade_testuser_ad_b", firstEventTime, lastEventTime, 539)},
                {new AdeRawEnrichedTestData("ade_testuser_ad_c", firstEventTime, lastEventTime, 540)}
        };
    }

    @BeforeClass
    public void prepare() throws GeneratorException {
        adeTestManager.clearAllCollections();

        // Generate events
        List<ActiveDirectoryEvent> events = new ArrayList<>();

        // OPERATION TYPE "High Number Of (Distinct)" scenarios
        events.addAll(AdHighNumberOfOperations.getHighNumSensitiveGroupMembershipEvents("ade_testuser_ad_a", 2));
        events.addAll(AdHighNumberOfOperations.getHighNumGroupMembershipEvents("ade_testuser_ad_b", 2));
        events.addAll(AdHighNumberOfOperations.getHighNumFailedActiveDirectoryEvents("ade_testuser_ad_c", 2));

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
        List<ActiveDirectoryEnrichStoredData> actualEvents = repository.findByTimeAndUser(data.getStartInstant(), data.getEndInstant(), data.getTestUser());
        System.out.println("Actual events count: " + actualEvents.size());
        Assert.assertTrue(actualEvents.size() >= data.getExpectedCount(), String.format("Unexpected events count on enriched collection: enrich_%s for user: %s\n", SCHEMA, data.getTestUser()));
    }

    @Test(dataProvider = "testParams")
    public void scoredDataVerifications(AdeScoredTestData data) {
        System.out.println(data.toString());
        Assert.assertEquals(adeTestManager.getNumberOfFDocuments(data.getTestUser(), data.getLowestScore(), data.getHighestScore(), data.getCollection()),
                data.getExpectedCount(), String.format(data.getTestFailedMessage(), data.getCollection(), data.getTestUser(), data.getLowestScore(), data.getHighestScore()));
    }
}
