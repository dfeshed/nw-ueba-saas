package fortscale.utils.logging.aop;

import java.lang.reflect.Method;
import java.util.Arrays;

import fortscale.utils.logging.annotation.HideSensitiveArgumentsFromLog;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import fortscale.utils.logging.Logger;

@Component("loggingAspect")
public class LoggingAspect {

	public void logException(JoinPoint joinPoint, Throwable exception) {
		Class<?> loggedClass = joinPoint.getTarget().getClass(); 
		Logger logger = Logger.getLogger(loggedClass);
		logger.error("{}, message: '{}', method: {}() - args: {}", 
				new Object[]{
				exception.getClass().getName(),
				StringUtils.isEmpty(exception.getMessage()) ? "" : exception.getMessage(),
				joinPoint.getSignature().getName(), 
				Arrays.asList(joinPoint.getArgs()), exception});
	}
	
	public void logBeforeCall(JoinPoint joinPoint) {
		Class<?> loggedClass = joinPoint.getTarget().getClass();
		Logger logger = Logger.getLogger(loggedClass);

		boolean shouldPrintArguments = !areRegumentsSensitive(joinPoint);

		if (shouldPrintArguments){
		logger.info("method: {}() - args: {}", 
				new Object[]{
				joinPoint.getSignature().getName(), 
				Arrays.asList(joinPoint.getArgs())});
		} else {
			logger.info("method: {}() - args: Sensitive Args not displayed",
					new Object[]{joinPoint.getSignature().getName()});
		}
	}

	/**
	 * That method return true only if the arguments are not sensitive.
	 * Sensitive arguments will not be printed
	 *
	 * @param joinPoint
	 * @return
	 */
	private boolean areRegumentsSensitive(JoinPoint joinPoint) {

		//Try to fetch HideSensitiveArgumentsFromLog annotation from method, if such anotation exists
		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		HideSensitiveArgumentsFromLog isSensitiveArgumentAnotation = method.getAnnotation(HideSensitiveArgumentsFromLog.class);

		if (isSensitiveArgumentAnotation==null){
			//No such anotation, arguments are not sensitive
			return false;
		} else{

			if (isSensitiveArgumentAnotation.sensitivityCondition()== null){
				//If there is LogHideSensitive annotation without sensitivity function, all the arguments sensitive
				return true;
			}
			//Print the arguments if the function allows it
			return isSensitiveArgumentAnotation.sensitivityCondition().getIsSensitiveFunction().apply(joinPoint.getArgs());

		}

	}


}
