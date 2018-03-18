package fortscale.utils.logging.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface HideSensitiveArgumentsFromLog {
	//This annotation indicate that LogAspect should skip loging the arguments of current methods.
	//According to the function- the LogAspect know if the parameters are sensitive
	LogSensitiveFunctionsAsEnum sensitivityCondition() default LogSensitiveFunctionsAsEnum.ALWAYS_SENSITIVE;

}
