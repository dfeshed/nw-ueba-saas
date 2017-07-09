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

import static fortscale.utils.common.general.CommonStrings.INCREMENT_METRIC_AFTER;
import static fortscale.utils.common.general.CommonStrings.INCREMENT_METRIC_AFTER_THROWING;
import static fortscale.utils.common.general.CommonStrings.INCREMENT_METRIC_BEFORE;
import static fortscale.utils.common.general.CommonStrings.RUN_TIME_METRIC;
import static fortscale.utils.common.general.CommonStrings.VALIDATOR_METRIC_AROUND_AFTER_VALIDATION;
import static fortscale.utils.common.general.CommonStrings.VALIDATOR_METRIC_AROUND_BEFORE_VALIDATION;

@Aspect
@Component
public class MonitoringAspects {


    @Autowired
    private DropwizardMetricServices counterService;

    @Before("@annotation(fortscale.utils.monitoring.aspect.annotations.IncrementMetricBefore)")
    public void incrementMetricBefore(JoinPoint joinPoint) {
        counterService.increment(joinPoint.getSignature().toShortString() + INCREMENT_METRIC_BEFORE);
    }

    @After("@annotation(fortscale.utils.monitoring.aspect.annotations.IncrementMetricAfter)")
    public void incrementMetricAfter(JoinPoint joinPoint) {
        counterService.increment(joinPoint.getSignature().toShortString() + INCREMENT_METRIC_AFTER);
    }

    @AfterThrowing("@annotation(fortscale.utils.monitoring.aspect.annotations.IncrementMetricAfterThrowing)")
    public void incrementMetricAfterThrowing(JoinPoint joinPoint) {
        counterService.increment(joinPoint.getSignature().toShortString() + INCREMENT_METRIC_AFTER_THROWING);
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
     * This behavior occurs when a method is annotated with the annotation @ValidatorMetricAround ,has single arg with the name documents
     * of type List<? extends AbstractAuditableDocument> and returns type List<? extends AbstractAuditableDocument> not null.
     * @param documents - list of rows from repository that going to be validated.
     * @param joinPoint - a point that represent a methods execution, holds data on the method that is going to be executed.
     * @throws Throwable - any exceptin that can be thrown from the execution of the method.
     */
    @Around("@annotation(fortscale.utils.monitoring.aspect.annotations.ValidatorMetricAround)&& args(documents)")
    public void validatorMetricAround(ProceedingJoinPoint joinPoint, List<? extends Serializable> documents) throws Throwable {
        String metricName = joinPoint.getSignature().toShortString();
        counterService.submit(metricName + VALIDATOR_METRIC_AROUND_BEFORE_VALIDATION, documents.size());
        List<? extends Serializable> retVal =(List<? extends Serializable>) joinPoint.proceed();
        counterService.submit(metricName + VALIDATOR_METRIC_AROUND_AFTER_VALIDATION,retVal.size());
    }

}
