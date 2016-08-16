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
		List<LogRepository> logRepositories = logRepositoryService.getLogRepositoriesFromDatabase();
		if (CollectionUtils.isNotEmpty(logRepositories)) {
			//TODO - currently only supports single log repository
			configuredSIEM = logRepositories.get(0).getType();
		} else {
			throw new JobExecutionException("No log repository configuration found");
		}
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
		//TODO - currently only supports single log repository
		fetchJob.getJobParameters(map, jobDataMapExtension, logRepositories.get(0));
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return true; }

}