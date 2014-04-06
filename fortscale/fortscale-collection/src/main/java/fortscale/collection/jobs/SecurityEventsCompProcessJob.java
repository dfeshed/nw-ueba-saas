package fortscale.collection.jobs;

import java.io.IOException;

import org.kitesdk.morphline.api.Record;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import fortscale.collection.morphlines.MorphlinesItemsProcessor;


@DisallowConcurrentExecution
public class SecurityEventsCompProcessJob extends GenericSecurityEventsJob{
	
	private MorphlinesItemsProcessor loginMorphline;
		
	
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		super.getJobParameters(jobExecutionContext);
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		
		loginMorphline = jobDataMapExtension.getMorphlinesItemsProcessor(map, "morphlineFile4768");
	}

	@Override
	protected Record processLine(String line) throws IOException {
		// process each line
		Record record = super.processLine(line);
		if(record == null){
			return null;
		}
		
		return loginMorphline.process(record);		
	}
}
