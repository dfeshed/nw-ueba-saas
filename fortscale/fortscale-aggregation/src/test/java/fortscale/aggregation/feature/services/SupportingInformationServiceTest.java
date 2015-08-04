package fortscale.aggregation.feature.services;


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsMongoStore;
import fortscale.domain.core.SupportingInformationData;
import fortscale.utils.TimestampUtils;

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

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNoFeatureBucketsFound() {
        String entityType = "normalized_username";
        String entityName = "dan@gmail.com";
        String dataSource = "kerberos_logins";
        String feature = "dst_machine_histogram";
        Long evidenceTime = 1438084610l; // Tue, 28 Jul 2015 11:56:48 GMT

        when(featureBucketsStore.getFeatureBuckets(any(FeatureBucketConf.class), anyString(), anyString(), anyString(), anyLong(), anyLong())).thenReturn(new ArrayList<FeatureBucket>());

        SupportingInformationData evidenceSupportingInformationData = supportingInformationService.getEvidenceSupportingInformationData(entityType, entityName, dataSource, feature, TimestampUtils.convertToMilliSeconds(evidenceTime));

        Assert.assertTrue(evidenceSupportingInformationData.getHistogram().isEmpty());
    }

    public void testUnknownFeature() {
        String entityType = "normalized_username";
        String entityName = "dan@gmail.com";
        String dataSource = "kerberos_logins";
        String feature = "zzz";
        Long evidenceTime = 1438084610l; // Tue, 28 Jul 2015 11:56:48 GMT

        SupportingInformationData evidenceSupportingInformationData = supportingInformationService.getEvidenceSupportingInformationData(entityType, entityName, dataSource, feature, TimestampUtils.convertToMilliSeconds(evidenceTime));

        Assert.assertTrue(evidenceSupportingInformationData.getHistogram().isEmpty());
    }

    @Test(expected = SupportingInformationException.class)
    public void testUnknownEntityType() {
        String entityType = "xxx";
        String entityName = "dan@gmail.com";
        String dataSource = "kerberos_logins";
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
