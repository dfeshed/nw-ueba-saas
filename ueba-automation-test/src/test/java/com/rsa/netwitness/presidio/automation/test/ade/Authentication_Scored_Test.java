package com.rsa.netwitness.presidio.automation.test.ade;

import com.rsa.netwitness.presidio.automation.common.helpers.DateTimeHelperUtils;
import com.rsa.netwitness.presidio.automation.domain.ade.AdeRawEnrichedTestData;
import com.rsa.netwitness.presidio.automation.domain.ade.AdeScoredTestData;
import com.rsa.netwitness.presidio.automation.domain.authentication.EnrichedAuthenticationStoredData;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.repository.AuthenticationEnrichStoredDataRepository;
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
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.common.GeneratorException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.rsa.netwitness.presidio.automation.common.scenarios.authentication.AuthenticationDateTimeAnomalies.getAbnormalLunchTimeActivity;
import static com.rsa.netwitness.presidio.automation.common.scenarios.authentication.AuthenticationDateTimeAnomalies.getAnomalyOnTwoNormalIntervalsActivity;
import static com.rsa.netwitness.presidio.automation.common.scenarios.authentication.AuthenticationMachineAnomalies.getAbnormalDstMachineActivity;
import static com.rsa.netwitness.presidio.automation.common.scenarios.authentication.AuthenticationMachineAnomalies.getAbnormalSrcMachineActivity;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, ADETestManagerConfig.class})
public class Authentication_Scored_Test extends AbstractTestNGSpringContextTests {
    public static final String SCORED_ENRICHED_AUTHENTICATION = "scored_enriched_authentication_";
    private static final String SCHEMA = "authentication";
    private static final String unexpectedDocsCountMsg = "Number of collection \"%s\" documents do not match expected for user %s (scores between %d-%d)\n";
    Instant firstEventTime;
    Instant lastEventTime;
    @Autowired
    private ADETestManager adeTestManager;
    @Autowired
    private AuthenticationEnrichStoredDataRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    /***
     * This is test verification parameters provider for scenario:
     *      presidio.integration.common.scenarios.authentication.FileDateTimeAnomalies
     *
     * Per each row in the Object[][], separate test will run.
     * AdeScoredTestData object will be passed to the test as input parameter.
     *
     * @return AdeScoredTestData objects
     */
    @DataProvider(name = "testParams")
    public Object[][] getTestParams() {
        return new Object[][]{
                {new AdeScoredTestData("ade_testuser21", 10, 100, SCORED_ENRICHED_AUTHENTICATION + "startInstant_userId_authentication_score", 20, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_testuser22", 1, 90, SCORED_ENRICHED_AUTHENTICATION + "startInstant_userId_authentication_score", 12, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_testuser23", 80, 100, SCORED_ENRICHED_AUTHENTICATION + "srcMachine_userId_authentication_score", 4, unexpectedDocsCountMsg)},
                {new AdeScoredTestData("ade_testuser24", 80, 100, SCORED_ENRICHED_AUTHENTICATION + "dstMachine_userId_authentication_score", 4, unexpectedDocsCountMsg)},
        };
    }

    /***
     * Verification parameters provider for input (enriched) data amounts per user
     */
    @DataProvider(name = "preconditionTestParams")
    public Object[][] getPreconditionTestParams() {
        return new Object[][]{
                {new AdeRawEnrichedTestData("ade_testuser21", firstEventTime, lastEventTime, 96)},
                {new AdeRawEnrichedTestData("ade_testuser22", firstEventTime, lastEventTime, 108)},
                {new AdeRawEnrichedTestData("ade_testuser23", firstEventTime, lastEventTime, 476)},
                {new AdeRawEnrichedTestData("ade_testuser24", firstEventTime, lastEventTime, 476)},
        };
    }

    @BeforeClass
    public void prepare() throws GeneratorException {
        adeTestManager.clearAllCollections();

        // Generate events - call scenarios from presidio.integration.common package
        List<AuthenticationEvent> events = new ArrayList<>();
        events.addAll(getAbnormalLunchTimeActivity("ade_testuser21", 2));
        events.addAll(getAnomalyOnTwoNormalIntervalsActivity("ade_testuser22", 2));
        events.addAll(getAbnormalSrcMachineActivity("ade_testuser23", 2));
        events.addAll(getAbnormalDstMachineActivity("ade_testuser24", 2));

        // find time interval for "process" ADE commands
        firstEventTime = DateTimeHelperUtils.getFirstEventTime(events);
        lastEventTime = DateTimeHelperUtils.getLastEventTime(events);

        // Store all events
        adeTestManager.insert(events);

        // Process all events
        adeTestManager.processEnriched2Scored(firstEventTime, SCHEMA);
    }

    @Test(dataProvider = "preconditionTestParams")
    public void preconditionTestDataValidation(AdeRawEnrichedTestData data) {
        List<EnrichedAuthenticationStoredData> actualEvents = repository.findByTimeAndUser(data.getStartInstant(), data.getEndInstant(), data.getTestUser());
        System.out.println("Actual events count: " + actualEvents.size());
        Assert.assertTrue(actualEvents.size() >= data.getExpectedCount(), "missing expected data on enriched collection\n");
    }

    @Test(dataProvider = "testParams")
    public void scoredDataVerifications(AdeScoredTestData data) {
        Assert.assertEquals(adeTestManager.getNumberOfScoredEnrichedDocuments(data.getTestUser(),
                data.getLowestScore(), data.getHighestScore(), data.getCollection()), data.getExpectedCount(),
                String.format(data.getTestFailedMessage(), data.getCollection(), data.getTestUser(), data.getLowestScore(), data.getHighestScore()));
    }
}
