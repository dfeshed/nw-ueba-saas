package presidio.monitoring.aspect;


import fortscale.utils.logging.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import presidio.monitoring.endPoint.PresidioMetricBucket;
import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Aspect
@Component
public class MonitoringAspects {

    private static final Logger logger = Logger.getLogger(MonitoringAspects.class);
    private final String START = ".Start";
    private final String END = ".End";
    private final String EXCEPTION_THROWN = ".ExceptionThrown";
    private final String RUN_TIME = ".RunTime";
    private final String NUMBER_OF_FAILED_VALIDATION = ".NumberOfFailedValidation";
    private final String NUMBER_OF_FILTERED_EVENTS = ".NumberOfFilteredEvents";


    private PresidioMetricBucket presidioMetricBucket;

    public MonitoringAspects() {
    }

    public void setMetrics(PresidioMetricBucket presidioMetricBucket) {
        this.presidioMetricBucket = presidioMetricBucket;
    }

    /**
     * This method provides us counting of a method invocation.
     * The annotation Before lets us perform custom behavior before a method invocation.
     * This behavior occurs when a method is annotated with the annotation @start.
     *
     * @param joinPoint - a point that represent a methods execution, holds data on the method that is going to be executed.
     * @throws Throwable - any exceptin that can be thrown from the execution of the method.
     */

    @Before("@annotation(presidio.monitoring.aspect.annotations.Start)")
    public void start(JoinPoint joinPoint) throws Throwable {
        String metric = joinPoint.getSignature().toShortString() + START;
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName(metric).
                setMetricValue(1).
                setMetricUnit(MetricEnums.MetricUnitType.NUMBER).
                build());
        logger.info("Metric {} increment with annotation Start. ", metric);
    }

    /**
     * This method provides us counting of a method invocation.
     * The annotation After lets us perform custom behavior after a method invocation.
     * This behavior occurs when a method is annotated with the annotation @End.
     *
     * @param joinPoint - a point that represent a methods execution, holds data on the method that is going to be executed.
     * @throws Throwable - any exceptin that can be thrown from the execution of the method.
     */

    @After("@annotation(presidio.monitoring.aspect.annotations.End)")
    public void end(JoinPoint joinPoint) throws Throwable {
        String metric = joinPoint.getSignature().toShortString() + END;
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName(metric).
                setMetricValue(1).
                setMetricUnit(MetricEnums.MetricUnitType.NUMBER).
                build());
        logger.debug("Metric {} increment with annotation End. ", metric);
    }

    /**
     * This method provides us counting of a method throwing exception.
     * The annotation AfterThrowing lets us perform custom behavior after a method invocation.
     * This behavior occurs when a method is annotated with the annotation @ExceptionThrown.
     *
     * @param joinPoint - a point that represent a methods execution, holds data on the method that is going to be executed.
     * @throws Throwable - any exceptin that can be thrown from the execution of the method.
     */

    @AfterThrowing("@annotation(presidio.monitoring.aspect.annotations.ExceptionThrown)")
    public void exceptionThrown(JoinPoint joinPoint) throws Throwable {
        String metric = joinPoint.getSignature().toShortString() + EXCEPTION_THROWN;
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName(metric).
                setMetricValue(1).
                setMetricUnit(MetricEnums.MetricUnitType.NUMBER).
                build());
        logger.debug("Metric {} increment with annotation exceptionThrown. ", metric);
    }

    /**
     * This method provides us the duration of a method invocation.
     * The annotation Around lets us perform custom behavior before and after a method invocation.
     * This behavior occurs when a method is annotated with the annotation @runTime.
     *
     * @param joinPoint - a point that represent a methods execution, holds data on the method that is going to be executed.
     * @throws Throwable - any exceptin that can be thrown from the execution of the method.
     */

    @Around("@annotation(presidio.monitoring.aspect.annotations.RunTime)")
    public void runTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String metricName = joinPoint.getSignature().toShortString() + RUN_TIME;
        long startTime = System.nanoTime();
        joinPoint.proceed();
        long endTime = System.nanoTime();
        long time = Long.divideUnsigned(endTime - startTime, 1000000000);
        Map<MetricEnums.MetricValues, Number> map = new HashMap<>();
        map.put(MetricEnums.MetricValues.DEFAULT_METRIC_VALUE, time);
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName(metricName).
                setMetricMultipleValues(map).
                setMetricUnit(MetricEnums.MetricUnitType.DATE).
                build());
        logger.debug("Metric {} run time is {} milli seconds. ", metricName, time);
    }

    /**
     * The annotation Around lets us perform custom behavior before and after a method invocation.
     * This behavior occurs when a method is annotated with the annotation @runTime.
     *
     * @param joinPoint - a point that represent a methods execution, holds data on the method that is going to be executed.
     * @throws Throwable - any exceptin that can be thrown from the execution of the method.
     */

    @Around("@annotation(presidio.monitoring.aspect.annotations.NumberOfFilteredEvents)")
    public Object numberOfFilteredEvents(ProceedingJoinPoint joinPoint) throws Throwable {
        String metricName = joinPoint.getSignature().toShortString() + NUMBER_OF_FILTERED_EVENTS;
        List events = (List) joinPoint.getArgs()[0];
        String schema = joinPoint.getArgs()[1].toString();
        int numberOfEventsEntered = events.size();
        Object returnVal = joinPoint.proceed();
        List result = (List) returnVal;
        int numberOfFilteredEvents = numberOfEventsEntered - result.size();
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        tags.put(MetricEnums.MetricTagKeysEnum.SCHEMA, schema);
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName(metricName).
                setMetricValue(numberOfFilteredEvents).
                setMetricTags(tags).
                setMetricUnit(MetricEnums.MetricUnitType.NUMBER).
                build());
        logger.debug("Metric {} add {} events filtered. ", metricName, numberOfFilteredEvents);
        return returnVal;
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
    @Around("@annotation(presidio.monitoring.aspect.annotations.NumberOfFailedValidation)")
    public void numberOfFailedValidation(ProceedingJoinPoint joinPoint) throws Throwable {
        String metricName = joinPoint.getSignature().toShortString() + NUMBER_OF_FAILED_VALIDATION;
        int numberOfFailedValidationDocuments = ((List<? extends Serializable>) joinPoint.proceed()).size();
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName(metricName).
                setMetricValue(numberOfFailedValidationDocuments).
                setMetricUnit(MetricEnums.MetricUnitType.NUMBER).
                build());
        logger.debug("Metric {} got {} failed validations. ", metricName, numberOfFailedValidationDocuments);
    }

//Example
    /**
     * This method provides us counting of a method invocation that process a data source .
     * The annotation Before lets us perform custom behavior before a method invocation.
     * This behavior occurs when a method is annotated with the annotation @DataSourceProcess.
     *
     * @param joinPoint - a point that represent a methods execution, holds data on the method that is going to be executed.
     * @param schema    - enum of a type date source.
     * @throws Throwable - any exceptin that can be thrown from the execution of the method.
     */
/*
    @Before("@annotation(presidio.monitoring.aspect.annotations.DataSourceProcess) && args(schema,..)")
    public void dataSourceProcess(JoinPoint joinPoint, Schema schema) throws Throwable {
        String metric = joinPoint.getSignature().toShortString() + schema.getName();
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap();
        tags.put(MetricEnums.MetricTagKeysEnum.SCHEMA, schema.getName());
        presidioMetricBucket.addMetric(new Metric.MetricBuilder().
                setMetricName(metric).
                setMetricValue(1).
                setMetricTags(tags).
                setMetricUnit(MetricEnums.MetricUnitType.NUMBER).
                build());
        logger.debug("Metric {} increment with annotation DataSourceProcess. ", metric);
    }
*/
}
