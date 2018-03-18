package fortscale.utils.spring;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by rans on 25/10/15.
 * This class overrides Spring's PropertyPlaceholderConfigurer
 * By this class we can access any property programatically from our code using the getProperty() method.
 */
public class SpringPropertiesUtil extends PropertyPlaceholderConfigurer {

    /**
     * Map of properties that can be available programatically from anywhere in the code
     */
    private static Map<String, String> propertiesMap;
    // Default as in PropertyPlaceholderConfigurer
    private int springSystemPropertiesMode = SYSTEM_PROPERTIES_MODE_FALLBACK;

    @Override
    public void setSystemPropertiesMode(int systemPropertiesMode) {
        super.setSystemPropertiesMode(systemPropertiesMode);
        springSystemPropertiesMode = systemPropertiesMode;
    }

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) throws BeansException {
        super.processProperties(beanFactory, props);

        propertiesMap = new HashMap<String, String>();
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String valueStr = resolvePlaceholder(keyStr, props, springSystemPropertiesMode);
            propertiesMap.put(keyStr, valueStr);
        }
    }

    /**
     * method used to retrieve value of property from the code
     * @param name
     * @return value of the prperty
     */
    public static String getProperty(String name) {
        if (propertiesMap == null){
            return null;
        }

        return propertiesMap.get(name);
    }

    /**
     * method used to retrieve all attributes with specific prefix
     * @param prefix
     * @return value of the prperty
     */
    public static Map<String, String> getPropertyMapByPrefix(String prefix) {
        Map<String, String> subSet = new HashMap<>();
        for (Map.Entry<String, String> prop : propertiesMap.entrySet()){
            if (StringUtils.startsWith(prop.getKey(), prefix)){
                subSet.put(prop.getKey(), prop.getValue());
            }
        }
        return subSet;
    }

}
