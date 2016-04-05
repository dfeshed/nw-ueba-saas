package fortscale.utils.splunk;

import com.splunk.*;
import fortscale.utils.logging.Logger;
import fortscale.utils.siem.ISplunkEventsHandler;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.*;

public class SplunkApi {
	private static final Logger logger = Logger.getLogger(SplunkApi.class);

	// No timeout for job
	public static final int NO_TIMEOUT = -1;

	private static String SPLUNK_SERVER_HOST_NAME_PROPERTY_FIELD_NAME = "splunkServer";
	private static String SPLUNK_SERVER_PORT_PROPERTY_FIELD_NAME = "splunkPort";
	private static String SPLUNK_SERVER_USER_PROPERTY_FIELD_NAME = "splunkUser";
	private static String SPLUNK_SERVER_USER_PASSWORD_PROPERTY_FIELD_NAME = "splunkPassword";
	
	
	private static final String SPLUNK_TIMESTAMP_FIELD = "_time";
	private static final String SPLUNK_SOURCE_TYPE_FIELD = "sourcetype";
	private static final String SPLUNK_DATA_SOURCE_FIELD = "source";
	
	
	
	
	private String splunkServerHostName;
	private int splunkServerPort;
	private String splunkServerUser;
	private String splunkServerUserPassword;
	
	private Service splunkService;
	
	public SplunkApi(String splunkServerHostName, int splunkServerPort, String splunkServerUser, String splunkServerUserPassword){
		setSplunkServerHostName(splunkServerHostName);
		setSplunkServerPort(splunkServerPort);
		setSplunkServerUser(splunkServerUser);
		setSplunkServerUserPassword(splunkServerUserPassword);
		splunkService = splunkConnect();
	}
	
	public SplunkApi(Properties properties) throws Exception{
		loadProperties(properties);
		splunkService = splunkConnect();
	}
	
	public Service getSplunkService(){
		return splunkService;
	}
	
	private void loadProperties(Properties properties) throws Exception{
		if(properties.containsKey(SPLUNK_SERVER_HOST_NAME_PROPERTY_FIELD_NAME)){
			setSplunkServerHostName((String)properties.get(SPLUNK_SERVER_HOST_NAME_PROPERTY_FIELD_NAME));
		} else{
			logger.error("the property {} is missing.", SPLUNK_SERVER_HOST_NAME_PROPERTY_FIELD_NAME);
			throw new Exception(SPLUNK_SERVER_HOST_NAME_PROPERTY_FIELD_NAME + " property is missing.");
		}
		
		if(properties.containsKey(SPLUNK_SERVER_PORT_PROPERTY_FIELD_NAME)){
			setSplunkServerPort(Integer.parseInt((String)properties.get(SPLUNK_SERVER_PORT_PROPERTY_FIELD_NAME)));
		} else{
			logger.error("the property {} is missing.", SPLUNK_SERVER_PORT_PROPERTY_FIELD_NAME);
			throw new Exception(SPLUNK_SERVER_PORT_PROPERTY_FIELD_NAME + " property is missing.");
		}
		
		if(properties.containsKey(SPLUNK_SERVER_USER_PROPERTY_FIELD_NAME)){
			setSplunkServerUser((String)properties.get(SPLUNK_SERVER_USER_PROPERTY_FIELD_NAME));
		} else{
			logger.error("the property {} is missing.", SPLUNK_SERVER_USER_PROPERTY_FIELD_NAME);
			throw new Exception(SPLUNK_SERVER_USER_PROPERTY_FIELD_NAME + " property is missing.");
		}
		
		if(properties.containsKey(SPLUNK_SERVER_USER_PASSWORD_PROPERTY_FIELD_NAME)){
			setSplunkServerUserPassword((String)properties.get(SPLUNK_SERVER_USER_PASSWORD_PROPERTY_FIELD_NAME));
		} else{
			logger.error("the property {} is missing.", SPLUNK_SERVER_USER_PASSWORD_PROPERTY_FIELD_NAME);
			throw new Exception(SPLUNK_SERVER_USER_PASSWORD_PROPERTY_FIELD_NAME + " property is missing.");
		}
		
	}
	
	private Service splunkConnect(){
		logger.info("connecting to splunk server {} with user {}", splunkServerHostName, splunkServerUser);
		ServiceArgs loginArgs = new ServiceArgs();
		loginArgs.setUsername(splunkServerUser);// "admin"
		loginArgs.setPassword(splunkServerUserPassword);// "p@ssw0rd"
		loginArgs.setHost(splunkServerHostName);// "localhost"
		loginArgs.setPort(splunkServerPort);// 8089

		// Create a Service instance and log in with the argument map
		Service ret = Service.connect(loginArgs);
		
		logger.info("connecting to splunk completed succefully.");
		
		return ret;
	}
	
	public void deleteIndex(String splunkDataIndex) throws Exception{
		Index myIndex = splunkService.getIndexes().get(splunkDataIndex);
		if (myIndex == null) {
			logger.warn("splunk data index {} does not exist.", splunkDataIndex);
			return;
		}
		
		myIndex.remove();
		splunkService.getIndexes().refresh();
		int i = 0;
		while((myIndex = splunkService.getIndexes().get(splunkDataIndex)) != null && i < 30){
			Thread.sleep(1000);
			i++;
		}
		if (myIndex != null) {
			logger.error("Splunk data index {} was not removed.",
					splunkDataIndex);
			throw new Exception("Splunk data index "
					+ splunkDataIndex + " was not removed.");
		}
	}
	
	public void createIndex(String splunkDataIndex) throws Exception{
		//Get the collection of indexes
		IndexCollection myIndexes = splunkService.getIndexes();

		Index myIndex = myIndexes.get(splunkDataIndex);
		if (myIndex != null) {
			logger.error("Splunk data index {} already exist.",
					splunkDataIndex);
			throw new Exception("Splunk data index "
					+ splunkDataIndex + " already exist.");
		}

		//Create a new index
		myIndex = myIndexes.create(splunkDataIndex);
		if (myIndex == null) {
			logger.error("Splunk data index {} was not created.",
					splunkDataIndex);
			throw new SplunkIndexNotExistException("Splunk data index "
					+ splunkDataIndex + " was not created.");
		}
	}
	
	public String writeDataToSplunk(String fileFullPath, String splunkDataIndex, String splunkSourceType, String splunkDataSource)
			throws IOException, SplunkIndexNotExistException {
		String line = null;
		BufferedReader buf = null;
		try{
			logger.debug("Creates the local file input stream.");
			FileInputStream fin = new FileInputStream(new File(fileFullPath));
			buf = new BufferedReader(new InputStreamReader(fin));
			line = writeDataToSplunk(buf, splunkDataIndex, splunkSourceType, splunkDataSource);
		} finally{
			if(buf != null){
				buf.close();
			}
		}
		return line;
	}
	
	public String writeDataToSplunk(BufferedReader buf, String splunkDataIndex, String splunkSourceType, String splunkDataSource)
			throws IOException, SplunkIndexNotExistException {
		int numOfWrittenLines = 0;
		// Retrieve the index for the data
		Index myIndex = splunkService.getIndexes().get(splunkDataIndex);
		if (myIndex == null) {
			logger.error("Splunk data index {} does not exist.",
					splunkDataIndex);
			throw new SplunkIndexNotExistException("Splunk data index "
					+ splunkDataIndex + " does not exist");
		}

		Writer out = null;
		

		Args attachedArgs = new Args();
		if (splunkSourceType != null) {
			attachedArgs.put(SPLUNK_SOURCE_TYPE_FIELD, splunkSourceType);
		}
		if (splunkDataSource != null) {
			attachedArgs.put(SPLUNK_DATA_SOURCE_FIELD, splunkDataSource);
		}
		String line = null;
		try {
			logger.debug("Creates a writable socket to the given splunk index.");
			Socket socket = myIndex.attach(attachedArgs);
			OutputStream ostream = socket.getOutputStream();
			out = new OutputStreamWriter(ostream, "UTF-8");

			

			logger.debug("Start writing to splunk.");
			
			while ((line = buf.readLine()) != null) {
				out.write(line + "\r\n");
				numOfWrittenLines++;
			}
			out.flush();
			logger.info("finished writing to splunk {} lines.", numOfWrittenLines);
		} finally {
			if (out != null) {
				out.close();
			}
		}
		return line;
	}
	
	
	
	/*
	 * Assumption: 1. the results contain the _time field. 2. the results are sorted from latest to earliest.
	 */
	public String getSearchQueryEventsLatestTime(String splunkSearchQuery) throws Exception{
		logger.info("getting events latest time.");
		
		String timestamp = null;
		
		

		Job job= null;
		ResultsReaderXml resultsReader = null;
		try{
			JobArgs jobArgs = new JobArgs();
	        jobArgs.setAutoFinalizeEventCount(1);
	        			
			InputStream results = splunkService.oneshotSearch(splunkSearchQuery, jobArgs);
		       
		    resultsReader = new ResultsReaderXml(results);
		    Event event = resultsReader.getNextEvent();
		    if(event != null){
			    timestamp = event.get(SPLUNK_TIMESTAMP_FIELD);
		    	if(timestamp != null){
		    		logger.info("return timestamp: {}", timestamp);
		    	} else{
		    		logger.error("event does not contain timestamp. event: {}", event.toString());
		    	}
		    } else{
		    	logger.error("No events for the search query");
		    }
		} finally{
			if(job != null){
				job.cancel();
			}
			if(resultsReader != null){
				resultsReader.close();
			}
		}
		
		return timestamp;
	}


//	public String runSearchQuery(String splunkSearchQuery, String earliestTimeCursor, ISplunkEventsHandler splunkEventsHandler) throws Exception{
//		return runSearchQuery(splunkSearchQuery, earliestTimeCursor, splunkEventsHandler, true);
//	}
//
	public String runSearchQuery(String splunkSearchQuery, Properties arguments, String earliestTimeCursor,
			ISplunkEventsHandler splunkEventsHandler, int timeoutInSeconds) throws Exception {
		SearchJob searchJob = new SearchQueryJob(splunkSearchQuery, arguments);
		searchJob.setContainTime(true);
		return runSearch(searchJob, earliestTimeCursor, splunkEventsHandler, timeoutInSeconds);
	}
	
	public String runSavedSearch(String savedSearchName, Properties arguments, String earliestTimeCursor,
			ISplunkEventsHandler splunkEventsHandler, int timeoutInSeconds) throws Exception{
		return runSavedSearch(savedSearchName, arguments, earliestTimeCursor, splunkEventsHandler, true, timeoutInSeconds);
	}
	
	public String runSavedSearch(String savedSearchName, Properties arguments, String earliestTimeCursor,
			ISplunkEventsHandler splunkEventsHandler, boolean isContainTime, int timeoutInSeconds) throws Exception{
		SearchJob searchJob = new SavedSearchJob(savedSearchName, arguments);
		searchJob.setContainTime(isContainTime);
		
		return runSearch(searchJob, earliestTimeCursor, splunkEventsHandler, timeoutInSeconds);
	}

	private String runSearch(SearchJob searchJob, String earliestTimeCursor, ISplunkEventsHandler splunkEventsHandler, int timeoutInSeconds) throws Exception{
		if(earliestTimeCursor != null){
			logger.info("search with earliest time: " + earliestTimeCursor);
		} else{
			logger.info("search with no earliest time");
		}
		
		
		String retCursor = null;
		Entity restApi = splunkService.getConfs().get("limits").get("restapi");
 		int maxresults = Integer
 				.parseInt((String) restApi.get("maxresultrows"));
		
 		logger.info("search maxresultrows = {}", maxresults);
        
        
 		splunkEventsHandler.open();
        try{
        	retCursor = runSearch(searchJob, maxresults, earliestTimeCursor, splunkEventsHandler, -1, timeoutInSeconds);
        } finally{
    		splunkEventsHandler.flush();
    		splunkEventsHandler.close();
		}
        
        return retCursor;
	}


	private Job runSearchJob(SearchJob searchJob, String earliestTimeCursor, String cursor,int timeoutInSeconds) throws Exception{
		//inner class uses for running the job in a different thread
		class RunJobCallable implements Callable<Job> {

			SearchJob searchJob;
			String earliestTimeCursor;
			String cursor;

			RunJobCallable(SearchJob searchJob, String earliestTimeCursor, String cursor){
				this.searchJob = searchJob;
				this.earliestTimeCursor = earliestTimeCursor;
				this.cursor = cursor;
			}

			public Job call() throws Exception {
				return searchJob.run(splunkService, earliestTimeCursor, cursor);
			}
		}
		Job job = null;
		ExecutorService exService = Executors.newSingleThreadExecutor();
		Future<Job> futureJob = exService.submit(new RunJobCallable(searchJob, earliestTimeCursor,cursor));
		try {
			if (timeoutInSeconds > 0) {
				job = futureJob.get(timeoutInSeconds, TimeUnit.SECONDS);
			}
			else {
				job = futureJob.get();
			}
		}
		catch (TimeoutException e){
			// in the case of a timeout
			logger.error("Fetch job has reached a timeout of {} seconds. Canceling job...", timeoutInSeconds);
			throw new Exception("Timeout in job");
		}
		finally {
			exService.shutdownNow();
		}
		return job;
	}

	private String runSearch(SearchJob searchJob, int maxresults, String earliestTimeCursor, ISplunkEventsHandler splunkEventsHandler, int maxNumOfEvents, int timeoutInSeconds) throws Exception{
		String retCursor = null;
		
		String cursor = null;
        
        int numOfEvents = -1;
        int dispatchMaxCount = searchJob.getDispatchMaxCount(splunkService);
        
		
        int eventSum = 0;
        Job job = null;
        int numOfTimesJobFinalized = 0;
    	do{
    		if(job != null){
				job.cancel();
			}
			job = runSearchJob(searchJob, earliestTimeCursor, cursor, timeoutInSeconds);
    		if(job == null){
    			break;
    		}
			
			numOfEvents = job.getEventCount();
			if(job.isFinalized() || job.isPaused()){
				if(numOfEvents < dispatchMaxCount){
					numOfTimesJobFinalized++;
					if(numOfTimesJobFinalized > 3){
						logger.error("The search job was finalized or paused {} times. The last job unique search identifier is {}.", numOfTimesJobFinalized, job.getSid());
						throw new RuntimeException(String.format("The search job was finalized %d times. The last job unique search identifier is %s.", numOfTimesJobFinalized, job.getSid()));
					}
				}
			}
			
			logger.info("going over {} events", numOfEvents);
			int offset = 0;
			 // Specify JSON as the output mode for results
			while(offset < numOfEvents && (maxNumOfEvents < 0 || ((eventSum + offset) < maxNumOfEvents) ) ){
		        JobResultsArgs resultsArgs = new JobResultsArgs();
		        resultsArgs.setOffset(offset);
		        resultsArgs.setCount(maxresults);
		        resultsArgs.setOutputMode(JobResultsArgs.OutputMode.XML);
		        ResultsReaderXml reader = null;
				Event event = null;
		        try{
			        // Display results in JSON using ResultsReaderJson
			        InputStream results = job.getResults(resultsArgs);		        
			        reader = new ResultsReaderXml(results);
	

			        Event lastEvent = null;
			        while((event = getNextEvent(reader)) != null){
			        	if(retCursor == null && searchJob.isContainTime()){
			            	retCursor = event.get(SPLUNK_TIMESTAMP_FIELD);
			            	if(retCursor != null){
			            		logger.info("return cursor: {}", retCursor);
			            	} else{
			            		if(offset > 0){
			            			logger.error("event does not contain timestamp. event: {}", event.toString());
			            			throw new RuntimeException(String.format("event does not contain timestamp. event: %s", event.toString()));
			            		}
			            	}
			            }
			       
			        	splunkEventsHandler.handle(event);
			        	
			        	offset++;
			        	lastEvent = event;
			        }
			        if(searchJob.isContainTime() && lastEvent != null){
			        	cursor = lastEvent.get(SPLUNK_TIMESTAMP_FIELD);
			        	if(cursor == null){
			        		logger.error("last event does not contain timestamp. event: {}", lastEvent.toString());
			        	} else{
			        		logger.info("last event time {}", cursor);
			        	}
			        }
		        } catch (Exception e) {
					// In case of a problem in reading the events, increment the loop counter.
					// This ensures that we won't enter an endless loop
					if	(event == null) {
						logger.warn("Could not read event number: {}", Integer.toString(offset));
						offset++;
					}
				} finally{
		        	if(reader != null){
		        		reader.close();
		        	}
		        	if(splunkEventsHandler != null){
		        		splunkEventsHandler.flush();
		        	}
		        }
			}
			if(!searchJob.isContainTime() && (job.isFinalized() || job.isPaused())){
				logger.warn("you might have missed some events since the job was {}", job.isFinalized() ? "finalized" : "paused");
			}
			eventSum += offset;
    	} while(cursor != null && numOfEvents > 0 && (maxNumOfEvents < 0 || (eventSum < maxNumOfEvents) ) && (earliestTimeCursor == null || SplunkUtil.compareCursors(earliestTimeCursor, cursor) < 0) && (job.isFinalized() || job.isPaused()));
        
    	logger.info("finished search");
    	if(eventSum == 0){
    		if(job != null && !job.isFailed()){
    			logger.warn("no events!!! The unique search identifier is {}.", job.getSid());
    		}
    	} else{
    		logger.info("total num of events: {}", eventSum);
    	}
    	if(searchJob.isContainTime() &&retCursor == null && eventSum > 0){
    		logger.error("no timestamp was recieved from search!!! though {} events were recieved.", eventSum);
    	}
		
		if(job != null && !job.isFailed()){
			try {
				job.cancel();
			} catch (Exception e) {
				logger.warn("error cancelling saved search from splunk", e);
			}
		}
		
        return retCursor;
	}
	
	private Event getNextEvent(ResultsReaderXml reader) throws IOException{
		Event event = null;
		int numOfTries = 0;
        while(numOfTries < 5 && event == null){
        	numOfTries++;
        	try{
        		event = reader.getNextEvent();
        	} catch(IOException ioe){
        		throw ioe;
        	} catch(Exception e){
				if (numOfTries == 5) {
					logger.warn("got the following exception while trying to get the next event from splunk", e);
					throw e;
				}
        	}
        }
        return event;
	}
	
	
	public void createMonitor(String monitorFilePath, String splunkDataIndex, String splunkDataSourceType){
		// Get the collection of data inputs
		InputCollection myInputs = splunkService.getInputs();
		MonitorInput monitorInput= myInputs.create(monitorFilePath, InputKind.Monitor);
		monitorInput.setIndex(splunkDataIndex);
		monitorInput.setSourcetype(splunkDataSourceType);
		monitorInput.update();
		
		logger.info("New monitor was created: {}", monitorInput.toString());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public String getSplunkServerHostName() {
		return splunkServerHostName;
	}

	public void setSplunkServerHostName(String splunkServerHostName) {
		this.splunkServerHostName = splunkServerHostName;
	}

	public int getSplunkServerPort() {
		return splunkServerPort;
	}

	public void setSplunkServerPort(int splunkServerPort) {
		this.splunkServerPort = splunkServerPort;
	}

	public String getSplunkServerUser() {
		return splunkServerUser;
	}

	public void setSplunkServerUser(String splunkServerUser) {
		this.splunkServerUser = splunkServerUser;
	}

	public String getSplunkServerUserPassword() {
		return splunkServerUserPassword;
	}

	public void setSplunkServerUserPassword(String splunkServerUserPassword) {
		this.splunkServerUserPassword = splunkServerUserPassword;
	}
}
