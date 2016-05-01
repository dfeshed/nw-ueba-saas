package fortscale.utils.spring;

import fortscale.utils.logging.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Properties;


/**
 *
 * The PropertySourceConfigurer class enables to add java Property objects to the Spring environment properties list.
 *
 * Its functionality is similar to @PropertySource(filename) except that it add Property objects rather than property
 * file.
 *
 * How it works:
 * 1. At ctor, it just keeps the property object and calculate the source name (from the class name)
 * 2. The class gets the relevant environment by implementing the EnvironmentAware interface
 * 3. The class implements BeanFactoryPostProcessor. At postProcessBeanFactory() step, it wraps the property object
 *    with PropertySource object and add its to the environment properties list.
 *
 * How to use:
 *
 * At @Configuration class
 *
 *     @Bean
 *     private static PropertySourceConfigurer environmentPropertyConfigurer() {
 *
 *     Properties properties = SomeServiceProperties.getProperties();  // A static function
 *
 *     PropertySourceConfigurer configurer = new PropertySourceConfigurer(StatSpringConfig.class, properties);
 *
 *     return configurer;
 *
 * Created by gaashh on 5/1/16.
 */
public class PropertySourceConfigurer implements EnvironmentAware, BeanFactoryPostProcessor {

    private static final Logger logger = Logger.getLogger(PropertySourceConfigurer.class);

    // Hold the environment. The property source is add to it
    ConfigurableEnvironment environment;

    // Property source name
    String sourceName;

    // The properties object to add
    Properties properties;

    /**
     *
     * Add Java Properties object.
     *
     * The ctor just saves the parameters, most of the work is done later on
     *
     * @param clazz        - the class to used in the property source name
     * @param properties   - the Properties object to add
     */
    public PropertySourceConfigurer(Class<?> clazz, Properties properties) {

        // Calculate the properties source name
        this.sourceName = String.format("PropertySourceConfigurer: [%s]", clazz.getName());

        // Save the properties object
        this.properties = properties;
    }

    /**
     *
     * Saves the environment for later use.
     *
     * Called by Sprint due to EnvironmentAware
     *
     * @param environment
     */
    // Called by spring due to EnvironmentAware
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment)environment;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        logger.info("Adding property source {} to environment with properties {}", sourceName , properties.toString());

        // Create new properties source wrapping the Properties object
        PropertySource propertySource = new PropertiesPropertySource(sourceName, properties);

        // Get the environment property sources
        MutablePropertySources propertySources = environment.getPropertySources();

        // Add the new property source at the end of the list (lowest priority)
        propertySources.addLast(propertySource);

    }


}


