package fortscale.collection.jobs.gds.configurators;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.Impl.IpResolvingTaskConfiguration;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;
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
    public GDSCompositeConfigurationState configure(Map<String, ConfigurationParam> configurationParams) throws Exception {

        GDSEnrichmentDefinitionState.IPResolvingState ipResolvingState = currGDSConfigurationState.getGDSEnrichmentDefinitionState().getIpResolvingState();

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

        return currGDSConfigurationState;
    }

    @Override
    public void apply() throws Exception {
        if (configurationService.init()) {
            configurationService.applyConfiguration();
        }

        configurationService.done();
    }

    @Override
    public void reset() throws Exception {
        currGDSConfigurationState.getGDSEnrichmentDefinitionState().getIpResolvingState().reset();
    }
}
