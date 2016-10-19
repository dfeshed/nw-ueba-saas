package fortscale.collection;

import fortscale.collection.configuration.CollectionPropertiesResolver;
import fortscale.collection.morphlines.MorphlinesItemsProcessor;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

/**
 * static helper extensions method on top of JobDataMap
 */
@Component
public class JobDataMapExtension implements ApplicationContextAware{

	private static Logger logger = LoggerFactory.getLogger(JobDataMapExtension.class);
	
	@Autowired
	CollectionPropertiesResolver propertiesResolver;
	
	@Autowired
	protected ResourceLoader resourceLoader;

	private ApplicationContext applicationContext;
	
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

	public Instant getJobDataMapInstantValue(JobDataMap map,String key)  throws JobExecutionException
	{
		String jobDataMapStringValue = getJobDataMapStringValue(map, key);

		return Instant.parse(jobDataMapStringValue);
	}

	/**
	 * get all job data map keys that start with the given prefix.
	 */
	public Iterable<String> getJobDataMapKeysStartingWith(JobDataMap map, String keyPrefix) {
		List<String> keys = new LinkedList<>();
		for (String key : map.keySet()) {
			if (key.startsWith(keyPrefix))
				keys.add(key);
		}
		return keys;
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

	public List<String> getJobDataMapListOfStringsValue(JobDataMap map, String key, String delimiter) throws JobExecutionException {
		String errorMsg;

		if (!map.containsKey(key)) {
			errorMsg = String.format("JobDataMap does not contain key %s", key);
			logger.error(errorMsg);
			throw new JobExecutionException(errorMsg);
		}

		String joinedValues = map.getString(key);
		if (StringUtils.isEmpty(joinedValues)) {
			errorMsg = String.format("JobDataMap key %s does not have a value", key);
			logger.error(errorMsg);
			throw new JobExecutionException(errorMsg);
		}

		if (delimiter == null) {
			errorMsg = "delimiter cannot be null";
			logger.error(errorMsg);
			throw new JobExecutionException(errorMsg);
		}

		List<String> values = new LinkedList<>();
		for (String value : StringUtils.split(joinedValues, delimiter)) {
			values.add(getEnvPropertyValue(value, key));
		}

		return values;
	}

	public boolean getJobDataMapBooleanValue(JobDataMap map, String key, boolean defaultValue) {
		if (!map.containsKey(key))
			return defaultValue;
		
		try {
			String valueStr = map.getString(key);
			valueStr = getEnvPropertyValue(valueStr, key);
			
			// convert string to boolean
			return Boolean.parseBoolean(valueStr);
		} catch (ClassCastException e) {
			logger.warn("value for key {} in job data map is not boolean", key);
			return defaultValue;
		}
	}
	
	private String getEnvPropertyValue(String value, String mapKey) {
		try{
			return propertiesResolver.getEnvPropertyValue(value);
		} catch(Exception e){
			logger.error(String.format("got an exception for the following job parameter: key: %s, value: %s", mapKey, value), e);
			throw new RuntimeException(String.format("got an exception for the following job parameter: key: %s, value: %s", mapKey, value), e);
		}
	}
	
	/**
	 * get the job data map int value, throw exception if the key does not exists of the value is empty
	 * @param map the merged job data map
	 * @param key the field key
	 * @return the field value
	 * @throws JobExecutionException 
	 */
	public int getJobDataMapIntValue(JobDataMap map, String key) throws JobExecutionException {
		if (!map.containsKey(key)) {
			logger.error("job data map does not contain key {}", key);
			throw new JobExecutionException("JobDataMap does not contains key " + key);
		}
		
		String value = map.getString(key);
		if (StringUtils.isEmpty(value)) {
			logger.error("JobDataMap key {} does not have value", key);
			throw new JobExecutionException("JobDataMap key " + key + " does not have value");
		}
		
		value = getEnvPropertyValue(value, key);
		try {
			return Integer.parseInt(value);
		} catch (ClassCastException e) {
			logger.error("error getting int value for key {}", key);
			throw new JobExecutionException("error getting int value for key " + key, e);
		}
	}

	public boolean isJobDataMapContainKey(JobDataMap map, String key){
		return map.containsKey(key);
	}

	/**
	 * get the job data map int value, if the key does not exists or the value is empty return the default value.
	 * @param map the merged job data map
	 * @param key the field key
	 * @param defaultValue the default value
	 * @return the field value
	 * @throws JobExecutionException 
	 */
	public Integer getJobDataMapIntValue(JobDataMap map, String key, Integer defaultValue) throws JobExecutionException {
		if (!map.containsKey(key)) {
			return defaultValue;
		}
		
		String value = map.getString(key);
		if (StringUtils.isEmpty(value)) {
			return defaultValue;
		}
		
		value = getEnvPropertyValue(value, key);
		try {
			return Integer.parseInt(value);
		} catch (ClassCastException e) {
			logger.error("error getting int value for key {}", key);
			throw new JobExecutionException("error getting int value for key " + key, e);
		}
	}
	
	/**
	 * get the job data map long value, throw exception if the key does not exists or the value is empty
	 * @param map the merged job data map
	 * @param key the field key
	 * @return the field value
	 * @throws JobExecutionException 
	 */
	public long getJobDataMapLongValue(JobDataMap map, String key) throws JobExecutionException {
		if (!map.containsKey(key)) {
			logger.error("job data map does not contain key {}", key);
			throw new JobExecutionException("JobDataMap does not contains key " + key);
		}
		
		String value = map.getString(key);
		if (StringUtils.isEmpty(value)) {
			logger.error("JobDataMap key {} does not have value", key);
			throw new JobExecutionException("JobDataMap key " + key + " does not have value");
		}
		
		value = getEnvPropertyValue(value, key);
		try {
			return Long.parseLong(value);
		} catch (ClassCastException e) {
			logger.error("error getting long value for key {}", key);
			throw new JobExecutionException("error getting long value for key " + key, e);
		}
	}
	
	/**
	 * get the job data map long value, if the key does not exists or the value is empty return the default value
	 * @param map the merged job data map
	 * @param key the field key
	 * @param defaultValue the default value
	 * @return the field value
	 * @throws JobExecutionException 
	 */
	public Long getJobDataMapLongValue(JobDataMap map, String key, Long defaultValue) throws JobExecutionException {
		if (!map.containsKey(key)) {
			return defaultValue;
		}
		
		String value = map.getString(key);
		if (StringUtils.isEmpty(value)) {
			return defaultValue;
		}
		
		value = getEnvPropertyValue(value, key);
		try {
			return Long.parseLong(value);
		} catch (ClassCastException e) {
			logger.error("error getting long value for key {}", key);
			throw new JobExecutionException("error getting long value for key " + key, e);
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
		
		return resourceLoader.getResource(value);
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

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException{
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getSpringApplicationContext(){
		return  this.applicationContext;
	}


}
