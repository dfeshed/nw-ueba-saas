package fortscale.utils.splunk;

import java.util.Properties;
import java.util.Map.Entry;

import com.splunk.Job;
import com.splunk.SavedSearch;
import com.splunk.SavedSearchCollection;
import com.splunk.SavedSearchDispatchArgs;
import com.splunk.Service;

import fortscale.utils.logging.Logger;

public class SavedSearchJob extends SearchJob {
	private static final Logger logger = Logger.getLogger(SavedSearchJob.class);
	
	private static final String SAVED_SEARCH_EARLIEST_ARG_NAME = "args.earliest";
	private static final String SAVED_SEARCH_LATEST_ARG_NAME = "args.latest";
	
	private String savedSearchName;
	private Properties arguments;
	
	
	
	public SavedSearchJob(String savedSearchName, Properties arguments){
		this.savedSearchName = savedSearchName;
		this.arguments = arguments;
	}
	




	@Override
	protected Job runJob(Service service, String earliestTimeCursor, String latestTimeCursor) throws Exception{
		SavedSearchCollection savedSearchCollection = service.getSavedSearches();
		SavedSearch savedSearch = savedSearchCollection.get(savedSearchName);
		if(savedSearch == null){
			logger.error("the saved search does not exist: {}", savedSearchName);
			throw new Exception("the saved search does not exist: " + savedSearchName);
		}
						
		// Set the arguments for dispatching the saved search
		SavedSearchDispatchArgs dispatchArgs = new SavedSearchDispatchArgs();

		// This attribute is set using a key-value pair 
		boolean isLatestExist = false;
		for(Entry<Object, Object> arg: arguments.entrySet()){
			if(SAVED_SEARCH_EARLIEST_ARG_NAME.equals((String) arg.getKey())){
				if(earliestTimeCursor != null){
					dispatchArgs.add(SAVED_SEARCH_EARLIEST_ARG_NAME, SplunkUtil.cursorToSearchTimeQueryFormat(earliestTimeCursor));
				}else{
					dispatchArgs.add((String) arg.getKey(), (String)arg.getValue());
				}
			}else if(SAVED_SEARCH_LATEST_ARG_NAME.equals((String) arg.getKey())){
				isLatestExist = true;
				if(latestTimeCursor != null){
					dispatchArgs.add(SAVED_SEARCH_LATEST_ARG_NAME, SplunkUtil.cursorToSearchTimeQueryFormat(latestTimeCursor));
				}else{
					dispatchArgs.add((String) arg.getKey(), (String)arg.getValue());
				}
			} else{
				dispatchArgs.add((String) arg.getKey(), (String)arg.getValue());
			}
		}
		if(latestTimeCursor != null && !isLatestExist){
			logger.error("can not bring all the events since the saved search does not contain args.latest");
			return null;
		}

		// Run a saved search and poll for completion
		logger.debug("Run the '{}' search ({}) with the following run time parameters: {}", savedSearch.getName(),
				savedSearch.getSearch(), dispatchArgs.toString());
		Job jobSavedSearch = null;

		// Run the saved search
		try {
		    jobSavedSearch = savedSearch.dispatch(dispatchArgs);
		} catch (InterruptedException e) {
		    logger.error("failed to run the search", e);
		    throw e;
		}
		
		return jobSavedSearch;

	}

	@Override
	protected int getDispatchMaxCount(Service service) throws Exception {
		SavedSearchCollection savedSearchCollection = service.getSavedSearches();
		SavedSearch savedSearch = savedSearchCollection.get(savedSearchName);
		if(savedSearch == null){
			logger.error("the saved search does not exist: {}", savedSearchName);
			throw new Exception("the saved search does not exist: " + savedSearchName);
		}
		
		return savedSearch.getDispatchMaxCount();
	}
	
	
	
	
	public String getSavedSearchName() {
		return savedSearchName;
	}




	public void setSavedSearchName(String savedSearchName) {
		this.savedSearchName = savedSearchName;
	}




	public Properties getArguments() {
		return arguments;
	}




	public void setArguments(Properties arguments) {
		this.arguments = arguments;
	}





	
}
