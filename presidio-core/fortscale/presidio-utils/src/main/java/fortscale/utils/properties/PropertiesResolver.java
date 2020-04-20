package fortscale.utils.properties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.core.io.support.ResourcePropertySource;

import fortscale.utils.logging.Logger;

public class PropertiesResolver {
	private static Logger logger = Logger.getLogger(PropertiesResolver.class);
	
	private static final String ENV_PROP_PERFIX = "${";
	private static final String ENV_PROP_SUFFIX = "}";

	
	List<ResourcePropertySource> propertiesResources = new ArrayList<>();
	private Properties properties = new Properties();
	
	
	
	public PropertiesResolver(String... locations){
		init(locations);
	}
		
	public void init(String... locations){
		for(String location: locations){
			try {
				propertiesResources.add(new ResourcePropertySource(location));
			} catch (IOException e) {
				logger.error("failed to load properties", e);
			}
		}
		init();
	}

	public void init(){
		for(ResourcePropertySource resourcePropertySource: propertiesResources){
			for(String key: resourcePropertySource.getPropertyNames()){
				String val = (String)resourcePropertySource.getProperty(key);
				try{
					String envVal = resolvePropertyValue(resourcePropertySource, val);
					properties.put(key, envVal);
				} catch(PropertyNotExistException | IllegalStructuredProperty e){
					logger.error(String.format("got an exception while trying to resolve the value %s of the key %s", val, key), e);
				}
			}
		}
	}
	
	public String getProperty(String key) throws PropertyNotExistException, IllegalStructuredProperty{
		String ret = properties.getProperty(key);
		if (ret==null) {
			logger.error("propery key ({}) does not exist.", ret, key);
			throw new PropertyNotExistException(key);
		}
		return ret;
	}
	
	public String resolvePropertyValue(ResourcePropertySource resourcePropertySource, String value) throws PropertyNotExistException, IllegalStructuredProperty{
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
				if (propVal==null && resourcePropertySource != null) {
					propVal = (String) resourcePropertySource.getProperty(propkey);
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
