package fortscale.collection.jobs;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.FeatureNumericValue;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.domain.core.*;
import fortscale.services.AlertsService;
import fortscale.services.EvidencesService;
import fortscale.services.UserService;
import fortscale.services.exceptions.HdfsException;
import fortscale.services.impl.HdfsService;
import fortscale.services.impl.SpringService;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.hdfs.split.FileSplitStrategy;
import fortscale.utils.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.util.*;

/**
 * Created by Amir Keren on 14/12/2015.
 *
 * This task generates demo scenarios
 *
 */
public class ScenarioGeneratorJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(ScenarioGeneratorJob.class);

    private static final String CONTEXT = "classpath*:META-INF/spring/collection-context.xml";

    @Autowired
    private UserService userService;
    @Autowired
    private AlertsService alertsService;
    @Autowired
    private EvidencesService evidencesService;
    @Autowired
    private MongoTemplate mongoTemplate;

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

    public void generateScenario1()
            throws ClassNotFoundException, IOException, HdfsException, InstantiationException, IllegalAccessException {

        /*********************** Entity **********************/
        String username = "alrusr51@somebigcompany.com";
        String srcMachine = "alrusr51_PC";
        String dstMachine = "alrusr51_SRV";
        User user = userService.findByUsername(username);
        String key = "normalized_username";
        String dataSource = "kerberos_logins";
        String timeSpan = "Hourly";

        /*********************** Time & Title*****************/
        DateTime dt = new DateTime()
                .withZone(DateTimeZone.UTC)
                .withHourOfDay(4)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        long startTimeMillis = dt.getMillis();
        long startTime = startTimeMillis / 1000;
        long endTimeMillis = dt.plusHours(1).minusMillis(1).getMillis();
        long endTime = dt.plusHours(1).minusMillis(1).getMillis() / 1000;
        Date date = dt.plusMinutes(37).plusSeconds(12).plusMillis(240).toDate();
        String title = "Suspicious " + timeSpan + " User Activity";

        /*********************** Events **********************/
        createEvents(CONTEXT, username, srcMachine, dstMachine);

        /*********************** Buckets *********************/
        createBuckets(username, key, dataSource, timeSpan.toLowerCase(), startTime, endTime);

        /********************** Score ************************/
        int alertScore = 80;
        double indicatorScore = 98;
        Severity severity = Severity.High;

        /********************** Indicators *******************/
        List<Evidence> indicators = new ArrayList();
        Evidence indicator1 = createIndicator(username, EvidenceType.AnomalySingleEvent, date, date, dataSource,
                indicatorScore, "destination_machine", dstMachine, 1, EvidenceTimeframe.Hourly);
        indicators.add(indicator1);

        /********************** Alert ************************/
        createAlert(title, startTimeMillis, endTimeMillis, user, indicators, alertScore, severity);
    }

    /**
     *
     * This method generates the necessary buckets
     *
     * @param username
     * @param key
     * @param dataSource
     * @param timeSpan
     * @param startTime
     * @param endTime
     */
    public void createBuckets(String username, String key, String dataSource, String timeSpan, long startTime,
            long endTime) {
        FeatureBucket bucket = new FeatureBucket();
        bucket.setBucketId("fixed_duration_" + timeSpan + "_" + startTime + "_" + key + " _" + username);
        bucket.setCreatedAt(new Date());
        bucket.setContextFieldNames(Arrays.asList(new String[] { key }));
        bucket.setDataSources(Arrays.asList(new String[] { dataSource }));
        bucket.setFeatureBucketConfName(key + "_" + dataSource + "_daily");
        bucket.setStrategyId("fixed_duration_" + timeSpan + "_" + startTime);
        bucket.setStartTime(startTime);
        bucket.setEndTime(endTime);
        Feature feature = new Feature();
        feature.setName("destination_machine_histogram");
        feature.setValue(new FeatureNumericValue(1));
        Map<String, Feature> features = new HashMap();
        features.put("destination_machine_histogram", feature);
        bucket.setAggregatedFeatures(features);
        mongoTemplate.insert(bucket, "aggr_" + key + "_" + dataSource + "_" + timeSpan);
    }

    /**
     *
     * This method generates the events in HDFS and Impala
     *
     * @param context
     * @param username
     * @param srcMachine
     * @param dstMachine
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     * @throws HdfsException
     */
    public void createEvents(String context, String username, String srcMachine, String dstMachine)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException, HdfsException {
        String SEPARATOR = ",";
        SpringService.init(context);
        FileSplitStrategy splitStrategy = (FileSplitStrategy)Class.
                forName("fortscale.utils.hdfs.split.DailyFileSplitStrategy").newInstance();
        PartitionStrategy partitionStrategy = PartitionsUtils.getPartitionStrategy("daily");
        String impalaTable = "authenticationscores";
        String fileName = "secData.csv";
        HdfsService service = new HdfsService("/user/cloudera/processeddata/authentication", fileName,
                partitionStrategy, splitStrategy, impalaTable, 1, 0, SEPARATOR);
        //TODO - randomize datetime
        DateTime dt = new DateTime();
        int dateTimeScore = 0;
        String domain = "FORTSCALE";
        boolean isAdmin = false;
        boolean isService = false;
        boolean isExecutive = false;
        boolean isSensitive = false;
        String failureCode = "0x0";
        int failureCodeScore = 0;
        String clientAddress = "192.168.171.2";
        boolean isNat = false;
        int normalizedSrcMachineScore = 0;
        String srcClass = "Desktop";
        int normalizedDstMachineScore = 0;
        String dstClass = "Server";
        String serviceId = "FORTSCALE\\FS-DC-01$";
        boolean isLR = false;
        int eventScore = 0;
        DateTimeFormatter hdfsFolderFormat = DateTimeFormat.forPattern("yyyyMMdd");
        DateTimeFormatter hdfsTimestampFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        long timestamp = new Date().getTime();
        StringBuilder sb = new StringBuilder()
                .append(hdfsTimestampFormat.print(dt)).append(SEPARATOR)
                .append(dt.getMillis() / 1000).append(SEPARATOR)
                .append(dateTimeScore).append(SEPARATOR)
                .append(username).append(SEPARATOR)
                .append(domain).append(SEPARATOR)
                .append(username).append(SEPARATOR)
                .append(isAdmin).append(SEPARATOR)
                .append(isService).append(SEPARATOR)
                .append(isExecutive).append(SEPARATOR)
                .append(isSensitive).append(SEPARATOR)
                .append(failureCode).append(SEPARATOR)
                .append(failureCodeScore).append(SEPARATOR)
                .append(clientAddress).append(SEPARATOR)
                .append(isNat).append(SEPARATOR)
                .append(srcMachine.toUpperCase()).append(SEPARATOR)
                .append(srcMachine.toUpperCase()).append(SEPARATOR)
                .append(normalizedSrcMachineScore).append(SEPARATOR)
                .append(srcClass).append(SEPARATOR)
                .append(dstMachine).append(SEPARATOR)
                .append(dstMachine.toUpperCase()).append(SEPARATOR)
                .append(normalizedDstMachineScore).append(SEPARATOR)
                .append(dstClass).append(SEPARATOR)
                .append(serviceId).append(SEPARATOR)
                .append(isLR).append(SEPARATOR)
                .append(eventScore).append(SEPARATOR)
                .append(timestamp)
                .append(SEPARATOR).append(hdfsFolderFormat.print(dt)).append(SEPARATOR);
        service.writeLineToHdfs(sb.toString(), timestamp);
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
    public Evidence createIndicator(String username, EvidenceType evidenceType, Date startTime, Date endTime,
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
    public void createAlert(String title, long startTime, long endTime, User user, List<Evidence> evidences, int                   roundScore, Severity severity) {
        Alert alert = new Alert(title, startTime, endTime, EntityType.User, user.getUsername(), evidences,
                evidences.size(), roundScore, severity, AlertStatus.Open, AlertFeedback.None, "", user.getId());
        alertsService.add(alert);
    }

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

}