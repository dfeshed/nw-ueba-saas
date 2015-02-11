package fortscale.streaming.task;

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
			wrappedProcess(envelope, collector, coordinator);
			processExceptionHandler.clear();
		} catch(Exception exception){
			logger.error("got an exception while processing steam message", exception);
			processExceptionHandler.handleException(exception);
		}		
	}
	
	@Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception{
		try{
			wrappedWindow(collector, coordinator);
			windowExceptionHandler.clear();
		} catch(Exception exception){
			logger.error("got an exception while processing steam message", exception);
			windowExceptionHandler.handleException(exception);
		}
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
