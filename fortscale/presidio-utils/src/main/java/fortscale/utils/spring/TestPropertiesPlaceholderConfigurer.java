package fortscale.utils.spring;

import fortscale.utils.logging.Logger;

import java.util.Properties;


/**
 * StandardProcessPropertiesPlaceholderConfigurer class is a configurer bean class that handles the process properties overrides.
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
 * @Bean public static StandardProcessPropertiesPlaceholderConfigurer mainProcessPropertiesConfigurer() {
 * <p>
 * String[] overridingFileList = {"weakest-overriding.properties", "medium-overriding.properties"};
 * <p>
 * Properties properties = new Properties();
 * properties.put("foo.goo,"strongest-override");
 * properties.put("foo.goo2,"strongest-override2");
 * <p>
 * StandardProcessPropertiesPlaceholderConfigurer configurer = new StandardProcessPropertiesPlaceholderConfigurer(overridingFileList, properties);
 * <p>
 * return configurer;
 * }
 * <p>
 * Created by gaashh &barak on 5/1/16.
 */
public class TestPropertiesPlaceholderConfigurer extends GenericPropertiesPlaceholderConfigurer {

    private static final Logger logger = Logger.getLogger(TestPropertiesPlaceholderConfigurer.class);

    public TestPropertiesPlaceholderConfigurer(Properties overrideProperties) {
        super(overrideProperties);
    }


}
