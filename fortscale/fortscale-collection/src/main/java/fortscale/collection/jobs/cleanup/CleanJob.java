package fortscale.collection.jobs.cleanup;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.services.EvidencesService;
import fortscale.services.Service;
import fortscale.utils.logging.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Amir Keren on 18/09/2015.
 *
 * This task clears indicators after a particular date
 *
 */
public class CleanJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(CleanJob.class);

	@Autowired
	EvidencesService evidencesService;

	private Date startTime;
	private Date endTime;
	private String dataSource;
	private Map<String, Service> serviceMap;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		createServiceMap();
		// get parameters values from the job data map
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		try {
			startTime = sdf.parse(jobDataMapExtension.getJobDataMapStringValue(map, "startTime"));
			endTime = sdf.parse(jobDataMapExtension.getJobDataMapStringValue(map, "endTime"));
		} catch (ParseException ex) {
			logger.error("Bad date format - {}", ex);
			throw new JobExecutionException(ex);
		}
		dataSource = jobDataMapExtension.getJobDataMapStringValue(map, "dataSource");
	}

	@Override
	protected void runSteps() throws Exception {
		startNewStep("Running Clean Job");
		long deletedRecords = serviceMap.get(dataSource).deleteBetween(startTime, endTime);
		logger.info("Deleted {} records", deletedRecords);
		finishStep();
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

	private Map<String, Service> createServiceMap() {
		Map<String, Service> result = new HashMap();
		result.put("evidence", evidencesService);
		return result;
	}

}