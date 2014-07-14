package fortscale.streaming;

import java.util.LinkedList;
import java.util.List;

import org.apache.samza.config.Config;
import org.apache.samza.job.StreamJob;
import org.apache.samza.job.local.LocalJobFactory;
import org.apache.samza.job.local.ThreadJob;

/**
 * Samza job factory that supports graceful shutdown
 */
public class GracefulShutdownLocalJobFactory extends LocalJobFactory {

	private List<ThreadJob> jobs = new LinkedList<ThreadJob>();
	
	public GracefulShutdownLocalJobFactory() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// go over jobs and kill them all
				for (ThreadJob job : jobs) 
					job.kill();
			}
		});
	}
	
	@Override
	public StreamJob getJob(Config config) {
		StreamJob job = super.getJob(config);
		if (job instanceof ThreadJob) {
			ThreadJob threadJob = (ThreadJob)job;
			jobs.add(threadJob);
		} else {
			throw new RuntimeException("job " + config.get("job.name") + " is not a ThreadJob");
		}
		
		return job;
	}

}
