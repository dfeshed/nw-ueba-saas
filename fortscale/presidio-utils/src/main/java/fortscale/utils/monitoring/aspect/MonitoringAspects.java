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

@Aspect
@Component
public class MonitoringAspects {

    @Autowired
    private DropwizardMetricServices counterService;

    @Before("@annotation(fortscale.utils.monitoring.aspect.annotations.IncrementMetricBefore)")
    public void incrementMetricBefore(JoinPoint joinPoint) {
        counterService.increment(joinPoint.getSignature().toShortString());
    }

    @After("@annotation(fortscale.utils.monitoring.aspect.annotations.IncrementMetricAfter)")
    public void incrementMetricAfter(JoinPoint joinPoint) {
        counterService.increment(joinPoint.getSignature().toShortString());
    }

    @AfterThrowing("@annotation(fortscale.utils.monitoring.aspect.annotations.IncrementMetricAfterThrowing)")
    public void incrementMetricAfterThrowing(JoinPoint joinPoint) {
        counterService.increment(joinPoint.getSignature().toShortString());
    }

    @Around("@annotation(fortscale.utils.monitoring.aspect.annotations.IncrementMetricAround)")
    public void incrementMetricAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String metricName = joinPoint.getSignature().toShortString();
        counterService.increment(metricName + "start");
        joinPoint.proceed();
        counterService.increment(metricName + "finish");


    }

    @Around("@annotation(fortscale.utils.monitoring.aspect.annotations.IncrementMetricAround)")
    public void totalRunTimeMetric(ProceedingJoinPoint joinPoint) throws Throwable {
        String metricName = joinPoint.getSignature().toShortString();
        long startTime=System.currentTimeMillis();
        joinPoint.proceed();
        long endTime=System.currentTimeMillis();
        counterService.submit(metricName ,endTime-startTime);
    }

}
