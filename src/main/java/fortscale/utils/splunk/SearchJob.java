package fortscale.utils.splunk;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.splunk.Job;
import com.splunk.Service;

import fortscale.utils.logging.Logger;

public abstract class SearchJob {
	private static final Logger logger = Logger.getLogger(SearchJob.class);
	
	private boolean isContainTime = false;
	
	
	
	protected abstract Job runJob(Service service, String earliestTimeCursor, String latestTimeCursor) throws Exception;
	protected abstract int getDispatchMaxCount(Service service) throws Exception;
	
	public Job run(Service service, String earliestTimeCursor, String latestTimeCursor) throws Exception{
		Job ret = runJob(service, earliestTimeCursor, latestTimeCursor);
		while (ret != null && !ret.isDone()) {
			Thread.sleep(1000);
			
		}
		if(ret != null && ret.isFailed()){
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
