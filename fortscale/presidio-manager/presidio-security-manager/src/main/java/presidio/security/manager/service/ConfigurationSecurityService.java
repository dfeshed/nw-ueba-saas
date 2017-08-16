package presidio.security.manager.service;


import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.ValidationResults;
import presidio.manager.api.service.ConfigurationProcessingService;

public class ConfigurationSecurityService implements ConfigurationProcessingService {

    @Override
    public ValidationResults validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration) {
        return new ValidationResults();
    }

    @Override
    public boolean applyConfiguration() {
        return true;
    }


}
