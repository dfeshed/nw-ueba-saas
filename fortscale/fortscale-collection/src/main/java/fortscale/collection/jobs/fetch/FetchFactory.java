package fortscale.collection.jobs.fetch;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.jobs.fetch.siem.QRadar;
import fortscale.collection.jobs.fetch.siem.Splunk;
import fortscale.domain.fetch.LogRepository;
import fortscale.domain.fetch.SIEMType;
import fortscale.services.LogRepositoryService;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by Amir Keren on 4/4/16.
 */
@DisallowConcurrentExecution
public class FetchFactory extends FortscaleJob {

	private static final String SIEM_ID_PARAM = "siem_id";

	@Autowired
	private LogRepositoryService logRepositoryService;
	@Autowired
	private QRadar qradarFetch;
	@Autowired
	private Splunk splunkFetch;

	private FetchJob fetchJob;
	private String configuredSIEM;

	@Override
	protected void runSteps() throws Exception {
		fetchJob.runSteps();
	}

	@Override
	protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();
		LogRepository logRepository;
		if (map.containsKey(SIEM_ID_PARAM)) {
			logRepository = logRepositoryService.getLogRepositoryFromDatabase(jobDataMapExtension.
					getJobDataMapStringValue(map, SIEM_ID_PARAM));
			if (logRepository == null) {
				throw new JobExecutionException("No log repository configuration found");
			}
		} else {
			List<LogRepository> logRepositories = logRepositoryService.getLogRepositoriesFromDatabase();
			if (CollectionUtils.isNotEmpty(logRepositories)) {
				logRepository = logRepositories.get(0);
			} else {
				throw new JobExecutionException("No log repository configuration found");
			}
		}
		configuredSIEM = logRepository.getType();
		SIEMType type;
		try {
			type = SIEMType.valueOf(configuredSIEM.toUpperCase());
		} catch (Exception ex) {
			throw new JobExecutionException("SIEM " + configuredSIEM + " is not supported");
		}
		switch (type) {
			case SPLUNK: fetchJob = splunkFetch; break;
			case QRADAR: fetchJob = qradarFetch; break;
		}
		fetchJob.getJobParameters(map, jobDataMapExtension, logRepository);
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return true; }

}