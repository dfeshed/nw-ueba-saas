package fortscale.collection.jobs.event.process;

import fortscale.collection.monitoring.ItemContext;
import fortscale.collection.JobDataMapExtension;
import fortscale.collection.morphlines.RecordToBeanItemConverter;
import fortscale.domain.events.PxGridIPEvent;
import fortscale.services.ipresolving.PxGridResolver;
import fortscale.utils.monitoring.stats.StatsService;
import org.kitesdk.morphline.api.Record;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Created by tomerd on 12/05/2015.
 */
public class PxGridEventsProcessJob extends EventProcessJob {

	private static Logger logger = LoggerFactory.getLogger(PxGridEventsProcessJob.class);

	@Autowired
	private PxGridResolver pxGridResolver;

	@Autowired
	private JobDataMapExtension jobDataMapExtension;

	@Autowired
	private StatsService statsService;

	private RecordToBeanItemConverter<PxGridIPEvent> recordToBeanItemConverter = new RecordToBeanItemConverter<PxGridIPEvent>(
			new PxGridIPEvent(),"pxgrid-event-job",statsService);

	@Override
	protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();

		// get parameters values from the job data map
		filesFilter = jobDataMapExtension.getJobDataMapStringValue(map, "filesFilter");

		// build record to items processor
		morphline = jobDataMapExtension.getMorphlinesItemsProcessor(map, "specificMorphlineFile");
	}

	@Override
	protected Record processLine(String line, ItemContext itemContext) throws IOException {
		// process each line
		Record record = morphline.process(line, itemContext);

		// skip records that failed on parsing
		if (record==null) {
			return null;
		}
		try {
			PxGridIPEvent pxGridIPEvent = new PxGridIPEvent();
			recordToBeanItemConverter.convert(record, pxGridIPEvent);
			pxGridResolver.addPxGridEvent(pxGridIPEvent);
			return record;
		} catch (Exception e) {
			logger.warn(String.format("error writing record %s to mongo", record.toString()));
			return null;
		}
	}

	@Override protected void createOutputAppender() throws JobExecutionException {}
	@Override protected void flushOutputAppender() throws IOException {}
	@Override protected void closeOutputAppender() throws JobExecutionException {}
	@Override protected void refreshImpala() throws JobExecutionException {}
	@Override protected void initializeStreamingAppender() throws JobExecutionException {}
	@Override protected void streamMessage(String key, String message) throws IOException {}
	@Override protected void closeStreamingAppender() throws JobExecutionException {}
}
