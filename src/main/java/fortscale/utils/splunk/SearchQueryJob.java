package fortscale.utils.splunk;

import com.splunk.Job;
import com.splunk.JobArgs;
import com.splunk.Service;

import fortscale.utils.logging.Logger;

public class SearchQueryJob extends SearchJob {
	private static final Logger logger = Logger.getLogger(SearchQueryJob.class);
	
	private static final String EARLIEST_TIME_FIELD_PLACER = "$earliest$";
	private static final String EARLIEST_TIME_FIELD = "earliest";
	private static final String LATEST_TIME_FIELD = "latest";
//	private static final String DISPATCH_MAX_COUNT_FIELD_NAME = "dispatch.max_count";
	private static final int DEFAULT_DISPATCH_MAX_COUNT = 500000;
	
	private String splunkSearchQuery;
	
	public SearchQueryJob(String splunkSearchQuery){
		this.splunkSearchQuery = splunkSearchQuery;
	}

	@Override
	protected Job runJob(Service service, String earliestTimeCursor, String latestTimeCursor) throws Exception {
		String search = splunkSearchQuery;
		
        JobArgs jobArgs = new JobArgs();
        if(search.contains(EARLIEST_TIME_FIELD_PLACER)){
        	StringBuffer buf = new StringBuffer();
        	if(earliestTimeCursor != null){
	        	logger.info(earliestTimeCursor);
	        	buf.append(EARLIEST_TIME_FIELD).append("=").append(SplunkUtil.cursorToSearchTimeQueryFormat(earliestTimeCursor));
	        }
	        if(latestTimeCursor != null){
	        	buf.append(" ").append(LATEST_TIME_FIELD).append("=").append(SplunkUtil.cursorToSearchTimeQueryFormat(latestTimeCursor));
	        }
	        search = search.replace(EARLIEST_TIME_FIELD_PLACER, buf.toString());
        } else{
	        if(earliestTimeCursor != null){
	        	logger.info(earliestTimeCursor);
	        	jobArgs.setEarliestTime(earliestTimeCursor);
	        }
	        if(latestTimeCursor != null){
	        	jobArgs.setLatestTime(latestTimeCursor);
	        }
        }

        
        logger.info("running search job with the query = {}",search);
        Job job = service.getJobs().create(search,jobArgs);//
        return job;
	}
	
	
	
	

	public String getSplunkSearchQuery() {
		return splunkSearchQuery;
	}

	public void setSplunkSearchQuery(String splunkSearchQuery) {
		this.splunkSearchQuery = splunkSearchQuery;
	}

	@Override
	protected int getDispatchMaxCount(Service service) throws Exception {
		//TODO: Check from where do I get this default value.
		return DEFAULT_DISPATCH_MAX_COUNT;
	}

}
