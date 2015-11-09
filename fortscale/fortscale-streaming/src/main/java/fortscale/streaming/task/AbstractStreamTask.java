package fortscale.streaming.task;

import fortscale.streaming.task.monitor.TaskMonitoringHelper;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;
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

public abstract class AbstractStreamTask implements StreamTask, WindowableTask, InitableTask, ClosableTask {

	public static final String DATA_SOURCE_FIELD_NAME = "DataSource";
	public static final String CANNOT_PARSE_MESSAGE_LABEL = "Cannot parse message";
	private static Logger logger = Logger.getLogger(AbstractStreamTask.class);
	
	private ExceptionHandler processExceptionHandler;
	private ExceptionHandler windowExceptionHandler;
	
	protected abstract void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception;
	protected abstract void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception;
	protected abstract void wrappedInit(Config config, TaskContext context) throws Exception;
	protected abstract void wrappedClose() throws Exception;


	protected TaskMonitoringHelper taskMonitoringHelper;



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

		taskMonitoringHelper = SpringService.getInstance().resolve(TaskMonitoringHelper.class);

		boolean isMonitoredTask = config.getBoolean("fortscale.monitoring.enable",false);
		taskMonitoringHelper.setIsMonitoredTask(isMonitoredTask);
		taskMonitoringHelper.resetCountersPerWindow();

        logger.info("Task init finished");
	}
	
	@Override
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		try{
			taskMonitoringHelper.handleNewEvent();
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
			taskMonitoringHelper.saveJobStatusReport(getJobLabel());
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
			taskMonitoringHelper.saveJobStatusReport(getJobLabel());
			wrappedClose();
		} finally {
			SpringService.shutdown();
		}
        logger.info("task closed");
	}



	//Extract the name of the datasource from the message.
	//Currently this field is not exists, but after marging with the code of generic data source,
	//We will have this field.
	protected String getDataSource(JSONObject message){
		String datasource = null;
		try {
			datasource = message.getAsString(DATA_SOURCE_FIELD_NAME);
		} catch (Exception e){

		}
		if (StringUtils.isBlank(datasource)){
			datasource = "No Data Source";
		}
		return datasource;

	}

	//This is the name of job that will be presented in the monitoring screen
	//The method should be override
	protected String getJobLabel(){
		return this.getClass().getName();
	}


	public TaskMonitoringHelper getTaskMonitoringHelper() {
		return taskMonitoringHelper;
	}

	public void setTaskMonitoringHelper(TaskMonitoringHelper taskMonitoringHelper) {
		this.taskMonitoringHelper = taskMonitoringHelper;
	}

	protected JSONObject parseJsonMessage(IncomingMessageEnvelope envelope) throws ParseException {
		try {
			String messageText = (String) envelope.getMessage();
			return (JSONObject) JSONValue.parseWithException(messageText);
		} catch (ParseException e){
			taskMonitoringHelper.countNewFilteredEvents(CANNOT_PARSE_MESSAGE_LABEL);
			throw e;
		}
	}
}
