package fortscale.streaming.alert.subscribers.evidence.applicable;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.EvidenceType;
import fortscale.domain.core.VpnGeoHoppingSupportingInformation;
import fortscale.services.ApplicationConfigurationService;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by shays on 16/03/2016.
 */
public class LimitGeoHoppingPreAlertCreation implements PreAlertDeciderFilter {

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;

    private final String MAX_IDENTICAL_GEO_HOPPING_PER_USER = "maxIdenticalGeoHoppingPerUser";
    private final String MAX_IDENTICAL_GEO_HOPPING_GLOBAL = "maxIdenticalGeoHoppingGlobal";
    private final String MAX_SINGLE_CITY = "maxSingleCity";



    public boolean canCreateAlert(EnrichedFortscaleEvent evidencesOrEntityEvents){

        String supportingInformationAsString = evidencesOrEntityEvents.getSupportingInformation();

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
