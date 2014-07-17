package fortscale.streaming.task;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskCoordinator;

import fortscale.streaming.ExceptionHandler;
import fortscale.utils.logging.Logger;

public abstract class AbstractStreamTask implements StreamTask{
	private static Logger logger = Logger.getLogger(AbstractStreamTask.class);
	
	private ExceptionHandler exceptionHandler = new ExceptionHandler();
	
	protected abstract void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception;

	public void configNumOfContinuesExceptionsToFilter(Class<Exception> exceptionClass, int numOfContinuesExceptionsToFilter){
		exceptionHandler.configNumOfContinuesExceptionsToFilter(exceptionClass, numOfContinuesExceptionsToFilter);
	}
	@Override
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		Exception e = null;
		try{
			wrappedProcess(envelope, collector, coordinator);
		} catch(Exception exception){
			logger.error("got an exception while processing steam message", exception);
			e = exception;
		}
		
		if(e != null){
			exceptionHandler.handleException(e);
		} else{
			exceptionHandler.clear();
		}
	}
}
