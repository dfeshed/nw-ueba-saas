package fortscale.streaming.alert.subscribers.evidence.applicable;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.EvidenceType;
import fortscale.domain.core.VpnGeoHoppingSupportingInformation;
import fortscale.services.ApplicationConfigurationService;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shays on 16/03/2016.
 */
public class LimitGeoHoppingPreAlertCreation implements AlertPreAlertDeciderFilter {

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;

    private final static String MAX_IDENTICAL_GEO_HOPPING_PER_USER = "LimitGeoHoppingPreAlertCreation.maxIdenticalGeoHoppingPerUser";
    private final static String MAX_IDENTICAL_GEO_HOPPING_GLOBAL = "LimitGeoHoppingPreAlertCreation.maxIdenticalGeoHoppingGlobal";
    private final static String MAX_SINGLE_CITY = "LimitGeoHoppingPreAlertCreation.maxSingleCity";

    private final Logger logger = Logger.getLogger(this.getClass());

    private final static Map<String, Integer> DEFAULT_CONFIGURATIONS = new HashMap<>();
    static {
        DEFAULT_CONFIGURATIONS.put(MAX_IDENTICAL_GEO_HOPPING_PER_USER,1);
        DEFAULT_CONFIGURATIONS.put(MAX_IDENTICAL_GEO_HOPPING_GLOBAL,3);
        DEFAULT_CONFIGURATIONS.put(MAX_SINGLE_CITY,10);
    }


    public boolean canCreateAlert(EnrichedFortscaleEvent evidencesOrEntityEvents, Long startTime, Long endTime){

        //Check that supporting information instance of VpnGeoHoppingSupportingInformation
        if (evidencesOrEntityEvents.getSupportingInformation() == null ||
            !(evidencesOrEntityEvents.getSupportingInformation() instanceof VpnGeoHoppingSupportingInformation)){
            logger.error("LimitGeoHoppingPreAlertCreation got geo hopping evidence without VpnGeoHoppingSupportingInformation");
            return  false;
        }

        VpnGeoHoppingSupportingInformation supportingInformation = (VpnGeoHoppingSupportingInformation)evidencesOrEntityEvents.getSupportingInformation();


        if (largeThenConfiguration(supportingInformation.getPairInstancesPerUser(), MAX_IDENTICAL_GEO_HOPPING_PER_USER) ){
            return false;
        }

        if (largeThenConfiguration(supportingInformation.getPairInstancesGlobalUser(), MAX_IDENTICAL_GEO_HOPPING_GLOBAL) ){
            return false;
        }

        if (largeThenConfiguration(supportingInformation.getMaximumGlobalSingleCity(), MAX_SINGLE_CITY) ){
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
