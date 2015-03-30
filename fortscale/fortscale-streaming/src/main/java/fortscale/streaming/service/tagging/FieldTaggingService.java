package fortscale.streaming.service.tagging;

import fortscale.services.cache.CacheHandler;
import net.minidev.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.utils.ConversionUtils.convertToString;

/**
 * Created by idanp on 3/29/2015.
 */
public class FieldTaggingService {



	private CacheHandler<String,String> listOfValuToTag;
	private String filePath;
	private String tagFieldName;
	private String taggingBaesdFieldName;

	private String outPutTopic;
	private String partitionField;

	public static Logger logger = LoggerFactory.getLogger(FieldTaggingService.class);

	/**
	 * This method will initialize the FieldTaggingService, by filing the cache with the list of values
	 * @param filePath - The path to the file with the list ov values to tag based on the taggingBaesdFieldName
	 * @param tagFieldName - The name of the tag field
	 * @param taggingBaesdFieldName - The name of the field that we will use as base to the tagging
	 */
	public FieldTaggingService (String filePath, CacheHandler<String,String> listOfValuToTag,String tagFieldName,String taggingBaesdFieldName,String outPutTopic,String partitionField)
	throws IOException {

		// initialize private properties
		this.listOfValuToTag = listOfValuToTag;
		this.filePath = filePath;
		this.tagFieldName = tagFieldName;
		this.taggingBaesdFieldName = taggingBaesdFieldName;
		this.outPutTopic = outPutTopic;
		this.partitionField =partitionField;


		//clear the current cache
		listOfValuToTag.clear();


		//initialize the cache with the current values
		if (!StringUtils.isEmpty(filePath)) {
			File valuesFromFile = new File(filePath);
			if (valuesFromFile.exists() && valuesFromFile.isFile()) {
				Set<String> setOfValusFromFile = null;
				setOfValusFromFile = new HashSet<String>(FileUtils.readLines(valuesFromFile));

				for (String value : setOfValusFromFile) {
					String strValue = value.toUpperCase();
					this.listOfValuToTag.put(strValue, null);

					}
			}
			else {
				logger.warn(taggingBaesdFieldName+" file not found in path: {}", filePath);
			}

		}
		else {
			logger.info(taggingBaesdFieldName+" file path not configured");
		}






	}

	public void setListOfValuToTag(CacheHandler<String, String> listOfValuToTag) {
		this.listOfValuToTag = listOfValuToTag;
	}

	public String getOutPutTopic() {
		return outPutTopic;
	}



	/**
	 * This method will tag an event based on a value of a given field compare to given cache with value to tag
	 * @param event - The event
	 */
	public JSONObject enrichEvent(JSONObject event)
	{

		checkNotNull(event);
		// Check if the event contain the based for tagging field
		if(event.containsKey(taggingBaesdFieldName))
		{
			String fieldValue = convertToString(event.get(taggingBaesdFieldName));

			// check if need to tag this event
			if (this.listOfValuToTag.containsKey(fieldValue))
			{
				event.put(tagFieldName,true);
			}

			else
			{
				event.put(tagFieldName,false);
			}

		}

		else
			event.put(tagFieldName,false);

		return event;
	}


	/** Get the partition key to use for outgoing message envelope for the given event */
	public Object getPartitionKey(JSONObject event) {

		checkNotNull(event);

		return event.get(partitionField);
	}

	public void close() throws Exception
	{
		this.listOfValuToTag.close();
	}


}
