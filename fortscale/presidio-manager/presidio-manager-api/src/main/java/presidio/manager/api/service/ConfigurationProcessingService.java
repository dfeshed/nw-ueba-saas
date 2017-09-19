package presidio.manager.api.service;


import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.ValidationResults;
//todo: should be inside managerService
public interface ConfigurationProcessingService {
    ValidationResults validateConfiguration(PresidioManagerConfiguration presidioManagerConfiguration);

    boolean applyConfiguration();
}
