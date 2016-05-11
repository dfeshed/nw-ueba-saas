package fortscale.utils.splunk;

import com.splunk.Job;
import com.splunk.JobArgs;
import com.splunk.Service;
import fortscale.utils.logging.Logger;

import java.util.Map;
import java.util.Properties;

public class SearchQueryJob extends SearchJob {

	private static final Logger logger = Logger.getLogger(SearchQueryJob.class);

	private static final int DEFAULT_DISPATCH_MAX_COUNT = 500000;

	private String splunkSearchQuery;
	private Properties arguments;

	public SearchQueryJob(String splunkSearchQuery, Properties arguments){
		this.splunkSearchQuery = splunkSearchQuery;
		this.arguments = arguments;
	}

	@Override
	protected Job runJob(Service service, String earliestTimeCursor, String latestTimeCursor) throws Exception {
		String search = splunkSearchQuery;
		JobArgs jobArgs = new JobArgs();
		StringBuilder args = new StringBuilder();
		for(Map.Entry<Object, Object> arg: arguments.entrySet()) {
			args.append(arg.getKey() + "=" + arg.getValue() + " ");
		}
		logger.info("running search job with the query = {}",search);
		Job job = null;
		try {
			search = "search=" + search + " " + args.toString();
			job = service.getJobs().create(search, jobArgs);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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