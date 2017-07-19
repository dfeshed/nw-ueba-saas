package fortscale.utils.monitoring.aspect;


import fortscale.utils.logging.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.boot.actuate.metrics.dropwizard.DropwizardMetricServices;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

import static fortscale.utils.monitoring.aspect.MetricsNames.END;
import static fortscale.utils.monitoring.aspect.MetricsNames.EXCEPTION_THROWN;
import static fortscale.utils.monitoring.aspect.MetricsNames.NUMBER_OF_FAILED_VALIDATION;
import static fortscale.utils.monitoring.aspect.MetricsNames.RUN_TIME;
import static fortscale.utils.monitoring.aspect.MetricsNames.START;

@Aspect
@Component
public class MonitoringAspects {

    private static final Logger logger = Logger.getLogger(MonitoringAspects.class);

    @Autowired
    public MetricsEndpoint metricsEndpoint;

    @Autowired
    public CustomMetric customMetric;

    private boolean isRegisterd =false;

    public MonitoringAspects(){
        logger.info("Aspect is activated");
    }


    private boolean toRegister(){
        if(!isRegisterd){
            return isRegisterd=true;
        }
        return false;
    }

    /**
     * This method provides us counting of a method invocation.
     * The annotation Before lets us perform custom behavior before a method invocation.
     * This behavior occurs when a method is annotated with the annotation @start.
     *
     * @param joinPoint - a point that represent a methods execution, holds data on the method that is going to be executed.
     * @throws Throwable - any exceptin that can be thrown from the execution of the method.
     */

    @Before("@annotation(fortscale.utils.monitoring.aspect.annotations.Start)")
    public void start(JoinPoint joinPoint) {
        String metric = joinPoint.getSignature().toShortString();
        logger.info("Metric {} increment with annotation Start. ", metric);
        customMetric.addIncrementMetric(new StringBuilder(metric).append(START).toString());
        if(toRegister())
            metricsEndpoint.registerPublicMetrics(customMetric);
    }

    /**
     * This method provides us counting of a method invocation.
     * The annotation After lets us perform custom behavior after a method invocation.
     * This behavior occurs when a method is annotated with the annotation @End.
     *
     * @param joinPoint - a point that represent a methods execution, holds data on the method that is going to be executed.
     * @throws Throwable - any exceptin that can be thrown from the execution of the method.
     */

    @After("@annotation(fortscale.utils.monitoring.aspect.annotations.End)")
    public void end(JoinPoint joinPoint) {
        String metric  = joinPoint.getSignature().toShortString();
        logger.info("Metric {} increment with annotation End. ", metric);
        customMetric.addIncrementMetric(new StringBuilder(metric).append(END).toString());
        if(toRegister())
            metricsEndpoint.registerPublicMetrics(customMetric);
    }

    /**
     * This method provides us counting of a method throwing exception.
     * The annotation AfterThrowing lets us perform custom behavior after a method invocation.
     * This behavior occurs when a method is annotated with the annotation @ExceptionThrown.
     *
     * @param joinPoint - a point that represent a methods execution, holds data on the method that is going to be executed.
     * @throws Throwable - any exceptin that can be thrown from the execution of the method.
     */

    @AfterThrowing("@annotation(fortscale.utils.monitoring.aspect.annotations.ExceptionThrown)")
    public void exceptionThrown(JoinPoint joinPoint) {
        String metric  = joinPoint.getSignature().toShortString();
        logger.info("Metric {} increment with annotation exceptionThrown. ", metric);
        customMetric.addMetric(new StringBuilder(metric).append(EXCEPTION_THROWN).toString(),1);
        if(toRegister())
            metricsEndpoint.registerPublicMetrics(customMetric);

    }

    /**
     * This method provides us the duration of a method invocation.
     * The annotation Around lets us perform custom behavior before and after a method invocation.
     * This behavior occurs when a method is annotated with the annotation @runTime.
     *
     * @param joinPoint - a point that represent a methods execution, holds data on the method that is going to be executed.
     * @throws Throwable - any exceptin that can be thrown from the execution of the method.
     */

    @Around("@annotation(fortscale.utils.monitoring.aspect.annotations.RunTime)")
    public void runTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String metricName = joinPoint.getSignature().toShortString();
        long startTime = System.nanoTime();
        joinPoint.proceed();
        long endTime = System.nanoTime();
        long time=Long.divideUnsigned(endTime-startTime,1000000000);
        customMetric.addGaugeMetric(new StringBuilder(metricName).append(RUN_TIME).toString(),(int)time);
        if(toRegister())
            metricsEndpoint.registerPublicMetrics(customMetric);
        logger.info("Metric {} run time is {} milli seconds. ", metricName,time);
    }

    /**
     * This method provides us the total number of documents sent to validate and the amount that pass the validation.
     * The annotation Around lets us perform custom behavior before and after a method invocation.
     * This behavior occurs when a method is annotated with the annotation @numberOfFailedValidation
     * and returns type List<? extends AbstractAuditableDocument> not null.
     *
     * @param joinPoint - a point that represent a methods execution, holds data on the method that is going to be executed.
     * @throws Throwable - any exceptin that can be thrown from the execution of the method.
     */
    @Around("@annotation(fortscale.utils.monitoring.aspect.annotations.NumberOfFailedValidation)")
    public void numberOfFailedValidation(ProceedingJoinPoint joinPoint) throws Throwable {
        String metricName = joinPoint.getSignature().toShortString();
        int numberOfFailedValidationDocuments = ((List<? extends Serializable>) joinPoint.proceed()).size();
        customMetric.addGaugeMetric(new StringBuilder(metricName).append(NUMBER_OF_FAILED_VALIDATION).toString(),numberOfFailedValidationDocuments);
        if(toRegister())
            metricsEndpoint.registerPublicMetrics(customMetric);
        logger.info("Metric {} got {} failed validations. ", metricName, numberOfFailedValidationDocuments);
    }

}
