package fortscale.collection.jobs.activity;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.services.UserActivityConfiguration;
import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.collection.services.UserActivityLocationConfigurationService;
import fortscale.collection.services.UserActivityNetworkAuthenticationConfigurationService;
import fortscale.utils.logging.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author gils
 * 23/05/2016
 */
@DisallowConcurrentExecution
public class UserActivityJob extends FortscaleJob {

    private static final int NUMBER_OF_ACTIVITIES = 2;
    private static Logger logger = Logger.getLogger(UserActivityJob.class);

    @Value("${user.activity.num.of.last.days.to.calculate:90}")
    protected int userActivityNumOfLastDaysToCalculate;

    @Autowired
    private UserActivityLocationConfigurationService userActivityLocationConfigurationService;

    @Autowired
    private UserActivityNetworkAuthenticationConfigurationService userActivityNetworkAuthenticationConfigurationService;

    @Autowired
    private UserActivityHandlerFactory userActivityHandlerFactory;

    public UserActivityJob() {
    }

    @Override
    protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Loading Entity Activity Job Parameters..");
        logger.info("Finished Loading Entity Activity Job Parameters");
    }

    @Override
    protected int getTotalNumOfSteps() {
        return 1;
    }

    @Override
    protected boolean shouldReportDataReceived() {
        return false;
    }

    @Override
    public void runSteps() throws Exception {
        logger.info("Start Executing User Activity job..");
        ExecutorService activitiesThreadPool = createThreadPool();
        Set<Runnable> activitiesTasks = createActivitiesTasks();
        try {
            activitiesTasks.forEach(activitiesThreadPool::execute);
        } finally {
            activitiesThreadPool.shutdown();
        }
        logger.info("Finished executing User Activity job");
    }

    protected Set<Runnable> createActivitiesTasks() {
        Set<Runnable> activities = new HashSet<>();
        Runnable locationsTask = () -> calculateActivity(userActivityLocationConfigurationService);
        Runnable networkAuthenticationTask = () -> calculateActivity(userActivityLocationConfigurationService);
        activities.add(locationsTask);
        activities.add(networkAuthenticationTask);
        return activities;
    }

    protected ExecutorService createThreadPool() {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
				.setNameFormat("Activity-%d")
				.setDaemon(true)
				.build();
        return Executors.newFixedThreadPool(NUMBER_OF_ACTIVITIES, threadFactory);
    }

    protected void calculateActivity(UserActivityConfigurationService userActivityConfigurationService) {
        final UserActivityConfiguration userActivityConfiguration = userActivityConfigurationService.getUserActivityConfiguration();
        Set<String> activityNames = userActivityConfiguration.getActivities();
        for (String activity : activityNames) {
            logger.debug("Executing calculation for activity: {}", activity);
            UserActivityHandler userActivityHandler = userActivityHandlerFactory.createUserActivityHandler(activity);
            userActivityHandler.calculate(userActivityNumOfLastDaysToCalculate);
        }
    }
}
