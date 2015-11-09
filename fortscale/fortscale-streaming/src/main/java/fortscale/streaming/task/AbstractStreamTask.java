package fortscale.streaming.task;

import fortscale.streaming.exceptions.*;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.state.StreamingMessageState;
import fortscale.streaming.service.state.StreamingStepType;
import fortscale.streaming.task.monitor.TaskMonitoringHelper;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;

public abstract class AbstractStreamTask implements StreamTask, WindowableTask, InitableTask, ClosableTask {

	private static Logger logger = Logger.getLogger(AbstractStreamTask.class);
	
	private ExceptionHandler processExceptionHandler;
	private ExceptionHandler windowExceptionHandler;

	public static final String DATA_SOURCE_FIELD_NAME = "data_source";

	public static final String LAST_STATE_FIELD_NAME = "last_state";
	
	protected abstract void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception;
	protected abstract void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception;
	protected abstract void wrappedInit(Config config, TaskContext context) throws Exception;
	protected abstract void wrappedClose() throws Exception;

	protected StreamingMessageState getCurrentStreamingMessageState() {
		return new StreamingMessageState(getCurrentStreamingStepType(), this.getClass().getSimpleName());
	}

	protected StreamingStepType getCurrentStreamingStepType() {
		return StreamingStepType.ENRICH;
	}

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
			StatefulMessageCollector statefulMessageCollector = new StatefulMessageCollector(collector, getCurrentStreamingMessageState());
			wrappedProcess(envelope, statefulMessageCollector, coordinator);
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

			StatefulMessageCollector statefulMessageCollector = new StatefulMessageCollector(collector, getCurrentStreamingMessageState());
			wrappedWindow(statefulMessageCollector, coordinator);

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
}
