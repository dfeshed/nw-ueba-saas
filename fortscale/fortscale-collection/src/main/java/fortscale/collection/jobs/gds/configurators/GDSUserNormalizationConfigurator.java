package fortscale.collection.jobs.gds.configurators;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.Impl.UserNormalizationTaskConfiguration;
import fortscale.services.configuration.gds.state.GDSEnrichmentDefinitionState;

import java.util.List;
import java.util.Map;

/**
 * User normalization configurator implementation
 *
 * @author gils
 * 04/01/2016
 */
public class GDSUserNormalizationConfigurator extends GDSBaseConfigurator {

    private static final String SOURCE_USERNAME_CONFIG_ENTRY = "source.";
    private static final String TARGET_USERNAME_CONFIG_ENTRY = "target.";

    public GDSUserNormalizationConfigurator() {
        configurationService = new UserNormalizationTaskConfiguration();
    }

    @Override
    public void configure(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception {

        List<GDSEnrichmentDefinitionState.UserNormalizationState> userNormalizationStates = currGDSConfigurationState.getEnrichmentDefinitionState().getUserNormalizationStates();

        addConfiguration(userNormalizationStates, configurationParams, GDS_CONFIG_ENTRY + SOURCE_USERNAME_CONFIG_ENTRY);
        addConfiguration(userNormalizationStates, configurationParams, GDS_CONFIG_ENTRY + TARGET_USERNAME_CONFIG_ENTRY);
    }

    private void addConfiguration(List<GDSEnrichmentDefinitionState.UserNormalizationState> userNormalizationStates, Map<String, Map<String, ConfigurationParam>> configurationParams, String configurationKey) {
        Map<String, ConfigurationParam> paramsMap = configurationParams.get(configurationKey);

        ConfigurationParam userNameField = paramsMap.get("userNameField");
        ConfigurationParam domainField = paramsMap.get("domainFieldName");
        ConfigurationParam domainValue = paramsMap.get("domainValue");
        ConfigurationParam normalizedUserNameField = paramsMap.get("normalizedUserNameField");
        ConfigurationParam normalizeServiceName = paramsMap.get("normalizeServiceName");
        ConfigurationParam updateOnlyFlag = paramsMap.get("updateOnlyFlag");

        GDSEnrichmentDefinitionState.UserNormalizationState userNormalizationState = new GDSEnrichmentDefinitionState.UserNormalizationState();

        userNormalizationState.setUserNameField(userNameField.getParamValue());
        userNormalizationState.setDomainField(domainField.getParamValue());
        userNormalizationState.setDomainValue(domainValue.getParamValue());
        userNormalizationState.setNormalizedUserNameField(normalizedUserNameField.getParamValue());
        userNormalizationState.setNormalizeServiceName(normalizeServiceName.getParamValue());
        userNormalizationState.setUpdateOnly(updateOnlyFlag.getParamValue());

        userNormalizationStates.add(userNormalizationState);
    }

    @Override
    public void reset() throws Exception {
        currGDSConfigurationState.getEnrichmentDefinitionState().getUserNormalizationStates().clear();
    }
}
