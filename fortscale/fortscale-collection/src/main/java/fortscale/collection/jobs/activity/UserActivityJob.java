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

    private static final int NUMBER_OF_ACTIVITIES = 4;
    private static Logger logger = Logger.getLogger(UserActivityJob.class);

	private static final String ACTIVITY_PARAM = "activity";

    @Value("${user.activity.num.of.last.days.to.calculate:90}")
    protected int userActivityNumOfLastDaysToCalculate;

    @Autowired
    private UserActivityLocationConfigurationService userActivityLocationConfigurationService;
    @Autowired
    private UserActivityNetworkAuthenticationConfigurationService userActivityNetworkAuthenticationConfigurationService;
	@Autowired
	private UserActivityDataUsageConfigurationService userActivityDataUsageConfigurationService;
    @Autowired
    private UserActivityWorkingHoursConfigurationService userActivityWorkingHoursConfigurationService;
    @Autowired
    private UserActivityHandlerFactory userActivityHandlerFactory;

	private UserActivityType userActivityType;

    public UserActivityJob() {}

    @Override
    protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Loading Entity Activity Job Parameters..");
        logger.info("Finished Loading Entity Activity Job Parameters");
        JobDataMap map = jobExecutionContext.getMergedJobDataMap();
        // get parameters values from the job data map
		if (map.containsKey(ACTIVITY_PARAM)) {
			String activityName = jobDataMapExtension.getJobDataMapStringValue(map, ACTIVITY_PARAM);
			try {
				userActivityType = UserActivityType.valueOf(activityName);
			} catch (Exception ex) {
				logger.error("Activity " + activityName + " not found! exiting...");
				System.exit(1);
			}
		} else {
			userActivityType = UserActivityType.ALL;
		}
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
            activitiesTasks.forEach(activitiesThreadPool::execute);
        } finally {
            activitiesThreadPool.shutdown();
            activitiesThreadPool.awaitTermination(24, TimeUnit.HOURS);
            System.out.println("DONE!!!");
        }
        logger.info("Finished executing User Activity job");
    }

    private Set<Runnable> createActivitiesTasks() {
        Set<Runnable> activities = new HashSet();
		switch (userActivityType) {
			case LOCATIONS: createActivity(activities, userActivityLocationConfigurationService); break;
			case NETWORK_AUTHENTICATION: {
				createActivity(activities, userActivityNetworkAuthenticationConfigurationService);
				break;
			}
			case WORKING_HOURS: createActivity(activities, userActivityWorkingHoursConfigurationService); break;
			case DATA_USAGE: createActivity(activities, userActivityDataUsageConfigurationService); break;
			case ALL: {
				createActivity(activities, userActivityLocationConfigurationService);
				createActivity(activities, userActivityNetworkAuthenticationConfigurationService);
				createActivity(activities, userActivityWorkingHoursConfigurationService);
				createActivity(activities, userActivityDataUsageConfigurationService);
				break;
			}
		}
        return activities;
    }

	private void createActivity(Set<Runnable> activities,
			BaseUserActivityConfigurationService baseUserActivityConfigurationService) {
		Runnable task = () -> createCalculateActivityRunnable(baseUserActivityConfigurationService);
		activities.add(task);
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