package com.rsa.netwitness.presidio.automation.test.ade;

import com.rsa.netwitness.presidio.automation.common.helpers.DateTimeHelperUtils;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.test_managers.ADETestManager;
import com.rsa.netwitness.presidio.automation.utils.ade.config.ADETestManagerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import presidio.data.generators.common.GeneratorException;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, ADETestManagerConfig.class})
public class ProcessSmarts_ForCoreTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private ADETestManager adeTestManager;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Parameters({"historical_days_back", "anomaly_day_back"})
    @BeforeClass
    public void prepare(@Optional("10") int historicalDaysBack, @Optional("1") int anomalyDay) throws GeneratorException {
        boolean testFile = true;
        boolean testAuthentication = true;
        boolean testActiveDirectory = true;
        boolean testProcess = true;
        boolean testRegistry = true;
        boolean testTls = true;

        /** Static Ps scenario on day back 5**/
        //Ps for normal 35-5
        if (testFile)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), "hourly", "file");
        if (testActiveDirectory)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), "hourly", "active_directory");
        if (testAuthentication)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), "hourly", "authentication");
        if (testProcess)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), "hourly", "process");
        if (testRegistry)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), "hourly", "registry");
        if (testTls)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), "hourly", "tls");

        //SMART
        adeTestManager.processSmart(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3));
        adeTestManager.processSmart(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), "sslSubject_hourly");
        adeTestManager.processSmart(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), "ja3_hourly");
        adeTestManager.processAccumulateSmart(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3));
        adeTestManager.processAccumulateSmart(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), "sslSubject_hourly");
        adeTestManager.processAccumulateSmart(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), "ja3_hourly");
        adeTestManager.processModeling("smart-record-models", "test-run", DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3));

        // new Ps again, 5-4 db
        if (testFile)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 2), "hourly", "file");
        if (testActiveDirectory)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 2), "hourly", "active_directory");
        if (testAuthentication)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 2), "hourly", "authentication");
        if (testProcess)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 2), "hourly", "process");
        if (testRegistry)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 2), "hourly", "registry");
        if (testTls)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 2), "hourly", "tls");

        // New Smarts
        adeTestManager.processSmart(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 2));
        adeTestManager.processSmart(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 2), "sslSubject_hourly");
        adeTestManager.processSmart(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 2), "ja3_hourly");
        /** Static Ps and Fs scenario on anomaly day **/

        //Ps
        if (testFile)
            adeTestManager.processModelFeatureBuckets(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), "hourly", "file");
        if (testActiveDirectory)
            adeTestManager.processModelFeatureBuckets(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), "hourly", "active_directory");
        if (testAuthentication)
            adeTestManager.processModelFeatureBuckets(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), "hourly", "authentication");
        if (testProcess)
            adeTestManager.processModelFeatureBuckets(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), "hourly", "process");
        if (testRegistry)
            adeTestManager.processModelFeatureBuckets(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), "hourly", "registry");
        if (testTls)
            adeTestManager.processModelFeatureBuckets(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), "hourly", "tls");

        adeTestManager.processModeling("enriched-record-models", "test-run", DateTimeHelperUtils.truncateAndMinusDays(anomalyDay));

        if (testFile)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay - 1), "hourly", "file");
        if (testActiveDirectory)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay - 1), "hourly", "active_directory");
        if (testAuthentication)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay - 1), "hourly", "authentication");
        if (testProcess)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay - 1), "hourly", "process");
        if (testRegistry)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay - 1), "hourly", "registry");
        if (testTls)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay - 1), "hourly", "tls");

        //Fs
        if (testFile)
            adeTestManager.processAccumulateAggr(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), "file");
        if (testActiveDirectory)
            adeTestManager.processAccumulateAggr(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), "active_directory");
        if (testAuthentication)
            adeTestManager.processAccumulateAggr(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), "authentication");
        if (testProcess)
            adeTestManager.processAccumulateAggr(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), "process");
        if (testRegistry)
            adeTestManager.processAccumulateAggr(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), "registry");
        if (testTls)
            adeTestManager.processAccumulateAggr(DateTimeHelperUtils.truncateAndMinusDays(historicalDaysBack), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), "tls");

        adeTestManager.processModeling("feature-aggregation-record-models", "test-run", DateTimeHelperUtils.truncateAndMinusDays(anomalyDay));

        if (testFile)
            adeTestManager.processFeatureAggr(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay - 1), "hourly", "file");
        if (testActiveDirectory)
            adeTestManager.processFeatureAggr(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay - 1), "hourly", "active_directory");
        if (testAuthentication)
            adeTestManager.processFeatureAggr(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay - 1), "hourly", "authentication");
        if (testProcess)
            adeTestManager.processFeatureAggr(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay - 1), "hourly", "process");
        if (testRegistry)
            adeTestManager.processFeatureAggr(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay - 1), "hourly", "registry");
        if (testTls)
            adeTestManager.processFeatureAggr(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay - 1), "hourly", "tls");

        /** New SMART models **/
        adeTestManager.processAccumulateSmart(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay));
        adeTestManager.processAccumulateSmart(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), "sslSubject_hourly");
        adeTestManager.processAccumulateSmart(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay + 3), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), "ja3_hourly");
        adeTestManager.processModeling("smart-record-models", "test-run", DateTimeHelperUtils.truncateAndMinusDays(anomalyDay));
        adeTestManager.processSmart(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay - 1));
        adeTestManager.processSmart(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay - 1), "sslSubject_hourly");
        adeTestManager.processSmart(DateTimeHelperUtils.truncateAndMinusDays(anomalyDay), DateTimeHelperUtils.truncateAndMinusDays(anomalyDay - 1), "ja3_hourly");
    }

    @Test
    public void dummy() {
        Assert.assertTrue(true);
    }

    @Test
    public void EndPointIndicatorsCreationTest() {
        /** This test is to verify that Smarts processing result covers all expected features:
         *
         *  * smart_user_id_hourly collection contains high score documents
         *  * all indicators covered
         *
         * **/

        /*
            Parameters for test: threshold for alert creation
            ...?
         */
    }

}