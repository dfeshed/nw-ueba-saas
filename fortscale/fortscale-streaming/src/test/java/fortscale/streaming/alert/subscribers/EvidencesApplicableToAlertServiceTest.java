package fortscale.streaming.alert.subscribers;

import fortscale.domain.core.Evidence;
import fortscale.domain.core.EvidenceType;
import fortscale.domain.core.VpnGeoHoppingSupportingInformation;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import fortscale.streaming.alert.subscribers.evidence.applicable.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    private FilterUnconfiguredEvidences filterUnconfiguredEvidences;

    @Before
    public void setUp() throws Exception {

        //Because in other tests we override this method, we need to be sure that in this test I call the real method
        Mockito.doCallRealMethod().when(limitGeoHoppingPreAlertCreation).canCreateAlert(Mockito.any(), Mockito.anyLong(), Mockito.anyLong());


    }

    @Test
    public void filterUnconfiguredEvidencesTest(){;
        AlertFilterApplicableEvidencesServiceImpl service = new AlertFilterApplicableEvidencesServiceImpl();
        service.setAlertCreatorCandidatesFilter(Arrays.asList(filterUnconfiguredEvidences));

        EnrichedFortscaleEvent configuredEvidence = new EnrichedFortscaleEventBuilder().
                                                    setEvidenceType(EvidenceType.Notification).
                                                    setAnomalyTypeFieldName("normalized_username_daily").buildObject();

        EnrichedFortscaleEvent notConfiguredEvidence = new EnrichedFortscaleEventBuilder().
                setEvidenceType(EvidenceType.Notification).
                setAnomalyTypeFieldName("NOT-EXISTS").buildObject();


        List<EnrichedFortscaleEvent> enrichedFortscaleEvent = Arrays.asList(configuredEvidence,notConfiguredEvidence);
        List<EnrichedFortscaleEvent> actualApplicable =  service.createIndicatorListApplicableForDecider(
                enrichedFortscaleEvent, 0L,0L);

        Assert.assertEquals(1, actualApplicable.size());
        Assert.assertEquals("normalized_username_daily", actualApplicable.get(0).getAnomalyTypeFieldName());

    }

    @Test
    public void limitGeoHoppingPreAlertCreationTest(){;
        AlertFilterApplicableEvidencesServiceImpl service = new AlertFilterApplicableEvidencesServiceImpl();
        service.setAlertCreatorCandidatesFilter(Arrays.asList(limitGeoHoppingPreAlertCreation));


        //"pairInstancesPerUser":0,"pairInstancesGlobalUser":1,"maximumGlobalSingleCity":5}
        //The evidence is not filtering
        VpnGeoHoppingSupportingInformation vpnGeoHoppingSupportingInformation1 = new VpnGeoHoppingSupportingInformation();
        vpnGeoHoppingSupportingInformation1.setPairInstancesPerUser(0);
        vpnGeoHoppingSupportingInformation1.setPairInstancesGlobalUser(1);
        vpnGeoHoppingSupportingInformation1.setMaximumGlobalSingleCity(5);
        EnrichedFortscaleEvent vpnGeoHoppingEvidence = new EnrichedFortscaleEventBuilder().
                setEvidenceType(EvidenceType.Notification).
                setAnomalyTypeFieldName("vpn_geo_hopping").
                setSupportingInformation(vpnGeoHoppingSupportingInformation1)
                .buildObject();

        //"pairInstancesPerUser":1,"pairInstancesGlobalUser":100,"maximumGlobalSingleCity":5
        //Evidence should be filtered
        VpnGeoHoppingSupportingInformation vpnGeoHoppingSupportingInformation2 = new VpnGeoHoppingSupportingInformation();
        vpnGeoHoppingSupportingInformation2.setPairInstancesPerUser(1);
        vpnGeoHoppingSupportingInformation2.setPairInstancesGlobalUser(100);
        vpnGeoHoppingSupportingInformation2.setMaximumGlobalSingleCity(5);
        EnrichedFortscaleEvent vpnGeoHoppingEvidenceToFilter = new EnrichedFortscaleEventBuilder().
                setEvidenceType(EvidenceType.Notification).
                setAnomalyTypeFieldName("vpn_geo_hopping").
                setSupportingInformation(vpnGeoHoppingSupportingInformation2)
                .buildObject();


        List<EnrichedFortscaleEvent> enrichedFortscaleEvent = Arrays.asList(vpnGeoHoppingEvidence, vpnGeoHoppingEvidenceToFilter);
        List<EnrichedFortscaleEvent> actualApplicable =  service.createIndicatorListApplicableForDecider(
                enrichedFortscaleEvent, 0L,0L);
        Assert.assertEquals(1, actualApplicable.size());
        Assert.assertEquals("vpn_geo_hopping", actualApplicable.get(0).getAnomalyTypeFieldName());

    }

    @Test
    public void multiFiltersTest(){;
        AlertFilterApplicableEvidencesServiceImpl service = new AlertFilterApplicableEvidencesServiceImpl();
        service.setAlertCreatorCandidatesFilter(Arrays.asList(limitGeoHoppingPreAlertCreation, filterUnconfiguredEvidences));


        //"pairInstancesPerUser":0,"pairInstancesGlobalUser":1,"maximumGlobalSingleCity":5}
        //The evidence is not filtering
        VpnGeoHoppingSupportingInformation vpnGeoHoppingSupportingInformation1 = new VpnGeoHoppingSupportingInformation();
        vpnGeoHoppingSupportingInformation1.setPairInstancesPerUser(0);
        vpnGeoHoppingSupportingInformation1.setPairInstancesGlobalUser(1);
        vpnGeoHoppingSupportingInformation1.setMaximumGlobalSingleCity(5);
        EnrichedFortscaleEvent vpnGeoHoppingEvidence = new EnrichedFortscaleEventBuilder().
                setEvidenceType(EvidenceType.Notification).
                setAnomalyTypeFieldName("vpn_geo_hopping").
                setSupportingInformation(vpnGeoHoppingSupportingInformation1)
                .buildObject();

        //"pairInstancesPerUser":1,"pairInstancesGlobalUser":100,"maximumGlobalSingleCity":5
        //Evidence should be filtered
        VpnGeoHoppingSupportingInformation vpnGeoHoppingSupportingInformation2 = new VpnGeoHoppingSupportingInformation();
        vpnGeoHoppingSupportingInformation2.setPairInstancesPerUser(1);
        vpnGeoHoppingSupportingInformation2.setPairInstancesGlobalUser(100);
        vpnGeoHoppingSupportingInformation2.setMaximumGlobalSingleCity(5);
        EnrichedFortscaleEvent vpnGeoHoppingEvidenceToFilter = new EnrichedFortscaleEventBuilder().
                setEvidenceType(EvidenceType.Notification).
                setAnomalyTypeFieldName("vpn_geo_hopping").
                setSupportingInformation(vpnGeoHoppingSupportingInformation2)
                .buildObject();

        EnrichedFortscaleEvent configuredEvidence = new EnrichedFortscaleEventBuilder().
                setEvidenceType(EvidenceType.Notification).
                setAnomalyTypeFieldName("normalized_username_daily").buildObject();

        EnrichedFortscaleEvent notConfiguredEvidence = new EnrichedFortscaleEventBuilder().
                setEvidenceType(EvidenceType.Notification).
                setAnomalyTypeFieldName("NOT-EXISTS").buildObject();


        List<EnrichedFortscaleEvent> enrichedFortscaleEvent = Arrays.asList(vpnGeoHoppingEvidence, vpnGeoHoppingEvidenceToFilter,
                                                            configuredEvidence, notConfiguredEvidence);
        List<EnrichedFortscaleEvent> actualApplicable =  service.createIndicatorListApplicableForDecider(
                enrichedFortscaleEvent, 0L,0L);
        Assert.assertEquals(2, actualApplicable.size());
        Assert.assertEquals("vpn_geo_hopping", actualApplicable.get(0).getAnomalyTypeFieldName());
        Assert.assertEquals("normalized_username_daily", actualApplicable.get(1).getAnomalyTypeFieldName());

    }









}
