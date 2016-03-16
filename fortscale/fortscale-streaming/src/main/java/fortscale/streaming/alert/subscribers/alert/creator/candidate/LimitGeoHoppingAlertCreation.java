package fortscale.streaming.alert.subscribers.alert.creator.candidate;

import fortscale.domain.core.EntitySupportingInformation;
import fortscale.domain.core.EvidenceType;
import fortscale.domain.core.VpnGeoHoppingSupportingInformation;
import fortscale.services.impl.SpringService;
import fortscale.streaming.task.EntitySupportingInformationPopulator;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by shays on 16/03/2016.
 */
public class LimitGeoHoppingAlertCreation implements AlertCreatorCandidate {



    public boolean canCreateAlert(String anomalyType, EvidenceType evidenceType,Map<String, String> evidence){

        String supportingInformationAsString = evidence.get("supportingInformation");

        VpnGeoHoppingSupportingInformation info = new VpnGeoHoppingSupportingInformation();
        info.setData(null, supportingInformationAsString, false);
        //To finish this code , I 

       return true;
    }
    public boolean filterMatch(String anomalyType, EvidenceType evidenceType){
        return EvidenceType.Notification.equals(evidenceType) && anomalyType.equals("VPN_GEO_HOPPING");
    }



}
