package fortscale.monitoring.scheduler;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author gils
 * 20/03/2016
 */
public class MonitoringScheduler {

    private static Logger logger = LoggerFactory.getLogger(MonitoringScheduler.class);

    private Scheduler scheduler;
    private ClassPathXmlApplicationContext context;

    private void init() throws Exception {
        logger.info("Loading spring context..");

        context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/monitoring-context.xml");
        logger.info("Finished loading spring context");

        logger.info("Initializing quartz scheduler..");
        scheduler = new StdSchedulerFactory("jobs/quartz.properties").getScheduler();

        logger.info("Quartz initialization completed");
    }

    private void start() throws Exception {
        scheduler.start();
    }

    public static void main(String[] args) {
        logger.info("Monitoring Scheduler started");

        MonitoringScheduler monitoringScheduler = new MonitoringScheduler();

        try {
            monitoringScheduler.init();
            monitoringScheduler.start();
        } catch (Exception e) {
            logger.error("Exception while trying to schedule: ", e);
        }
    }
}
