package presidio.sdk.api.service;

import presidio.manager.api.service.ConfigurationProcessingService;

/**
 * Created by shays on 19/08/2017.
 */
public interface ConnectorService extends ConfigurationProcessingService {
    boolean applyCollector();
}
