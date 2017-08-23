package presidio.sdk.api.service;

import presidio.manager.api.service.ConfigurationProcessingService;

/**
 * Extends ConfigurationProcessingService
 */
public interface ConnectorService extends ConfigurationProcessingService {

    /**
     * The connector SDK operate refresh the configurations of the collector
     *
     * @return
     */
    boolean applyCollector();
}
