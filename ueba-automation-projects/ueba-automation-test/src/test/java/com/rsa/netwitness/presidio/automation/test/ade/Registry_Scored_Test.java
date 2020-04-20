package com.rsa.netwitness.presidio.automation.test.ade;


import com.rsa.netwitness.presidio.automation.common.helpers.DateTimeHelperUtils;
import com.rsa.netwitness.presidio.automation.common.scenarios.registry.RegistryOperationAnomalies;
import com.rsa.netwitness.presidio.automation.domain.ade.AdeRawEnrichedTestData;
import com.rsa.netwitness.presidio.automation.domain.ade.AdeScoredTestData;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.registry.RegistryEnrichStoredData;
import com.rsa.netwitness.presidio.automation.domain.repository.RegistryEnrichStoredDataRepository;
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
import presidio.data.domain.event.registry.RegistryEvent;
import presidio.data.generators.common.GeneratorException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, ADETestManagerConfig.class})
public class Registry_Scored_Test extends AbstractTestNGSpringContextTests {
    private static final String SCHEMA = "registry";
    private static final String unexpectedDocsCountMsg = "Number of collection \"%s\" documents do not match expected for user %s (scores between %d-%d)\n";
    private static final String registryStartInstantCollection = "scored_enriched_registry_startInstant_userId_registry_score";
    private static final String registryActionCollection = "scored_enriched_registry_processFilePath_registryKeyGroup_registry_score";
    Instant firstEventTime;
    Instant lastEventTime;
    @Autowired
    private ADETestManager adeTestManager;
    @Autowired
    private RegistryEnrichStoredDataRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @DataProvider(name = "testParams")
    public Object[][] getData() {
        return new Object[][]{
                {new AdeScoredTestData("ade_testuser1", 90, 100, registryStartInstantCollection, 0, unexpectedDocsCountMsg)},

                {new AdeScoredTestData("ade_testuser1", 80, 100, registryActionCollection, 10, unexpectedDocsCountMsg)},
        };
    }

    @BeforeClass
    public void prepare() throws GeneratorException {
        adeTestManager.clearAllCollections();

        // Generate events
        List<RegistryEvent> events = new ArrayList<>();

        // StartInstant scenarios
        events.addAll(RegistryOperationAnomalies.getAbnormalProcessModifiedServiceKey("ade_testuser1", 2));

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
                {new AdeRawEnrichedTestData("ade_testuser1", firstEventTime, lastEventTime, 313)},
        };
    }

    @Test(dataProvider = "preconditionTestParams")
    public void preconditionTestDataValidation(AdeRawEnrichedTestData data) {
        List<RegistryEnrichStoredData> actualEvents = repository.findByTimeAndUser(data.getStartInstant(), data.getEndInstant(), data.getTestUser());
        System.out.println("Actual events count: " + actualEvents.size());
        Assert.assertTrue(actualEvents.size() >= data.getExpectedCount(), String
                .format("Unexpected events count on enriched collection: enrich_%s for user: %s\n", "registry", data.getTestUser()));
    }

    @Test(dataProvider = "testParams")
    public void scoredDataVerifications(AdeScoredTestData data) {
        Assert.assertEquals(adeTestManager.getNumberOfScoredEnrichedDocuments(data.getTestUser(),
                data.getLowestScore(), data.getHighestScore(), data.getCollection()), data.getExpectedCount(),
                String.format(data.getTestFailedMessage(), data.getCollection(), data.getTestUser(), data.getLowestScore(), data.getHighestScore()));
    }
}
