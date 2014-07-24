package fortscale.collection.jobs;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import org.kitesdk.morphline.api.Record;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import fortscale.collection.morphlines.MorphlinesItemsProcessor;

@DisallowConcurrentExecution
public class SecurityEventsComputerJob extends GenericSecurityEventsJob {

	private Map<String, MorphlinesItemsProcessor> morphlineForEventCode = new HashMap<String, MorphlinesItemsProcessor>();
	
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		
		filesFilter = jobDataMapExtension.getJobDataMapStringValue(map, "filesFilter");
		morphline = jobDataMapExtension.getMorphlinesItemsProcessor(map, "morphlineFile");
		
		for (String specificMorphlineKey : jobDataMapExtension.getJobDataMapKeysStartingWith(map, "eventMorphlineFile")) {
			// extract the event code and get the morphline processor
			String eventCode = specificMorphlineKey.substring(18);
			MorphlinesItemsProcessor processor = jobDataMapExtension.getMorphlinesItemsProcessor(map, specificMorphlineKey);
			morphlineForEventCode.put(eventCode, processor);
		}
		
	}
	
	@Override
	protected Record processLine(String line) throws IOException {
		// run the basic morphline to get the event code from the message
		Record record = morphline.process(line); 
		
		// run specific morphline for event code
		if (record!=null) {
			// get the event code
			Object eventCode = record.getFirstValue("eventCode");
			if (eventCode!=null) {
				MorphlinesItemsProcessor processor = morphlineForEventCode.get(eventCode.toString());
				if (processor!=null) {
					record = processor.process(record);
				}
			}
		}
		return record;
	}

}
