package fortscale.aggregation.feature.services;


import fortscale.aggregation.feature.bucket.*;
import fortscale.domain.core.SupportingInformationData;
import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.utils.TimestampUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    private FeatureBucketsMongoStore featureBucketsStore;

    @Mock
    private BucketConfigurationService bucketConfigurationService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(featureBucketsStore.getFeatureBuckets(any(FeatureBucketConf.class), anyString(), anyString(), anyString(), anyLong(), anyLong())).thenReturn(new ArrayList<FeatureBucket>());
    }

    @Test
    public void testSupportingInformation() {
//        String entityType = "normalized_username";
//        String entityName = "dan@gmail.com";
//        String dataSource = "kerberos";
//        String feature = "zzz";
//        Long evidenceTime = 1438084610l; // Tue, 28 Jul 2015 11:56:48 GMT
//
//        SupportingInformationData evidenceSupportingInformationData = supportingInformationService.getEvidenceSupportingInformationData(entityType, entityName, dataSource, feature, TimestampUtils.convertToMilliSeconds(evidenceTime));
//
//        Assert.assertTrue(evidenceSupportingInformationData.getHistogram().isEmpty());
    }


    @Test(expected = SupportingInformationException.class)
    public void testUnknownFeature() {
        String entityType = "normalized_username";
        String entityName = "dan@gmail.com";
        String dataSource = "kerberos";
        String feature = "zzz";
        Long evidenceTime = 1438084610l; // Tue, 28 Jul 2015 11:56:48 GMT

        supportingInformationService.getEvidenceSupportingInformationData(entityType, entityName, dataSource, feature, TimestampUtils.convertToMilliSeconds(evidenceTime));
    }

    @Test(expected = SupportingInformationException.class)
    public void testUnknownEntityType() {
        String entityType = "xxx";
        String entityName = "dan@gmail.com";
        String dataSource = "kerberos";
        String feature = "dst_machine_histogram";
        Long evidenceTime = 1438084610l; // Tue, 28 Jul 2015 11:56:48 GMT

        supportingInformationService.getEvidenceSupportingInformationData(entityType, entityName, dataSource, feature, TimestampUtils.convertToMilliSeconds(evidenceTime));
    }

    @Test(expected = SupportingInformationException.class)
    public void testUnknownDatasource() {
        String entityType = "normalized_username";
        String entityName = "dan@gmail.com";
        String dataSource = "yyy";
        String feature = "dst_machine_histogram";
        Long evidenceTime = 1438084610l; // Tue, 28 Jul 2015 11:56:48 GMT

        supportingInformationService.getEvidenceSupportingInformationData(entityType, entityName, dataSource, feature, TimestampUtils.convertToMilliSeconds(evidenceTime));
    }

    @Test(expected = SupportingInformationException.class)
    public void testNoRelevantSupportingInformationForEntityTypeAndDatasource() {
        String entityType = "normalized_username";
        String entityName = "dan@gmail.com";
        String dataSource = "ssh";
        String feature = "dst_machine_histogram";
        Long evidenceTime = 1438084610l; // Tue, 28 Jul 2015 11:56:48 GMT

        supportingInformationService.getEvidenceSupportingInformationData(entityType, entityName, dataSource, feature, TimestampUtils.convertToMilliSeconds(evidenceTime));
    }

}
