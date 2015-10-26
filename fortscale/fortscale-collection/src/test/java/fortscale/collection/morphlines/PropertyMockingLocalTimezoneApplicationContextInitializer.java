package fortscale.collection.morphlines;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.mock.env.MockPropertySource;

/**
* Created by rans on 20/10/15.
 * A mock implementation to override global-config properties
 * Int this case we set the timezone property for morphlines to Asia/Jerusalem
 *
 * The reason we do it here and mot in regulat spring loading of properties file for JUnit, is that the properties file that is read
 * by morphline is taken from global-config module and we cannot override it from Spring
*/
public class PropertyMockingLocalTimezoneApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
        MockPropertySource mockEnvVars = new MockPropertySource().withProperty("morphline.timezone", "{ \"defaultTimezone\" : \"Asia/Jerusalem\" }");
        propertySources.replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, mockEnvVars);
    }
}
