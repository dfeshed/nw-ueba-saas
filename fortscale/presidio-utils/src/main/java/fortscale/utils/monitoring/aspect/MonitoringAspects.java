package fortscale.utils.monitoring.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.dropwizard.DropwizardMetricServices;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static fortscale.utils.monitoring.aspect.MetricsNames.CALLING_METHOD_METRIC;
import static fortscale.utils.monitoring.aspect.MetricsNames.EXCEPTION_THROWED_METRIC;
import static fortscale.utils.monitoring.aspect.MetricsNames.FINISHED_SUCCESSFULLY_METHOD_METRIC;
import static fortscale.utils.monitoring.aspect.MetricsNames.NUMBER_OF_FAILED_VALIDATION_METRIC;
import static fortscale.utils.monitoring.aspect.MetricsNames.RUN_TIME_METRIC;

@Aspect
@Component
public class MonitoringAspects {


    @Autowired
    private DropwizardMetricServices counterService;

    @Before("@annotation(fortscale.utils.monitoring.aspect.annotations.CallingMethodMetric)")
    public void callingMethodMetric(JoinPoint joinPoint) {
        counterService.increment(new StringBuffer(joinPoint.getSignature().toShortString()).append(CALLING_METHOD_METRIC).toString());
    }

    @After("@annotation(fortscale.utils.monitoring.aspect.annotations.FinishedSuccessfullyMethodMetric)")
    public void finishedSuccessfullyMethodMetric(JoinPoint joinPoint) {
        counterService.increment(new StringBuffer(joinPoint.getSignature().toShortString()).append(FINISHED_SUCCESSFULLY_METHOD_METRIC).toString());
    }

    @AfterThrowing("@annotation(fortscale.utils.monitoring.aspect.annotations.ExceptionThrowedMetric)")
    public void exceptionThrowedMetric(JoinPoint joinPoint) {
        counterService.increment(new StringBuffer(joinPoint.getSignature().toShortString()).append(EXCEPTION_THROWED_METRIC).toString());
    }

    /**
     * This method provides us the duration of a method invocation.
     * The annotation Around lets us perform custom behavior before and after a method invocation.
     * This behavior occurs when a method is annotated with the annotation @IncrementMetricAround.
     *
     * @param joinPoint - a point that represent a methods execution, holds data on the method that is going to be executed.
     * @throws Throwable - any exceptin that can be thrown from the execution of the method.
     */

    @Around("@annotation(fortscale.utils.monitoring.aspect.annotations.RunTimeMetric)")
    public void runTimeMetric(ProceedingJoinPoint joinPoint) throws Throwable {
        String metricName = joinPoint.getSignature().toShortString() + RUN_TIME_METRIC;
        Instant startTime = Instant.now();
        joinPoint.proceed();
        Instant endTime = Instant.now();
        counterService.submit(metricName, Duration.between(startTime, endTime).getSeconds());
    }

    /**
     * This method provides us the total number of documents sent to validate and the amount that pass the validation.
     * The annotation Around lets us perform custom behavior before and after a method invocation.
     * This behavior occurs when a method is annotated with the annotation @ValidatorMetricAround
     * and returns type List<? extends AbstractAuditableDocument> not null.
     *
     * @param joinPoint - a point that represent a methods execution, holds data on the method that is going to be executed.
     * @throws Throwable - any exceptin that can be thrown from the execution of the method.
     */
    @Around("@annotation(fortscale.utils.monitoring.aspect.annotations.NumberOfFailedValidationMetric)")
    public void numberOfFailedValidationMetric(ProceedingJoinPoint joinPoint) throws Throwable {
        StringBuffer metricName = new StringBuffer(joinPoint.getSignature().toShortString());
        List<? extends Serializable> retVal = (List<? extends Serializable>) joinPoint.proceed();
        counterService.submit(metricName.append(NUMBER_OF_FAILED_VALIDATION_METRIC).toString(), retVal.size());
    }

}
