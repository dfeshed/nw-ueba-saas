package presidio.manager.api.service;


import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.ValidationResponse;

public interface ConfigurationProcessingService {
    ValidationResponse validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration);

    boolean applyConfiguration();
}
