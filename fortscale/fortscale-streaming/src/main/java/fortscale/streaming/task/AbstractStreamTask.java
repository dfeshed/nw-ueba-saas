package fortscale.streaming.task;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.WindowableTask;

import fortscale.streaming.exceptions.ExceptionHandler;
import fortscale.streaming.exceptions.HdfsException;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.exceptions.LevelDbException;
import fortscale.streaming.exceptions.TaskCoordinatorException;
import fortscale.utils.logging.Logger;

public abstract class AbstractStreamTask implements StreamTask, WindowableTask{
	private static Logger logger = Logger.getLogger(AbstractStreamTask.class);
	
	private ExceptionHandler processExceptionHandler;
	private ExceptionHandler windowExceptionHandler;
	
	protected abstract void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception;
	protected abstract void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception;
	
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
}
