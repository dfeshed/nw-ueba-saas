package fortscale.collection.jobs.activity;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.services.*;
import fortscale.utils.logging.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
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

	private static Logger logger = Logger.getLogger(UserActivityJob.class);

    private static final int NUMBER_OF_ACTIVITIES = 6;
	private static final String ACTIVITY_PARAM = "activity";
	private static final String RUN_SEQUENTIAL_PARAM = "sequential";

    @Value("${user.activity.num.of.last.days.to.calculate:90}")
    protected int userActivityNumOfLastDaysToCalculate;

	@Autowired
	Set<UserActivityConfigurationService> userActivityConfigurationServices;
	@Autowired
	private UserActivityHandlerFactory userActivityHandlerFactory;

	private UserActivityType userActivityType;
	private boolean runSequential;

    public UserActivityJob() {}

    @Override
    protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Loading Entity Activity Job Parameters..");
        JobDataMap map = jobExecutionContext.getMergedJobDataMap();
        // get parameters values from the job data map
		if (map.containsKey(ACTIVITY_PARAM)) {
			String activityName = jobDataMapExtension.getJobDataMapStringValue(map, ACTIVITY_PARAM);
			try {
				userActivityType = UserActivityType.valueOf(activityName.toUpperCase());
			} catch (Exception ex) {
				logger.error("Activity " + activityName + " not found! exiting...");
				throw new JobExecutionException("Activity " + activityName + " not found! exiting...");
			}
		}
		runSequential = jobDataMapExtension.getJobDataMapBooleanValue(map, RUN_SEQUENTIAL_PARAM, false);
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
		ExecutorService activitiesThreadPool;
		if (runSequential) {
			activitiesThreadPool = Executors.newFixedThreadPool(1);
		} else {
			activitiesThreadPool = Executors.newFixedThreadPool(NUMBER_OF_ACTIVITIES);
		}
        Set<Runnable> activitiesTasks = createActivitiesTasks();
        try {
            activitiesTasks.forEach(activitiesThreadPool::execute);
        } finally {
            activitiesThreadPool.shutdown();
            activitiesThreadPool.awaitTermination(12, TimeUnit.HOURS);
        }
        logger.info("Finished executing User Activity job");
    }

  
	private Set<Runnable> createActivitiesTasks() {
		Set<Runnable> activities = new HashSet();
		if (userActivityType != null) {
			for (UserActivityConfigurationService userActivityConfigurationService: userActivityConfigurationServices) {
				if (userActivityType == UserActivityType.valueOf(userActivityConfigurationService.getActivityName())) {
					createCalculateActivityRunnable(userActivityConfigurationService);
					break;
				}
			}
		} else {
			userActivityConfigurationServices.forEach(userActivityConfigurationService -> activities.add(() ->
					createCalculateActivityRunnable(userActivityConfigurationService)));
		}
		return activities;
	}

     

    private void createCalculateActivityRunnable(UserActivityConfigurationService userActivityConfigurationService) {
        final String activityName = userActivityConfigurationService.getUserActivityConfiguration().getActivities().
				toString();
        Thread.currentThread().setName(String.format("Activity-%s-thread", activityName));
        calculateActivity(userActivityConfigurationService);
    }

    private void calculateActivity(UserActivityConfigurationService userActivityConfigurationService) {
        final UserActivityConfiguration userActivityConfiguration = userActivityConfigurationService.
				getUserActivityConfiguration();
        Set<String> activityNames = userActivityConfiguration.getActivities();
        for (String activity : activityNames) {
            logger.debug("Executing calculation for activity: {}", activity);
            UserActivityHandler userActivityHandler = userActivityHandlerFactory.createUserActivityHandler(activity);
            userActivityHandler.calculate(userActivityNumOfLastDaysToCalculate);
        }
    }

}