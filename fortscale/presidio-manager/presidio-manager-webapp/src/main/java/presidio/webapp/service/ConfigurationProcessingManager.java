package presidio.webapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import fortscale.utils.logging.Logger;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.ValidationResponse;
import presidio.manager.api.service.ConfigurationProcessingService;
import presidio.webapp.model.configuration.DataConfiguration;
import presidio.webapp.model.configuration.ModelConfiguration;
import presidio.webapp.model.configuration.SystemConfiguration;


public class ConfigurationProcessingManager implements ConfigurationProcessingService {

    private static final Logger logger = Logger.getLogger(ConfigurationProcessingManager.class);


    private ConfigurationProcessingService CPSAirflow;
    private ConfigurationProcessingService CPSSecurityManager;
    private ModelConfiguration modelConfiguration;


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


    public ConfigurationProcessingManager(ConfigurationProcessingService CPSAirflow, ConfigurationProcessingService CPSSecurityManager, ModelConfiguration modelConfiguration) {
        this.CPSAirflow = CPSAirflow;
        this.CPSSecurityManager = CPSSecurityManager;
        this.modelConfiguration = modelConfiguration;
    }

    public void setConfiguration(JsonNode node) {
        SystemConfiguration systemConfiguration = modelConfiguration.getSystem();
        DataConfiguration dataConfiguration = modelConfiguration.getDataPipeline();
        JsonNode system = node.get("system");
        JsonNode data = node.get("dataPipeline");
        if (data != null)
            systemConfiguration.setParameters(system);
        else
            logger.info("Json is missing the system configuration.");
        if (system != null)
            dataConfiguration.setParameters(data);
        else
            logger.info("Json is missing the dataPipeLine configuration.");

    }

}
