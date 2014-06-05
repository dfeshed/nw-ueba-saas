package fortscale.collection.jobs;


import java.io.IOException;

import org.kitesdk.morphline.api.Record;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.JobDataMapExtension;
import fortscale.collection.morphlines.RecordToBeanItemConverter;
import fortscale.domain.events.DhcpEvent;
import fortscale.domain.events.dao.DhcpEventRepository;

/**
 * Scheduled job to process dhcp events into mongodb
 */
@DisallowConcurrentExecution
public class DHCPEventsProcessJob extends EventProcessJob {

	private static Logger logger = LoggerFactory.getLogger(DHCPEventsProcessJob.class);
	
	@Autowired
	private DhcpEventRepository dhcpEventRepository;
	
	@Autowired
	private JobDataMapExtension jobDataMapExtension;
	
	
	
	private RecordToBeanItemConverter<DhcpEvent> recordToBeanItemConverter = new RecordToBeanItemConverter<DhcpEvent>(new DhcpEvent());
	
	@Override
	protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();

		// get parameters values from the job data map
		filesFilter = jobDataMapExtension.getJobDataMapStringValue(map, "filesFilter");
		
		// build record to items processor
		morphline = jobDataMapExtension.getMorphlinesItemsProcessor(map, "morphlineFile");
	}
	
	@Override
	protected boolean processLine(String line) throws IOException {
		// process each line
		Record record = morphline.process(line);
		
		// write data to mongodb
		if (record==null) 
			return false;
		
		try {
			DhcpEvent dhcpEvent = new DhcpEvent();
			recordToBeanItemConverter.convert(record, dhcpEvent);
			dhcpEventRepository.save(dhcpEvent);
			return true;
		} catch (Exception e) {
			logger.warn(String.format("error writing record %s to mongo", record.toString()));
			return false;
		}			
	}
	
	@Override protected void createOutputAppender() throws JobExecutionException {}
	@Override protected void flushOutputAppender() throws IOException {}
	@Override protected void closeOutputAppender() throws JobExecutionException {}
	@Override protected void refreshImpala() throws JobExecutionException {}
	@Override protected void initializeStreamingAppender() throws JobExecutionException {}
	@Override protected void streamMessage(String message) throws IOException {}
	@Override protected void closeStreamingAppender() throws JobExecutionException {}
}
