package fortscale.monitoring.stats.collector.scheduler;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.listeners.SchedulerListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Scheduler listener to provide callback functionality. Used to destroy resources (e.g. spring context)
 * after job scheduling is finished
 *
 * @author gils
 * 21/03/2016
 */
class MonitoringSchedulerListener extends SchedulerListenerSupport {
    private static final Logger logger = LoggerFactory.getLogger(MonitoringSchedulerListener.class);
    private static final int JOB_POLLING_INTERVAL_TIME = 5000; // 5 seconds

    private ClassPathXmlApplicationContext context;
    private Scheduler scheduler;

    MonitoringSchedulerListener(Scheduler scheduler, ClassPathXmlApplicationContext context) {
        this.scheduler = scheduler;
        this.context = context;
    }

    public void schedulerShuttingdown() {
        logger.info("Scheduler is shutting down..");
    }

    public void schedulerShutdown() {
        logger.info("Waiting for all jobs to finish executing..");
        try {
            while (!scheduler.getCurrentlyExecutingJobs().isEmpty()) {
                try {
                    Thread.sleep(JOB_POLLING_INTERVAL_TIME);
                } catch (InterruptedException e) {
                    logger.error("Monitoring scheduling thread interrupted", e);
                }
            }

        } catch (SchedulerException e) {
            logger.error("Error while trying to get current executing jobs", e);
        }

        logger.info("Scheduler has shutdown, going to close spring context..");

        if (context != null) {
            context.close();
            context = null;
        }

        scheduler = null;

        logger.info("Spring context closed");
    }
}

