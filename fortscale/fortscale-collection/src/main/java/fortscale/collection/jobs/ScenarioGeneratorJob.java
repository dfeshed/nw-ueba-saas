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
        String title = "Suspicious " + timeSpan + " User Activity";
        int alertScore = 80;
        Severity alertSeverity = Severity.High;
        double indicatorScore = 98;

        String username = samaccountname + "@" + domain;
        String srcMachine = samaccountname + "_PC";
        String dstMachine = samaccountname + "_SRV";
        Computer computer = computerRepository.findByName(srcMachine);
        if (computer == null) {
            logger.error("computer {} not found - exiting", srcMachine);
        }
        User user = userService.findByUsername(username);
        if (user == null) {
            logger.error("user {} not found - exiting", username);
        }
        HDFSProperties hdfsProperties = dataSourceToHDFSProperties.get(dataSource);

        /*********************** Events **********************/
        HdfsService service = new HdfsService(hdfsProperties.hdfsPartition, hdfsProperties.fileName, partitionStrategy,
                splitStrategy, hdfsProperties.getImpalaTable(), 1, 0, SEPARATOR);
        createEvents(user, computer, dstMachine, service, alertScore, alertSeverity, indicatorScore, title, dataSource);

        /*********************** Buckets *********************/
        //createBucket(username, KEY, dataSource, timeSpan.toLowerCase(), startTime, endTime, 1, "destination_machine_histogram");

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
     * @param service
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     * @throws HdfsException
     */
    public void createEvents(User user, Computer computer, String dstMachine, HdfsService service, int alertScore,
            Severity alertSeverity, double indicatorScore, String title, String dataSource)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException, HdfsException {
        Random random = new Random();
        DateTime now = new DateTime().withZone(DateTimeZone.UTC);
        DateTime dt = now.minusDays(numOfDaysBack);
        List<Pair<Long, String>> eventsToWrite = new ArrayList();
        while (dt.isBefore(now)) {
            int numberOfMorningEvents = random.nextInt(numberOfMaxEventsPerTimePeriod - numberOfMinEventsPerTimePeriod)
                    + numberOfMinEventsPerTimePeriod;
            int numberOfAfternoonEvents = random.nextInt(numberOfMaxEventsPerTimePeriod -
                    numberOfMinEventsPerTimePeriod) + numberOfMinEventsPerTimePeriod;
            for (int j = 0; j < numberOfMorningEvents; j++) {
                DateTime dateTime = generateRandomTimeForDay(dt, standardDeviation, morningMedianHour, maxHourOfWork,
                        minHourOfWork);
                /*eventsToWrite.add(new ImmutablePair(dateTime.getMillis(),
                        buildKerberosHDFSLine(dateTime, user, computer, dstMachine, 0)));*/
                service.writeLineToHdfs(buildKerberosHDFSLine(dateTime, user, computer, dstMachine, 0),
                        dateTime.getMillis());
            }
            for (int j = 0; j < numberOfAfternoonEvents; j++) {
                DateTime dateTime = generateRandomTimeForDay(dt, standardDeviation, afternoonMedianHour, maxHourOfWork,
                        minHourOfWork);
                /*eventsToWrite.add(new ImmutablePair(dateTime.getMillis(),
                        buildKerberosHDFSLine(dateTime, user, computer, dstMachine, 0)));*/
                service.writeLineToHdfs(buildKerberosHDFSLine(dateTime, user, computer, dstMachine, 0),
                        dateTime.getMillis());
            }
            dt = dt.plusDays(1);
            if (skipWeekend && dt.getDayOfWeek() == DateTimeConstants.SATURDAY) {
                dt = dt.plusDays(2);
            } else if (skipWeekend && dt.getDayOfWeek() == DateTimeConstants.SUNDAY) {
                dt = dt.plusDays(1);
            }
        }
        //TODO - extract these - 2, 3, 3, 5
        /*eventsToWrite.addAll(generateTimeLoginAnomalies(now.minusDays(1), now.minusDays(numOfDaysBack), 2, 3, 3, 5,
                user, computer, dstMachine, dataSource, title, indicatorScore, alertScore, alertSeverity));
        sendToHDFS(service, eventsToWrite);*/
    }

    /**
     *
     * This method generates a random number of time login anomalies
     *
     * @param start
     * @param end
     * @param minNumberOfAnomalies
     * @param maxNumberOfAnomalies
     * @return
     */
    public List<Pair<Long, String>> generateTimeLoginAnomalies(DateTime start, DateTime end, int minNumberOfAnomalies,
            int maxNumberOfAnomalies, int minHourForAnomaly, int maxHourForAnomaly, User user, Computer computer,
            String dstMachine, String dataSource, String title, double indicatorScore, int alertScore,
            Severity alertSeverity) {
        DateTimeFormatter hdfsFolderFormat = DateTimeFormat.forPattern("yyyyMMdd");
        List<Pair<Long, String>> anomalousEvents = new ArrayList();
        Set<String> usedDates = new HashSet();
        Random random = new Random();
        int numberOfAnomalies = random.nextInt(maxNumberOfAnomalies - minNumberOfAnomalies) + minNumberOfAnomalies;
        List<Evidence> indicators = new ArrayList();
        for (int i = 0; i < numberOfAnomalies; i++) {
            long randomTimeStamp = (long)(start.getMillis() + Math.random() * (end.getMillis() - start.getMillis()));
            String randomDateStr = hdfsFolderFormat.print(new DateTime(randomTimeStamp));
            while (!usedDates.isEmpty() && usedDates.contains(randomDateStr)) {
                randomTimeStamp = (long)(start.getMillis() + Math.random() * (end.getMillis() - start.getMillis()));
                randomDateStr = hdfsFolderFormat.print(new DateTime(randomTimeStamp));
            }
            usedDates.add(randomDateStr);
            DateTime randomDate = new DateTime(randomTimeStamp)
                .withZone(DateTimeZone.UTC)
                .withHourOfDay(random.nextInt(maxHourForAnomaly - minHourForAnomaly) + minHourForAnomaly)
                .withMinuteOfHour(random.nextInt(60))
                .withSecondOfMinute(random.nextInt(60))
                .withMillisOfSecond(random.nextInt(1000));
            anomalousEvents.add(new ImmutablePair(randomDate.getMillis(), buildKerberosHDFSLine(randomDate, user,
                    computer, dstMachine, (int)indicatorScore)));
            indicators.add(createIndicator(user.getUsername(), EvidenceType.AnomalySingleEvent, randomDate.toDate(),
                    randomDate.toDate(), dataSource, indicatorScore + 0.0, "destination_machine", dstMachine, 1,
                    EvidenceTimeframe.Hourly));
        }
        //createAlert(title, startTimeMillis, endTimeMillis, user, indicators, alertScore, alertSeverity);
        return anomalousEvents;
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
        for (Pair<Long, String> tuple: eventsToWrite) {
			service.writeLineToHdfs(tuple.getValue(), tuple.getKey());
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
    public String buildKerberosHDFSLine(DateTime dt, User user, Computer srcMachine, String dstMachine, int score) {
        DateTimeFormatter hdfsFolderFormat = DateTimeFormat.forPattern("yyyyMMdd");
        DateTimeFormatter hdfsTimestampFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        //TODO - extract these values
        int dateTimeScore = score;
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
     * This class contains the necessasry properties to write to HDFS
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