package fortscale.utils.splunk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Properties;

import com.splunk.Args;
import com.splunk.Entity;
import com.splunk.Event;
import com.splunk.Index;
import com.splunk.IndexCollection;
import com.splunk.InputCollection;
import com.splunk.InputKind;
import com.splunk.Job;
import com.splunk.JobArgs;
import com.splunk.JobResultsArgs;
import com.splunk.MonitorInput;
import com.splunk.ResultsReaderCsv;
import com.splunk.ResultsReaderXml;
import com.splunk.Service;
import com.splunk.ServiceArgs;

import fortscale.utils.logging.Logger;

public class SplunkApi {
	private static final Logger logger = Logger.getLogger(SplunkApi.class);
	
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
	
	
	public String runSearchQuery(String splunkSearchQuery, String earliestTimeCursor, ISplunkEventsHandler splunkEventsHandler) throws Exception{
		return runSearchQuery(splunkSearchQuery, earliestTimeCursor, splunkEventsHandler, false);
	}
	
	public String runSearchQuery(String splunkSearchQuery, String earliestTimeCursor, ISplunkEventsHandler splunkEventsHandler, boolean isContainTime) throws Exception{
		SearchJob searchJob = new SearchQueryJob(splunkSearchQuery);
		searchJob.setContainTime(isContainTime);
		
		return runSearch(searchJob, earliestTimeCursor, splunkEventsHandler);
	}
	
	public String runSavedSearch(String savedSearchName, Properties arguments, String earliestTimeCursor, ISplunkEventsHandler splunkEventsHandler) throws Exception{
		return runSavedSearch(savedSearchName, arguments, earliestTimeCursor, splunkEventsHandler, false);
	}
	
	public String runSavedSearch(String savedSearchName, Properties arguments, String earliestTimeCursor, ISplunkEventsHandler splunkEventsHandler, boolean isContainTime) throws Exception{
		SearchJob searchJob = new SavedSearchJob(savedSearchName, arguments);
		searchJob.setContainTime(isContainTime);
		
		return runSearch(searchJob, earliestTimeCursor, splunkEventsHandler);
	}
	
	private String runSearch(SearchJob searchJob, String earliestTimeCursor, ISplunkEventsHandler splunkEventsHandler) throws Exception{
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
        	retCursor = runSearch(searchJob, maxresults, earliestTimeCursor, splunkEventsHandler, -1);
        } finally{
    		splunkEventsHandler.flush();
    		splunkEventsHandler.close();
		}
        
        return retCursor;
	}
	
	private String runSearch(SearchJob searchJob, int maxresults, String earliestTimeCursor, ISplunkEventsHandler splunkEventsHandler, int maxNumOfEvents) throws Exception{
		String retCursor = null;
		
		String cursor = null;
        
        int numOfEvents = -1;
        
		
        int eventSum = 0;
        Job job = null;
    	do{
    		
    		job = searchJob.run(splunkService, earliestTimeCursor, cursor);
    		if(job == null){
    			break;
    		}
			
			numOfEvents = job.getEventCount();
			if(numOfEvents > 500000){
				numOfEvents = 500000;
			}
			
			
			logger.info("going over {} events", numOfEvents);
			int offset = 0;
			 // Specify JSON as the output mode for results
			while(offset < numOfEvents && (maxNumOfEvents < 0 || ((eventSum + offset) < maxNumOfEvents) ) ){
		        JobResultsArgs resultsArgs = new JobResultsArgs();
		        resultsArgs.setOffset(offset);
		        resultsArgs.setCount(maxresults);
		        resultsArgs.setOutputMode(JobResultsArgs.OutputMode.CSV);
		        ResultsReaderCsv reader = null;
		        try{
			        // Display results in JSON using ResultsReaderJson
			        InputStream results = job.getResults(resultsArgs);		        
			        reader = new ResultsReaderCsv(results);
	
			        Event event = null;
			        Event lastEvent = null;
			        while((event = reader.getNextEvent()) != null){
			        	if(retCursor == null && searchJob.isContainTime()){
			            	retCursor = event.get(SPLUNK_TIMESTAMP_FIELD);
			            	if(retCursor != null){
			            		logger.info("return cursor: {}", retCursor);
			            	} else{
			            		logger.error("event does not contain timestamp. event: {}", event.toString());
			            	}
			            }
			       
			        	splunkEventsHandler.handle(event);
			        	
			        	offset++;
			        	lastEvent = event;
			        }
			        if(lastEvent != null){
			        	cursor = lastEvent.get("_time");
			        	logger.info("last event time {}", cursor);
			        } else{
			        	break;
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
			eventSum += offset;
    	} while(numOfEvents > 0 && (maxNumOfEvents < 0 || (eventSum < maxNumOfEvents) ) && (earliestTimeCursor == null || SplunkUtil.compareCursors(earliestTimeCursor, cursor) < 0) && numOfEvents >= searchJob.getDispatchMaxCount(splunkService));
        
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
        return retCursor;
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
