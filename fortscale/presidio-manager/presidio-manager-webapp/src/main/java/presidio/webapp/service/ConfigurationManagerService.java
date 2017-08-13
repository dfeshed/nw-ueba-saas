package presidio.webapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import presidio.manager.api.records.DataPipeLineConfiguration;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.PresidioSystemConfiguration;
import presidio.manager.api.records.ValidationResults;
import presidio.manager.api.service.ConfigurationProcessingService;


public class ConfigurationManagerService implements ConfigurationProcessingService {


    private final String SYSTEM = "system";
    private final String DATA_PIPE_LINE = "dataPipeline";
    private ConfigurationProcessingService CPSAirflow;
    private ConfigurationProcessingService CPSSecurityManager;

    public ConfigurationManagerService(ConfigurationProcessingService CPSAirflow, ConfigurationProcessingService CPSSecurityManager) {
        this.CPSAirflow = CPSAirflow;
        this.CPSSecurityManager = CPSSecurityManager;
    }

    @Override
    public boolean applyConfiguration() {
        return CPSAirflow.applyConfiguration() && CPSAirflow.applyConfiguration();
    }

    @Override
    public ValidationResults validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {
        ValidationResults securityManagerValidationResults = CPSSecurityManager.validateConfiguration(presidioManagerConfiguration);
        ValidationResults airflowManagerValidationResults = CPSAirflow.validateConfiguration(presidioManagerConfiguration);
        securityManagerValidationResults.addErrors(airflowManagerValidationResults.getErrorsList());
        if (securityManagerValidationResults.isValid())
            return new ValidationResults();
        else
            return securityManagerValidationResults;
    }




    public PresidioManagerConfiguration presidioManagerConfigurationFactory(JsonNode node) {
        DataPipeLineConfiguration dataPipeLineConfiguration = null;
        PresidioSystemConfiguration presidioSystemConfiguration = null;
        if (node != null) {
            JsonNode system = node.has(SYSTEM) ? node.get(SYSTEM) : null;
            JsonNode data = node.has(DATA_PIPE_LINE) ? node.get(DATA_PIPE_LINE) : null;
            if (system != null)
                presidioSystemConfiguration = new PresidioSystemConfiguration(system);
            if (data != null)
                dataPipeLineConfiguration = new DataPipeLineConfiguration(data);
        }
        return new PresidioManagerConfiguration(dataPipeLineConfiguration, presidioSystemConfiguration);
    }

}
