package fortscale.streaming.task;

import static fortscale.utils.ConversionUtils.convertToDouble;
import static fortscale.utils.ConversionUtils.convertToInteger;
import static fortscale.utils.ConversionUtils.convertToString;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import net.minidev.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.task.TaskContext;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.ObjectMapper;

import fortscale.utils.impala.ImpalaDateTime;
import fortscale.utils.logging.Logger;

public class AMTSessionsModelStreamTask extends EventsPrevalenceModelStreamTask {
	private static Logger logger = Logger.getLogger(AMTSessionsModelStreamTask.class);

	protected String hostPattern;
	protected String hostReplacement;
	protected Boolean sourceIpInsteadEmptyHost;

	protected NormalizeFieldList normalizeFieldList;
	
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		super.wrappedInit(config, context);
		internalInit(config);
	}

 /*
  *
  * inner method out from the wrappedInit for unit testing purpose
  * since can't mock the super method and in order to allow it's functionality the mock of the config arg will become very complex
  * and also involve loading the context and creating integration with mongo.
  *
  */
	protected void internalInit(Config config) throws Exception{
		// get the hostname normalization regex and replacement from configuration if it exists
		hostPattern = config.get("fortscale.host.normalization.pattern", "");
		hostReplacement = config.get("fortscale.host.normalization.replacement","");
		sourceIpInsteadEmptyHost = config.getBoolean("fortscale.sourceIp.inCase.EmptyHost",false);

		String normalizeFieldsJSON = config.get("fortscale.fields.normalize.list","");

		ObjectMapper mapper = new ObjectMapper();
		normalizeFieldList = mapper.readValue(normalizeFieldsJSON, NormalizeFieldList.class);
	}



	@Override
	protected boolean acceptMessage(JSONObject message) {
		
		

		// normalize hostname according to configuration
		
		
		//Adding start time epoch in order to use DailyTimeModel
		DateTime startTime = null;
		try {
			String startTimeStr = convertToString(message.get("start_time"));
			if(startTimeStr != null){
				startTime = ImpalaDateTime.parseTimeDateToDateTime(startTimeStr);
			} else{
				logger.error("start_time field doesn't exist in the event.");
			}
		} catch (Exception e) {
			logger.error("Got an exception while extracting the start_time value from the event and parsing it to DateTime.");
		}
		if(startTime != null){
			message.put("start_time_epoch", startTime.getMillis());
		}


		//initialize the normalized_amt_host
		message.put("normalized_amt_host", hostname); // this field is scored

        //in case that the hostname is empty
        if(StringUtils.isEmpty(hostname))
        {
			//we want to use the source for the UI
            hostname = convertToString(message.get(("source_ip")));
			message.put("amt_host", hostname); // this field is saved to impala

			//if the source ip instead hot name flag is turn on we want to model based on source ip
			if (sourceIpInsteadEmptyHost)
				message.put("normalized_amt_host", hostname);

        }

		


		
		
		return true;
	}

	// Setters

	public void setHostPattern(String hostPattern) {
		this.hostPattern = hostPattern;
	}

	public void setHostReplacement(String hostReplacement) {
		this.hostReplacement = hostReplacement;
	}

	public void setSourceIpInsteadEmptyHost(Boolean sourceIpInsteadEmptyHost) {
		this.sourceIpInsteadEmptyHost = sourceIpInsteadEmptyHost;
	}

	public void setNormalizeFieldList(NormalizeFieldList normalizeFieldList) {
		this.normalizeFieldList = normalizeFieldList;
	}

	/*
 	 *
 	 * This class use for representing a list of NormalizeField
 	 * the class is only for internal use, for deserialize the configuration value
 	 *
 	 */
	protected static final class NormalizeFieldList{

		private List<NormalizeField> normalizeFields = new ArrayList<NormalizeField>();

		public List<NormalizeField> getNormalizeFields() {
			return normalizeFields;
		}

		public void setNormalizeFields(List<NormalizeField> normalizeFields) {
			this.normalizeFields = normalizeFields;
		}

		@Override public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			NormalizeFieldList that = (NormalizeFieldList) o;

			if (normalizeFields != null ? !normalizeFields.equals(that.normalizeFields) : that.normalizeFields != null)
				return false;

			return true;
		}

		@Override public int hashCode() {
			return normalizeFields != null ? normalizeFields.hashCode() : 0;
		}
	}

	/*
 	 *
 	 * This class use for representing the fields names and conditions needed for normalizing field value according to the session duration
 	 * the class is only for internal use, for deserialize the configuration value
 	 *
 	 */
	protected static final class NormalizeField{
		private int durationAdditionInMin;
		private String  originalFieldName;
		private String normalizedFieldName;

		public NormalizeField() {
		}

		public NormalizeField(int durationAdditionInMin, String originalFieldName, String normalizedFieldName) {
			this.durationAdditionInMin = durationAdditionInMin;
			this.originalFieldName = originalFieldName;
			this.normalizedFieldName = normalizedFieldName;
		}

		public int getDurationAdditionInMin() {
			return durationAdditionInMin;
		}

		public void setDurationAdditionInMin(int durationAdditionInMin) {
			this.durationAdditionInMin = durationAdditionInMin;
		}

		public String getOriginalFieldName() {
			return originalFieldName;
		}

		public void setOriginalFieldName(String originalFieldName) {
			this.originalFieldName = originalFieldName;
		}

		public String getNormalizedFieldName() {
			return normalizedFieldName;
		}

		public void setNormalizedFieldName(String normalizedFieldName) {
			this.normalizedFieldName = normalizedFieldName;
		}

		@Override public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			NormalizeField that = (NormalizeField) o;

			if (durationAdditionInMin != that.durationAdditionInMin)
				return false;
			if (normalizedFieldName != null ? !normalizedFieldName.equals(that.normalizedFieldName) : that.normalizedFieldName != null)
				return false;
			if (originalFieldName != null ? !originalFieldName.equals(that.originalFieldName) : that.originalFieldName != null)
				return false;

			return true;
		}

		@Override public int hashCode() {
			int result = durationAdditionInMin;
			result = 31 * result + (originalFieldName != null ? originalFieldName.hashCode() : 0);
			result = 31 * result + (normalizedFieldName != null ? normalizedFieldName.hashCode() : 0);
			return result;
		}
	}
}
