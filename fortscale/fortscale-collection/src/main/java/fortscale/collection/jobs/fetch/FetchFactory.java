package fortscale.collection.jobs.fetch;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.jobs.fetch.siem.QRadar;
import fortscale.collection.jobs.fetch.siem.Splunk;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.services.ApplicationConfigurationService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Amir Keren on 4/4/16.
 */
@DisallowConcurrentExecution
public class FetchFactory extends FortscaleJob {

	private static final String SIEM_TYPE_KEY = "system.siem.type";
	private static final String DEFAULT_SIEM = "splunk";

	@Autowired
	private ApplicationConfigurationService applicationConfigurationService;
	@Autowired
	private QRadar qradar;
	@Autowired
	private Splunk splunk;

	private FetchJob fetchJob;
	private String configuredSIEM;

	@Override
	protected void runSteps() throws Exception {
		fetchJob.runSteps();
	}

	@Override
	protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();
		ApplicationConfiguration applicationConfiguration = applicationConfigurationService.
				getApplicationConfiguration(SIEM_TYPE_KEY);
		if (applicationConfiguration != null) {
			configuredSIEM = applicationConfiguration.getValue();
		} else {
			configuredSIEM = DEFAULT_SIEM;
		}
		switch (configuredSIEM.toLowerCase()) {
			case Splunk.SIEM_NAME: fetchJob = splunk; break;
			case QRadar.SIEM_NAME: fetchJob = qradar; break;
			default: throw new JobExecutionException("SIEM " + configuredSIEM + " is not supported");
		}
		fetchJob.getJobParameters(map, jobDataMapExtension, configuredSIEM);
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return true; }

}