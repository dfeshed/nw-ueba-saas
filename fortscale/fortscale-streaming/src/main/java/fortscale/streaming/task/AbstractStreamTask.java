package fortscale.streaming.task;

import fortscale.streaming.exceptions.*;
import fortscale.streaming.service.FortscaleStringValueResolver;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.service.state.MessageCollectorStateDecorator;
import fortscale.streaming.task.monitor.TaskMonitoringHelper;
import fortscale.utils.ConversionUtils;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;

import static fortscale.streaming.ConfigUtils.getConfigString;

public abstract class AbstractStreamTask implements StreamTask, WindowableTask, InitableTask, ClosableTask {

	private static Logger logger = Logger.getLogger(AbstractStreamTask.class);

	private static final String DATA_SOURCE_FIELD_NAME = "data_source";
	private static final String LAST_STATE_FIELD_NAME = "last_state";
	protected static final String NO_STATE_CONFIGURATION_MESSAGE = "Cannot find configuration for state";
	protected static final String CANNOT_EXTRACT_STATE_MESSAGE = "Message not contains DataSource and / or LastState";
	public static final StreamingTaskDataSourceConfigKey UNKNOW_CONFIG_KEY =
						new StreamingTaskDataSourceConfigKey("Unknonw","Unknonw");


	public static final String CANNOT_PARSE_MESSAGE_LABEL = "Cannot parse message";

	protected static final String KAFKA_MESSAGE_QUEUE = "kafka";

	private ExceptionHandler processExceptionHandler;
	private ExceptionHandler windowExceptionHandler;

	protected  FortscaleStringValueResolver res;



	protected TaskMonitoringHelper taskMonitoringHelper;

	protected abstract void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception;
	protected abstract void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception;
	protected abstract void wrappedInit(Config config, TaskContext context) throws Exception;
	protected abstract void wrappedClose() throws Exception;

	protected String resolveStringValue(Config config, String string, FortscaleStringValueResolver resolver) {
		return resolver.resolveStringValue(getConfigString(config, string));
	}

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

		initTaskMonitoringHelper(config);

		// call specific task init method
		wrappedInit(config, context);

		logger.info("Task init finished");
	}

	private void initTaskMonitoringHelper(Config config) {
		taskMonitoringHelper = SpringService.getInstance().resolve(TaskMonitoringHelper.class);

		boolean isMonitoredTask = config.getBoolean("fortscale.monitoring.enable",false);
		taskMonitoringHelper.setIsMonitoredTask(isMonitoredTask);
		taskMonitoringHelper.resetCountersPerWindow();
	}

	@Override
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		try{
			countNewMessage(envelope);
			String streamingTaskMessageState = resolveOutputMessageState();

			MessageCollectorStateDecorator messageCollectorStateDecorator = new MessageCollectorStateDecorator(collector);
			messageCollectorStateDecorator.setStreamingTaskMessageState(streamingTaskMessageState);

			wrappedProcess(envelope, messageCollectorStateDecorator, coordinator);

			processExceptionHandler.clear();
		} catch(Exception exception){
			String messageText = (String) envelope.getMessage();
			logger.error("got an exception while processing stream message. Message text = {}. Exception: {}", messageText, exception);

			processExceptionHandler.handleException(exception);
		}
	}

	private void countNewMessage(IncomingMessageEnvelope envelope) {
		try {
			JSONObject message = parseJsonMessage(envelope);
			taskMonitoringHelper.handleNewEvent(extractDataSourceConfigKey(message));
		} catch (Exception e){
			//Do nothing
		}
	}

	@Override
	public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception{
		try{
			taskMonitoringHelper.saveJobStatusReport(getJobLabel(),true);
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
			taskMonitoringHelper.saveJobStatusReport(getJobLabel(),true);
			wrappedClose();
		} finally {
			SpringService.shutdown();
		}
		logger.info("task closed");
	}



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
	protected void handleUnfilteredEvent(JSONObject event, StreamingTaskDataSourceConfigKey key){



		Long eventTime = ConversionUtils.convertToLong(event.get("date_time_unix"));
		String eventTimeAsString = event.getAsString("date_time");
		taskMonitoringHelper.handleUnFilteredEvents(key, eventTime, eventTimeAsString);
	}

	protected StreamingTaskDataSourceConfigKey extractDataSourceConfigKey(JSONObject message) {
		String dataSource = (String) message.get(DATA_SOURCE_FIELD_NAME);
		String lastState = (String) message.get(LAST_STATE_FIELD_NAME);

		if (dataSource == null) {
			throw new IllegalStateException("Message does not contain " + DATA_SOURCE_FIELD_NAME + " field: " + message.toJSONString());
		}

		return new StreamingTaskDataSourceConfigKey(dataSource, lastState);
	}

	//Get the data source and last state without exception, return  null if cannot extract
	protected StreamingTaskDataSourceConfigKey extractDataSourceConfigKeySafe(JSONObject message) {
		StreamingTaskDataSourceConfigKey configKey;
		try {
			configKey = extractDataSourceConfigKey(message);
		} catch (Exception e){
			return null;
		}
		return configKey;
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
