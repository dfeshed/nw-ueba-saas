package fortscale.collection.jobs;

import org.quartz.DisallowConcurrentExecution;

/**
 * Scheduler job to process files from folder and output them into hadoop 
 * after transformation
 */
@DisallowConcurrentExecution
public class SSHEventsProcessJob extends AbstractEventProcessJob {

	public SSHEventsProcessJob() {
		super("ETL", "SSH");
	}
}
