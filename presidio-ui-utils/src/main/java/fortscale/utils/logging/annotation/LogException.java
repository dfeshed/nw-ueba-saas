package fortscale.utils.logging.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import fortscale.utils.logging.aop.LoggingAspect;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface LogException {
	boolean logException() default true;

	boolean ignoreDeclaredExceptions() default true;
	
	Class<?> loggedClass() default LoggingAspect.class;
}
