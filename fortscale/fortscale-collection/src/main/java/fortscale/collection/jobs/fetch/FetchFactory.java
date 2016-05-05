package fortscale.collection.jobs.fetch;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.jobs.fetch.siem.QRadar;
import fortscale.collection.jobs.fetch.siem.Splunk;
import fortscale.services.impl.SpringService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by Amir Keren on 4/4/16.
 */
@DisallowConcurrentExecution
public class FetchFactory extends FortscaleJob {

	private static final String CONTEXT_PATH = "classpath*:META-INF/spring/collection-context.xml";

	private FetchJob fetchJob;

	@Value("${fortscale.collection.siem}")
	protected String configuredSIEM;

	@Override
	protected void runSteps() throws Exception {
		fetchJob.runSteps();
	}

	/**
	 *
	 * This method gets the specific job parameters
	 *
	 * @param context
	 * @throws JobExecutionException
	 */
	@Override
	protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		SpringService.init(CONTEXT_PATH);
		SpringService springService = SpringService.getInstance();
		switch (configuredSIEM.toLowerCase()) {
			case Splunk.SIEM_NAME: fetchJob = springService.resolve(Splunk.class); break;
			case QRadar.SIEM_NAME: fetchJob = springService.resolve(QRadar.class); break;
			default: throw new JobExecutionException("SIEM " + configuredSIEM + " is not supported");
		}
		fetchJob.getJobParameters(context, configuredSIEM);
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return true; }

}