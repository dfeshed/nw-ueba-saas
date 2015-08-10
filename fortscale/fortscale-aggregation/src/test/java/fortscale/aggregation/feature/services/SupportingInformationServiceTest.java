package fortscale.aggregation.feature.services;


import fortscale.aggregation.feature.services.historicaldata.SupportingInformationPopulatorFactory;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for Supporting Information service
 *
 * @author gils
 * Date: 29/07/2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/supporting-information-service-context-test.xml" })
public class SupportingInformationServiceTest {


    @InjectMocks
    @Autowired
    SupportingInformationService supportingInformationService;

    @Mock
    private SupportingInformationPopulatorFactory supportingInformationPopulatorFactory;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNoFeatureBucketsFound() {
//        String contextType = "normalized_username";
//        String contextValue = "dan@gmail.com";
//        String dataEntity = "kerberos_logins";
//        String featureName = "dst_machine_histogram";
//        String anomalyType = "String";
//        String anomalyValue = "SRV_123";
//        String aggregationFunc = "Count";
//        Long evidenceTime = 1438084610l; // Tue, 28 Jul 2015 11:56:48 GMT
//        int timePeriodInDays = 90;
//
//        //when(featureBucketsStore.getFeatureBucketsByContextAndTimeRange(any(FeatureBucketConf.class), anyString(), anyString(), anyString(), anyLong(), anyLong())).thenReturn(new ArrayList<FeatureBucket>());
//
//        SupportingInformationData evidenceSupportingInformationData = supportingInformationService.getEvidenceSupportingInformationData(contextType, contextValue, dataEntity, featureName, anomalyType, anomalyValue, TimestampUtils.convertToMilliSeconds(evidenceTime), timePeriodInDays, aggregationFunc);
//
//        Assert.assertTrue(evidenceSupportingInformationData.getHistogram().isEmpty());
    }

    public void testUnknownFeature() {
//        String contextType = "normalized_username";
//        String contextValue = "dan@gmail.com";
//        String dataEntity = "kerberos_logins";
//        String featureName = "xxx";
//        String anomalyType = "String";
//        String anomalyValue = "SRV_123";
//        String aggregationFunc = "Count";
//        Long evidenceTime = 1438084610l; // Tue, 28 Jul 2015 11:56:48 GMT
//        int timePeriodInDays = 90;
//
//        SupportingInformationData evidenceSupportingInformationData = supportingInformationService.getEvidenceSupportingInformationData(contextType, contextValue, dataEntity, featureName, anomalyType, anomalyValue, TimestampUtils.convertToMilliSeconds(evidenceTime), timePeriodInDays, aggregationFunc);
//
//        Assert.assertTrue(evidenceSupportingInformationData.getHistogram().isEmpty());
    }

    //@Test(expected = SupportingInformationException.class)
    public void testUnknownContextType() {
//        String contextType = "xxx";
//        String contextValue = "dan@gmail.com";
//        String dataEntity = "kerberos_logins";
//        String featureName = "dst_machine_histogram";
//        String anomalyType = "String";
//        String anomalyValue = "SRV_123";
//        String aggregationFunc = "Count";
//        Long evidenceTime = 1438084610l; // Tue, 28 Jul 2015 11:56:48 GMT
//        int timePeriodInDays = 90;
//
//        SupportingInformationData evidenceSupportingInformationData = supportingInformationService.getEvidenceSupportingInformationData(contextType, contextValue, dataEntity, featureName, anomalyType, anomalyValue, TimestampUtils.convertToMilliSeconds(evidenceTime), timePeriodInDays, aggregationFunc);
    }

    //@Test(expected = SupportingInformationException.class)
    public void testUnknownDatasource() {
//        String contextType = "normalized_username";
//        String contextValue = "dan@gmail.com";
//        String dataEntity = "xxx";
//        String featureName = "dst_machine_histogram";
//        String anomalyType = "String";
//        String anomalyValue = "SRV_123";
//        String aggregationFunc = "Count";
//        Long evidenceTime = 1438084610l; // Tue, 28 Jul 2015 11:56:48 GMT
//        int timePeriodInDays = 90;
//
//        SupportingInformationData evidenceSupportingInformationData = supportingInformationService.getEvidenceSupportingInformationData(contextType, contextValue, dataEntity, featureName, anomalyType, anomalyValue, TimestampUtils.convertToMilliSeconds(evidenceTime), timePeriodInDays, aggregationFunc);
    }

    //@Test(expected = SupportingInformationException.class)
    public void testNoRelevantSupportingInformationForEntityTypeAndDatasource() {
//        String contextType = "normalized_username";
//        String contextValue = "dan@gmail.com";
//        String dataEntity = "ssh";
//        String featureName = "dst_machine_histogram";
//        String anomalyType = "String";
//        String anomalyValue = "SRV_123";
//        String aggregationFunc = "Count";
//        Long evidenceTime = 1438084610l; // Tue, 28 Jul 2015 11:56:48 GMT
//        int timePeriodInDays = 90;
//
//        SupportingInformationData evidenceSupportingInformationData = supportingInformationService.getEvidenceSupportingInformationData(contextType, contextValue, dataEntity, featureName, anomalyType, anomalyValue, TimestampUtils.convertToMilliSeconds(evidenceTime), timePeriodInDays, aggregationFunc);
    }

}
