package fortscale.collection.jobs;

import fortscale.domain.core.*;
import fortscale.services.AlertsService;
import fortscale.services.EvidencesService;
import fortscale.services.UserService;
import fortscale.utils.hdfs.HDFSUtil;
import fortscale.utils.logging.Logger;
import org.joda.time.DateTime;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Amir Keren on 14/12/2015.
 *
 * This task generates demo scenarios
 *
 */
public class ScenarioGeneratorJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(ScenarioGeneratorJob.class);

    @Autowired
    private UserService userService;
    @Autowired
    private AlertsService alertsService;
    @Autowired
    private EvidencesService evidencesService;
    @Autowired
    private HDFSUtil hdfsUtil;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.info("Initializing scenario generator job");
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
        //TODO - read map
        logger.info("Job initialized");
	}

	@Override
	protected void runSteps() throws Exception {
		logger.info("Running scenario generator job");
        generateScenario1();
        finishStep();
	}

    private void generateScenario1() {
        /*********************** Events **********************/
        //TODO - add events to impala

        /*********************** Buckets *********************/
        //TODO - add buckets

        /*********************** Time & Title*****************/
        DateTime dt = new DateTime()
                .withHourOfDay(4)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        long startTime = dt.getMillis();
        long endTime = dt.plusHours(1).minusMillis(1).getMillis();
        Date date = dt.plusMinutes(37).plusSeconds(12).plusMillis(240).toDate();

        String title = "Suspicious Hourly User Activity";

        /*********************** Entity **********************/
        String username = "alrusr51@somebigcompany.com";
        User user = userService.findByUsername(username);

        /********************** Score ************************/
        int alertScore = 80;
        double indicatorScore = 98;
        Severity severity = Severity.High;

        /********************** Indicators *******************/
        List<Evidence> indicators = new ArrayList();
        Evidence indicator1 = createIndicator(username, EvidenceType.AnomalySingleEvent, date, date, "kerberos_logins",
                indicatorScore, "destination_machine", "alrusr43_SRV", 1, EvidenceTimeframe.Hourly);
        indicators.add(indicator1);

        /********************** Alert ************************/
        createAlert(title, startTime, endTime, user, indicators, alertScore, severity);
    }

    /**
     *
     * This method creates the indicator and adds it to Mongo
     *
     * @param username
     * @param evidenceType
     * @param startTime
     * @param endTime
     * @param dataEntityId
     * @param score
     * @param anomalyValue
     * @param anomalyTypeFieldName
     * @param numberOfEvents
     * @param evidenceTimeframe
     * @return
     */
    private Evidence createIndicator(String username, EvidenceType evidenceType, Date startTime, Date endTime,
            String dataEntityId, Double score, String anomalyTypeFieldName, String anomalyValue, int numberOfEvents,
            EvidenceTimeframe evidenceTimeframe) {
        List<String> dataEntitiesIds = new ArrayList();
        dataEntitiesIds.add(dataEntityId);
        Evidence indicator = evidencesService.createTransientEvidence(EntityType.User, "normalized_username", username,
                evidenceType, startTime, endTime, dataEntitiesIds, score, anomalyValue, anomalyTypeFieldName,
                numberOfEvents, evidenceTimeframe);
        evidencesService.saveEvidenceInRepository(indicator);
        return indicator;
    }

    /**
     *
     * This method creates the alert and adds it to Mongo
     *
     * @param title
     * @param startTime
     * @param endTime
     * @param user
     * @param evidences
     * @param roundScore
     * @param severity
     */
    private void createAlert(String title, long startTime, long endTime, User user, List<Evidence> evidences, int                   roundScore, Severity severity) {
        Alert alert = new Alert(title, startTime, endTime, EntityType.User, user.getUsername(), evidences,
                evidences.size(), roundScore, severity, AlertStatus.Open, AlertFeedback.None, "", user.getId());
        alertsService.add(alert);
    }

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

}