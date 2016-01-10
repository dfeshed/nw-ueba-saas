package fortscale.collection.jobs.gds.configurators;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.Impl.IpResolvingTaskConfiguration;
import fortscale.services.configuration.gds.state.GDSEnrichmentDefinitionState;

import java.util.List;
import java.util.Map;

/**
 * IP Resolving configurator implementation
 *
 * @author gils
 * 04/01/2016
 */
public class GDSIPResolvingConfigurator extends GDSBaseConfigurator {

    private static final String SOURCE_IP_CONFIG_ENTRY = "source.";
    private static final String TARGET_IP_CONFIG_ENTRY = "target.";

    public GDSIPResolvingConfigurator() {
        configurationService = new IpResolvingTaskConfiguration();
    }

    public void configure(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception {

        List<GDSEnrichmentDefinitionState.IPResolvingState> ipResolvingStates = currGDSConfigurationState.getEnrichmentDefinitionState().getIpResolvingStates();

        addConfiguration(ipResolvingStates, configurationParams, GDS_CONFIG_ENTRY + SOURCE_IP_CONFIG_ENTRY);
        addConfiguration(ipResolvingStates, configurationParams, GDS_CONFIG_ENTRY + TARGET_IP_CONFIG_ENTRY);
    }

    private void addConfiguration(List<GDSEnrichmentDefinitionState.IPResolvingState> ipResolvingStates, Map<String, Map<String, ConfigurationParam>> configurationParams, String configurationKey) {
        Map<String, ConfigurationParam> paramsMap = configurationParams.get(configurationKey);

        ConfigurationParam restrictToAD = paramsMap.get("restrictToAD");
        ConfigurationParam shortNameUsage = paramsMap.get("shortNameUsage");
        ConfigurationParam removeLastDotUsage = paramsMap.get("removeLastDotUsage");
        ConfigurationParam dropOnFailUsage = paramsMap.get("dropOnFailUsage");
        ConfigurationParam overrideIpWithHostNameUsage = paramsMap.get("overrideIpWithHostNameUsage");
        ConfigurationParam updateOnlyFlag = paramsMap.get("ipField");
        ConfigurationParam hostField = paramsMap.get("hostField");

        GDSEnrichmentDefinitionState.IPResolvingState ipResolvingState = new GDSEnrichmentDefinitionState.IPResolvingState();

        ipResolvingState.setRestrictToAD(restrictToAD.getParamFlag());
        ipResolvingState.setShortNameUsage(shortNameUsage.getParamFlag());
        ipResolvingState.setDropOnFailUsage(dropOnFailUsage.getParamFlag());
        ipResolvingState.setOverrideIpWithHostNameUsage(overrideIpWithHostNameUsage.getParamFlag());
        ipResolvingState.setHostField(updateOnlyFlag.getParamValue());
        ipResolvingState.setRemoveLastDotUsage(removeLastDotUsage.getParamFlag());

        ipResolvingStates.add(ipResolvingState);
    }

    @Override
    public void reset() throws Exception {
        currGDSConfigurationState.getEnrichmentDefinitionState().getIpResolvingStates().clear();
    }

    @Override
    public String getConfiguratorName() {
        return "IP Resolving Task";
    }
}
