package fortscale.collection.jobs.gds.configurators;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.Impl.IpResolvingTaskConfiguration;
import fortscale.services.configuration.gds.state.GDSEnrichmentDefinitionState;

import java.util.Map;

/**
 * IP Resolving configurator implementation
 *
 * @author gils
 * 04/01/2016
 */
public class GDSIPResolvingConfigurator extends GDSBaseConfigurator {

    public GDSIPResolvingConfigurator() {
        configurationService = new IpResolvingTaskConfiguration();
    }

    @Override
    public void configure(Map<String, ConfigurationParam> configurationParams) throws Exception {

        GDSEnrichmentDefinitionState.IPResolvingState ipResolvingState = currGDSConfigurationState.getEnrichmentDefinitionState().getIpResolvingState();

        ConfigurationParam restrictToAD = configurationParams.get("restrictToAD");
        ConfigurationParam shortNameUsage = configurationParams.get("shortNameUsage");
        ConfigurationParam removeLastDotUsage = configurationParams.get("removeLastDotUsage");
        ConfigurationParam dropOnFailUsage = configurationParams.get("dropOnFailUsage");
        ConfigurationParam overrideIpWithHostNameUsage = configurationParams.get("overrideIpWithHostNameUsage");
        ConfigurationParam updateOnlyFlag = configurationParams.get("ipField");
        ConfigurationParam hostField = configurationParams.get("hostField");

        ipResolvingState.setRestrictToAD(restrictToAD.getParamFlag());
        ipResolvingState.setShortNameUsage(shortNameUsage.getParamFlag());
        ipResolvingState.setDropOnFailUsage(dropOnFailUsage.getParamFlag());
        ipResolvingState.setOverrideIpWithHostNameUsage(overrideIpWithHostNameUsage.getParamFlag());
        ipResolvingState.setHostField(updateOnlyFlag.getParamValue());

        configurationService.setGDSConfigurationState(currGDSConfigurationState);
    }

    @Override
    public void reset() throws Exception {
        currGDSConfigurationState.getEnrichmentDefinitionState().getIpResolvingState().reset();
    }
}
