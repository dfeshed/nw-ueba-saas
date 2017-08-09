package presidio.webapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import fortscale.utils.logging.Logger;
import presidio.manager.api.records.DataPipeLineConfiguration;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.PresidioSystemConfiguration;
import presidio.manager.api.records.ValidationResponse;
import presidio.manager.api.service.ConfigurationProcessingService;


public class ConfigurationProcessingManager implements ConfigurationProcessingService {

    private static final Logger logger = Logger.getLogger(ConfigurationProcessingManager.class);

    private final String SYSTEM = "system";
    private final String DATA_PIPE_LINE = "dataPipeline";
    private ConfigurationProcessingService CPSAirflow;
    private ConfigurationProcessingService CPSSecurityManager;


    @Override
    public boolean applyConfiguration() {
        return CPSAirflow.applyConfiguration() && CPSAirflow.applyConfiguration();
    }

    @Override
    public ValidationResponse validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {
        ValidationResponse securityManagerValidationResponse = CPSSecurityManager.validateConfiguration(presidioManagerConfiguration);
        ValidationResponse airflowManagerValidationResponse = CPSAirflow.validateConfiguration(presidioManagerConfiguration);
        securityManagerValidationResponse.addToErrorList(airflowManagerValidationResponse.getErrorsList());
        if (CPSSecurityManager.validateConfiguration(presidioManagerConfiguration).isValid() && CPSAirflow.validateConfiguration(presidioManagerConfiguration).isValid())
            return new ValidationResponse();
        else
            return securityManagerValidationResponse;
    }


    public ConfigurationProcessingManager(ConfigurationProcessingService CPSAirflow, ConfigurationProcessingService CPSSecurityManager) {
        this.CPSAirflow = CPSAirflow;
        this.CPSSecurityManager = CPSSecurityManager;
    }

    public PresidioManagerConfiguration presidioManagerConfigurationFactory(JsonNode node) {
        DataPipeLineConfiguration dataPipeLineConfiguration = null;
        PresidioSystemConfiguration presidioSystemConfiguration = null;
        JsonNode system = node.get(SYSTEM);
        JsonNode data = node.get(DATA_PIPE_LINE);
        if (data != null)
            dataPipeLineConfiguration = DataPipeLineConfiguration.dataPipeLineConfigurationFactory(system);
        else
            logger.info("Json is missing the system configuration.");
        if (system != null)
            presidioSystemConfiguration = PresidioSystemConfiguration.presidioSystemConfigurationFactory(data);
        else
            logger.info("Json is missing the dataPipeLine configuration.");
        return new PresidioManagerConfiguration(dataPipeLineConfiguration, presidioSystemConfiguration);
    }

}
