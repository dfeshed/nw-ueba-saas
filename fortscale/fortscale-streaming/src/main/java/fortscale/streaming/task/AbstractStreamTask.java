package fortscale.streaming.task;

import fortscale.streaming.exceptions.*;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.service.state.MessageCollectorStateDecorator;
import fortscale.streaming.task.monitor.TaskMonitoringHelper;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;

public abstract class AbstractStreamTask implements StreamTask, WindowableTask, InitableTask, ClosableTask {

	private static Logger logger = Logger.getLogger(AbstractStreamTask.class);

	private static final String DATA_SOURCE_FIELD_NAME = "data_source";
	private static final String LAST_STATE_FIELD_NAME = "last_state";

	public static final String CANNOT_PARSE_MESSAGE_LABEL = "Cannot parse message";

	private ExceptionHandler processExceptionHandler;
	private ExceptionHandler windowExceptionHandler;

	protected TaskMonitoringHelper taskMonitoringHelper;

	protected abstract void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception;
	protected abstract void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception;
	protected abstract void wrappedInit(Config config, TaskContext context) throws Exception;
	protected abstract void wrappedClose() throws Exception;

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

			String streamingTaskMessageState = resolveOutputMessageState();

			MessageCollectorStateDecorator messageCollectorStateDecorator = new MessageCollectorStateDecorator(collector);
			messageCollectorStateDecorator.setStreamingTaskMessageState(streamingTaskMessageState);

			wrappedProcess(envelope, messageCollectorStateDecorator, coordinator);

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
			datasource = null;
		}
		return datasource;

	}

	private String resolveOutputMessageState() throws Exception {
		return this.getClass().getSimpleName();
	}

	//This is the name of job that will be presented in the monitoring screen
	//The method should be override
	protected String getJobLabel(){
		return this.getClass().getName();
	}

	/**
	 * handleUnfilteredEvent
	 * @param event
	 */
	protected void handleUnfilteredEvent(JSONObject event){

		String dataSource  = getDataSource(event);
		Number eventTitleAsLong = event.getAsNumber("date_time_unix");
		String eventTimeAsString = event.getAsString("date_time");
		taskMonitoringHelper.handleUnFilteredEvents(dataSource, eventTitleAsLong, eventTimeAsString);
	}

	protected StreamingTaskDataSourceConfigKey extractDataSourceConfigKey(JSONObject message) {
		String dataSource = (String) message.get(DATA_SOURCE_FIELD_NAME);
		String lastState = (String) message.get(LAST_STATE_FIELD_NAME);

		if (dataSource == null) {
			throw new IllegalStateException("Message does not contain" + LAST_STATE_FIELD_NAME + " field");
		}

		return new StreamingTaskDataSourceConfigKey(dataSource, lastState);
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
			taskMonitoringHelper.countNewFilteredEvents(null,CANNOT_PARSE_MESSAGE_LABEL);
			throw e;
		}
	}
}
