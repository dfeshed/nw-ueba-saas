package presidio.input.sdk.impl.factory;

import org.springframework.beans.factory.annotation.Autowired;
import presidio.sdk.api.factory.PresidioInputPersistencyServiceFactory;
import presidio.sdk.api.services.PresidioInputPersistencyService;

/**
 * An implementation of PresidioInputPersistencyServiceFactory that allows its user to receive a Spring-created PresidioInputPersistencyService
 * This implementation should be changed per release
 */
public class PresidioInputPersistencyServiceFactoryImpl implements PresidioInputPersistencyServiceFactory {

    @Autowired
    private PresidioInputPersistencyService presidioInputPersistencyService;

    @Override
    public PresidioInputPersistencyService createPresidioInputPersistencyService() {
        return presidioInputPersistencyService;
    }
}
