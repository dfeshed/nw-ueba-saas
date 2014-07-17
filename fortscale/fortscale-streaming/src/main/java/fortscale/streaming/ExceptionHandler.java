package fortscale.streaming;

import java.util.HashMap;
import java.util.Map;

import fortscale.utils.logging.Logger;

public class ExceptionHandler {
	private static Logger logger = Logger.getLogger(ExceptionHandler.class);

	private int defaultNumOfContinuesExceptionToFilter = 10;
	
	private int numOfContinuesExceptions = 0;
	
	Map<Class<Exception>, Integer> numOfContinuesExceptionsToFilterMap = new HashMap<>();
	
	public ExceptionHandler(){}
	
	public void configNumOfContinuesExceptionsToFilter(Class<Exception> exceptionClass, int numOfContinuesExceptionsToFilter){
		numOfContinuesExceptionsToFilterMap.put(exceptionClass, numOfContinuesExceptionsToFilter);
	}
	
	public void handleException(Exception e) throws Exception{
		numOfContinuesExceptions++;
		Integer numOfContinuesExceptionToFilter = numOfContinuesExceptionsToFilterMap.get(e.getClass());
		numOfContinuesExceptionToFilter = numOfContinuesExceptionToFilter != null ? numOfContinuesExceptionToFilter : defaultNumOfContinuesExceptionToFilter;
		if(numOfContinuesExceptions >= numOfContinuesExceptionToFilter){
			logger.error("continuesly got the following exception", e);
			throw e;
		}
	}
	
	public void clear(){
		numOfContinuesExceptions = 0;
	}
}
