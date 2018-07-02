package presidio.webapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import presidio.manager.api.records.*;
import presidio.manager.api.service.ConfigurationProcessingService;
import presidio.manager.api.records.UIIntegrationConfiguration;

import java.util.Iterator;
import java.util.Map;


public class ConfigurationManagerService implements ConfigurationProcessingService {

    private final String GENERAL = "general";
    private final String UNSUPPORTED_FIELD_ERROR = "unsupportedFieldError";
    public static final String JSON_PATH = "jsonPath";
    private final String GENERAL_ERROR_MESSAGE = "General error, field %s is unsupported, Valid values are [system,dataPipeline].";
    private ConfigurationProcessingService CPSAirflow;
    private ConfigurationProcessingService CPSSecurityManager;
    private ConfigurationProcessingService CPSOutput;
    private ConfigurationProcessingService CPSDataPullingService;
    private ConfigurationProcessingService CPSUIIntegrationConfigurationService;
    private ValidationResults validationResults;

    public ConfigurationManagerService(ConfigurationProcessingService CPSAirflow,
                                       ConfigurationProcessingService CPSSecurityManager,
                                       ConfigurationProcessingService CPSOutput,
                                       ConfigurationProcessingService CPSDataPullingService,
                                       ConfigurationProcessingService CPSUIIntegrationConfigurationService) {
        this.CPSAirflow = CPSAirflow;
        this.CPSSecurityManager = CPSSecurityManager;
        this.CPSOutput = CPSOutput;
        this.CPSDataPullingService = CPSDataPullingService;
        this.CPSUIIntegrationConfigurationService = CPSUIIntegrationConfigurationService;
    }

    @Override
    public boolean applyConfiguration() {
        return CPSAirflow.applyConfiguration() && CPSSecurityManager.applyConfiguration() &&
                CPSOutput.applyConfiguration() && CPSDataPullingService.applyConfiguration() &&
                CPSUIIntegrationConfigurationService.applyConfiguration();
    }

    @Override
    public ValidationResults validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {
        validationResults.addErrors(CPSSecurityManager.validateConfiguration(presidioManagerConfiguration).getErrorsList());
        validationResults.addErrors(CPSAirflow.validateConfiguration(presidioManagerConfiguration).getErrorsList());
        validationResults.addErrors(CPSOutput.validateConfiguration(presidioManagerConfiguration).getErrorsList());
        validationResults.addErrors(CPSDataPullingService.validateConfiguration(presidioManagerConfiguration).getErrorsList());
        validationResults.addErrors(CPSUIIntegrationConfigurationService.validateConfiguration(presidioManagerConfiguration).getErrorsList());

        if (validationResults.isValid())
            return new ValidationResults();
        else
            return validationResults;
    }


    // todo: refactor everything in this area. bah.
    public PresidioManagerConfiguration presidioManagerConfigurationFactory(JsonNode node) {
        validationResults = new ValidationResults();
        DataPipeLineConfiguration dataPipeLineConfiguration = null;
        PresidioSystemConfiguration presidioSystemConfiguration = null;
        OutputConfigurationCreator outputConfigurationCreator = null;
        DataPullingConfiguration dataPullingConfiguration = null;
        UIIntegrationConfiguration uiIntegrationConfiguration = null;
        if (node != null) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            Map.Entry<String, JsonNode> map;
            String key;
            JsonNode value;
            while (fields.hasNext()) {
                map = fields.next();
                key = map.getKey();
                value = map.getValue();
                if (key.equals(PresidioManagerConfiguration.SYSTEM)) {
                    presidioSystemConfiguration = value != null ? new PresidioSystemConfiguration(value) : null;
                } else {
                    if (key.equals(PresidioManagerConfiguration.DATA_PIPE_LINE)) {
                        dataPipeLineConfiguration = value != null ? new DataPipeLineConfiguration(value) : null;
                    } else {
                        if (key.equals(PresidioManagerConfiguration.OUTPUT_FORWARDING)) {
                            outputConfigurationCreator = value != null ? new OutputConfigurationCreator(value) : null;
                        } else {
                            if (key.equals(PresidioManagerConfiguration.DATA_PULLING)) {
                                dataPullingConfiguration = value != null ? new DataPullingConfiguration(value) : null;
                            }
                            else {
                                if (key.equals(PresidioManagerConfiguration.UI_INTEGRATION)) {
                                    uiIntegrationConfiguration = value != null ? new UIIntegrationConfiguration(value) : null;
                                } else {
                                    validationResults.addError(new ConfigurationBadParamDetails(GENERAL, key, UNSUPPORTED_FIELD_ERROR, JSON_PATH, String.format(GENERAL_ERROR_MESSAGE, key)));
                                }
                            }

                        }
                    }
                }
            }
        }
        return new PresidioManagerConfiguration(dataPipeLineConfiguration, presidioSystemConfiguration, outputConfigurationCreator, dataPullingConfiguration,uiIntegrationConfiguration);
    }

}
