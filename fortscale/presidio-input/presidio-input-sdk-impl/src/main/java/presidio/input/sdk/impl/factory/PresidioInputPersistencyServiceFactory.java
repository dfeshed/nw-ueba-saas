package presidio.input.sdk.impl.factory;

import org.springframework.beans.factory.annotation.Autowired;
import presidio.sdk.api.services.PresidioInputPersistencyService;

/**
 * This class allows its user to receive a Spring-created PresidioInputPersistencyService (without knowing it was created by Spring)
 */
public class PresidioInputPersistencyServiceFactory {

    @Autowired
    private PresidioInputPersistencyService presidioInputPersistencyService;

    public PresidioInputPersistencyService createPresidioInputPersistencyService() {
        return presidioInputPersistencyService;
    }
}
