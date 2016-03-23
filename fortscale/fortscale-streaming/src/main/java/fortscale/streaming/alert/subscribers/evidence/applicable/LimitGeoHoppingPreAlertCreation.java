package fortscale.streaming.alert.subscribers.evidence.applicable;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.EvidenceType;
import fortscale.domain.core.VpnGeoHoppingSupportingInformation;
import fortscale.services.ApplicationConfigurationService;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shays on 16/03/2016.
 */
public class LimitGeoHoppingPreAlertCreation implements PreAlertDeciderFilter {

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;

    private final static String MAX_IDENTICAL_GEO_HOPPING_PER_USER = "maxIdenticalGeoHoppingPerUser";
    private final static String MAX_IDENTICAL_GEO_HOPPING_GLOBAL = "maxIdenticalGeoHoppingGlobal";
    private final static String MAX_SINGLE_CITY = "maxSingleCity";

    private final static Map<String, Integer> DEFAULT_CONFIGURATIONS = new HashMap<>();
    static {
        DEFAULT_CONFIGURATIONS.put(MAX_IDENTICAL_GEO_HOPPING_PER_USER,1);
        DEFAULT_CONFIGURATIONS.put(MAX_IDENTICAL_GEO_HOPPING_GLOBAL,3);
        DEFAULT_CONFIGURATIONS.put(MAX_SINGLE_CITY,10);
    }


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
        return EvidenceType.Notification.equals(evidenceType) && anomalyType.equalsIgnoreCase("VPN_GEO_HOPPING");
    }

    private boolean largeThenConfiguration(int value, String configurationKey){
        ApplicationConfiguration conf = applicationConfigurationService.getApplicationConfigurationByKey(configurationKey);

        int confValue;
        if (conf == null){
            //throw new RuntimeException(configurationKey + " configuration required");
            confValue = DEFAULT_CONFIGURATIONS.get(configurationKey);
            String configuationValueAsString = Integer.toString(confValue);
            applicationConfigurationService.insertConfigItem(configurationKey, configuationValueAsString);

        } else {
            confValue = Integer.parseInt(conf.getValue());
        }

        return value >= confValue;

    }


}
