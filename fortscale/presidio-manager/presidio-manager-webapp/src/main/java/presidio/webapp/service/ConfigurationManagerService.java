package presidio.webapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import presidio.manager.api.records.ConfigurationBadParamDetails;
import presidio.manager.api.records.DataPipeLineConfiguration;
import presidio.manager.api.records.OutputConfigurationCreator;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.PresidioSystemConfiguration;
import presidio.manager.api.records.ValidationResults;
import presidio.manager.api.service.ConfigurationProcessingService;

import java.util.Iterator;
import java.util.Map;


public class ConfigurationManagerService implements ConfigurationProcessingService {

    private final String GENERAL = "general";
    private final String UNSUPPORTED_FIELD_ERROR = "unsupportedFieldError";
    private final String JSON_PATH = "jsonPath";
    private final String GENERAL_ERROR_MESSAGE = "General error, field %s is unsupported, Valid values are [system,dataPipeline].";
    private ConfigurationProcessingService CPSAirflow;
    private ConfigurationProcessingService CPSSecurityManager;
    private ConfigurationProcessingService CPSOutput;
    private ValidationResults validationResults;

    public ConfigurationManagerService(ConfigurationProcessingService CPSAirflow, ConfigurationProcessingService CPSSecurityManager, ConfigurationProcessingService CPSOutput) {
        this.CPSAirflow = CPSAirflow;
        this.CPSSecurityManager = CPSSecurityManager;
        this.CPSOutput = CPSOutput;
    }

    @Override
    public boolean applyConfiguration() {
        return CPSAirflow.applyConfiguration() && CPSSecurityManager.applyConfiguration() && CPSOutput.applyConfiguration();
    }

    @Override
    public ValidationResults validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {
        validationResults.addErrors(CPSSecurityManager.validateConfiguration(presidioManagerConfiguration).getErrorsList());
        validationResults.addErrors(CPSAirflow.validateConfiguration(presidioManagerConfiguration).getErrorsList());
        validationResults.addErrors(CPSOutput.validateConfiguration(presidioManagerConfiguration).getErrorsList());
        if (validationResults.isValid())
            return new ValidationResults();
        else
            return validationResults;
    }


    public PresidioManagerConfiguration presidioManagerConfigurationFactory(JsonNode node) {
        validationResults = new ValidationResults();
        DataPipeLineConfiguration dataPipeLineConfiguration = null;
        PresidioSystemConfiguration presidioSystemConfiguration = null;
        OutputConfigurationCreator outputConfigurationCreator = null;
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
                            validationResults.addError(new ConfigurationBadParamDetails(GENERAL, key, UNSUPPORTED_FIELD_ERROR, JSON_PATH, String.format(GENERAL_ERROR_MESSAGE, key)));
                        }
                    }
                }
            }
        }
        return new PresidioManagerConfiguration(dataPipeLineConfiguration, presidioSystemConfiguration, outputConfigurationCreator, dataPullingConfiguration);
    }

}
