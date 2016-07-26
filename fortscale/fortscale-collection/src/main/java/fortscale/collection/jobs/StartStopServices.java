package fortscale.collection.jobs;

import fortscale.services.cloudera.ClouderaService;
import fortscale.utils.logging.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amir Keren on 07/10/15.
 */
public class StartStopServices extends FortscaleJob {

    private static Logger logger = Logger.getLogger(StartStopServices.class);

    private static final String SERVICE_PARAM = "service";

    @Autowired
    private ClouderaService clouderaService;

    private boolean isStop;
    private List<String> services;

    @Override
    protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.debug("Initializing Cloudera Services Job");
        JobDataMap map = jobExecutionContext.getMergedJobDataMap();
        isStop = jobDataMapExtension.getJobDataMapBooleanValue(map, "isStop", false);
        services = new ArrayList();
        if (map.containsKey(SERVICE_PARAM)) {
			services.add(jobDataMapExtension.getJobDataMapStringValue(map, SERVICE_PARAM));
		} else {
			for (String service : jobDataMapExtension.getJobDataMapStringValue(map, "services").split(",")) {
				services.add(service);
			}
		}
        logger.debug("Job Initialized");
    }

    @Override
    protected void runSteps() throws Exception {
        logger.debug("Running Cloudera Services Job");
        String action = "Stop";
        if (!isStop) {
            action = "Start";
        }
        for (String service: services) {
            logger.info("{} service {}", action, service);
            if (clouderaService.startOrStopService(service, isStop)) {
                logger.info("{} {} - successful");
            } else {
                logger.error("{} {} - failed");
            }
        }
        finishStep();
    }

    @Override
    protected int getTotalNumOfSteps() { return 1; }

    @Override
    protected boolean shouldReportDataReceived() { return false; }

}