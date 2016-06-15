package fortscale.utils.spring;

import fortscale.utils.logging.Logger;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * StandardProcessPropertiesConfigurer class is a configurer bean class that handles the process properties overrides.
 * <p>
 * Its ctor take a property file list and a Java Properties object. All of those override the previously defined properties.
 * <p>
 * A property order of importance is (from highest to lowest):
 * <p>
 * 1.   Overriding property object (to this class, if any)
 * 2.   Overriding property last file (of this class, if any)
 * ...
 * n-2. Overriding property 2nd file (to this class, if any)
 * n-1. Overriding property 1st file (to this class, if any)
 * n.   Standard property definition (defined else where)
 * <p>
 * The class extends PropertySourcesPlaceholderConfigurer
 * <p>
 * HOW TO USE:
 * <p>
 * The class should be used as a static bean.
 * <p>
 * EACH PROCESS MUST HAVE EXACTLY *ONE* INSTANCE OF THIS BEAN!
 * <p>
 * Example (in the process main configuration class):
 *
 * @Bean public static StandardProcessPropertiesConfigurer mainProcessPropertiesConfigurer() {
 * <p>
 * String[] overridingFileList = {"weakest-overriding.properties", "medium-overriding.properties"};
 * <p>
 * Properties properties = new Properties();
 * properties.put("foo.goo,"strongest-override");
 * properties.put("foo.goo2,"strongest-override2");
 * <p>
 * StandardProcessPropertiesConfigurer configurer = new StandardProcessPropertiesConfigurer(overridingFileList, properties);
 * <p>
 * return configurer;
 * }
 * <p>
 */
public abstract class GenericPropertiesConfigurer extends PropertySourcesPlaceholderConfigurer {

    private static final Logger logger = Logger.getLogger(GenericPropertiesConfigurer.class);
    public static final String SYSTEM_ENVIRONMENT = "systemEnvironment";
    protected StandardEnvironment environment;
    protected volatile boolean isEnvironmentSet = false;
    protected List<ResourcePropertySource> overridePropertyFilesResources;
    protected String[] overridePropertyFilesList;
    PropertySource overridingPropertiesSource;


    /**
     * Creates a configurer bean. See class description for details
     *
     * @param overridePropertyFilesList - a list of overriding properties file. File path with in the class path
     *                                  relative to the resources directory. Might be null
     *                                  for example: "foo.properties" map to "fortscale-xxx/resources/foo.properties"
     * @param overrideProperties        - A Java properties object. Might be null
     */
    public GenericPropertiesConfigurer(String[] overridePropertyFilesList, Properties overrideProperties) {

        super();
        this.overridePropertyFilesList = overridePropertyFilesList;

        // Set bean order. 10000 is a magic number, leaving the highest number for other beans. be kind
        setOrder(Ordered.HIGHEST_PRECEDENCE + 10000);
        // Give priority to property files and object defined here over the system/environments defined else where
        setLocalOverride(true);

        setIgnoreUnresolvablePlaceholders(true);

        // Add overriding property object, if any
        if (overrideProperties != null) {
            this.overridingPropertiesSource = new PropertiesPropertySource("overridingProperties", overrideProperties);
        }
    }

    /**
     * A syntactic sugar ctor that takes only overriding property file list
     *
     * @param overridePropertyFilesList
     */
    public GenericPropertiesConfigurer(String[] overridePropertyFilesList) {
        this(overridePropertyFilesList, null);
    }

    /**
     * A syntactic sugar ctor that takes only overriding property object
     *
     * @param overrideProperties
     */
    public GenericPropertiesConfigurer(Properties overrideProperties) {
        this(null, overrideProperties);
    }

    /**
     * A syntactic sugar ctor that takes no arguments
     */
    public GenericPropertiesConfigurer() {
        this(null, null);
    }

    /**
     * edit spring environment
     *
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        if (isEnvironmentSet) {
            String msg = "environment is already set, you should not set it twice in one process";
            logger.error(msg);
            throw new RuntimeException(msg);
        }

        this.environment = (StandardEnvironment) environment;
        updateOverridePropertyFilesResources();
        updateEnvironmentOverridingProperties();

        super.setEnvironment(environment);

        isEnvironmentSet = true;
    }

    /**
     * update overridePropertyFilesResources by overridePropertyFilesList
     */
    protected void updateOverridePropertyFilesResources() {
        overridePropertyFilesResources = new LinkedList<>();
        updateOverridingFileList();
        // Add overriding properties files as class path resources as "locations", if any
        if (overridePropertyFilesList != null) {
            for (String filePath : overridePropertyFilesList) {
                File file = new File(filePath);
                if (file.exists()) {
                    try {
                        FileSystemResource resource = new FileSystemResource(filePath);
                        ResourcePropertySource fileResource = new ResourcePropertySource(resource);
                        logger.debug("adding overriding properties file: {}", filePath);
                        overridePropertyFilesResources.add(fileResource);
                    } catch (Exception e) {
                        logger.error(String.format("error while loading ClassPathResource from properties file: %s", filePath), e);
                    }
                } else {
                    logger.debug("overriding properties file: {} does not exist", filePath);
                }
            }
        }
    }

    /**
     * giving the change for derived classes to ovverride the file list
     */
    public void updateOverridingFileList() {
    }

    /**
     * add overriding properties to environment
     */
    private void updateEnvironmentOverridingProperties() {
        // add propertiesFile resources to environment, if any
        if (!overridePropertyFilesResources.isEmpty()) {
            overridePropertyFilesResources.stream().forEach(resource ->
                    {
                        try {
                            logger.info("Adding properties file resource: {}", resource.getName());
                            this.environment.getPropertySources().addAfter(SYSTEM_ENVIRONMENT, resource);
                        } catch (Exception e) {
                            String msg = String.format("An error occurred while adding properties file: %s to environment", resource.getName());
                            logger.error(msg, e);
                            throw new RuntimeException(msg, e);
                        }
                    }
            );
        }
        // add property resource class to environment, if any
        if (overridingPropertiesSource != null) {
            try {
                logger.info("Adding overriding property object with {}", overridingPropertiesSource.getSource().toString());
                // if properties class exist, it is added before properties files
                this.environment.getPropertySources().addAfter(SYSTEM_ENVIRONMENT, overridingPropertiesSource);
            } catch (Exception e) {
                String msg = String.format("An error occurred while adding properties class to environment");
                logger.error(msg, e);
                throw new RuntimeException(msg, e);
            }
        }
    }


}
