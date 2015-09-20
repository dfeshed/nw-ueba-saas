package fortscale.collection.jobs.cleanup;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.fetch.FetchConfiguration;
import fortscale.domain.fetch.FetchConfigurationRepository;
import fortscale.services.EvidencesService;
import fortscale.services.Service;
import fortscale.streaming.service.SpringService;
import fortscale.utils.logging.Logger;
import org.joda.time.DateTime;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Amir Keren on 18/09/2015.
 *
 * This task clears indicators after a particular date
 *
 */
public class CleanIndicatorsJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(CleanIndicatorsJob.class);

	private Date startTime;
	private Date endTime;
	private String dataSource;
	private Map<String, Service> serviceMap;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		try {
			createServiceMap(jobDataMapExtension.getJobDataMapStringValue(map, "serviceMap"));
		} catch (ClassNotFoundException ex) {
			logger.error("Bad service map - {}", ex);
			throw new JobExecutionException(ex);
		}
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

	private Map<String, Service> createServiceMap(String serviceMapString) throws ClassNotFoundException {
		Map<String, Service> result = new HashMap();
		for (String pair: serviceMapString.split(",")) {
			String dataSource = pair.split(":")[0];
			String className = pair.split(":")[1];
			Service service = (Service)SpringService.getInstance().resolve(Class.forName(className));
			result.put(dataSource, service);
		}
		return result;
	}


}