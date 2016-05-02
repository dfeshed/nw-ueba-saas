package fortscale.utils.spring;

import fortscale.utils.logging.Logger;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;

import java.util.Properties;


/**
 * MainProcessPropertiesConfigurer class is a configurer bean class that handles the process properties overrides.
 *
 * Its ctor take a property file list and a Java Properties object. All of those override the previously defined properties.
 *
 * A property order of importance is (from highest to lowest):
 *
 *   1.   Overriding property object (to this class, if any)
 *   2.   Overriding property last file (of this class, if any)
 *   ...
 *   n-2. Overriding property 2nd file (to this class, if any)
 *   n-1. Overriding property 1st file (to this class, if any)
 *   n.   Standard property definition (defined else where)
 *
 * The class extends PropertySourcesPlaceholderConfigurer
 *
 * HOW TO USE:
 *
 *   The class should be used as a static bean.
 *
 *   EACH PROCESS MUST HAVE EXACTLY *ONE* INSTANCE OF THIS BEAN!
 *
 *   Example (in the process main configuration class):
 *
 *   @Bean
 *   public static MainProcessPropertiesConfigurer mainProcessPropertiesConfigurer() {
 *
 *       String[] overridingFileList = {"weakest-overriding.properties", "medium-overriding.properties"};
 *
 *       Properties properties = new Properties();
 *       properties.put("foo.goo,"strongest-override");
 *       properties.put("foo.goo2,"strongest-override2");
 *
 *       MainProcessPropertiesConfigurer configurer = new MainProcessPropertiesConfigurer(overridingFileList, properties);
 *
 *      return configurer;
 *   }
 *
 * Created by gaashh on 5/1/16.
 */
public class MainProcessPropertiesConfigurer extends PropertySourcesPlaceholderConfigurer {

    private static final Logger logger = Logger.getLogger(MainProcessPropertiesConfigurer.class);

    /**
     *
     * Creates a configurer bean. See class description for details
     *
     * @param overridePropertyFilesList - a list of overriding properties file. File path with in the class path
     *                                    relative to the resources directory. Might be null
     *                                    for example: "foo.properties" map to "fortscale-xxx/resources/foo.properties"
     * @param overrideProperties        - A Java properties object. Might be null
     */
    public MainProcessPropertiesConfigurer(String[] overridePropertyFilesList, Properties overrideProperties) {

        super();

        // Set bean order. 10000 is a magic number, leaving the highest number for other beans. be kind
        setOrder(Ordered.HIGHEST_PRECEDENCE + 10000);
        // Give priority to property files and object defined here over the system/environments defined else where
        setLocalOverride(true);

        setIgnoreUnresolvablePlaceholders(true);


        // Add overriding properties files as class path resources as "locations", if any
        if (overridePropertyFilesList != null) {

            // Convert the file path list to resource class array
            ClassPathResource[] overridePropertyFilesResources = new ClassPathResource[overridePropertyFilesList.length];

            int i = 0;
            for (String propertyFile : overridePropertyFilesList) {
                logger.info("Adding overriding property file {}", propertyFile);
                overridePropertyFilesResources[i] = new ClassPathResource(propertyFile);
                i++;
            }

            // Set it
            setLocations(overridePropertyFilesResources);
        }

        // Add overriding property object, if any
        if (overrideProperties != null) {
            logger.info("Adding overriding property object with {}", overrideProperties.toString());
            setProperties(overrideProperties);
        }


    }

    /**
     *
     * A syntactic sugar ctor that takes only overriding property file list
     *
     * @param overridePropertyFilesList
     */
    public MainProcessPropertiesConfigurer(String[] overridePropertyFilesList) {
        this(overridePropertyFilesList, null );
    }

    /**
     *
     * A syntactic sugar ctor that takes only overriding property object
     *
     * @param overrideProperties
     */
    public MainProcessPropertiesConfigurer(Properties overrideProperties) {
        this(null, overrideProperties);
    }

}
