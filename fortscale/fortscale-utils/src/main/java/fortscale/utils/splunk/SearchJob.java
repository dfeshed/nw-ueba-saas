package fortscale.utils.splunk;

import com.splunk.Job;
import com.splunk.Service;
import fortscale.utils.logging.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public abstract class SearchJob {
	private static final Logger logger = Logger.getLogger(SearchJob.class);
	
	private boolean isContainTime = false;
	
	
	
	protected abstract Job runJob(Service service, String earliestTimeCursor, String latestTimeCursor) throws Exception;
	protected abstract int getDispatchMaxCount(Service service) throws Exception;

	public Job run(Service service, String earliestTimeCursor, String latestTimeCursor) throws Exception{
		Job ret = runJob(service, earliestTimeCursor, latestTimeCursor);
		if (ret == null) {
			return null;
		}
		try {
			// Wait for the job to finish
			int i = 1;
			while (!ret.isDone()) {

				// sleep one second
				Thread.sleep(1000);

				// print progress every 2 minutes
				if (i == 0) {
					logger.info("search progress: {}", ret.getDoneProgress() * 100);
				}
				i = (i + 1) % 120;

				// check if done/failed
				if (ret.isReady() && ret.isFailed()) {
					break;
				}
			}
		}
			catch (Exception e){
				//cancel splunk job - in the case of any problem, (mainly for timeout interrupts
				ret.cancel();
			}
		if(ret.isFailed()){
			handleJobFailure(ret);
		}
		
		return ret;
	}
	
	private void handleJobFailure(Job job) throws Exception{
		logger.error("the job failed. The unique search identifier is {}.", job.getSid());
		logger.error("Following are the error messages from the search log.");
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new InputStreamReader(job.getSearchLog(), "UTF-8"));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				if(line.contains("ERROR")){
					logger.info(line);
				}
		    }
		} finally{
			if(reader != null){
				reader.close();
			}
		}
		throw new Exception("the job failed.");
	}

	public boolean isContainTime() {
		return isContainTime;
	}

	public void setContainTime(boolean isContainTime) {
		this.isContainTime = isContainTime;
	}
}
