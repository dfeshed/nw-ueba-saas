package fortscale.collection.jobs.event.process;

import fortscale.collection.monitoring.ItemContext;
import fortscale.collection.JobDataMapExtension;
import fortscale.collection.morphlines.RecordToBeanItemConverter;
import fortscale.domain.events.IseEvent;
import fortscale.services.ipresolving.IseResolver;
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
public class ISEEventsProcessJob extends EventProcessJob {

    private static Logger logger = LoggerFactory.getLogger(ISEEventsProcessJob.class);

    @Autowired
    private IseResolver iseResolver;

    @Autowired
    private JobDataMapExtension jobDataMapExtension;

    @Autowired
    private StatsService statsService;

    private RecordToBeanItemConverter<IseEvent> recordToBeanItemConverter = new RecordToBeanItemConverter<IseEvent>(
            new IseEvent(),"ise-event-job",statsService);

    @Override
    protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
        JobDataMap map = context.getMergedJobDataMap();

        // get parameters values from the job data map
        filesFilter = jobDataMapExtension.getJobDataMapStringValue(map, "filesFilter");
		timestampField = jobDataMapExtension.getJobDataMapStringValue(map, "timestampField");

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
            IseEvent iseEvent = new IseEvent();
            recordToBeanItemConverter.convert(record, iseEvent);
            iseResolver.addIseEvent(iseEvent);
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
