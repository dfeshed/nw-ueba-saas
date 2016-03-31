package fortscale.monitoring.external.stats.collector.scheduler;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

/**
 * Scheduler for monitoring statistics collector jobs. Periodically triggers the monitoring jobs (MongoDB, HDFS etc.).
 *
 * @author gils
 * 20/03/2016
 */
public class MonitoringExternalStatsCollector {

    private static Logger logger = LoggerFactory.getLogger(MonitoringExternalStatsCollector.class);

    private static final String SPRING_CONTEXT_FILE_PATH = "classpath*:META-INF/spring/monitoring-external-stats-collector-context.xml";

    private static final String DEFAULT_MONITORING_STATS_COLLECTOR_QUARTZ_PROPERTY_FILE = "jobs/quartz-monitoring-external-stats-collector-factory-settings.properties";
    private static final String OVERRIDING_MONITORING_STATS_COLLECTOR_QUARTZ_PROPERTY_FILE = "resources/quartz-monitoring-external-stats-collector.properties";

    private Scheduler scheduler;
    private ClassPathXmlApplicationContext context;

    private void init() throws Exception {
        logger.info("Loading spring context..");

        context = new ClassPathXmlApplicationContext(SPRING_CONTEXT_FILE_PATH);
        logger.info("Finished loading spring context");

        logger.info("Initializing quartz scheduler..");

        boolean quartzOverridingFileExist = isQuartzOverridingFileExist();

        if (quartzOverridingFileExist) {
            logger.info("Quartz overriding file exist, loading properties from " + OVERRIDING_MONITORING_STATS_COLLECTOR_QUARTZ_PROPERTY_FILE);
            scheduler = new StdSchedulerFactory(OVERRIDING_MONITORING_STATS_COLLECTOR_QUARTZ_PROPERTY_FILE).getScheduler();
        }
        else {
            logger.info("Quartz overriding file does not exist, loading properties from " + DEFAULT_MONITORING_STATS_COLLECTOR_QUARTZ_PROPERTY_FILE);
            scheduler = new StdSchedulerFactory(DEFAULT_MONITORING_STATS_COLLECTOR_QUARTZ_PROPERTY_FILE).getScheduler();
        }

        scheduler.getListenerManager().addSchedulerListener(new MonitoringExternalStatsCollectorSchedulerListener(scheduler, context));

        logger.info("Quartz initialization completed");
    }

    private boolean isQuartzOverridingFileExist() {
        logger.info("Checking if quartz override file exist in: " + OVERRIDING_MONITORING_STATS_COLLECTOR_QUARTZ_PROPERTY_FILE);

        File quartzFile = new File(OVERRIDING_MONITORING_STATS_COLLECTOR_QUARTZ_PROPERTY_FILE);

        return quartzFile.exists() && !quartzFile.isDirectory();
    }

    private void start() throws Exception {
        scheduler.start();
    }

    public static void main(String[] args) {
        logger.info("Monitoring Scheduler started");

        MonitoringExternalStatsCollector monitoringScheduler = new MonitoringExternalStatsCollector();

        try {
            monitoringScheduler.init();
            monitoringScheduler.start();
        } catch (Exception e) {
            logger.error("Exception while trying to schedule: ", e);
        }
    }
}
