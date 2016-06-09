package fortscale.collection.jobs.activity;

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
import java.util.concurrent.TimeUnit;

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

    @SuppressWarnings("ThrowFromFinallyBlock")
    @Override
    public void runSteps() throws Exception {
        logger.info("Start Executing User Activity job..");
        ExecutorService activitiesThreadPool = Executors.newFixedThreadPool(NUMBER_OF_ACTIVITIES);
        Set<Runnable> activitiesTasks = createActivitiesTasks();
        try {
            for (Runnable task : activitiesTasks) {
                activitiesThreadPool.execute(task);
            }
        } finally {
            activitiesThreadPool.shutdown();
            activitiesThreadPool.awaitTermination(1, TimeUnit.HOURS);// Todo: is this a good timeout?
        }
        logger.info("Finished executing User Activity job");
    }

    private Set<Runnable> createActivitiesTasks() {
        Set<Runnable> activities = new HashSet<>();
        Runnable locationsTask = () -> createCalculateActivityRunnable(userActivityLocationConfigurationService);
        Runnable networkAuthenticationTask = () -> createCalculateActivityRunnable(userActivityNetworkAuthenticationConfigurationService);
        activities.add(locationsTask);
        activities.add(networkAuthenticationTask);
        return activities;
    }

    private void calculateActivity(UserActivityConfigurationService userActivityConfigurationService) {
        final UserActivityConfiguration userActivityConfiguration = userActivityConfigurationService.getUserActivityConfiguration();
        Set<String> activityNames = userActivityConfiguration.getActivities();
        for (String activity : activityNames) {
            logger.debug("Executing calculation for activity: {}", activity);
            UserActivityHandler userActivityHandler = userActivityHandlerFactory.createUserActivityHandler(activity);
            userActivityHandler.calculate(userActivityNumOfLastDaysToCalculate);
        }
    }

    private void createCalculateActivityRunnable(UserActivityConfigurationService userActivityConfigurationService) {
        final String activityName = userActivityConfigurationService.getUserActivityConfiguration().getActivities().toString();
        Thread.currentThread().setName(String.format("Activity-%s-thread", activityName));
        calculateActivity(userActivityConfigurationService);
    }
}
