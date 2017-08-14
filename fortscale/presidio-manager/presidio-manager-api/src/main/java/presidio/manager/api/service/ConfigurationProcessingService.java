package presidio.manager.api.service;


import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.ValidationResults;

public interface ConfigurationProcessingService {
    ValidationResults validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration);

    boolean applyConfiguration();
}
