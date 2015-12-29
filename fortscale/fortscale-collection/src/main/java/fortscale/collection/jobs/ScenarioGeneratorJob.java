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
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
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
 * This job generates demo scenarios
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
    private Map<String, HDFSProperties> dataSourceToHDFSProperties;
    private int numOfDaysBack;
    private int maxHourOfWork;
    private int minHourOfWork;
    private int numberOfMaxEventsPerTimePeriod;
    private int numberOfMinEventsPerTimePeriod;
    private int standardDeviation;
    private int morningMedianHour;
    private int afternoonMedianHour;
    private boolean skipWeekend;

    /**
     *
     * This method gets the job parameters from the job xml file
     *
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.info("Initializing scenario generator job");
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
        SpringService.init(CONTEXT);
        try {
            splitStrategy = (FileSplitStrategy)Class.forName(SPLIT_STRATEGY).newInstance();
        } catch (Exception ex) {
            logger.error("failed to find split strategy");
            throw new JobExecutionException(ex);
        }
        partitionStrategy = PartitionsUtils.getPartitionStrategy("daily");
        dataSourceToHDFSProperties = buildDataSourceToHDFSPropertiesMap(map);
        numOfDaysBack = jobDataMapExtension.getJobDataMapIntValue(map, "numOfDaysBack");
        maxHourOfWork = jobDataMapExtension.getJobDataMapIntValue(map, "maxHourOfWork");
        minHourOfWork = jobDataMapExtension.getJobDataMapIntValue(map, "minHourOfWork");
        numberOfMaxEventsPerTimePeriod = jobDataMapExtension.getJobDataMapIntValue(map,
                "numberOfMaxEventsPerTimePeriod");
        numberOfMinEventsPerTimePeriod = jobDataMapExtension.getJobDataMapIntValue(map,
                "numberOfMinEventsPerTimePeriod");
        standardDeviation = jobDataMapExtension.getJobDataMapIntValue(map, "standardDeviation");
        morningMedianHour = jobDataMapExtension.getJobDataMapIntValue(map, "morningMedianHour");
        afternoonMedianHour = jobDataMapExtension.getJobDataMapIntValue(map, "afternoonMedianHour");
        skipWeekend = jobDataMapExtension.getJobDataMapBooleanValue(map, "skipWeekend", true);
        logger.info("Job initialized");
	}

    /**
     *
     * This method builds the map required to save data to HDFS
     *
     * @param map
     * @return
     * @throws JobExecutionException
     */
    private Map<String, HDFSProperties> buildDataSourceToHDFSPropertiesMap(JobDataMap map)
            throws JobExecutionException {
        Map<String, HDFSProperties> result = new HashMap();
        for (String dataSource: jobDataMapExtension.getJobDataMapStringValue(map, "dataEntities").split(",")) {
            String impalaTable = jobDataMapExtension.getJobDataMapStringValue(map, "impalaTableName-" + dataSource);
            String hdfsPartition = jobDataMapExtension.getJobDataMapStringValue(map, "hdfsPartition-" + dataSource);
            String fileName = jobDataMapExtension.getJobDataMapStringValue(map, "fileName-" + dataSource);
            result.put(dataSource, new HDFSProperties(impalaTable, fileName, hdfsPartition));
        }
        return result;
    }

    /**
     *
     * This is the main method of the job
     *
     * @throws Exception
     */
    @Override
	protected void runSteps() throws Exception {
		logger.info("Running scenario generator job");
        generateScenario1();
        //TODO - generate scenario2 and scenario3
        finishStep();
	}

    /**
     *
     * This method generates scenario1 as described here:
     * https://fortscale.atlassian.net/browse/FV-9286
     *
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws HdfsException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void generateScenario1()
            throws ClassNotFoundException, IOException, HdfsException, InstantiationException, IllegalAccessException {
        //TODO - extract these
        String samaccountname = "alrusr51";
        String domain = "somebigcompany.com";
        String dataSource = "kerberos_logins";
        String timeSpan = "Hourly";
        int indicatorScore = 98;
        String title = "Suspicious " + timeSpan + " User Activity";
        int alertScore = 80;
        Severity alertSeverity = Severity.High;
        int minNumberOfAnomalies = 2;
        int maxNumberOfAnomalies = 3;
        int minHourForAnomaly = 3;
        int maxHourForAnomaly = 5;

        DateTime anomalyDate = generateRandomDayForAnomaly(numOfDaysBack, numOfDaysBack / 2);
        String username = samaccountname + "@" + domain;
        String srcMachine = samaccountname + "_PC";
        String dstMachine = samaccountname + "_SRV";
        Computer computer = computerRepository.findByName(srcMachine.toUpperCase());
        if (computer == null) {
            logger.error("computer {} not found - exiting", srcMachine.toUpperCase());
            return;
        }
        User user = userService.findByUsername(username);
        if (user == null) {
            logger.error("user {} not found - exiting", username);
            return;
        }
        //generate scenario
        List<Evidence> indicators = new ArrayList();
        createLoginEvents(user, computer, dstMachine, dataSource);
        indicators.addAll(createTimeLoginAnomalies(dataSource, anomalyDate,
                minNumberOfAnomalies, maxNumberOfAnomalies, minHourForAnomaly, maxHourForAnomaly, user, computer,
                dstMachine, indicatorScore));
        //TODO - generate indicators 2,3 and 4
        createAlert(title, anomalyDate.getMillis(), anomalyDate.plusDays(1).minusMillis(1).getMillis(), user,
                indicators, alertScore, alertSeverity);
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
     * @param count
     * @param featureName
     */
    public void createBucket(String username, String key, String dataSource, String timeSpan, long startTime,
                             long endTime, int count, String featureName) {
        FeatureBucket bucket = new FeatureBucket();
        bucket.setBucketId("fixed_duration_" + timeSpan + "_" + startTime + "_" + key + " _" + username);
        bucket.setCreatedAt(new Date());
        bucket.setContextFieldNames(Arrays.asList(new String[] { key }));
        bucket.setDataSources(Arrays.asList(new String[] { dataSource }));
        bucket.setFeatureBucketConfName(key + "_" + dataSource + "_" + timeSpan);
        bucket.setStrategyId("fixed_duration_" + timeSpan + "_" + startTime);
        bucket.setStartTime(startTime);
        bucket.setEndTime(endTime);
        Feature feature = new Feature();
        feature.setName(featureName);
        feature.setValue(new FeatureNumericValue(count));
        Map<String, Feature> features = new HashMap();
        features.put(featureName, feature);
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
     * @param dataSource
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     * @throws HdfsException
     */
    public void createLoginEvents(User user, Computer computer, String dstMachine, String dataSource)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException, HdfsException {
        HDFSProperties hdfsProperties = dataSourceToHDFSProperties.get(dataSource);
        HdfsService service = new HdfsService(hdfsProperties.getHdfsPartition(), hdfsProperties.getFileName(),
                partitionStrategy, splitStrategy, hdfsProperties.getImpalaTable(), 1, 0, SEPARATOR);
        Random random = new Random();
        DateTime now = new DateTime().withZone(DateTimeZone.UTC);
        DateTime dt = now.minusDays(numOfDaysBack);
        while (dt.isBefore(now)) {
            int numberOfMorningEvents = random.nextInt(numberOfMaxEventsPerTimePeriod - numberOfMinEventsPerTimePeriod)
                    + numberOfMinEventsPerTimePeriod;
            int numberOfAfternoonEvents = random.nextInt(numberOfMaxEventsPerTimePeriod -
                    numberOfMinEventsPerTimePeriod) + numberOfMinEventsPerTimePeriod;
            Map<DateTime, Integer> bucketMap = new HashMap();
            for (int j = 0; j < numberOfMorningEvents; j++) {
                DateTime dateTime = generateRandomTimeForDay(dt, standardDeviation, morningMedianHour, maxHourOfWork,
                        minHourOfWork);
                addToBucketMap(dateTime, bucketMap);
                service.writeLineToHdfs(buildKerberosHDFSLine(dateTime, user, computer, dstMachine, 0),
                        dateTime.getMillis());
            }
            for (int j = 0; j < numberOfAfternoonEvents; j++) {
                DateTime dateTime = generateRandomTimeForDay(dt, standardDeviation, afternoonMedianHour, maxHourOfWork,
                        minHourOfWork);
                addToBucketMap(dateTime, bucketMap);
                service.writeLineToHdfs(buildKerberosHDFSLine(dateTime, user, computer, dstMachine, 0),
                        dateTime.getMillis());
            }
            //create hourly buckets
            for (Map.Entry<DateTime, Integer> bucket: bucketMap.entrySet()) {
                createBucket(user.getUsername(), KEY, dataSource, "hourly", bucket.getKey().getMillis() / 1000,
                        bucket.getKey().plusHours(1).minusMillis(1).getMillis() / 1000, bucket.getValue(),
                        "number_of_events_per_hour_histogram");
            }
            //create daily bucket
            DateTime midnight = new DateTime(dt)
                    .withHourOfDay(0)
                    .withMinuteOfHour(0)
                    .withSecondOfMinute(0)
                    .withMillisOfSecond(0);
            createBucket(user.getUsername(), KEY, dataSource, "daily", midnight.getMillis() / 1000,
                    midnight.plusDays(1).minusMillis(1).getMillis() / 1000, numberOfMorningEvents +
                            numberOfAfternoonEvents, "number_of_events_per_hour_histogram");
            dt = dt.plusDays(1);
            if (skipWeekend && dt.getDayOfWeek() == DateTimeConstants.SATURDAY) {
                dt = dt.plusDays(2);
            } else if (skipWeekend && dt.getDayOfWeek() == DateTimeConstants.SUNDAY) {
                dt = dt.plusDays(1);
            }
        }
    }

    /**
     *
     * This method adds the bucket to the bucket map
     *
     * @param dateTime
     * @param bucketMap
     */
    private void addToBucketMap(DateTime dateTime, Map<DateTime, Integer> bucketMap) {
        DateTime startOfHour = new DateTime(dateTime)
				.withMinuteOfHour(0)
				.withSecondOfMinute(0)
				.withMillisOfSecond(0);
        int count = 0;
        if (bucketMap.containsKey(startOfHour)) {
			count = bucketMap.get(startOfHour);
		}
        bucketMap.put(startOfHour, count + 1);
    }

    /**
     *
     * This method generates a random number of time login anomalies
     *
     * @param dataSource
     * @param anomalyDate
     * @param minNumberOfAnomalies
     * @param maxNumberOfAnomalies
     * @param minHourForAnomaly
     * @param maxHourForAnomaly
     * @param user
     * @param computer
     * @param dstMachine
     * @param indicatorScore
     * @throws HdfsException
     * @throws IOException
     * @return
     */
    public List<Evidence> createTimeLoginAnomalies(String dataSource, DateTime anomalyDate,
            int minNumberOfAnomalies, int maxNumberOfAnomalies, int minHourForAnomaly, int maxHourForAnomaly,
            User user, Computer computer, String dstMachine, int indicatorScore) throws HdfsException, IOException {
        HDFSProperties hdfsProperties = dataSourceToHDFSProperties.get(dataSource);
        HdfsService service = new HdfsService(hdfsProperties.getHdfsPartition(), hdfsProperties.getFileName(),
                partitionStrategy, splitStrategy, hdfsProperties.getImpalaTable(), 1, 0, SEPARATOR);
        HdfsService service_top = new HdfsService(hdfsProperties.getHdfsPartition() + "_top",
                hdfsProperties.getFileName(), partitionStrategy, splitStrategy, hdfsProperties.getImpalaTable() +
                "_top", 1, 0, SEPARATOR);
        Random random = new Random();
        List<Evidence> indicators = new ArrayList();
        int numberOfAnomalies = random.nextInt(maxNumberOfAnomalies - minNumberOfAnomalies) + minNumberOfAnomalies;
        Map<DateTime, Integer> bucketMap = new HashMap();
        for (int i = 0; i < numberOfAnomalies; i++) {
            DateTime randomDate = generateRandomTimeForAnomaly(anomalyDate, minHourForAnomaly, maxHourForAnomaly);
            service.writeLineToHdfs(buildKerberosHDFSLine(randomDate, user, computer, dstMachine,
                    indicatorScore), randomDate.getMillis());
            service_top.writeLineToHdfs(buildKerberosHDFSLine(randomDate, user, computer, dstMachine,
                    indicatorScore), randomDate.getMillis());
            addToBucketMap(randomDate, bucketMap);
            //create only one indicator
            if (i == 0) {
                indicators.add(createIndicator(user.getAdDn(), EvidenceType.AnomalySingleEvent, randomDate.toDate(),
                        randomDate.toDate(), dataSource, indicatorScore + 0.0, "event_time",
                        randomDate.toDate().toString(), 1, EvidenceTimeframe.Hourly));
            }
        }
        //create hourly buckets
        for (Map.Entry<DateTime, Integer> bucket: bucketMap.entrySet()) {
            createBucket(user.getUsername(), KEY, dataSource, "hourly", bucket.getKey().getMillis() / 1000,
                    bucket.getKey().plusHours(1).minusMillis(1).getMillis() / 1000, bucket.getValue(),
                    "number_of_events_per_hour_histogram");
        }
        //create daily bucket
        //TODO - ??
        return indicators;
    }

    /**
     *
     * This method generates a random hour for a specific day
     *
     * @param dt
     * @param standardDeviation
     * @param mean
     * @param max
     * @param min
     * @return
     */
    private DateTime generateRandomTimeForDay(DateTime dt, int standardDeviation, int mean, int max, int min) {
        Random random = new Random();
        //temp initialization
        int hour = 0;
        //while the randomized time is not between normal work hours
        while (hour < min || hour > max) {
            hour = (int)(random.nextGaussian() * standardDeviation + mean);
        }
        return new DateTime(dt).withHourOfDay(hour)
                .withMinuteOfHour(random.nextInt(60))
                .withSecondOfMinute(random.nextInt(60))
                .withMillisOfSecond(random.nextInt(1000));
    }

    /**
     *
     * This method generates a random time for an anomaly
     *
     * @param dt
     * @param minHour
     * @param maxHour
     * @return
     */
    private DateTime generateRandomTimeForAnomaly(DateTime dt, int minHour, int maxHour) {
        Random random = new Random();
        int hour = random.nextInt(maxHour - minHour) + minHour;
        return new DateTime(dt).withHourOfDay(hour)
                .withMinuteOfHour(random.nextInt(60))
                .withSecondOfMinute(random.nextInt(60))
                .withMillisOfSecond(random.nextInt(1000));
    }

    /**
     *
     * This method generates a random day for the anomaly
     *
     * @param minDaysBack
     * @param maxDaysBack
     * @return
     */
    private DateTime generateRandomDayForAnomaly(int minDaysBack, int maxDaysBack) {
        Random random = new Random();
        int daysBack = random.nextInt(minDaysBack - maxDaysBack) + maxDaysBack;
        return new DateTime().withZone(DateTimeZone.UTC).minusDays(daysBack)
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
    }

    /**
     *
     * This method creates the actual csv line to write in HDFS
     *
     * @param dt
     * @param user
     * @param srcMachine
     * @param dstMachine
     * @param score
     * @return
     */
    private String buildKerberosHDFSLine(DateTime dt, User user, Computer srcMachine, String dstMachine, int score) {
        DateTimeFormatter hdfsFolderFormat = DateTimeFormat.forPattern("yyyyMMdd");
        DateTimeFormatter hdfsTimestampFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        String srcClass = "Desktop";
        String dstClass = "Server";

        //TODO - extract these values
        int dateTimeScore = score;
        String domain = "FORTSCALE";
        String failureCode = "0x0";
        int failureCodeScore = 0;
        String clientAddress = "192.168.171.2";
        boolean isNat = false;
        int normalizedSrcMachineScore = 0;
        int normalizedDstMachineScore = 0;
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
                .append(srcMachine.getIsSensitive() == null ? false : srcMachine.getIsSensitive()).append(SEPARATOR)
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
     * @param anomalyTypeFieldName
     * @param anomalyValue
     * @param numberOfEvents
     * @param evidenceTimeframe
     * @return
     */
    private Evidence createIndicator(String username, EvidenceType evidenceType, Date startTime, Date endTime,
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
     * This method generates a random IP address
     *
     * @return
     */
    private String generateRandomIPAddress() {
        Random random = new Random();
        return random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256);
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
    private void createAlert(String title, long startTime, long endTime, User user, List<Evidence> evidences,
            int roundScore, Severity severity) {
        Alert alert = new Alert(title, startTime, endTime, EntityType.User, user.getUsername(), evidences,
                evidences.size(), roundScore, severity, AlertStatus.Open, AlertFeedback.None, "", user.getId());
        alertsService.add(alert);
    }

    /**
     *
     * This method returns the number of steps in the job
     *
     * @return
     */
	@Override
	protected int getTotalNumOfSteps() { return 1; }

    /**
     *
     * This method notifies the FortscaleJob class if it should report data transfers
     *
     * @return
     */
	@Override
	protected boolean shouldReportDataReceived() { return false; }

    /**
     *
     * This class contains the necessary properties to write to HDFS
     *
     */
    private class HDFSProperties {

        private String impalaTable;
        private String fileName;
        private String hdfsPartition;

        private HDFSProperties(String impalaTable, String fileName, String hdfsPartition) {
            this.impalaTable = impalaTable;
            this.fileName = fileName;
            this.hdfsPartition = hdfsPartition;
        }

        public String getImpalaTable() {
            return impalaTable;
        }

        public String getFileName() {
            return fileName;
        }

        public String getHdfsPartition() {
            return hdfsPartition;
        }

    }

}