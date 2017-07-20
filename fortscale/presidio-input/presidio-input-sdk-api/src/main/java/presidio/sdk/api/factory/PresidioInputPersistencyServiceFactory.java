package presidio.sdk.api.factory;

import presidio.sdk.api.services.PresidioInputPersistencyService;

/**
 * This class is meant to separate the creation of the PresidioInputPersistencyService from its implementation
 */
public interface PresidioInputPersistencyServiceFactory {

    PresidioInputPersistencyService createPresidioInputPersistencyService();
}
