package presidio.input.sdk.impl.factory;

import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.config.MongoConfig;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import presidio.input.sdk.impl.spring.PresidioInputPersistencyServiceConfig;
import presidio.sdk.api.services.PresidioInputPersistencyService;

/**
 * This class allows its user to receive a Spring-created PresidioInputPersistencyService (without knowing it was created by Spring)
 */
public class PresidioInputPersistencyServiceFactory {

    private static final Logger logger = Logger.getLogger(PresidioInputPersistencyServiceFactory.class);

    public PresidioInputPersistencyService createPresidioInputPersistencyService() throws Exception {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfig.class, PresidioInputPersistencyServiceConfig.class); //MongoConfig.class,
        final PresidioInputPersistencyService presidioInputPersistencyServiceBean = ctx.getBean(PresidioInputPersistencyService.class);
        if (presidioInputPersistencyServiceBean == null) {
            final String errorMessage = "Failed to create PresidioInputPersistencyService.";
            logger.error(errorMessage);
            throw new Exception(errorMessage);
        }
        return presidioInputPersistencyServiceBean;
    }

    public PresidioInputPersistencyService createPresidioInputPersistencyServiceForTests() throws Exception {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(MongodbTestConfig.class, PresidioInputPersistencyServiceConfig.class); //MongoConfig.class,
        final PresidioInputPersistencyService presidioInputPersistencyServiceBean = ctx.getBean(PresidioInputPersistencyService.class);
        if (presidioInputPersistencyServiceBean == null) {
            final String errorMessage = "Failed to create PresidioInputPersistencyService for tests.";
            logger.error(errorMessage);
            throw new Exception(errorMessage);
        }
        return presidioInputPersistencyServiceBean;
    }
}
