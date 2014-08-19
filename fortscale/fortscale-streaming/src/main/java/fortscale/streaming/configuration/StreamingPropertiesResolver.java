package fortscale.streaming.configuration;

import fortscale.utils.logging.Logger;
import fortscale.utils.properties.IllegalStructuredProperty;
import fortscale.utils.properties.PropertyNotExistException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by idanp on 8/19/2014.
 * This is a properties resolver for  streaming tasks
 */
public class StreamingPropertiesResolver implements InitializingBean {

    private static Logger logger = Logger.getLogger(StreamingPropertiesResolver.class);

    private static final String ENV_PROP_PERFIX = "${";
    private static final String ENV_PROP_SUFFIX = "}";

    @Autowired
    Environment env;

    @Value("file:${fortscale.config.properties}")
    Resource propertiesResource;
    private ResourcePropertySource resourcePropertySource;
    private Properties properties = new Properties();


    @Override
    public void afterPropertiesSet() throws Exception {
        resourcePropertySource = loadForscaleCollectionProperties();
        for(String key: resourcePropertySource.getPropertyNames()){
            String val = (String)resourcePropertySource.getProperty(key);
            try{
                String envVal = getEnvPropertyValue(val);
                properties.put(key, envVal);
            } catch(PropertyNotExistException | IllegalStructuredProperty e){
                logger.error(String.format("got an exception while trying to resolve the value %s of the key %s", val, key), e);
            }
        }
    }


    private ResourcePropertySource loadForscaleCollectionProperties(){
        ResourcePropertySource ret = null;
        try {
            ret = new ResourcePropertySource(propertiesResource);
        } catch (IOException e) {
            logger.error("failed to load properties", e);
        }


        return ret;
    }

    public String getEnvPropertyValue(String value) throws PropertyNotExistException, IllegalStructuredProperty{
        String ret = value;
        while(ret.contains(ENV_PROP_PERFIX)){
            StringBuilder builder = new StringBuilder();
            int cursor = 0;
            while(cursor < ret.length()){
                int nextStart = ret.indexOf(ENV_PROP_PERFIX, cursor);
                if(nextStart == -1){
                    builder.append(ret.substring(cursor));
                    break;
                } else{
                    if(cursor < nextStart){
                        builder.append(ret.substring(cursor, nextStart));
                        cursor = nextStart;
                    }
                }

                int endIndex = ret.indexOf(ENV_PROP_SUFFIX, cursor);
                if(endIndex == -1){
                    logger.error("mal structured env propery value {}.", ret);
                    throw new IllegalStructuredProperty(ret);
                }
                String propkey = ret.substring(cursor + ENV_PROP_PERFIX.length(),endIndex);
                cursor = endIndex + ENV_PROP_SUFFIX.length();
                String propVal = properties.getProperty(propkey);
                if (propVal==null) {
                    propVal = (String) resourcePropertySource.getProperty(propkey);
                    if(propVal == null){
                        propVal = env.getProperty(propkey);
                    }
                }
                if (propVal==null) {
                    logger.error("({}) contained env propery key ({}) which does not exist.", ret, propkey);
                    throw new PropertyNotExistException(propkey);
                }
                builder.append(propVal);
            }
            ret = builder.toString();
        }
        return ret;
    }


}
