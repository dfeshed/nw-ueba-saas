package presidio.security.manager.service;


import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.ValidationResponse;
import presidio.manager.api.service.ConfigurationProcessingService;

public class ConfigurationProcessingServiceImpl implements ConfigurationProcessingService {

    @Override
    public ValidationResponse validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {
        return null;
    }

    @Override
    public boolean applyConfiguration() {
        return true;
    }

    public ConfigurationProcessingServiceImpl() {
    }
}
