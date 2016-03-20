package fortscale.streaming.alert.subscribers.alert.creator.candidate;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.EvidenceType;
import fortscale.domain.core.VpnGeoHoppingSupportingInformation;
import fortscale.services.ApplicationConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by shays on 16/03/2016.
 */
public class LimitGeoHoppingAlertCreation implements AlertCreatorCandidate {

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;

    private final String MAX_IDENTICAL_GEO_HOPPING_PER_USER = "maxIdenticalGeoHoppingPerUser";
    private final String MAX_IDENTICAL_GEO_HOPPING_GLOBAL = "maxIdenticalGeoHoppingGlobal";
    private final String MAX_SINGLE_CITY = "maxSingleCity";



    public boolean canCreateAlert(String anomalyType, EvidenceType evidenceType,Map<String, String> evidence){

        String supportingInformationAsString = evidence.get("supportingInformation");

        VpnGeoHoppingSupportingInformation info = new VpnGeoHoppingSupportingInformation();
        info.setData(null, supportingInformationAsString, false);

        if (largeThenConfiguration(info.getPairInstancesPerUser(), MAX_IDENTICAL_GEO_HOPPING_PER_USER) ){
            return false;
        }

        if (largeThenConfiguration(info.getPairInstancesGlobalUser(), MAX_IDENTICAL_GEO_HOPPING_GLOBAL) ){
            return false;
        }

        if (largeThenConfiguration(info.getMaximumGlobalSingleCity(), MAX_SINGLE_CITY) ){
            return false;
        }

       return true;
    }
    public boolean filterMatch(String anomalyType, EvidenceType evidenceType){
        return EvidenceType.Notification.equals(evidenceType) && anomalyType.equals("VPN_GEO_HOPPING");
    }

    private boolean largeThenConfiguration(int value, String configurationKey){
        ApplicationConfiguration conf = applicationConfigurationService.getApplicationConfigurationByKey(configurationKey);
        if (conf == null){
            throw new RuntimeException(configurationKey + " configuration required");
        }
        int confValue = Integer.parseInt(conf.getValue());
        return value >confValue;

    }


}
