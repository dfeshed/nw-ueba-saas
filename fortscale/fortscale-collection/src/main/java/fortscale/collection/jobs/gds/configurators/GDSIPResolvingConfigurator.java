package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurator;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.ConfigurationService;
import fortscale.services.configuration.Impl.IpResolvingTaskConfiguration;
import fortscale.services.configuration.state.EnrichmentDefinitionState;
import fortscale.services.configuration.state.GDSCompositeConfigurationState;
import fortscale.utils.logging.Logger;

import java.util.Map;

/**
 * @author gils
 * 04/01/2016
 */
public class GDSIPResolvingConfigurator implements GDSConfigurator {

    private static Logger logger = Logger.getLogger(GDSIPResolvingConfigurator.class);

    private GDSCompositeConfigurationState gdsConfigurationState = new GDSCompositeConfigurationState();

    private ConfigurationService ipResolvingTaskConfiguration = new IpResolvingTaskConfiguration();

    @Override
    public GDSCompositeConfigurationState configure(Map<String, ConfigurationParam> configurationParams) throws Exception {

        EnrichmentDefinitionState.IPResolvingState ipResolvingState = gdsConfigurationState.getEnrichmentDefinitionState().getIpResolvingState();

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

        ipResolvingTaskConfiguration.setGDSConfigurationState(gdsConfigurationState);

        return gdsConfigurationState;
    }

    @Override
    public void apply() throws Exception {
        if (ipResolvingTaskConfiguration.init()) {
            ipResolvingTaskConfiguration.applyConfiguration();
        }

        ipResolvingTaskConfiguration.done();
    }

    @Override
    public void reset() throws Exception {
        gdsConfigurationState.reset();
    }
}
