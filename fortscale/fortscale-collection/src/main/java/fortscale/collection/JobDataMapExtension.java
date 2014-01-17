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
	
	private String getEnvPropertyValue(String value, String mapKey) throws JobExecutionException{
		String ret = value;
		if(value.startsWith("${") && value.endsWith("}")){
			String propkey = value.substring(2,value.length()-1);
			ret = env.getProperty(propkey);
			if (ret==null || ret.length()==0) {
				logger.error("JobDataMap key {} contained env propery value {} which does not exist.", mapKey, value);
				throw new JobExecutionException(String.format("JobDataMap key %s contained env propery value %s which does not exist.", mapKey, value));
			}
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
