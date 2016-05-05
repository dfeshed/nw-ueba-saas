package fortscale.collection.jobs.fetch;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.jobs.fetch.siem.QRadar;
import fortscale.collection.jobs.fetch.siem.Splunk;
import fortscale.services.impl.SpringService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by Amir Keren on 4/4/16.
 */
@DisallowConcurrentExecution
public class FetchFactory extends FortscaleJob {

	private static final String CONTEXT_PATH = "classpath*:META-INF/spring/collection-context.xml";

	@Value("${fortscale.collection.siem:splunk}")
	protected String defaultSIEM;

	private FetchJob fetchJob;
	private String configuredSIEM;

	@Override
	protected void runSteps() throws Exception {
		fetchJob.runSteps();
	}

	@Override
	protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		SpringService.init(CONTEXT_PATH);
		SpringService springService = SpringService.getInstance();
		JobDataMap map = context.getMergedJobDataMap();
		configuredSIEM = jobDataMapExtension.getJobDataMapStringValue(map, "siem", defaultSIEM);
		switch (configuredSIEM.toLowerCase()) {
			case Splunk.SIEM_NAME: fetchJob = springService.resolve(Splunk.class); break;
			case QRadar.SIEM_NAME: fetchJob = springService.resolve(QRadar.class); break;
			default: throw new JobExecutionException("SIEM " + configuredSIEM + " is not supported");
		}
		fetchJob.getJobParameters(map, configuredSIEM);
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return true; }

}