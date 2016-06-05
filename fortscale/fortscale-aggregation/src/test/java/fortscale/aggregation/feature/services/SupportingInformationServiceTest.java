package fortscale.aggregation.feature.services;


import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsStore;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationData;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationService;
import fortscale.common.dataqueries.querydto.DataQueryDTOImpl;
import fortscale.common.dataqueries.querydto.DataQueryHelper;
import fortscale.common.dataqueries.querygenerators.DataQueryRunner;
import fortscale.common.dataqueries.querygenerators.DataQueryRunnerFactory;
import fortscale.common.dataqueries.querygenerators.mysqlgenerator.MySqlQueryRunner;
import fortscale.common.datastructures.GenericHistogram;
import fortscale.common.feature.Feature;
import fortscale.domain.core.*;
import fortscale.utils.time.TimestampUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * Test class for Supporting Information service
 *
 * @author gils
 * Date: 29/07/2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = { "classpath*:META-INF/spring/supporting-information-service-context-test.xml" })
public class SupportingInformationServiceTest {

    @Autowired
    @ReplaceWithMock
    DataQueryHelper dataQueryHelper;

    @Autowired
    @ReplaceWithMock
    DataQueryRunnerFactory dataQueryRunnerFactory;

    @Autowired
    @ReplaceWithMock
    MySqlQueryRunner mySqlQueryRunner;

    @Autowired
    SupportingInformationService supportingInformationService;

    @Mock
    Evidence mockEvidence;

	@Mock
	DataQueryRunner dataQueryRunner;

    @Autowired
    @ReplaceWithMock
    FeatureBucketsStore featureBucketsStore;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(dataQueryRunnerFactory.getDataQueryRunner(any(DataQueryDTOImpl.class))).thenReturn(mySqlQueryRunner);
    }

    @Test
    public void testSingleEventEvidence() {

        String featureName = "source_machine";
        String aggregationFunc = "Count";
        String contextType = "normalized_username";
        String contextValue = "mosheb@somebigcompany.com";
        String evidenceAnomalyValue = "SERVER_5";



		try {
			when(dataQueryRunnerFactory.getDataQueryRunner(any(DataQueryDTOImpl.class))).thenReturn(dataQueryRunner);
		}
		catch (Exception e)
		{
			return;
		}


        int timePeriodInDays = 90;

        when(mockEvidence.getEntityType()).thenReturn(EntityType.User);
        when(mockEvidence.getEvidenceType()).thenReturn(EvidenceType.AnomalySingleEvent);
        when(mockEvidence.getAnomalyTypeFieldName()).thenReturn(featureName);
        when(mockEvidence.getAnomalyValue()).thenReturn(evidenceAnomalyValue);

        ArrayList<String> dataSources = new ArrayList<>();
        dataSources.add("kerberos_logins");

        when(mockEvidence.getDataEntitiesIds()).thenReturn(dataSources);

        long evidenceEndTimeInMillis = 1440336353000l; // Sun Aug 23 16:25:53 IDT 2015

        when(mockEvidence.getEndDate()).thenReturn(evidenceEndTimeInMillis);

        ArrayList<FeatureBucket> featureBuckets = createHistoricalFeatureBuckets("normalized_username", "mosheb@somebigcompany.com", evidenceEndTimeInMillis, timePeriodInDays, evidenceAnomalyValue);

        when(featureBucketsStore.getFeatureBucketsByContextAndTimeRange(any(FeatureBucketConf.class), anyString(), anyString(), anyLong(), anyLong())).thenReturn(featureBuckets);

        SupportingInformationData evidenceSupportingInformationHistogramData = supportingInformationService.getEvidenceSupportingInformationData(mockEvidence, contextType, contextValue, featureName, timePeriodInDays, aggregationFunc);

        Assert.assertTrue(!evidenceSupportingInformationHistogramData.getData().isEmpty() && evidenceSupportingInformationHistogramData.getAnomalyValue() != null);
    }

    private ArrayList<FeatureBucket> createHistoricalFeatureBuckets(String contextType, String contextValue, long evidenceEndTime, int timePeriodInDays, String anomalyValue) {
        ArrayList<FeatureBucket> featureBuckets = new ArrayList<>();

        long dayInSeconds = TimeUnit.DAYS.toMillis(1);

        Random random = new Random();

        final int MAX_COUNTS = 10;
        final int MAX_SERVERS = 10;

        FeatureBucket anomalyFeatureBucket = createAnomalyFeatureBucket(evidenceEndTime, anomalyValue);

        featureBuckets.add(anomalyFeatureBucket);

        for (int i = 0; i < timePeriodInDays - 1; ++i) {
            FeatureBucket featureBucket = new FeatureBucket();
            featureBucket.setContextFieldNameToValueMap(Collections.singletonMap(contextType, contextValue));
            featureBucket.setStartTime(TimestampUtils.convertToSeconds(evidenceEndTime - (dayInSeconds * (i + 1))));
            featureBucket.setEndTime(TimestampUtils.convertToSeconds(evidenceEndTime - (dayInSeconds * (i))));

            Map<String, Feature> featureMap = new HashMap<>();
            GenericHistogram histogramValues = new GenericHistogram();
            histogramValues.add("SERVER_" + random.nextInt(MAX_SERVERS), (double) random.nextInt(MAX_COUNTS));
            Feature feature = new Feature("source_machine_histogram", histogramValues);
            featureMap.put("source_machine_histogram", feature);

            featureBucket.setAggregatedFeatures(featureMap);

            featureBuckets.add(featureBucket);
        }

        return featureBuckets;
    }

    private FeatureBucket createAnomalyFeatureBucket(long evidenceEndTime, String anomalyValue) {
        final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        String dateInFormat = dateFormat.format(new Date(evidenceEndTime));

        DateTime evidenceTime = DateTime.parse(dateInFormat, DateTimeFormat.forPattern(DATE_FORMAT));

        DateTime bucketStartTime = evidenceTime.withTimeAtStartOfDay();
        DateTime bucketEndTime = evidenceTime.plusDays( 1 ).withTimeAtStartOfDay();

        FeatureBucket anomalyFeatureBucket = new FeatureBucket();
        anomalyFeatureBucket.setContextFieldNameToValueMap(Collections.singletonMap("normalized_username", "mosheb@somebigcompany.com"));
        anomalyFeatureBucket.setStartTime(TimestampUtils.convertToSeconds(bucketStartTime.getMillis()));
        anomalyFeatureBucket.setEndTime(TimestampUtils.convertToSeconds(bucketEndTime.getMillis()));

        Map<String, Feature> anomalyFeatureMap = new HashMap<>();
        GenericHistogram anomalyHistogramValues = new GenericHistogram();
        anomalyHistogramValues.add(anomalyValue, 100d);

        Feature anomalyFeature = new Feature("source_machine_histogram", anomalyHistogramValues);
        anomalyFeatureMap.put("source_machine_histogram", anomalyFeature);
        anomalyFeatureBucket.setAggregatedFeatures(anomalyFeatureMap);

        return anomalyFeatureBucket;
    }

    @Test
    public void testVPNOverlappingSessionEvidence() {

        String featureName = "VPN_user_creds_share";
        String aggregationFunc = "timeIntervals";
        String contextType = "normalized_username";
        String contextValue = "mosheb@somebigcompany.com";

        ArrayList<String> dataSources = new ArrayList<>();
        dataSources.add("vpn");

        int numOfEvents = 5;

        VpnOverlappingSupportingInformation vpnOverlappingSupportingInformation = createVpnOverlappingSupportingInformationOfEvidence(numOfEvents);

        when(mockEvidence.getEntityType()).thenReturn(EntityType.User);
        when(mockEvidence.getEvidenceType()).thenReturn(EvidenceType.Notification);
        when(mockEvidence.getAnomalyTypeFieldName()).thenReturn(featureName);
        when(mockEvidence.getDataEntitiesIds()).thenReturn(dataSources);
        when(mockEvidence.getSupportingInformation()).thenReturn(vpnOverlappingSupportingInformation);

        SupportingInformationData evidenceSupportingInformationHistogramData = supportingInformationService.getEvidenceSupportingInformationData(mockEvidence, contextType, contextValue, featureName, null, aggregationFunc);

        Assert.assertTrue(evidenceSupportingInformationHistogramData.getData().size() == numOfEvents);


    }

    private VpnOverlappingSupportingInformation createVpnOverlappingSupportingInformationOfEvidence(int numOfEvents) {
        VpnOverlappingSupportingInformation vpnOverlappingSupportingInformation = new VpnOverlappingSupportingInformation();

        vpnOverlappingSupportingInformation.setRawEvents(new ArrayList());

        for  (int i = 0; i < numOfEvents; i++) {
            VpnSessionOverlap vpnSessionOverlapEvent = createVpnSessionOverlapEvent(Integer.toString(i));

            vpnOverlappingSupportingInformation.getRawEvents().add(vpnSessionOverlapEvent);
        }

        return vpnOverlappingSupportingInformation;
    }

    private VpnSessionOverlap createVpnSessionOverlapEvent(String ipIndex) {
        VpnSessionOverlap vpnSessionOverlap = new VpnSessionOverlap();

        vpnSessionOverlap.setDate_time_unix(1440483492);
        vpnSessionOverlap.setDuration(28800);
        vpnSessionOverlap.setSource_ip("41.60.0." + ipIndex);
        vpnSessionOverlap.setLocal_ip("10.10.0.12");
        vpnSessionOverlap.setReadbytes(2411724);
        vpnSessionOverlap.setTotalbytes(2411724);
        vpnSessionOverlap.setDatabucket(2411724);
        vpnSessionOverlap.setCountry("Israel");
        vpnSessionOverlap.setHostname("SRVX_1");

        return vpnSessionOverlap;
    }

    @Test
    public void testAggregatedEventEvidence() {

//        String featureName = "number_of_successful_ssh_daily";
//        String aggregationFunc = "distinctEventsByTime";
//        String contextType = "context.normalized_username";
//        String contextValue = "mosheb@somebigcompany.com";
//        String evidenceAnomalyValue = "SERVER_5";
//
//        int timePeriodInDays = 90;
//
//        when(mockEvidence.getEntityType()).thenReturn(EntityType.User);
//        when(mockEvidence.getEvidenceType()).thenReturn(EvidenceType.AnomalyAggregatedEvent);
//        when(mockEvidence.getAnomalyTypeFieldName()).thenReturn(featureName);
//        when(mockEvidence.getAnomalyValue()).thenReturn(evidenceAnomalyValue);
//
//        ArrayList<String> dataSources = new ArrayList<>();
//        dataSources.add("ssh");
//
//        when(mockEvidence.getDataEntitiesIds()).thenReturn(dataSources);
//
//        long evidenceEndTimeInMillis = 1440336353000l;
//
//        when(mockEvidence.getEndDate()).thenReturn(evidenceEndTimeInMillis);
//
//        ArrayList<FeatureBucket> featureBuckets = createHistoricalFeatureBuckets(evidenceEndTimeInMillis, timePeriodInDays, "sss");
//
//        when(featureBucketsStore.getFeatureBucketsByContextAndTimeRange(any(FeatureBucketConf.class), anyString(), anyString(), anyLong(), anyLong())).thenReturn(featureBuckets);
//
//        SupportingInformationHistogramData evidenceSupportingInformationData = supportingInformationService.getEvidenceSupportingInformationData(mockEvidence, contextType, contextValue, featureName, timePeriodInDays, aggregationFunc);
//
//        Assert.assertTrue(!evidenceSupportingInformationData.getData().isEmpty() && evidenceSupportingInformationData.getAnomalyValue() != null);
    }


}
