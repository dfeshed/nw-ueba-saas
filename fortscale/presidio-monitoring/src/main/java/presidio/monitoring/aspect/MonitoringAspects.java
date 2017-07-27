package presidio.monitoring.aspect;


import fortscale.common.general.DataSource;
import fortscale.utils.logging.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.stereotype.Component;
import presidio.monitoring.aspect.metrics.PresidioCustomMetrics;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



@Aspect
@Component
public class MonitoringAspects {

    private static final Logger logger = Logger.getLogger(MonitoringAspects.class);
    private final String START = ".Start";
    private final String END = ".End";
    private final String EXCEPTION_THROWN = ".ExceptionThrown";
    private final String RUN_TIME = ".RunTime";
    private final String NUMBER_OF_FAILED_VALIDATION = ".NumberOfFailedValidation";

    private final String UNIT_TYPE_LONG ="long";
    public MetricsEndpoint metricsEndpoint;
    public PresidioCustomMetrics presidioCustomMetrics;



    public MonitoringAspects(MetricsEndpoint metricsEndpoint,PresidioCustomMetrics presidioCustomMetrics){
        logger.info("Aspect is activated");
        this.presidioCustomMetrics=presidioCustomMetrics;
        this.metricsEndpoint=metricsEndpoint;
        this.metricsEndpoint.registerPublicMetrics(this.presidioCustomMetrics);
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
    public void start(JoinPoint joinPoint) throws Throwable{
        String metric = joinPoint.getSignature().toShortString()+START;
        Set tags=new HashSet();
        presidioCustomMetrics.addMetric(metric,1,tags, UNIT_TYPE_LONG);
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
    public void end(JoinPoint joinPoint) throws Throwable{
        String metric  = joinPoint.getSignature().toShortString()+END;
        Set tags=new HashSet();
        presidioCustomMetrics.addMetric(metric,1,tags, UNIT_TYPE_LONG);
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
    public void exceptionThrown(JoinPoint joinPoint) throws Throwable{
        String metric  = joinPoint.getSignature().toShortString()+EXCEPTION_THROWN;
        Set tags=new HashSet();
        presidioCustomMetrics.addMetric(metric,1,tags, UNIT_TYPE_LONG);
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
        String metricName = joinPoint.getSignature().toShortString()+RUN_TIME;
        long startTime = System.nanoTime();
        joinPoint.proceed();
        long endTime = System.nanoTime();
        long time=Long.divideUnsigned(endTime-startTime,1000000000);
        Set tags=new HashSet();
        presidioCustomMetrics.addMetric(metricName,time,tags, UNIT_TYPE_LONG);
        logger.debug("Metric {} run time is {} milli seconds. ", metricName,time);
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
        String metricName = joinPoint.getSignature().toShortString()+NUMBER_OF_FAILED_VALIDATION;
        int numberOfFailedValidationDocuments = ((List<? extends Serializable>) joinPoint.proceed()).size();
        Set tags=new HashSet();
        presidioCustomMetrics.addMetric(metricName,numberOfFailedValidationDocuments,tags, UNIT_TYPE_LONG);
        logger.debug("Metric {} got {} failed validations. ", metricName, numberOfFailedValidationDocuments);
    }



    /**
     * This method provides us counting of a method invocation that process a data source .
     * The annotation Before lets us perform custom behavior before a method invocation.
     * This behavior occurs when a method is annotated with the annotation @DataSourceProcess.
     *
     * @param joinPoint - a point that represent a methods execution, holds data on the method that is going to be executed.
     * @param dataSource - enum of a type date source.
     * @throws Throwable - any exceptin that can be thrown from the execution of the method.
     */

    @Before("@annotation(presidio.monitoring.aspect.annotations.DataSourceProcess) && args(dataSource,..)")
    public void dataSourceProcess(JoinPoint joinPoint , DataSource dataSource) throws Throwable{
        String metric = joinPoint.getSignature().toShortString()+dataSource.getName();
        Set tags=new HashSet();
        presidioCustomMetrics.addMetric(metric,1,tags, UNIT_TYPE_LONG);
        logger.debug("Metric {} increment with annotation DataSourceProcess. ", metric);
    }

}
