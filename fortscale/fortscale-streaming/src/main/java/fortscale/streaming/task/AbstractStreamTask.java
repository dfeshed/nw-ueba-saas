package fortscale.streaming.task;

import fortscale.streaming.exceptions.*;
import fortscale.streaming.service.SpringService;
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
	
	private ExceptionHandler processExceptionHandler;
	private ExceptionHandler windowExceptionHandler;
	
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
        logger.info("Task init finished");
	}
	
	@Override
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		try{
			updateLastState(envelope);
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
			wrappedWindow(collector, coordinator);
			windowExceptionHandler.clear();
		} catch(Exception exception){
			logger.error("got an exception while processing stream message", exception);
			windowExceptionHandler.handleException(exception);
		}
    }

	protected void updateLastState(IncomingMessageEnvelope envelope) throws ParseException {
		String messageText = (String)envelope.getMessage();

		JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);
		message.put("last_state", this.getClass().getName());
	}
	
	@Override 
	public void close() throws Exception {
		try {
            logger.info("initiating task close");
			wrappedClose();
		} finally {
			SpringService.shutdown();
		}
        logger.info("task closed");
	}
}
