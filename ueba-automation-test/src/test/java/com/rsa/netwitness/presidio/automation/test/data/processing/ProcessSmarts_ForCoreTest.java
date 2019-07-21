package com.rsa.netwitness.presidio.automation.test.data.processing;

import com.rsa.netwitness.presidio.automation.common.helpers.DateTimeHelperUtils;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.utils.ade.ADETestManager;
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

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true",})
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
            adeTestManager.processScoreAggr(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay + 3), "hourly", "file");
        if (testActiveDirectory)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay + 3), "hourly", "active_directory");
        if (testAuthentication)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay + 3), "hourly", "authentication");
        if (testProcess)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay + 3), "hourly", "process");
        if (testRegistry)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay + 3), "hourly", "registry");
        if (testTls)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay + 3), "hourly", "tls");

        //SMART
        adeTestManager.processSmart(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay + 3));
        adeTestManager.processSmart(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay + 3), "sslSubject_hourly");
        adeTestManager.processSmart(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay + 3), "ja3_hourly");
        adeTestManager.processAccumulateSmart(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay + 3));
        adeTestManager.processAccumulateSmart(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay + 3), "sslSubject_hourly");
        adeTestManager.processAccumulateSmart(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay + 3), "ja3_hourly");
        adeTestManager.processModeling("smart-record-models", "test-run", DateTimeHelperUtils.getDate(anomalyDay + 3));

        // new Ps again, 5-4 db
        if (testFile)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.getDate(anomalyDay + 3), DateTimeHelperUtils.getDate(anomalyDay + 2), "hourly", "file");
        if (testActiveDirectory)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.getDate(anomalyDay + 3), DateTimeHelperUtils.getDate(anomalyDay + 2), "hourly", "active_directory");
        if (testAuthentication)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.getDate(anomalyDay + 3), DateTimeHelperUtils.getDate(anomalyDay + 2), "hourly", "authentication");
        if (testProcess)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.getDate(anomalyDay + 3), DateTimeHelperUtils.getDate(anomalyDay + 2), "hourly", "process");
        if (testRegistry)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.getDate(anomalyDay + 3), DateTimeHelperUtils.getDate(anomalyDay + 2), "hourly", "registry");
        if (testTls)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.getDate(anomalyDay + 3), DateTimeHelperUtils.getDate(anomalyDay + 2), "hourly", "tls");

        // New Smarts
        adeTestManager.processSmart(DateTimeHelperUtils.getDate(anomalyDay + 3),  DateTimeHelperUtils.getDate(anomalyDay + 2));
        adeTestManager.processSmart(DateTimeHelperUtils.getDate(anomalyDay + 3), DateTimeHelperUtils.getDate(anomalyDay + 2), "sslSubject_hourly");
        adeTestManager.processSmart(DateTimeHelperUtils.getDate(anomalyDay + 3), DateTimeHelperUtils.getDate(anomalyDay + 2), "ja3_hourly");
        /** Static Ps and Fs scenario on anomaly day **/

        //Ps
        if (testFile)
            adeTestManager.processModelFeatureBuckets(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay), "hourly", "file");
        if (testActiveDirectory)
            adeTestManager.processModelFeatureBuckets(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay), "hourly", "active_directory");
        if (testAuthentication)
            adeTestManager.processModelFeatureBuckets(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay), "hourly", "authentication");
        if (testProcess)
            adeTestManager.processModelFeatureBuckets(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay), "hourly", "process");
        if (testRegistry)
            adeTestManager.processModelFeatureBuckets(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay), "hourly", "registry");
        if (testTls)
            adeTestManager.processModelFeatureBuckets(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay), "hourly", "tls");

        adeTestManager.processModeling("enriched-record-models", "test-run", DateTimeHelperUtils.getDate(anomalyDay));

        if (testFile)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.getDate(anomalyDay), DateTimeHelperUtils.getDate(anomalyDay - 1), "hourly", "file");
        if (testActiveDirectory)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.getDate(anomalyDay), DateTimeHelperUtils.getDate(anomalyDay - 1), "hourly", "active_directory");
        if (testAuthentication)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.getDate(anomalyDay), DateTimeHelperUtils.getDate(anomalyDay - 1), "hourly", "authentication");
        if (testProcess)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.getDate(anomalyDay), DateTimeHelperUtils.getDate(anomalyDay - 1), "hourly", "process");
        if (testRegistry)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.getDate(anomalyDay), DateTimeHelperUtils.getDate(anomalyDay - 1), "hourly", "registry");
        if (testTls)
            adeTestManager.processScoreAggr(DateTimeHelperUtils.getDate(anomalyDay), DateTimeHelperUtils.getDate(anomalyDay - 1), "hourly", "tls");

        //Fs
        if (testFile)
            adeTestManager.processAccumulateAggr(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay), "file");
        if (testActiveDirectory)
            adeTestManager.processAccumulateAggr(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay), "active_directory");
        if (testAuthentication)
            adeTestManager.processAccumulateAggr(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay), "authentication");
        if (testProcess)
            adeTestManager.processAccumulateAggr(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay), "process");
        if (testRegistry)
            adeTestManager.processAccumulateAggr(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay), "registry");
        if (testTls)
            adeTestManager.processAccumulateAggr(DateTimeHelperUtils.getDate(historicalDaysBack), DateTimeHelperUtils.getDate(anomalyDay), "tls");

        adeTestManager.processModeling("feature-aggregation-record-models", "test-run", DateTimeHelperUtils.getDate(anomalyDay));

        if (testFile)
            adeTestManager.processFeatureAggr(DateTimeHelperUtils.getDate(anomalyDay), DateTimeHelperUtils.getDate(anomalyDay - 1), "hourly", "file");
        if (testActiveDirectory)
            adeTestManager.processFeatureAggr(DateTimeHelperUtils.getDate(anomalyDay), DateTimeHelperUtils.getDate(anomalyDay - 1), "hourly", "active_directory");
        if (testAuthentication)
            adeTestManager.processFeatureAggr(DateTimeHelperUtils.getDate(anomalyDay), DateTimeHelperUtils.getDate(anomalyDay - 1), "hourly", "authentication");
        if (testProcess)
            adeTestManager.processFeatureAggr(DateTimeHelperUtils.getDate(anomalyDay), DateTimeHelperUtils.getDate(anomalyDay - 1), "hourly", "process");
        if (testRegistry)
            adeTestManager.processFeatureAggr(DateTimeHelperUtils.getDate(anomalyDay), DateTimeHelperUtils.getDate(anomalyDay - 1), "hourly", "registry");
        if (testTls)
            adeTestManager.processFeatureAggr(DateTimeHelperUtils.getDate(anomalyDay), DateTimeHelperUtils.getDate(anomalyDay - 1), "hourly", "tls");

        /** New SMART models **/
        adeTestManager.processAccumulateSmart(DateTimeHelperUtils.getDate(anomalyDay + 3), DateTimeHelperUtils.getDate(anomalyDay));
        adeTestManager.processAccumulateSmart(DateTimeHelperUtils.getDate(anomalyDay + 3), DateTimeHelperUtils.getDate(anomalyDay), "sslSubject_hourly");
        adeTestManager.processAccumulateSmart(DateTimeHelperUtils.getDate(anomalyDay + 3), DateTimeHelperUtils.getDate(anomalyDay), "ja3_hourly");
        adeTestManager.processModeling("smart-record-models", "test-run", DateTimeHelperUtils.getDate(anomalyDay));
        adeTestManager.processSmart(DateTimeHelperUtils.getDate(anomalyDay),  DateTimeHelperUtils.getDate(anomalyDay - 1));
        adeTestManager.processSmart(DateTimeHelperUtils.getDate(anomalyDay), DateTimeHelperUtils.getDate(anomalyDay - 1), "sslSubject_hourly");
        adeTestManager.processSmart(DateTimeHelperUtils.getDate(anomalyDay), DateTimeHelperUtils.getDate(anomalyDay - 1), "ja3_hourly");
    }

    @Test
    public void dummy()
    {
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