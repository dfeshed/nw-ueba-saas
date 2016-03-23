package fortscale.monitoring.stats.collector.scheduler;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Scheduler for monitoring statistics collector jobs. Periodically triggers the monitoring jobs (MongoDB, HDFS etc.).
 *
 * @author gils
 * 20/03/2016
 */
public class MonitoringStatsCollectorScheduler {

    private static Logger logger = LoggerFactory.getLogger(MonitoringStatsCollectorScheduler.class);

    private Scheduler scheduler;
    private ClassPathXmlApplicationContext context;

    public void init() throws Exception {
        logger.info("Loading spring context..");

        context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/monitoring-stats-collector-context.xml");
        logger.info("Finished loading spring context");

        logger.info("Initializing quartz scheduler..");
        scheduler = new StdSchedulerFactory("jobs/quartz-monitoring-stats-collector.properties").getScheduler();
        scheduler.getListenerManager().addSchedulerListener(new MonitoringSchedulerListener(scheduler, context));

        logger.info("Quartz initialization completed");
    }

    public void start() throws Exception {
        scheduler.start();
    }

    public static void main(String[] args) {
        logger.info("Monitoring Scheduler started");

        MonitoringStatsCollectorScheduler monitoringScheduler = new MonitoringStatsCollectorScheduler();

        try {
            monitoringScheduler.init();
            monitoringScheduler.start();
        } catch (Exception e) {
            logger.error("Exception while trying to schedule: ", e);
        }
    }
}
