package fortscale.streaming.task;

import fortscale.services.exceptions.HdfsException;
import fortscale.services.impl.SpringService;
import fortscale.streaming.common.SamzaContainerService;
import fortscale.streaming.exceptions.ExceptionHandler;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.exceptions.KeyValueDBException;
import fortscale.streaming.exceptions.TaskCoordinatorException;
import fortscale.streaming.service.FortscaleValueResolver;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.service.state.MessageCollectorStateDecorator;
import fortscale.streaming.task.metrics.HDFSWriterStreamingTaskMetrics;
import fortscale.streaming.task.metrics.StreamingTaskCommonMetrics;
import fortscale.streaming.task.monitor.MonitorMessaages;
import fortscale.streaming.task.monitor.TaskMonitoringHelper;
import fortscale.utils.ConversionUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.process.processInfo.ProcessInfoService;
import fortscale.utils.process.processInfo.ProcessInfoServiceImpl;
import fortscale.utils.process.processType.ProcessType;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Gauge;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static fortscale.streaming.ConfigUtils.getConfigString;

public abstract class AbstractStreamTask implements StreamTask, WindowableTask, InitableTask, ClosableTask {

	private static Logger logger = Logger.getLogger(AbstractStreamTask.class);

	private static final String DATA_SOURCE_FIELD_NAME = "data_source";
	protected static final String LAST_STATE_FIELD_NAME = "last_state";
	public static final String JOB_DATA_SOURCE = "Streaming";

	public static final StreamingTaskDataSourceConfigKey UNKNOW_CONFIG_KEY =
			new StreamingTaskDataSourceConfigKey("Unknonw","Unknonw");

	protected static final String KAFKA_MESSAGE_QUEUE = "kafka";
	protected static final String JOB_NAME_PROPERTY_NAME = "job.name";
	protected static final String STREAMING_PROCESS_GROUP_NAME = "streaming";

	private ExceptionHandler processExceptionHandler;
	private ExceptionHandler windowExceptionHandler;

	protected FortscaleValueResolver res;
	private SamzaContainerService samzaContainerService;

	private Config config;
	private TaskContext context;

	protected TaskMonitoringHelper<StreamingTaskDataSourceConfigKey> taskMonitoringHelper;

	// Process Info service
	ProcessInfoService processInfoService;

	// Job name from task's .properties file. Note process name is the jobName
	protected String jobName = "UNKNOWN";

	// Holds the stats service object. Derived class may use it to register their stats monitoring metrics groups
	protected StatsService statsService;

	// Streaming task common metrics. Note some fields are update by this class and some by the derived classes
	protected StreamingTaskCommonMetrics streamingTaskCommonMetrics;

	protected abstract void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception;
	protected abstract void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception;
	protected abstract void wrappedInit(Config config, TaskContext context) throws Exception;
	protected abstract void wrappedClose() throws Exception;

	protected String resolveStringValue(Config config, String string, FortscaleValueResolver resolver) {
		return resolver.resolveStringValue(getConfigString(config, string));
	}

	protected Boolean resolveBooleanValue(Config config, String string, FortscaleValueResolver resolver) {
		return resolver.resolveBooleanValue(getConfigString(config, string));
	}

	public AbstractStreamTask(){
		processExceptionHandler = new ExceptionHandler();
		fillExceptionHandler(processExceptionHandler);

		windowExceptionHandler = new ExceptionHandler();
		fillExceptionHandler(windowExceptionHandler);
	}

	public static void fillExceptionHandler(ExceptionHandler exceptionHandler){
		exceptionHandler.configNumOfContinuesExceptionsToFilter(KeyValueDBException.class, 1);
		exceptionHandler.configNumOfContinuesExceptionsToFilter(KafkaPublisherException.class, 1);
		exceptionHandler.configNumOfContinuesExceptionsToFilter(HdfsException.class, 1);
		exceptionHandler.configNumOfContinuesExceptionsToFilter(TaskCoordinatorException.class, 1);
	}

	public void init(Config config, TaskContext context) throws Exception {
		this.config = config;
		this.context = context;

		// Get job name from configuration (~task's .properties file)
		jobName = getConfigString(config, JOB_NAME_PROPERTY_NAME);

		logger.info("AbstractStreamingTask init() called. jobName={} className={}", jobName, this.getClass().getName());

		// Create process PID service and init it
		processInfoService  = new ProcessInfoServiceImpl(jobName, STREAMING_PROCESS_GROUP_NAME, ProcessType.DAEMON);
		processInfoService.init();

		// get spring context from configuration
		String contextPath = config.get("fortscale.context", "");

		// Create the Spring context
		if(StringUtils.isNotBlank(contextPath)){

			// Create the Spring Context but don't refresh it
			boolean isRefresh = false;
			SpringService.initExtended(contextPath, isRefresh);

			// Get the spring context
			ClassPathXmlApplicationContext springContext = SpringService.getInstance().getContext();

			// Register process at spring context (e.g. add basic process properties to Spring context)
			processInfoService.registerToSpringContext(springContext);

			// Refresh it
			springContext.refresh();
		}

		res = SpringService.getInstance().resolve(FortscaleValueResolver.class);

		// Init stats monitoring service
		initStatsMonitoringService(context);

		// Init task metrics
		createTaskMetrics();

		initTaskMonitoringHelper(config);

		samzaContainerService = SpringService.getInstance().resolve(SamzaContainerService.class);
		samzaContainerService.init(config, context);

		// call specific task init method
		wrappedInit(config, context);

		logger.info("Task init finished");
	}

	/**
	 * Init the stats monitoring service for streaming tasks
	 *
	 * It does the following
	 * 1. Get the stats service from Spring
	 * 2. Creates a Samaza gauge to schedule stats service period sample
	 *
	 * @param context
	 */
	protected void initStatsMonitoringService(TaskContext context) {

		// Get stats service from Spring
		statsService = SpringService.getInstance().resolve(StatsService.class);
		logger.debug("Stats service resolved to ", statsService);

		// Check disabled or failed to created
		if (statsService == null) {
			logger.error("Failed to get stats service. Is it disabled?.");
			return;
		}

		// An helper Samza gauge class that will call the stats service metrics update function
		// The gauge value is meaning less
		class StatsServiceMetricsUpdateGauge extends Gauge<Long> {
			long getValueCount = 0;
			StatsServiceMetricsUpdateGauge() {
				super("StatsServiceMetricsUpdateGauge", 0L);
				logger.debug("Stats service Samza gauge created");
			}

			/**
			 * This is the trick. The gauge getValue() function is called periodically by Samza metrics server
			 * The stats monitoring service uses this call to update all the stats metrics group
			 *
			 * The value returned is the number of calls made (which is useful only for debugging)
			 *
			 * @return number of time getValue() was called
			 */
			@Override
			public Long getValue() {

				// Increase the call count
				getValueCount++;

				logger.debug("Stats service Samza gauge getValue() called. Calling external stats service metrics update tick." +
						     " getValueCount={}", getValueCount);

				// Do it
				statsService.externalMetricsUpdateTick(0);

				return getValueCount;
			}
		}

		// Create a Samza gauge and register it
		StatsServiceMetricsUpdateGauge gauge = new StatsServiceMetricsUpdateGauge();
		context.getMetricsRegistry().newGauge(getClass().getName(), gauge);
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

			streamingTaskCommonMetrics.processedMessages++;

			samzaContainerService.setConfig(config);
			samzaContainerService.setContext(context);
			samzaContainerService.setCoordinator(coordinator);
			countNewMessage(envelope);
			String streamingTaskMessageState = resolveOutputMessageState();

			MessageCollectorStateDecorator messageCollectorStateDecorator = new MessageCollectorStateDecorator(collector);
			messageCollectorStateDecorator.setStreamingTaskMessageState(streamingTaskMessageState);
			samzaContainerService.setCollector(messageCollectorStateDecorator);

			wrappedProcess(envelope, messageCollectorStateDecorator, coordinator);

			processExceptionHandler.clear();
		} catch(Exception exception){
			streamingTaskCommonMetrics.processedMessagesExceptions++;

			String messageText = (String) envelope.getMessage();
			logger.error(String.format("Got an exception while processing stream message. Message text = %s. Exception: %s", messageText, exception.getMessage()), exception);

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

			streamingTaskCommonMetrics.windows++;

			samzaContainerService.setConfig(config);
			samzaContainerService.setContext(context);
			samzaContainerService.setCoordinator(coordinator);
			samzaContainerService.setCollector(collector);
			taskMonitoringHelper.saveJobStatusReport(getJobLabel(),true, JOB_DATA_SOURCE);
			wrappedWindow(collector, coordinator);
			windowExceptionHandler.clear();
		} catch(Exception exception){
			streamingTaskCommonMetrics.windowsExceptions++;

			logger.error("got an exception while processing window call", exception);
			windowExceptionHandler.handleException(exception);
		}
	}

	@Override
	public void close() throws Exception {
		try {
			logger.info("initiating task close");
			samzaContainerService.setConfig(config);
			samzaContainerService.setContext(context);
			taskMonitoringHelper.saveJobStatusReport(getJobLabel(),true,JOB_DATA_SOURCE);
			wrappedClose();
		} finally {
			SpringService.shutdown();
		}
		logger.info("task closed");
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

		streamingTaskCommonMetrics.handledUnfilteredMessage++;

		Long eventTime = ConversionUtils.convertToLong(event.get("date_time_unix"));
		taskMonitoringHelper.handleUnFilteredEvents(key, eventTime);
	}

	protected StreamingTaskDataSourceConfigKey extractDataSourceConfigKey(JSONObject message) {
		String dataSource = (String) message.get(DATA_SOURCE_FIELD_NAME);
		String lastState = (String) message.get(LAST_STATE_FIELD_NAME);

		if (dataSource == null) {
			streamingTaskCommonMetrics.messagesWithoutDataSourceName++;
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
			streamingTaskCommonMetrics.parseMessageToJson++;
			String messageText = (String) envelope.getMessage();
			return (JSONObject) JSONValue.parseWithException(messageText);
		} catch (ParseException e){
			streamingTaskCommonMetrics.parseMessageToJsonExceptions++;
			taskMonitoringHelper.countNewFilteredEvents(null, MonitorMessaages.CANNOT_PARSE_MESSAGE_LABEL);
			throw e;
		}
	}

	/**
	 * Create the task's metrics. This function should be overridden by the specific task
	 *
	 * Typically, the function is called from init(). However it might be called directed in some tests as well.
	 */
	public void createTaskMetrics() {

		// Create the common metrics
		streamingTaskCommonMetrics = new StreamingTaskCommonMetrics(statsService);

		// Create specific metrics
		wrappedCreateTaskMetrics();
	}

	/**
	 * Create the task's metrics.
	 *
	 * NOTE: This function should be overridden by the specific task
	 *
	 */
	protected void wrappedCreateTaskMetrics() {

		logger.warn("Task {} does not implement createTaskMetrics(),pls do", jobName);

	}

	/**
	 * Get topic name out of incoming message envelope
	 * @param envelope - message received in {@link #wrappedProcess(IncomingMessageEnvelope, MessageCollector, TaskCoordinator)}
	 * @return topic name of incoming message
     */
	protected String getIncomingMessageTopicName(IncomingMessageEnvelope envelope) {
		return envelope.getSystemStreamPartition().getSystemStream().getStream();
	}

	// --- getters/setters ---

	public StatsService getStatsService() {
		return statsService;
	}

	public String getJobName() {
		return jobName;
	}
}
