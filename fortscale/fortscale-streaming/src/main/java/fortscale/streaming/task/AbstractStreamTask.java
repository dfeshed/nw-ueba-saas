package fortscale.streaming.task;

import fortscale.monitor.JobProgressReporter;
import fortscale.monitor.domain.JobDataReceived;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.ClosableTask;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.WindowableTask;

import fortscale.streaming.exceptions.ExceptionHandler;
import fortscale.streaming.exceptions.HdfsException;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.exceptions.LevelDbException;
import fortscale.streaming.exceptions.TaskCoordinatorException;
import fortscale.streaming.service.SpringService;
import fortscale.utils.logging.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractStreamTask implements StreamTask, WindowableTask, InitableTask, ClosableTask {
	private static Logger logger = Logger.getLogger(AbstractStreamTask.class);
	
	private ExceptionHandler processExceptionHandler;
	private ExceptionHandler windowExceptionHandler;
	
	protected abstract void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception;
	protected abstract void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception;
	protected abstract void wrappedInit(Config config, TaskContext context) throws Exception;
	protected abstract void wrappedClose() throws Exception;


	//This is the name of job that will be presented in the monitoring screen
	protected abstract String getJobLabel();

	//Parameters for Window statistics monitoring
	private JobProgressReporter jobMonitorReporter;
	private Map<String, Integer> countFilterByCause;
	protected int countNotFilteredEvents;
	private long timeOfFirstEventInWindow;
	private String timeOfFirstEventInWindowAsString;
	private long timeOfLastEventInWindow;
	private String timeOfLastEventInWindowAsString;
	private int totalAmountOfEventsInWindow; //Filtered and unfiltered events

	//Constant labels for JOB monitoring
	public static final String TOTAL_FILTERED_EVENTS_LABEL = "Filtered Events";
	public static final String FIRST_EVENT_TIME_LABEL = "First Event Original Time";
	public static final String LAST_EVENT_TIME_LABEL = "Last Event Original Time";
	public static final String TOTAL_EVENTS_LABEL = "Total Events";
	public static final String NOT_FILTERED_EVENTS_LABEL = "Processed Event";
	public static final String JOB_DATA_SOURCE = "Streaming";
	private static final String EVENTS_TYPE="EVENTS";
	private static final String FILTERED_EVENTS_PREFIX = "Filtered Events - Reason ";
	
	public AbstractStreamTask(){
		processExceptionHandler = new ExceptionHandler();
		fillExceptionHandler(processExceptionHandler);
		
		windowExceptionHandler = new ExceptionHandler();
		fillExceptionHandler(windowExceptionHandler);
	}
	
	public static void fillExceptionHandler(ExceptionHandler exceptionHandler){
		exceptionHandler.configNumOfContinuesExceptionsToFilter(LevelDbException.class, 1);
		exceptionHandler.configNumOfContinuesExceptionsToFilter(KafkaPublisherException.class, 1);
		exceptionHandler.configNumOfContinuesExceptionsToFilter(HdfsException.class, 1);
		exceptionHandler.configNumOfContinuesExceptionsToFilter(TaskCoordinatorException.class, 1);
	}

	public void init(Config config, TaskContext context) throws Exception {
		// get spring context from configuration
		String contextPath = config.get("fortscale.context", "");
		if(StringUtils.isNotBlank(contextPath)){
			SpringService.init(contextPath);
		}
		
		// call specific task init method
		wrappedInit(config, context);

		if (isMonitoredTask()) {
			jobMonitorReporter = SpringService.getInstance().resolve(JobProgressReporter.class);
			initCountersPerWindow();
		}

        logger.info("Task init finished");
	}
	
	@Override
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		try{
			if (isMonitoredTask()) {
				totalAmountOfEventsInWindow++;
			}
			wrappedProcess(envelope, collector, coordinator);
			processExceptionHandler.clear();
		} catch(Exception exception){
			logger.error("got an exception while processing stream message", exception);
			processExceptionHandler.handleException(exception);
		}		
	}
	
	@Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception{
		try{
			saveJobStatusReport();
			wrappedWindow(collector, coordinator);
			windowExceptionHandler.clear();
		} catch(Exception exception){
			logger.error("got an exception while processing stream message", exception);
			windowExceptionHandler.handleException(exception);
		}
    }
	
	@Override 
	public void close() throws Exception {
		try {
            logger.info("initiating task close");
			saveJobStatusReport();

			wrappedClose();
		} finally {
			SpringService.shutdown();
		}
        logger.info("task closed");
	}


	/**
	 * When event filtered, call that method with the cause.
	 * That method add the cause  countFilterByCause, if the cause already in the map,
	 * the counter of the cause increased
	 * @param cause
	 */
	protected void countNewFilteredEvents(String cause){
		Integer causeReason = countFilterByCause.get(cause);
		if (causeReason == null){
			causeReason = 1;
		} else {
			causeReason++;
		}
		countFilterByCause.put(cause,causeReason);
	}

	protected boolean isMonitoredTask(){
		return false; //Override to monitor
	}

	//Init all statistics per windows
	protected void initCountersPerWindow() {
		countFilterByCause = new HashMap<>();
		countNotFilteredEvents = 0;
		totalAmountOfEventsInWindow = 0;
		timeOfFirstEventInWindow = Long.MAX_VALUE;
		timeOfLastEventInWindow = Long.MIN_VALUE;
		timeOfFirstEventInWindowAsString= "";
		timeOfLastEventInWindowAsString = "";
	}


	protected void handleUnFilteredEvents(Number dateTimeUnix, String dateAsString){
		updateFirstLastEventInWindow(dateTimeUnix, dateAsString);
		countNotFilteredEvents++; //Count not filtered events per window
	}
	/**
	 * Create new instance of job report, with the time of the first event in the window,
	 * the time of last event of the window, the total number of events in window (filtered and not filtered)
	 * the number of unfiltered events in the window,
	 * and how many filtered events per each cause.
	 * If there where no filtered event, add one line of Filter events = 0
	 */
	private void saveJobStatusReport() {

		if (isMonitoredTask()) {
			String monitorId = jobMonitorReporter.startJob(JOB_DATA_SOURCE, getJobLabel(), 1, true);

			//All the events which arrive to the job in the windows
			addJobData(monitorId, TOTAL_EVENTS_LABEL, totalAmountOfEventsInWindow, EVENTS_TYPE);
			//Original time of first event in the window
			addJobData(monitorId, FIRST_EVENT_TIME_LABEL, null, timeOfFirstEventInWindowAsString);
			//Original time of last event in the window
			addJobData(monitorId, LAST_EVENT_TIME_LABEL, null, timeOfLastEventInWindowAsString);

			//Add all cause and how many events filtered per cause,
			//or add "filtered events = 0 if no filtered events in the window.
			if (countFilterByCause.size() > 0) {
				for (Map.Entry<String, Integer> cause : countFilterByCause.entrySet()) {
					String label = FILTERED_EVENTS_PREFIX + cause.getKey();
					addJobData(monitorId, label, cause.getValue(), EVENTS_TYPE);
				}
			} else {
				addJobData(monitorId, TOTAL_FILTERED_EVENTS_LABEL, 0, EVENTS_TYPE);
			}

			//How many events not filtered in the window
			addJobData(monitorId, NOT_FILTERED_EVENTS_LABEL, countNotFilteredEvents, EVENTS_TYPE);

			jobMonitorReporter.finishJob(monitorId);

			//Reset counters per window
			initCountersPerWindow();
		}
	}

	//Keep the time of the first and last event time in the windows
	//First and last could be the same if there is only one event in the window
	private void updateFirstLastEventInWindow(Number time, String dateAsString){
		long eventTime = time.longValue();
		if (eventTime < timeOfFirstEventInWindow){
			timeOfFirstEventInWindow = eventTime;
			timeOfFirstEventInWindowAsString = dateAsString;
		}

		if (eventTime > timeOfLastEventInWindow){
			timeOfLastEventInWindow = eventTime;
			timeOfLastEventInWindowAsString = dateAsString;
		}
	}

	protected String getDataSource(JSONObject message){
		String datasource = null;
		try {
			datasource = message.getAsString("DataSource");
		} catch (Exception e){

		}
		if (StringUtils.isBlank(datasource)){
			datasource = "No Data Source";
		}
		return datasource;

	}

	/**
	 * Create new instance of JobDataReceived and add it to monitor
	 * @param monitorId
	 * @param text
	 * @param value
	 * @param valueType
	 */
	private void addJobData(String monitorId, String text, Integer value, String valueType ){
		JobDataReceived dataReceived = new JobDataReceived(text, value, valueType);
		jobMonitorReporter.addDataReceived(monitorId,dataReceived);
	}
}
