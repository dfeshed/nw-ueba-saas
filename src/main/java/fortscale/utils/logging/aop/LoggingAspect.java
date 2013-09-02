package fortscale.utils.logging.aop;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
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
}
