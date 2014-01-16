package fortscale.collection;

import java.io.IOException;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import fortscale.collection.morphlines.MorphlinesItemsProcessor;

/**
 * static helper extensions method on top of JobDataMap
 */
public class JobDataMapExtension {

	private static Logger logger = LoggerFactory.getLogger(JobDataMapExtension.class);
	
	/**
	 * get the job data map string value, throw exception if the key does not exists of the value is empty
	 * @param map the merged job data map
	 * @param key the field key
	 * @return the field value
	 * @throws JobExecutionException 
	 */
	public static String getJobDataMapStringValue(JobDataMap map, String key) throws JobExecutionException {
		if (!map.containsKey(key)) {
			logger.error("job data map does not contain key {}", key);
			throw new JobExecutionException("JobDataMap does not contains key " + key);
		}
		
		String value = map.getString(key);
		if (value==null || value.length()==0) {
			logger.error("JobDataMap key {} does not have value", key);
			throw new JobExecutionException("JobDataMap key " + key + " does not have value");
		}
		return value;
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
	 * get an instance of MorphlinesItemsProcessor loaded with the morphlines config file 
	 * specified by the configuration key
	 */
	public static MorphlinesItemsProcessor getMorphlinesItemsProcessor(ResourceLoader resourceLoader, JobDataMap map, String key) throws JobExecutionException {
		String filename = "";
		try {
			filename = getJobDataMapStringValue(map, key);
			Resource morphlineConf = resourceLoader.getResource(filename);
			return new MorphlinesItemsProcessor(morphlineConf);
		} catch (IOException e) {
			logger.error("error loading morphline processor for " + filename, e);
			throw new JobExecutionException("error loading morphline processo for " + filename, e);
		}
	}
	
}
