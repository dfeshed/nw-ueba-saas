package fortscale.collection;

import java.io.IOException;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import fortscale.collection.morphlines.MorphlinesItemsProcessor;

/**
 * static helper extensions method on top of JobDataMap
 */
@Component
public class JobDataMapExtension {

	private static Logger logger = LoggerFactory.getLogger(JobDataMapExtension.class);
	
	private static final String ENV_PROP_PERFIX = "${";
	private static final String ENV_PROP_SUFFIX = "}";
	
	@Autowired
	Environment env;
	
	@Autowired
	protected ResourceLoader resourceLoader;
	
	/**
	 * get the job data map string value, throw exception if the key does not exists of the value is empty
	 * @param map the merged job data map
	 * @param key the field key
	 * @return the field value
	 * @throws JobExecutionException 
	 */
	public String getJobDataMapStringValue(JobDataMap map, String key) throws JobExecutionException {
		if (!map.containsKey(key)) {
			logger.error("job data map does not contain key {}", key);
			throw new JobExecutionException("JobDataMap does not contains key " + key);
		}
		
		String value = map.getString(key);
		if (value==null || value.length()==0) {
			logger.error("JobDataMap key {} does not have value", key);
			throw new JobExecutionException("JobDataMap key " + key + " does not have value");
		}
		
		value = getEnvPropertyValue(value, key);
		
		return value;
	}
	
	public String getJobDataMapStringValue(JobDataMap map, String key, String defaultValue) throws JobExecutionException {
		if (!map.containsKey(key))
			return defaultValue;
		
		String value = map.getString(key);
		if (value==null || value.length()==0) {
			return defaultValue;
		}
		
		value = getEnvPropertyValue(value, key);
		return value;
	}
	
	public boolean getJobDataMapBooleanValue(JobDataMap map, String key, boolean defaultValue) {
		if (!map.containsKey(key))
			return defaultValue;
		
		try {
			boolean value = map.getBoolean(key);
			return value;
		} catch (ClassCastException e) {
			logger.warn("value for key {} in job data map is not boolean", key);
			return defaultValue;
		}
	}
	
	private String getEnvPropertyValue(String value, String mapKey) throws JobExecutionException {
		String ret = value;
		if(value.contains(ENV_PROP_PERFIX)){
			StringBuilder builder = new StringBuilder();
			int cursor = 0;
			while(cursor < value.length()){
				int nextStart = value.indexOf(ENV_PROP_PERFIX, cursor);
				if(nextStart == -1){
					builder.append(value.substring(cursor));
					break;
				} else{
					if(cursor < nextStart){
						builder.append(value.substring(cursor, nextStart));
						cursor = nextStart;
					}
				}
				
				int endIndex = value.indexOf(ENV_PROP_SUFFIX, cursor);
				if(endIndex == -1){
					logger.error("JobDataMap key {} contained mal structured env propery value {}.", mapKey, value);
					throw new JobExecutionException(String.format("JobDataMap key %s contained mal structured env propery value {}.", mapKey, value));
				}
				String propkey = value.substring(cursor + ENV_PROP_PERFIX.length(),endIndex);
				cursor = endIndex + ENV_PROP_SUFFIX.length();
				String propVal = env.getProperty(propkey);
				if (propVal==null) {
					logger.error("JobDataMap key ({}) contained the value ({}) which contained env propery key ({}) which does not exist.", mapKey, value, propkey);
					throw new JobExecutionException(String.format("JobDataMap key (%s) contained the value (%s) which contained env propery key (%s) which does not exist.", mapKey, value, propkey));
				}
				builder.append(propVal);
			}
			ret = builder.toString();
		}
		return ret;
	}
	
	/**
	 * get the job data map int value, throw exception if the key does not exists of the value is empty
	 * @param map the merged job data map
	 * @param key the field key
	 * @return the field value
	 * @throws JobExecutionException 
	 */
	public static int getJobDataMapIntValue(JobDataMap map, String key) throws JobExecutionException {
		if (!map.containsKey(key)) {
			logger.error("job data map does not contain key {}", key);
			throw new JobExecutionException("JobDataMap does not contains key " + key);
		}

		try {
			return map.getInt(key);
		} catch (ClassCastException e) {
			logger.error("error getting int value for key {}", key);
			throw new JobExecutionException("error getting int value for key " + key, e);
		}
	}
	
	/**
	 * get the job data map resource value, throw exception if the key does not exists or the value is empty or not a resource
	 * @param map the merged job data map
	 * @param key the field key
	 * @return the field value
	 * @throws JobExecutionException 
	 */
	public Resource getJobDataMapResourceValue(JobDataMap map, String key) throws JobExecutionException {
		if (!map.containsKey(key)) {
			logger.error("job data map does not contain key {}", key);
			throw new JobExecutionException("JobDataMap does not contains key " + key);
		}
		
		String value = map.getString(key);
		if (value==null || value.length()==0) {
			logger.error("JobDataMap key {} does not have value", key);
			throw new JobExecutionException("JobDataMap key " + key + " does not have value");
		}
		
		value = getEnvPropertyValue(value, key);
		
		Resource resource = resourceLoader.getResource(value);
		
		return resource;
	}
	
	/**
	 * get an instance of MorphlinesItemsProcessor loaded with the morphlines config file 
	 * specified by the configuration key
	 */
	public MorphlinesItemsProcessor getMorphlinesItemsProcessor(JobDataMap map, String key) throws JobExecutionException {
		String filename = "";
		try {
			Resource morphlineConf = getJobDataMapResourceValue(map, key);
			return new MorphlinesItemsProcessor(morphlineConf);
		} catch (IOException e) {
			logger.error("error loading morphline processor for " + filename, e);
			throw new JobExecutionException("error loading morphline processo for " + filename, e);
		}
	}
	
}
