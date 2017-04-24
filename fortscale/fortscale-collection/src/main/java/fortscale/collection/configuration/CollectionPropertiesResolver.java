package fortscale.collection.configuration;

import fortscale.utils.logging.Logger;
import fortscale.utils.properties.IllegalStructuredProperty;
import fortscale.utils.properties.PropertyNotExistException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

@Component
public class CollectionPropertiesResolver implements InitializingBean{
	private static Logger logger = Logger.getLogger(CollectionPropertiesResolver.class);
	
	private static final String ENV_PROP_PERFIX = "${";
	private static final String ENV_PROP_SUFFIX = "}";

	@Autowired
	Environment env;
	
	@Value("file:${fortscale.collection.overriding.properties}")
	Resource propertiesResource;
	private ResourcePropertySource resourcePropertySource;
	private Properties properties = new Properties();
	
	
//	public String getProperty(String key){
//		String ret = properties.getProperty(key);
//		if(ret == null){
//			ret = env.getProperty(key);
//		}
//		return ret;
//	}
	
	public ResourcePropertySource loadForscaleCollectionProperties(){
		ResourcePropertySource ret = null;
		try {
			ret = new ResourcePropertySource(propertiesResource);
		} catch (IOException e) {
			logger.error("failed to load properties", e);
		}
		
		
		return ret;
	}

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

	/**
	 * this method returns the resolved value of an env variable (example ${somevalue})
	 * @param value
	 * @return
	 * @throws PropertyNotExistException
	 * @throws IllegalStructuredProperty
	 */
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

	/**
	 * This method will return boolean value from property configuration file
	 * In case of not existing property key or non boolean value it will return as default false
	 * @param key
	 * @return
	 */
	public Boolean getBooleanValue(String key)
	{
		try {

			String valueStr = getEnvPropertyValue(key);

			// convert string to boolean
			return Boolean.parseBoolean(valueStr);
		} catch (ClassCastException e) {
			logger.warn("value for key {} is not boolean return false as default", key);
			return false;
		}
		catch (PropertyNotExistException e) {
			logger.warn("value for key {}  is not exist return false as default", key);
			return false ;
		}
		catch (IllegalStructuredProperty e) {
			logger.warn("structure of value for key {}  is not legal return false as default", key);
			return false ;
		}
	}

	/**
	 * this method returns the resolved value of a regular variable (for an env variable see getEnvPropertyValue)
	 * @param key
	 * @return
	 */
	public String getPropertyValue(String key) {
		String propertyValue = properties.getProperty(key);
		if (propertyValue == null) {
			propertyValue = env.getProperty(key);
		}
		return propertyValue;
	}

}
