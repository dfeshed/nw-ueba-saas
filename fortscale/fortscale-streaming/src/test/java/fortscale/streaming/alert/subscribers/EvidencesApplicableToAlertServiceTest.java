package fortscale.streaming.alert.subscribers;

import fortscale.domain.core.EvidenceType;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import fortscale.streaming.alert.subscribers.evidence.applicable.*;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by shays on 23/03/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:META-INF/spring/streaming-UnifiedAlertGenerator-test-context.xml")
public class EvidencesApplicableToAlertServiceTest {

    @Autowired
    private LimitGeoHoppingPreAlertCreation limitGeoHoppingPreAlertCreation;

    @Autowired
    FilterUnconfiguredEvidences filterUnconfiguredEvidences;

    @Test
    public void filterUnconfiguredEvidencesTest(){;
        EvidencesApplicableToAlertServiceImpl service = new EvidencesApplicableToAlertServiceImpl();
        service.setAlertCreatorCandidatesFilter(Arrays.asList(filterUnconfiguredEvidences));

        EnrichedFortscaleEvent configuredEvidence = new EnrichedFortscaleEventBuilder().
                                                    setEvidenceType(EvidenceType.Notification).
                                                    setAnomalyTypeFieldName("smart").buildObject();

        EnrichedFortscaleEvent notConfiguredEvidence = new EnrichedFortscaleEventBuilder().
                setEvidenceType(EvidenceType.Notification).
                setAnomalyTypeFieldName("NOT-EXISTS").buildObject();


        List<EnrichedFortscaleEvent> enrichedFortscaleEvent = Arrays.asList(configuredEvidence,notConfiguredEvidence);
        List<EnrichedFortscaleEvent> actualApplicable =  service.createIndicatorListApplicableForDecider(
                enrichedFortscaleEvent, 0L,0L);

        Assert.assertEquals(1, actualApplicable.size());
        Assert.assertEquals("smart", actualApplicable.get(0).getAnomalyTypeFieldName());

    }

    @Test
    public void limitGeoHoppingPreAlertCreationTest(){;
        EvidencesApplicableToAlertServiceImpl service = new EvidencesApplicableToAlertServiceImpl();
        service.setAlertCreatorCandidatesFilter(Arrays.asList(limitGeoHoppingPreAlertCreation));


        //"pairInstancesPerUser":0,"pairInstancesGlobalUser":1,"maximumGlobalSingleCity":5}
        //The evidence is not filtering
        EnrichedFortscaleEvent vpnGeoHoppingEvidence = new EnrichedFortscaleEventBuilder().
                setEvidenceType(EvidenceType.Notification).
                setAnomalyTypeFieldName("vpn_geo_hopping").
                setSupportingInformation("{\"rawEvents\":[{\"_id\":null,\"username\":\"vpnusr7\",\"sourceIp\":\"1.0.32.0\",\"createdAtEpoch\":\"1455970407000\",\"localIp\":\"192.168.107.0\",\"normalizedUserName\":\"vpnusr7@somebigcompany.com\",\"hostname\":\"\",\"country\":\"China\",\"countryIsoCode\":\"CN\",\"region\":\"Guangdong\",\"city\":\"Guangzhou\",\"isp\":\"Chinatelecom.com.cn\",\"ispUsage\":\"isp\",\"geoHopping\":true},{\"_id\":{\"$oid\":\"56ef554fe4b0dd1c3d88dcdf\"},\"username\":\"vpnusr7\",\"sourceIp\":\"65.183.0.0\",\"createdAtEpoch\":\"1455969173000\",\"localIp\":\"192.168.107.0\",\"normalizedUserName\":\"vpnusr7@somebigcompany.com\",\"hostname\":\"\",\"country\":\"Jamaica\",\"countryIsoCode\":\"JM\",\"region\":\"Kingston\",\"city\":\"Kingston\",\"isp\":\"Columbuscommunications.com\",\"ispUsage\":\"isp\",\"geoHopping\":true}],\"pairInstancesPerUser\":0,\"pairInstancesGlobalUser\":1,\"maximumGlobalSingleCity\":5}")
                .buildObject();

        //"pairInstancesPerUser":1,"pairInstancesGlobalUser":100,"maximumGlobalSingleCity":5
        //Evidence should be filtered
        EnrichedFortscaleEvent vpnGeoHoppingEvidenceToFilter = new EnrichedFortscaleEventBuilder().
                setEvidenceType(EvidenceType.Notification).
                setAnomalyTypeFieldName("vpn_geo_hopping").
                setSupportingInformation("{\"rawEvents\":[{\"_id\":null,\"username\":\"vpnusr7\",\"sourceIp\":\"1.0.32.0\",\"createdAtEpoch\":\"1455970407000\",\"localIp\":\"192.168.107.0\",\"normalizedUserName\":\"vpnusr7@somebigcompany.com\",\"hostname\":\"\",\"country\":\"China\",\"countryIsoCode\":\"CN\",\"region\":\"Guangdong\",\"city\":\"Guangzhou\",\"isp\":\"Chinatelecom.com.cn\",\"ispUsage\":\"isp\",\"geoHopping\":true},{\"_id\":{\"$oid\":\"56ef554fe4b0dd1c3d88dcdf\"},\"username\":\"vpnusr7\",\"sourceIp\":\"65.183.0.0\",\"createdAtEpoch\":\"1455969173000\",\"localIp\":\"192.168.107.0\",\"normalizedUserName\":\"vpnusr7@somebigcompany.com\",\"hostname\":\"\",\"country\":\"Jamaica\",\"countryIsoCode\":\"JM\",\"region\":\"Kingston\",\"city\":\"Kingston\",\"isp\":\"Columbuscommunications.com\",\"ispUsage\":\"isp\",\"geoHopping\":true}],\"pairInstancesPerUser\":1,\"pairInstancesGlobalUser\":100,\"maximumGlobalSingleCity\":5}")
                .buildObject();


        List<EnrichedFortscaleEvent> enrichedFortscaleEvent = Arrays.asList(vpnGeoHoppingEvidence, vpnGeoHoppingEvidenceToFilter);
        List<EnrichedFortscaleEvent> actualApplicable =  service.createIndicatorListApplicableForDecider(
                enrichedFortscaleEvent, 0L,0L);
        Assert.assertEquals(1, actualApplicable.size());
        Assert.assertEquals("vpn_geo_hopping", actualApplicable.get(0).getAnomalyTypeFieldName());

    }

    @Test
    public void multiFiltersTest(){;
        EvidencesApplicableToAlertServiceImpl service = new EvidencesApplicableToAlertServiceImpl();
        service.setAlertCreatorCandidatesFilter(Arrays.asList(limitGeoHoppingPreAlertCreation, filterUnconfiguredEvidences));


        //"pairInstancesPerUser":0,"pairInstancesGlobalUser":1,"maximumGlobalSingleCity":5}
        //The evidence is not filtering
        EnrichedFortscaleEvent vpnGeoHoppingEvidence = new EnrichedFortscaleEventBuilder().
                setEvidenceType(EvidenceType.Notification).
                setAnomalyTypeFieldName("vpn_geo_hopping").
                setSupportingInformation("{\"rawEvents\":[{\"_id\":null,\"username\":\"vpnusr7\",\"sourceIp\":\"1.0.32.0\",\"createdAtEpoch\":\"1455970407000\",\"localIp\":\"192.168.107.0\",\"normalizedUserName\":\"vpnusr7@somebigcompany.com\",\"hostname\":\"\",\"country\":\"China\",\"countryIsoCode\":\"CN\",\"region\":\"Guangdong\",\"city\":\"Guangzhou\",\"isp\":\"Chinatelecom.com.cn\",\"ispUsage\":\"isp\",\"geoHopping\":true},{\"_id\":{\"$oid\":\"56ef554fe4b0dd1c3d88dcdf\"},\"username\":\"vpnusr7\",\"sourceIp\":\"65.183.0.0\",\"createdAtEpoch\":\"1455969173000\",\"localIp\":\"192.168.107.0\",\"normalizedUserName\":\"vpnusr7@somebigcompany.com\",\"hostname\":\"\",\"country\":\"Jamaica\",\"countryIsoCode\":\"JM\",\"region\":\"Kingston\",\"city\":\"Kingston\",\"isp\":\"Columbuscommunications.com\",\"ispUsage\":\"isp\",\"geoHopping\":true}],\"pairInstancesPerUser\":0,\"pairInstancesGlobalUser\":1,\"maximumGlobalSingleCity\":5}")
                .buildObject();

        //"pairInstancesPerUser":1,"pairInstancesGlobalUser":100,"maximumGlobalSingleCity":5
        //Evidence should be filtered
        EnrichedFortscaleEvent vpnGeoHoppingEvidenceToFilter = new EnrichedFortscaleEventBuilder().
                setEvidenceType(EvidenceType.Notification).
                setAnomalyTypeFieldName("vpn_geo_hopping").
                setSupportingInformation("{\"rawEvents\":[{\"_id\":null,\"username\":\"vpnusr7\",\"sourceIp\":\"1.0.32.0\",\"createdAtEpoch\":\"1455970407000\",\"localIp\":\"192.168.107.0\",\"normalizedUserName\":\"vpnusr7@somebigcompany.com\",\"hostname\":\"\",\"country\":\"China\",\"countryIsoCode\":\"CN\",\"region\":\"Guangdong\",\"city\":\"Guangzhou\",\"isp\":\"Chinatelecom.com.cn\",\"ispUsage\":\"isp\",\"geoHopping\":true},{\"_id\":{\"$oid\":\"56ef554fe4b0dd1c3d88dcdf\"},\"username\":\"vpnusr7\",\"sourceIp\":\"65.183.0.0\",\"createdAtEpoch\":\"1455969173000\",\"localIp\":\"192.168.107.0\",\"normalizedUserName\":\"vpnusr7@somebigcompany.com\",\"hostname\":\"\",\"country\":\"Jamaica\",\"countryIsoCode\":\"JM\",\"region\":\"Kingston\",\"city\":\"Kingston\",\"isp\":\"Columbuscommunications.com\",\"ispUsage\":\"isp\",\"geoHopping\":true}],\"pairInstancesPerUser\":1,\"pairInstancesGlobalUser\":100,\"maximumGlobalSingleCity\":5}")
                .buildObject();

        EnrichedFortscaleEvent configuredEvidence = new EnrichedFortscaleEventBuilder().
                setEvidenceType(EvidenceType.Notification).
                setAnomalyTypeFieldName("smart").buildObject();

        EnrichedFortscaleEvent notConfiguredEvidence = new EnrichedFortscaleEventBuilder().
                setEvidenceType(EvidenceType.Notification).
                setAnomalyTypeFieldName("NOT-EXISTS").buildObject();


        List<EnrichedFortscaleEvent> enrichedFortscaleEvent = Arrays.asList(vpnGeoHoppingEvidence, vpnGeoHoppingEvidenceToFilter,
                                                            configuredEvidence, notConfiguredEvidence);
        List<EnrichedFortscaleEvent> actualApplicable =  service.createIndicatorListApplicableForDecider(
                enrichedFortscaleEvent, 0L,0L);
        Assert.assertEquals(2, actualApplicable.size());
        Assert.assertEquals("vpn_geo_hopping", actualApplicable.get(0).getAnomalyTypeFieldName());
        Assert.assertEquals("smart", actualApplicable.get(1).getAnomalyTypeFieldName());

    }









}
