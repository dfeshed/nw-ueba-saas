import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gils
 * 20/03/2016
 */
public class MonitoringTest {
    private static Logger logger = LoggerFactory.getLogger(MonitoringTest.class);

    public static void main(String[] args) {
        try {
            Scheduler scheduler = new StdSchedulerFactory("jobs/quartz.properties").getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            logger.error("Error getting scheduler status", e);
        }
    }
}
