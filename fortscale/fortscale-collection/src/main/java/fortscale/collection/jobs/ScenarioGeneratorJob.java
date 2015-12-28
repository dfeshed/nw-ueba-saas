package fortscale.collection.jobs;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.FeatureNumericValue;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.domain.core.*;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.services.AlertsService;
import fortscale.services.EvidencesService;
import fortscale.services.UserService;
import fortscale.services.UserTagEnum;
import fortscale.services.exceptions.HdfsException;
import fortscale.services.impl.HdfsService;
import fortscale.services.impl.SpringService;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.hdfs.split.FileSplitStrategy;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
    private static final String SPLIT_STRATEGY = "fortscale.utils.hdfs.split.DailyFileSplitStrategy";
    private static final String KEY = "normalized_username";
    private static final String SEPARATOR = ",";

    @Autowired
    private UserService userService;
    @Autowired
    private ComputerRepository computerRepository;
    @Autowired
    private AlertsService alertsService;
    @Autowired
    private EvidencesService evidencesService;
    @Autowired
    private MongoTemplate mongoTemplate;

    private FileSplitStrategy splitStrategy;
    private PartitionStrategy partitionStrategy;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.info("Initializing scenario generator job");
		//JobDataMap map = jobExecutionContext.getMergedJobDataMap();
        //TODO - read map
        SpringService.init(CONTEXT);
        try {
            splitStrategy = (FileSplitStrategy) Class.forName(SPLIT_STRATEGY).newInstance();
        } catch (Exception ex) {
            logger.error("failed to find split strategy");
            throw new JobExecutionException(ex);
        }
        partitionStrategy = PartitionsUtils.getPartitionStrategy("daily");
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
        //TODO - extract these
        String samaccountname = "alrusr51";
        String username = samaccountname + "@somebigcompany.com";
        String srcMachine = samaccountname + "_PC";
        String dstMachine = samaccountname + "_SRV";
        String dataSource = "kerberos_logins";
        String timeSpan = "Hourly";
        String impalaTable = "authenticationscores";
        String fileName = "secData.csv";
        String hdfsPartition = "/user/cloudera/processeddata/authentication";
        String title = "Suspicious " + timeSpan + " User Activity";

        Computer computer = computerRepository.findByName(srcMachine);
        if (computer == null) {
            logger.error("computer {} not found - exiting", srcMachine);
        }
        User user = userService.findByUsername(username);
        if (user == null) {
            logger.error("user {} not found - exiting", username);
        }

        /*********************** Anomaly Time *****************/
        //TODO - extract these
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

        /*********************** Events **********************/
        HdfsService service = new HdfsService(hdfsPartition, fileName, partitionStrategy, splitStrategy, impalaTable,
                1, 0, SEPARATOR);
        createWorkBaselineEvents(user, computer, dstMachine, service);

        /*********************** Buckets *********************/
        createBuckets(username, KEY, dataSource, timeSpan.toLowerCase(), startTime, endTime);

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
        //TODO - generalize this
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
     * @param user
     * @param computer
     * @param dstMachine
     * @param service
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     * @throws HdfsException
     */
    public void createWorkBaselineEvents(User user, Computer computer, String dstMachine, HdfsService service)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException, HdfsException {
        //TODO - extract these
        int numOfDays = 30;
        int maxHourOfWork = 18;
        int minHourOfWork = 8;
        int numberOfMaxEventsPerTimePeriod = 5;
        int numberOfMinEventsPerTimePeriod = 2;
        int standardDeviation = 2;
        int morningMedianHour = 11;
        int afternoonMedianHour = 15;

        Random random = new Random();
        DateTime now = new DateTime().withZone(DateTimeZone.UTC);
        DateTime dt = now.minusDays(numOfDays);
        while (dt.isBefore(now)) {
            List<Pair<Long, String>> eventsToWrite = new ArrayList();
            int numberOfMorningEvents = random.nextInt(numberOfMaxEventsPerTimePeriod - numberOfMinEventsPerTimePeriod)
                    + numberOfMinEventsPerTimePeriod;
            int numberOfAfternoonEvents = random.nextInt(numberOfMaxEventsPerTimePeriod -
                    numberOfMinEventsPerTimePeriod) + numberOfMinEventsPerTimePeriod;
            for (int j = 0; j < numberOfMorningEvents; j++) {
                DateTime dateTime = generateRandomTimeForDay(dt, standardDeviation, morningMedianHour, maxHourOfWork,
                        minHourOfWork);
                Pair<Long, String> pair = new ImmutablePair(dateTime.getMillis(),
                        buildKerberosHDFSLine(dateTime, user, computer, dstMachine));
                eventsToWrite.add(pair);
            }
            for (int j = 0; j < numberOfAfternoonEvents; j++) {
                DateTime dateTime = generateRandomTimeForDay(dt, standardDeviation, afternoonMedianHour, maxHourOfWork,
                        minHourOfWork);
                Pair<Long, String> pair = new ImmutablePair(dateTime.getMillis(),
                        buildKerberosHDFSLine(dateTime, user, computer, dstMachine));
                eventsToWrite.add(pair);
            }
            sendToHDFS(service, eventsToWrite);
            dt = dt.plusDays(1);
            //skip the weekend
            if (dt.getDayOfWeek() == DateTimeConstants.SATURDAY) {
                dt = dt.plusDays(2);
            } else if (dt.getDayOfWeek() == DateTimeConstants.SUNDAY) {
                dt = dt.plusDays(1);
            }
        }
    }

    /**
     *
     * This method sorts the events by time and sends them to be written to HDFS
     *
     * @param service
     * @param eventsToWrite
     * @throws HdfsException
     */
    private void sendToHDFS(HdfsService service, List<Pair<Long, String>> eventsToWrite) throws HdfsException {
        Comparator<Pair<Long, String>> comparator = new Comparator<Pair<Long, String>>() {
			public int compare(Pair<Long, String> c1, Pair<Long, String> c2) {
				return (int)(c1.getKey() - c2.getKey());
			}
		};
        Collections.sort(eventsToWrite, comparator);
        for (Pair<Long, String> touple: eventsToWrite) {
			service.writeLineToHdfs(touple.getValue(), touple.getKey());
		}
    }

    /**
     *
     * This method generates a random hour for a specific day
     *
     * @param dt
     * @param standardDeviation
     * @param mean
     * @return
     */
    public DateTime generateRandomTimeForDay(DateTime dt, int standardDeviation, int mean, int max, int min) {
        Random random = new Random();
        DateTime dateTime = new DateTime(dt);
        //temp initialization
        int hour = 0;
        //while the randomized time is not between normal work hours
        while (hour < min || hour > max) {
            hour = (int)(random.nextGaussian() * standardDeviation + mean);
        }
        return dateTime.withHourOfDay(hour)
                .withMinuteOfHour(random.nextInt(60))
                .withSecondOfMinute(random.nextInt(60)).withMillisOfSecond(random.nextInt(1000));
    }

    /**
     *
     * This method creates the actual csv line to write in HDFS
     *
     * @param dt
     * @param user
     * @param srcMachine
     * @param dstMachine
     * @return
     */
    public String buildKerberosHDFSLine(DateTime dt, User user, Computer srcMachine, String dstMachine) {
        DateTimeFormatter hdfsFolderFormat = DateTimeFormat.forPattern("yyyyMMdd");
        DateTimeFormatter hdfsTimestampFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        //TODO - extract these values
        int dateTimeScore = 0;
        String domain = "FORTSCALE";
        String failureCode = "0x0";
        int failureCodeScore = 0;
        String clientAddress = "192.168.171.2";
        boolean isNat = false;
        int normalizedSrcMachineScore = 0;
        String srcClass = "Desktop";
        int normalizedDstMachineScore = 0;
        String dstClass = "Server";
        String serviceId = "FORTSCALE\\FS-DC-01$";

        int eventScore = Math.max(Math.max(dateTimeScore, failureCodeScore),
                Math.max(normalizedSrcMachineScore, normalizedDstMachineScore));
        long timestamp = new Date().getTime();
        StringBuilder sb = new StringBuilder()
                .append(hdfsTimestampFormat.print(dt)).append(SEPARATOR)
                .append(dt.getMillis() / 1000).append(SEPARATOR)
                .append(dateTimeScore).append(SEPARATOR)
                .append(user.getUsername()).append(SEPARATOR)
                .append(domain).append(SEPARATOR)
                .append(user.getUsername()).append(SEPARATOR)
                .append(user.getAdministratorAccount()).append(SEPARATOR)
                .append(user.getUserServiceAccount()).append(SEPARATOR)
                .append(user.getExecutiveAccount()).append(SEPARATOR)
                .append(srcMachine.getIsSensitive()).append(SEPARATOR)
                .append(failureCode).append(SEPARATOR)
                .append(failureCodeScore).append(SEPARATOR)
                .append(clientAddress).append(SEPARATOR)
                .append(isNat).append(SEPARATOR)
                .append(srcMachine.getName().toUpperCase()).append(SEPARATOR)
                .append(srcMachine.getName().toUpperCase()).append(SEPARATOR)
                .append(normalizedSrcMachineScore).append(SEPARATOR)
                .append(srcClass).append(SEPARATOR)
                .append(dstMachine).append(SEPARATOR)
                .append(dstMachine.toUpperCase()).append(SEPARATOR)
                .append(normalizedDstMachineScore).append(SEPARATOR)
                .append(dstClass).append(SEPARATOR)
                .append(serviceId).append(SEPARATOR)
                .append(user.getTags().contains(UserTagEnum.LR.getId())).append(SEPARATOR)
                .append(eventScore).append(SEPARATOR)
                .append(timestamp).append(SEPARATOR)
                .append(hdfsFolderFormat.print(dt)).append(SEPARATOR);
        return sb.toString();
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
        Evidence indicator = evidencesService.createTransientEvidence(EntityType.User, KEY, username,
                evidenceType, startTime, endTime, Arrays.asList(new String[] { dataEntityId }), score, anomalyValue,
                anomalyTypeFieldName, numberOfEvents, evidenceTimeframe);
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
    public void createAlert(String title, long startTime, long endTime, User user, List<Evidence> evidences,
            int roundScore, Severity severity) {
        Alert alert = new Alert(title, startTime, endTime, EntityType.User, user.getUsername(), evidences,
                evidences.size(), roundScore, severity, AlertStatus.Open, AlertFeedback.None, "", user.getId());
        alertsService.add(alert);
    }

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

}