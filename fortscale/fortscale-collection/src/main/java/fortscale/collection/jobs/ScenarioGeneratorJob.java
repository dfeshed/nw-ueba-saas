package fortscale.collection.jobs;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import fortscale.aggregation.feature.event.AggregatedEventQueryMongoService;
import fortscale.aggregation.feature.event.FeatureBucketQueryService;
import fortscale.aggregation.feature.util.GenericHistogram;
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
import net.minidev.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

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
    private static final String BUCKET_PREFIX = "fixed_duration_";

    @Autowired
    private UserService userService;
    @Autowired
    private ComputerRepository computerRepository;
    @Autowired
    private AlertsService alertsService;
    @Autowired
    private EvidencesService evidencesService;
    @Autowired
    private FeatureBucketQueryService featureBucketQueryService;
    @Autowired
    private AggrFeatureEventBuilderService aggrFeatureEventBuilderService;
    @Autowired
    private AggregatedEventQueryMongoService aggregatedEventQueryMongoService;

    private FileSplitStrategy splitStrategy;
    private PartitionStrategy partitionStrategy;
    private Map<DataSource, HDFSProperties> dataSourceToHDFSProperties;
    private int numOfDaysBack;
    private int maxHourOfWork;
    private int minHourOfWork;
    private int numberOfMaxEventsPerTimePeriod;
    private int numberOfMinEventsPerTimePeriod;
    private int limitNumberOfDestinationMachines;
    private int standardDeviation;
    private int morningMedianHour;
    private int afternoonMedianHour;
    private boolean skipWeekend;
    private List<Computer> machines;
    private int minNumberOfDestMachines;
    private int maxNumberOfDestMachines;

    private enum EventFailReason { TIME, FAILURE, SOURCE, DEST }
    private enum DataSource { kerberos_logins, ssh, vpn }

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
        limitNumberOfDestinationMachines = jobDataMapExtension.getJobDataMapIntValue(map,
                "limitNumberOfDestinationMachines");
        standardDeviation = jobDataMapExtension.getJobDataMapIntValue(map, "standardDeviation");
        morningMedianHour = jobDataMapExtension.getJobDataMapIntValue(map, "morningMedianHour");
        afternoonMedianHour = jobDataMapExtension.getJobDataMapIntValue(map, "afternoonMedianHour");
        skipWeekend = jobDataMapExtension.getJobDataMapBooleanValue(map, "skipWeekend", true);
        machines = computerRepository.getComputersOfType(ComputerUsageType.Server, limitNumberOfDestinationMachines);
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
    private Map<DataSource, HDFSProperties> buildDataSourceToHDFSPropertiesMap(JobDataMap map)
            throws JobExecutionException {
        Map<DataSource, HDFSProperties> result = new HashMap();
        for (String dataSourceStr: jobDataMapExtension.getJobDataMapStringValue(map, "dataEntities").split(",")) {
            DataSource dataSource = DataSource.valueOf(dataSourceStr);
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
    private void generateScenario1()
            throws ClassNotFoundException, IOException, HdfsException, InstantiationException, IllegalAccessException {

        //TODO - extract these
        String samaccountname = "alrusr51";
        String domain = "somebigcompany.com";
        String timeSpan = "Hourly";
        int eventScore = 98;
        String title = "Suspicious " + timeSpan + " User Activity";
        int alertScore = 80;
        Severity alertSeverity = Severity.High;
        minNumberOfDestMachines = 2;
        maxNumberOfDestMachines = 3;
        int minNumberOfDestMachineAnomalies = 8;
        int maxNumberOfDestMachineAnomalies = 10;
        int minNumberOfTimeAnomalies = 2;
        int maxNumberOfTimeAnomalies = 3;
        int minNumberOfFailureAnomalies = 5;
        int maxNumberOfFailureAnomalies = 6;
        int minHourForAnomaly = 3;
        int maxHourForAnomaly = 5;
        String computerDomain = "FORTSCALE";
        String dc = "FS-DC-01$";

        DateTime anomalyDate = new DateTime().withZone(DateTimeZone.UTC)
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        int anomalousHour = generateRandomTimeForAnomaly(anomalyDate, minHourForAnomaly, maxHourForAnomaly).
                getHourOfDay();
        if (skipWeekend && anomalyDate.getDayOfWeek() == DateTimeConstants.SATURDAY) {
            anomalyDate = anomalyDate.minusDays(1);
        } else if (skipWeekend && anomalyDate.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            anomalyDate = anomalyDate.minusDays(2);
        }
        String clientAddress = generateRandomIPAddress();
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
        createLoginEvents(user, computer, dstMachine, DataSource.kerberos_logins,
                computerDomain, dc, clientAddress, anomalyDate, "number_of_events_per_hour_histogram",
                "number_of_failed_" + DataSource.kerberos_logins + "_" + EvidenceTimeframe.Daily.name().toLowerCase());
        createLoginEvents(user, computer, dstMachine, DataSource.ssh, computerDomain, dc, clientAddress, anomalyDate,
                "number_of_events_per_hour_histogram", "number_of_" + DataSource.ssh + "_events_" + EvidenceTimeframe.
                        Hourly.name().toLowerCase());
        List<Evidence> indicators = new ArrayList();
        indicators.addAll(createLoginAnomalies(DataSource.kerberos_logins, anomalyDate, minNumberOfTimeAnomalies,
                maxNumberOfTimeAnomalies, minHourForAnomaly, maxHourForAnomaly, user, computer, dstMachine, eventScore,
                computerDomain, dc, clientAddress, EventFailReason.TIME, EvidenceTimeframe.Hourly,
                EvidenceType.AnomalySingleEvent, "event_time", "number_of_events_per_hour_histogram"));
        indicators.addAll(createLoginAnomalies(DataSource.kerberos_logins, anomalyDate,
                minNumberOfFailureAnomalies, maxNumberOfFailureAnomalies, minHourForAnomaly, maxHourForAnomaly, user,
                computer, dstMachine, eventScore, computerDomain, dc, clientAddress, EventFailReason.FAILURE,
                EvidenceTimeframe.Daily, EvidenceType.AnomalyAggregatedEvent, "number_of_failed_" + DataSource.
                        kerberos_logins + "_" + EvidenceTimeframe.Daily.name().toLowerCase(),
                "failure_code_histogram"));
        indicators.addAll(createLoginAnomalies(DataSource.ssh, anomalyDate,
                minNumberOfDestMachineAnomalies, maxNumberOfDestMachineAnomalies, anomalousHour, anomalousHour,
                user, computer, dstMachine, eventScore, computerDomain, dc, clientAddress, EventFailReason.DEST,
                EvidenceTimeframe.Hourly, EvidenceType.AnomalyAggregatedEvent, "distinct_number_of_dst_machines_" +
                        DataSource.ssh + "_" + EvidenceTimeframe.Hourly.name().toLowerCase(),
                "number_of_events_per_hour_histogram"));
        //TODO - generate last indicator
        createAlert(title, anomalyDate.getMillis(), anomalyDate.plusDays(1).minusMillis(1).getMillis(), user,
                indicators, alertScore, alertSeverity);
    }

    /**
     *
     * This method generates a number of random destination machines
     *
     * @param minNumberOfDestMachines
     * @param maxNumberOfDestMachines
     * @return
     */
    private List<String> generateRandomDestinationMachines(int minNumberOfDestMachines, int maxNumberOfDestMachines) {
        Random random = new Random();
        List<String> result = new ArrayList();
        int numberOfDestinationMachines = random.nextInt(maxNumberOfDestMachines - minNumberOfDestMachines) +
                minNumberOfDestMachines;
        for (int i = 0; i < numberOfDestinationMachines; i++) {
            int index = random.nextInt(machines.size());
            result.add(machines.get(index).getName());
        }
        return result;
    }

    /**
     *
     * This method generates a single bucket
     *
     * @param username
     * @param dataSource
     * @param timeSpan
     * @param start
     * @param end
     * @param genericHistogram
     * @param featureName
     */
    private void createBucket(String username, String dataSource, String timeSpan, DateTime start,
            DateTime end, GenericHistogram genericHistogram, String featureName) {
        long startTime = start.getMillis() / 1000;
        long endTime = end.getMillis() / 1000;
        String collectionName = "aggr_" + KEY + "_" + dataSource + "_" + timeSpan;
        String bucketId = BUCKET_PREFIX + timeSpan + "_" + startTime + "_" + KEY + " _" + username;
        FeatureBucket bucket = featureBucketQueryService.getFeatureBucketsById(bucketId, collectionName);
        if (bucket == null) {
            bucket = new FeatureBucket();
            bucket.setBucketId(bucketId);
            bucket.setCreatedAt(new Date());
            bucket.setContextFieldNames(Arrays.asList(new String[]{ KEY }));
            bucket.setDataSources(Arrays.asList(new String[]{ dataSource }));
            bucket.setFeatureBucketConfName(KEY + "_" + dataSource + "_" + timeSpan);
            bucket.setStrategyId(BUCKET_PREFIX + timeSpan + "_" + startTime);
            bucket.setStartTime(startTime);
            bucket.setEndTime(endTime);
            Feature feature = new Feature();
            feature.setName(featureName);
            feature.setValue(genericHistogram);
            Map<String, Feature> features = new HashMap();
            features.put(featureName, feature);
            bucket.setAggregatedFeatures(features);
            Map<String, String> contextFieldNameToValueMap = new HashMap();
            contextFieldNameToValueMap.put(KEY, username);
            bucket.setContextFieldNameToValueMap(contextFieldNameToValueMap);
            featureBucketQueryService.addBucket(bucket, collectionName);
        } else {
            Map<String, Feature> featureMap = bucket.getAggregatedFeatures();
            if (featureMap.containsKey(featureName)) {
                GenericHistogram histogram = (GenericHistogram)featureMap.get(featureName).getValue();
                histogram.add(genericHistogram);
            } else {
                Feature feature = new Feature();
                feature.setName(featureName);
                feature.setValue(genericHistogram);
                featureMap.put(featureName, feature);
            }
            featureBucketQueryService.updateBucketFeatureMap(bucket.getBucketId(), featureMap, collectionName);
        }
    }

    /**
     *
     * This method generates a single scored bucket
     *
     * @param username
     * @param aggrFeatureName
     * @param dataSource
     * @param timeSpan
     * @param start
     * @param end
     * @param count
     */
    private void createScoredBucket(String username, String aggrFeatureName, String dataSource, String timeSpan,
            DateTime start, DateTime end, int count) {
        long startTime = start.getMillis() / 1000;
        long endTime = end.getMillis() / 1000;
        String collectionName = AggregatedEventQueryMongoService.SCORED_AGGR_EVENT_COLLECTION_PREFIX + aggrFeatureName;
        String featureType = "F";
        String aggregatedFeatureName = "number_of_" + aggrFeatureName;
        String bucketConfName = KEY + "_" + dataSource + "_" + timeSpan;
        Map<String, String> context = new HashMap();
        context.put(KEY, username);
        Map<String, Object> additionalInfoMap = new HashMap();
        additionalInfoMap.put("total", count);
        List<String> dataSources = Arrays.asList(new String[] { dataSource });
        JSONObject event = aggrFeatureEventBuilderService.buildEvent(dataSource, featureType, aggregatedFeatureName,
                count + 0.0, additionalInfoMap, bucketConfName, context, startTime, endTime, dataSources,
                new Date().getTime());
        event.put(AggrEvent.EVENT_FIELD_SCORE, 0.0);
        AggrEvent aggrEvent = aggrFeatureEventBuilderService.buildEvent(event);
        aggregatedEventQueryMongoService.insertAggregatedEvent(collectionName, aggrEvent);
    }

    /**
     *
     * This method generates the events in HDFS and Impala
     *
     * @param user
     * @param computer
     * @param dstMachine
     * @param dataSource
     * @param dc
     * @param computerDomain
     * @param clientAddress
     * @param anomalyDate
     * @param featureName
     * @param aggrFeatureName
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     * @throws HdfsException
     */
    private void createLoginEvents(User user, Computer computer, String dstMachine, DataSource dataSource,
            String dc, String computerDomain, String clientAddress, DateTime anomalyDate, String featureName,
            String aggrFeatureName) throws ClassNotFoundException, IllegalAccessException, InstantiationException,
            IOException, HdfsException {
        HDFSProperties hdfsProperties = dataSourceToHDFSProperties.get(dataSource);
        HdfsService service = new HdfsService(hdfsProperties.getHdfsPartition(), hdfsProperties.getFileName(),
                partitionStrategy, splitStrategy, hdfsProperties.getImpalaTable(), 1, 0, SEPARATOR);
        Random random = new Random();
        DateTime dt = anomalyDate.minusDays(numOfDaysBack);
        while (dt.isBefore(anomalyDate.minusMillis(1))) {
            if (skipWeekend && dt.getDayOfWeek() == DateTimeConstants.SATURDAY) {
                dt = dt.plusDays(2);
            } else if (skipWeekend && dt.getDayOfWeek() == DateTimeConstants.SUNDAY) {
                dt = dt.plusDays(1);
            }
            List<String> targetMachines = null;
            if (dataSource == DataSource.ssh) {
                targetMachines = generateRandomDestinationMachines(minNumberOfDestMachines, maxNumberOfDestMachines);
            }
            int numberOfMorningEvents = random.nextInt(numberOfMaxEventsPerTimePeriod - numberOfMinEventsPerTimePeriod)
                    + numberOfMinEventsPerTimePeriod;
            int numberOfAfternoonEvents = random.nextInt(numberOfMaxEventsPerTimePeriod -
                    numberOfMinEventsPerTimePeriod) + numberOfMinEventsPerTimePeriod;
            Map<DateTime, Integer> bucketMap = new HashMap();
            String lineToWrite = null;
            for (int j = 0; j < numberOfMorningEvents; j++) {
                DateTime dateTime = generateRandomTimeForDay(dt, standardDeviation, morningMedianHour, maxHourOfWork,
                        minHourOfWork);
                addToBucketMap(dateTime, bucketMap);
                switch (dataSource) {
                    case kerberos_logins: lineToWrite = buildKerberosHDFSLine(dateTime, user, computer, dstMachine, 0,
                            EventFailReason.TIME, computerDomain, dc, clientAddress); break;
                    case ssh: {
                        dstMachine = selectRandomStringInList(targetMachines);
                        lineToWrite = buildSshHDFSLine(dateTime, user, computer, dstMachine, 0, EventFailReason.TIME,
                                clientAddress);
                        break;
                    }
                }
                service.writeLineToHdfs(lineToWrite, dateTime.getMillis());
            }
            for (int j = 0; j < numberOfAfternoonEvents; j++) {
                DateTime dateTime = generateRandomTimeForDay(dt, standardDeviation, afternoonMedianHour, maxHourOfWork,
                        minHourOfWork);
                addToBucketMap(dateTime, bucketMap);
                switch (dataSource) {
                    case kerberos_logins: lineToWrite = buildKerberosHDFSLine(dateTime, user, computer, dstMachine, 0,
                        EventFailReason.TIME, computerDomain, dc, clientAddress); break;
                    case ssh: {
                        dstMachine = selectRandomStringInList(targetMachines);
                        lineToWrite = buildSshHDFSLine(dateTime, user, computer, dstMachine, 0, EventFailReason.TIME,
                                clientAddress);
                        break;
                    }
                }
                service.writeLineToHdfs(lineToWrite, dateTime.getMillis());
            }
            //create hourly buckets
            GenericHistogram dailyHistogram = new GenericHistogram();
            for (Map.Entry<DateTime, Integer> bucket: bucketMap.entrySet()) {
                GenericHistogram genericHistogram = new GenericHistogram();
                genericHistogram.add(bucket.getKey().getHourOfDay(), bucket.getValue() + 0.0);
                dailyHistogram.add(bucket.getKey().getHourOfDay(), bucket.getValue() + 0.0);
                createBucket(user.getUsername(), dataSource.name(), "hourly", bucket.getKey(),
                        bucket.getKey().plusHours(1).minusMillis(1), genericHistogram, featureName);
                //TODO - check this logic
                if (!dt.equals(anomalyDate)) {
                    createScoredBucket(user.getUsername(), aggrFeatureName, dataSource.name(), "daily", bucket.getKey(),
                            bucket.getKey().plusDays(1).minusMillis(1), 0);
                }
            }
            //create daily bucket
            createBucket(user.getUsername(), dataSource.name(), "daily", dt, dt.plusDays(1).minusMillis(1),
                    dailyHistogram, featureName);
            if (!dt.equals(anomalyDate)) {
                createScoredBucket(user.getUsername(), aggrFeatureName, dataSource.name(), "daily", dt,
                        dt.plusDays(1).minusMillis(1), 0);
            }
            dt = dt.plusDays(1);
        }
    }

    /**
     *
     * This method picks one of the random strings in the list
     *
     * @param strList
     * @return
     */
    private String selectRandomStringInList(List<String> strList) {
        Random random = new Random();
        return strList.get(random.nextInt(strList.size()));
    }

    /**
     *
     * This method adds the bucket to the bucket map
     *
     * @param dateTime
     * @param bucketMap
     */
    private void addToBucketMap(DateTime dateTime, Map<DateTime, Integer> bucketMap) {
        DateTime startOfHour = dateTime
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
     * @param dc
     * @param computerDomain
     * @param clientAddress
     * @param reason
     * @param timeframe
     * @param evidenceType
     * @throws HdfsException
     * @throws IOException
     * @return
     */
    private List<Evidence> createLoginAnomalies(DataSource dataSource, DateTime anomalyDate, int minNumberOfAnomalies,
            int maxNumberOfAnomalies, int minHourForAnomaly, int maxHourForAnomaly, User user, Computer computer,
            String dstMachine, int indicatorScore, String dc, String computerDomain, String clientAddress,
            EventFailReason reason, EvidenceTimeframe timeframe, EvidenceType evidenceType, String anomalyTypeFieldName,
            String histogramName) throws HdfsException, IOException {
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
        List<String> targetMachines = null;
        if (dataSource == DataSource.ssh) {
            targetMachines = generateRandomDestinationMachines(minNumberOfDestMachines, maxNumberOfDestMachines);
        }
        for (int i = 0; i < numberOfAnomalies; i++) {
            DateTime randomDate = generateRandomTimeForAnomaly(anomalyDate, minHourForAnomaly, maxHourForAnomaly);
            String lineToWrite = null;
            switch (dataSource) {
                case kerberos_logins: lineToWrite = buildKerberosHDFSLine(randomDate, user, computer, dstMachine,
                        indicatorScore, reason, computerDomain, dc, clientAddress); break;
                case ssh: {
                    dstMachine = selectRandomStringInList(targetMachines);
                    lineToWrite = buildSshHDFSLine(randomDate, user, computer, dstMachine, indicatorScore, reason,
                            clientAddress);
                    break;
                }
            }
            service.writeLineToHdfs(lineToWrite, randomDate.getMillis());
            service_top.writeLineToHdfs(lineToWrite, randomDate.getMillis());
            addToBucketMap(randomDate, bucketMap);
            //create only one indicator
            if (i == 0) {
                if (evidenceType == EvidenceType.AnomalySingleEvent) {
                    if (reason == EventFailReason.TIME) {
                        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.0");
                        indicators.add(createIndicator(user.getUsername(), evidenceType,
                                randomDate.toDate(), randomDate.toDate(), dataSource.name(), indicatorScore + 0.0,
                                anomalyTypeFieldName, dateTimeFormatter.print(randomDate), 1, timeframe));
                    } else {
                        indicators.add(createIndicator(user.getUsername(), evidenceType,
                                randomDate.toDate(), randomDate.toDate(), dataSource.name(), indicatorScore + 0.0,
                                anomalyTypeFieldName, ((double)numberOfAnomalies) + "", numberOfAnomalies, timeframe));
                    }
                } else {
                    randomDate = randomDate.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
                    if (timeframe == EvidenceTimeframe.Hourly) {
                        indicators.add(createIndicator(user.getUsername(), evidenceType, randomDate.toDate(),
                                randomDate.plusHours(1).minusMillis(1).toDate(), dataSource.name(), indicatorScore +
                                        0.0, anomalyTypeFieldName, ((double)numberOfAnomalies) + "", numberOfAnomalies,
                                timeframe));
                    } else {
                        indicators.add(createIndicator(user.getUsername(), evidenceType, anomalyDate.toDate(),
                                anomalyDate.plusDays(1).minusMillis(1).toDate(), dataSource.name(), indicatorScore +
                                        0.0, anomalyTypeFieldName, ((double)numberOfAnomalies) + "", numberOfAnomalies,
                                timeframe));
                    }
                }
            }
        }
        //create hourly buckets
        GenericHistogram dailyHistogram = new GenericHistogram();
        for (Map.Entry<DateTime, Integer> bucket: bucketMap.entrySet()) {
            GenericHistogram genericHistogram = new GenericHistogram();
            genericHistogram.add(bucket.getKey().getHourOfDay(), bucket.getValue() + 0.0);
            dailyHistogram.add(bucket.getKey().getHourOfDay(), bucket.getValue() + 0.0);
            createBucket(user.getUsername(), dataSource.name(), "hourly", bucket.getKey(), bucket.getKey().plusHours(1).
                    minusMillis(1), genericHistogram, histogramName);
            createScoredBucket(user.getUsername(), anomalyTypeFieldName, dataSource.name(), "hourly",
                    bucket.getKey(), bucket.getKey().plusHours(1).minusMillis(1), (int)dailyHistogram.getTotalCount());
        }
        //create daily bucket
        createBucket(user.getUsername(), dataSource.name(), "daily", anomalyDate, anomalyDate.plusDays(1).
                        minusMillis(1), dailyHistogram, histogramName);
        createScoredBucket(user.getUsername(), anomalyTypeFieldName, dataSource.name(), "daily", anomalyDate,
                anomalyDate.plusDays(1).minusMillis(1), (int)dailyHistogram.getTotalCount());
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
        int hour = -1;
        //while the randomized time is not between normal work hours
        while (hour < min || hour > max) {
            hour = (int)(random.nextGaussian() * standardDeviation + mean);
        }
        return dt.withHourOfDay(hour)
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
        int hour;
        if (maxHour == minHour) {
            hour = maxHour;
        } else {
            hour = random.nextInt(maxHour - minHour) + minHour;
        }
        return dt.withHourOfDay(hour)
                .withMinuteOfHour(random.nextInt(60))
                .withSecondOfMinute(random.nextInt(60))
                .withMillisOfSecond(random.nextInt(1000));
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
     * This method creates the actual csv line to write in HDFS
     *
     * @param dt
     * @param user
     * @param srcMachine
     * @param dstMachine
     * @param score
     * @param reason
     * @param domain
     * @param dc
     * @param clientAddress
     * @return
     */
    private String buildKerberosHDFSLine(DateTime dt, User user, Computer srcMachine, String dstMachine, int score,
            EventFailReason reason, String domain, String dc, String clientAddress) {
        DateTimeFormatter hdfsFolderFormat = DateTimeFormat.forPattern("yyyyMMdd");
        DateTimeFormatter hdfsTimestampFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        String srcClass = "Desktop";
        String dstClass = "Server";
        String failureCode = "0x0";
        int dateTimeScore = 0;
        int failureCodeScore = 0;
        int normalizedSrcMachineScore = 0;
        int normalizedDstMachineScore = 0;
        boolean isNat = false;
        switch (reason) {
            case TIME: dateTimeScore = score; break;
            case FAILURE: failureCodeScore = score; failureCode = "0x12"; break;
            case SOURCE: normalizedSrcMachineScore = score; break;
            case DEST: normalizedDstMachineScore = score; break;
        }
        int eventScore = Math.max(Math.max(dateTimeScore, failureCodeScore),
                Math.max(normalizedSrcMachineScore, normalizedDstMachineScore));
        long timestamp = new Date().getTime();
        String serviceId = domain + "\\" + dc;
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
     * This method creates the actual csv line to write in HDFS
     *
     * @param dt
     * @param user
     * @param srcMachine
     * @param dstMachine
     * @param score
     * @param clientAddress
     * @return
     */
    private String buildSshHDFSLine(DateTime dt, User user, Computer srcMachine, String dstMachine, int score,
            EventFailReason reason, String clientAddress) {
        DateTimeFormatter hdfsFolderFormat = DateTimeFormat.forPattern("yyyyMMdd");
        DateTimeFormatter hdfsTimestampFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        String srcClass = "Desktop";
        String dstClass = "Server";
        String authMethod = "password";
        String status = "Accepted";
        int dateTimeScore = 0;
        int authMethodScore = 0;
        int normalizedSrcMachineScore = 0;
        int normalizedDstMachineScore = 0;
        boolean isNat = false;
        switch (reason) {
            case TIME: dateTimeScore = score; break;
            case FAILURE: authMethodScore = score; break;
            case SOURCE: normalizedSrcMachineScore = score; break;
            case DEST: normalizedDstMachineScore = score; break;
        }
        int eventScore = Math.max(Math.max(dateTimeScore, authMethodScore), Math.max(normalizedSrcMachineScore,
                normalizedDstMachineScore));
        long timestamp = new Date().getTime();
        StringBuilder sb = new StringBuilder().append(hdfsTimestampFormat.print(dt)).append(SEPARATOR)
                .append(dt.getMillis() / 1000).append(SEPARATOR)
                .append(dateTimeScore).append(SEPARATOR)
                .append(user.getUsername()).append(SEPARATOR)
                .append(user.getUsername()).append(SEPARATOR)
                .append(user.getAdministratorAccount()).append(SEPARATOR)
                .append(user.getExecutiveAccount()).append(SEPARATOR)
                .append(user.getUserServiceAccount()).append(SEPARATOR).append(status).append(SEPARATOR)
                .append(authMethod).append(SEPARATOR)
                .append(authMethodScore).append(SEPARATOR)
                .append(clientAddress).append(SEPARATOR)
                .append(isNat).append(SEPARATOR)
                .append(srcMachine.getName().toUpperCase()).append(SEPARATOR)
                .append(srcMachine.getName().toUpperCase()).append(SEPARATOR)
                .append(normalizedSrcMachineScore).append(SEPARATOR)
                .append(dstMachine).append(SEPARATOR)
                .append(dstMachine.toUpperCase()).append(SEPARATOR)
                .append(normalizedDstMachineScore).append(SEPARATOR)
                .append(srcMachine.getIsSensitive() == null ? false : srcMachine.getIsSensitive()).append(SEPARATOR)
                .append(user.getTags().contains(UserTagEnum.LR.getId())).append(SEPARATOR)
                .append(eventScore).append(SEPARATOR)
                .append(srcClass).append(SEPARATOR)
                .append(dstClass).append(SEPARATOR)
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