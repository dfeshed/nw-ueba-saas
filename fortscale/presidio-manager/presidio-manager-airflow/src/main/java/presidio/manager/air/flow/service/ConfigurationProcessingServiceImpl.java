package presidio.manager.air.flow.service;

import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.ValidationResponse;
import presidio.manager.api.service.ConfigurationProcessingService;


public class ConfigurationProcessingServiceImpl implements ConfigurationProcessingService {

    @Override
    public boolean applyConfiguration() {
        return true;
    }

    @Override
    public ValidationResponse validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {
        return new ValidationResponse();
    }

    public ConfigurationProcessingServiceImpl() {
    }
}
