package presidio.webapp.service;

import fortscale.utils.logging.Logger;
import org.apache.commons.lang.StringUtils;
import presidio.manager.api.records.ConfigurationBadParamDetails;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.UIIntegrationConfiguration;
import presidio.manager.api.records.ValidationResults;
import presidio.manager.api.service.ConfigurationProcessingService;

import static presidio.manager.api.records.UIIntegrationConfiguration.FIELD_ADMIN_SERVER;
import static presidio.manager.api.records.UIIntegrationConfiguration.FIELD_BROKER_ID;
import static presidio.webapp.service.ConfigurationDataPullingService.MISSING_DATA_ERROR_MESSAGE;
import static presidio.webapp.service.ConfigurationDataPullingService.MISSING_PROPERTY;
import static presidio.webapp.service.ConfigurationManagerService.JSON_PATH;

public class UIIntegrationConfigurationService implements ConfigurationProcessingService {

    private static final Logger logger = Logger.getLogger(ConfigurationDataPullingService.class);

    @Override
    public ValidationResults validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {
        logger.debug("Validating ui integration configuration");
        UIIntegrationConfiguration uiIntegrationConfiguration = presidioManagerConfiguration.getUiIntegrationConfiguration();
        ValidationResults validationResults = new ValidationResults();
        if (uiIntegrationConfiguration == null) {
            ConfigurationBadParamDetails error = new ConfigurationBadParamDetails("uiIntegration", "uiIntegration", MISSING_PROPERTY, JSON_PATH, MISSING_DATA_ERROR_MESSAGE);
            validationResults.addError(error);
            logger.error("Missing data pulling configuration");
            return validationResults;
        }
        if(StringUtils.isEmpty(uiIntegrationConfiguration.getAdminServerAddress())) {
            ConfigurationBadParamDetails error = new ConfigurationBadParamDetails("uiIntegration", FIELD_ADMIN_SERVER, MISSING_PROPERTY, JSON_PATH, MISSING_DATA_ERROR_MESSAGE);
            validationResults.addError(error);
        }
        if(StringUtils.isEmpty(uiIntegrationConfiguration.getBrokerId())) {
            ConfigurationBadParamDetails error = new ConfigurationBadParamDetails("uiIntegration", FIELD_BROKER_ID, MISSING_PROPERTY, JSON_PATH, MISSING_DATA_ERROR_MESSAGE);
            validationResults.addError(error);
        }
        return validationResults;
    }

    @Override
    public boolean applyConfiguration() {
        return true;
    }
}
