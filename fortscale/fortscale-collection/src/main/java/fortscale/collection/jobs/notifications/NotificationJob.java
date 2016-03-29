package fortscale.collection.jobs.notifications;

import fortscale.collection.jobs.FortscaleJob;

import fortscale.services.ApplicationConfigurationService;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * This job execute services which create notifications.
 * In the XML file, you can tell this class which services should be executed in each trigger.
 */
public class NotificationJob extends FortscaleJob {

    public static final String NOTIFICATIONS_SERVICE_LIST_NAME = "notificationsServiceList";
    public static final String DELIMITER = ",";
    private static Logger logger = LoggerFactory.getLogger(NotificationJob.class);

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;



    private Collection<NotificationGeneratorService> generatorServices = new ArrayList<>();
    private String sourceName;
    private String jobName;


    @Override
    protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {

        JobDataMap map = context.getMergedJobDataMap();

        //Fetch the relevant service generators which should be executed in this execution time
        ApplicationContext springContext = jobDataMapExtension.getSpringApplicationContext();
        List<String> notificationGeneratorsBeanNames = jobDataMapExtension.getJobDataMapListOfStringsValue(map, NOTIFICATIONS_SERVICE_LIST_NAME, DELIMITER);
        if (notificationGeneratorsBeanNames != null || notificationGeneratorsBeanNames.size() > 0 ){
            for (String notificationGeneratorsBeanName : notificationGeneratorsBeanNames){
                NotificationGeneratorService notificationGeneratorService = springContext.getBean(notificationGeneratorsBeanName,NotificationGeneratorService.class);
                generatorServices.add(notificationGeneratorService);
            }
        } else {
            //List of specific services was not defined, execute all services which implementeNotificationGeneratorService
            generatorServices = springContext.getBeansOfType(NotificationGeneratorService.class).values();
        }

        String listOfServices = "";
        for (NotificationGeneratorService service : generatorServices){
            listOfServices = service.getClass().getName() +".";
        }
        logger.info("Following list of services will be executed: "+listOfServices);


        sourceName = context.getJobDetail().getKey().getGroup();
        jobName = context.getJobDetail().getKey().getName();

    }

    @Override
    protected int getTotalNumOfSteps() {
        //1. get the last run time. 2.  creds share query. 3. supporting information query . 4. send to kafka
        return 4;
    }

    @Override
    protected boolean shouldReportDataReceived() {
        return true;
    }


    /*
        For each generator service - call generateNotification method
    */
    @Override
    protected void runSteps() throws Exception {

        logger.info("{} {} job started", jobName, sourceName);

        for (NotificationGeneratorService notificationGenerator : this.generatorServices){
           startNewStep("Executing notification generator: "+notificationGenerator.getClass().getName());
            try {
                boolean success = notificationGenerator.generateNotification();
                if (!success){
                    String error = String.format("Executing notification generator %s finished with an error",
                            notificationGenerator.getClass().getName());
                    addError(error);
                }
            } catch (Exception e){
                String error = String.format("Executing notification generator %s finished with exception %s",
                        notificationGenerator.getClass().getName(), e.getMessage());
                addError(error);
            }
           finishStep();;
        }

        logger.info("{} {} job ended", jobName, sourceName);
    }

}