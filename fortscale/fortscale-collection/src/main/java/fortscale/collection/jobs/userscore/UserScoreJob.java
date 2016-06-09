package fortscale.collection.jobs.userscore;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.services.UserScoreService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by shays on 01/06/2016.
 */
public class UserScoreJob extends FortscaleJob {

    @Autowired
    private UserScoreService userScoreService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String sourceName;
    private String jobName;

    @Override
    protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {

        sourceName = context.getJobDetail().getKey().getGroup();
        jobName = context.getJobDetail().getKey().getName();
    }

    @Override
    protected int getTotalNumOfSteps() {
        return 2;
    }

    @Override
    protected boolean shouldReportDataReceived() {
        return false;
    }

    @Override
    protected void runSteps() throws Exception {
        logger.info("{} {} job started", jobName, sourceName);

        startNewStep("Executing user score for all users: ");
        List<Pair<Double, Integer>> userScoreHistogram = userScoreService.calculateAllUsersScores();
        finishStep();

        if (CollectionUtils.isNotEmpty(userScoreHistogram)) {
            startNewStep("Executing users severities calculation: ");
            userScoreService.calculateUserSeverities(userScoreHistogram);
            finishStep();
        } else {
            logger.info("No users with score found");
        }

        logger.info("{} {} job ended", jobName, sourceName);

    }
}
