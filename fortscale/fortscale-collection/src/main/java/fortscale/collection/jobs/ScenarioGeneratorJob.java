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
    private static final String HOURLY_HISTOGRAM = "number_of_events_per_hour_histogram";

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

    private enum EventFailReason { TIME, FAILURE, SOURCE, DEST, COUNTRY, NONE }
    private enum DataSource { kerberos_logins, ssh, vpn, amt }

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
        //TODO - generalize this
        generateScenario1();
        generateScenario2();
        generateScenario3();
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

        //TODO - extract these general scenario fields
        String samaccountname = "alrusr51";
        String domain = "somebigcompany.com";
        String timeSpan = "Hourly";
        int indicatorScore = 98;
        int eventScore = 98;
        String title = "Suspicious " + timeSpan + " User Activity";
        int alertScore = 80;
        Severity alertSeverity = Severity.High;
        String computerDomain = "FORTSCALE";
        String dc = "FS-DC-01$";
        int minHourForAnomaly = 3;
        int maxHourForAnomaly = 5;
        int minNumberOfDestMachines = 2;
        int maxNumberOfDestMachines = 3;
        //TODO - extract these specific indicator fields
        int minNumberOfAnomaliesIndicator1 = 2;
        int maxNumberOfAnomaliesIndicator1 = 3;
        int minNumberOfAnomaliesIndicator2 = 5;
        int maxNumberOfAnomaliesIndicator2 = 6;
        int minNumberOfAnomaliesIndicator3 = 8;
        int maxNumberOfAnomaliesIndicator3 = 10;
        int minNumberOfAnomaliesIndicator4 = 1;
        int maxNumberOfAnomaliesIndicator4 = 1;

        DateTime anomalyDate = new DateTime().withZone(DateTimeZone.UTC)
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        if (skipWeekend && anomalyDate.getDayOfWeek() == DateTimeConstants.SATURDAY) {
            anomalyDate = anomalyDate.minusDays(1);
        } else if (skipWeekend && anomalyDate.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            anomalyDate = anomalyDate.minusDays(2);
        }
        int anomalousHour = generateRandomTimeForAnomaly(anomalyDate, minHourForAnomaly, maxHourForAnomaly).
                getHourOfDay();
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
        List<Computer> machines = computerRepository.getComputersOfType(ComputerUsageType.Server,
                limitNumberOfDestinationMachines);
        if (machines.isEmpty()) {
            logger.error("no server machines found");
            return;
        }
        String service = "sausr29fs";
        Computer serviceMachine = new Computer();
        serviceMachine.setName(service.toUpperCase() + "_PC");
        String anomalousMachine = service.toUpperCase() + "_SRV";
        User serviceAccount = new User();
        serviceAccount.setUsername(service + "@" + domain);
        serviceAccount.setUserServiceAccount(true);
        Set<String> baseLineMachinesSet = generateRandomDestinationMachines(machines, minNumberOfDestMachines,
                maxNumberOfDestMachines);
        String[] baseLineMachines = baseLineMachinesSet.toArray(new String[baseLineMachinesSet.size()]);
        Set<String> anomalousMachinesSet = generateRandomDestinationMachines(machines, minNumberOfAnomaliesIndicator3,
                maxNumberOfAnomaliesIndicator3);
        String[] anomalousMachines = anomalousMachinesSet.toArray(new String[anomalousMachinesSet.size()]);
        //generate scenario
        List<Evidence> indicators = new ArrayList();
        //create baseline
        createLoginEvents(user, computer, new String[] { dstMachine }, DataSource.kerberos_logins,
                computerDomain, dc, clientAddress, anomalyDate, HOURLY_HISTOGRAM, "number_of_failed_" + DataSource.
                        kerberos_logins);
        createLoginEvents(user, computer, baseLineMachines, DataSource.ssh, computerDomain, dc, clientAddress,
                anomalyDate, "destination_machine_histogram", "distinct_number_of_dst_machines_" + DataSource.ssh);
        createLoginEvents(serviceAccount, serviceMachine, new String[] { anomalousMachine }, DataSource.ssh,
                computerDomain, dc, clientAddress, anomalyDate, "destination_machine_histogram",
                "distinct_number_of_dst_machines_" + DataSource.ssh);
        //create anomalies
        indicators.addAll(createLoginAnomalies(DataSource.kerberos_logins, anomalyDate, minNumberOfAnomaliesIndicator1,
                maxNumberOfAnomaliesIndicator1, minHourForAnomaly, maxHourForAnomaly, user, computer, new String[]
                        { dstMachine }, indicatorScore, eventScore, computerDomain, dc, clientAddress,
                EventFailReason.TIME, EvidenceTimeframe.Hourly, EvidenceType.AnomalySingleEvent, "event_time",
                HOURLY_HISTOGRAM, "0x0"));
        indicators.addAll(createLoginAnomalies(DataSource.kerberos_logins, anomalyDate,
                minNumberOfAnomaliesIndicator2, maxNumberOfAnomaliesIndicator2, minHourForAnomaly, maxHourForAnomaly,
                user, computer, new String[] { dstMachine }, indicatorScore, eventScore, computerDomain, dc,
                clientAddress, EventFailReason.FAILURE, EvidenceTimeframe.Daily, EvidenceType.AnomalyAggregatedEvent,
                "number_of_failed_" + DataSource.kerberos_logins, "failure_code_histogram", "0x12"));
        indicators.addAll(createLoginAnomalies(DataSource.ssh, anomalyDate,
                minNumberOfAnomaliesIndicator3, maxNumberOfAnomaliesIndicator3, anomalousHour, anomalousHour,
                user, computer, anomalousMachines, indicatorScore, 50, computerDomain, dc, clientAddress,
                EventFailReason.TIME, EvidenceTimeframe.Hourly, EvidenceType.AnomalyAggregatedEvent,
                "distinct_number_of_dst_machines_" + DataSource.ssh, HOURLY_HISTOGRAM, "Accepted"));
        indicators.addAll(createLoginAnomalies(DataSource.ssh, anomalyDate, minNumberOfAnomaliesIndicator4,
                maxNumberOfAnomaliesIndicator4, minHourForAnomaly, maxHourForAnomaly, user, computer, new String[]
                        { anomalousMachine }, indicatorScore, eventScore, computerDomain, dc, clientAddress,
                EventFailReason.DEST, EvidenceTimeframe.Hourly, EvidenceType.AnomalySingleEvent, "destination_machine",
                HOURLY_HISTOGRAM, "0x0"));
        //create alert
        createAlert(title, anomalyDate.getMillis(), anomalyDate.plusDays(1).minusMillis(1).getMillis(), user,
                indicators, alertScore, alertSeverity);
    }

    private void generateScenario2() {
        //TODO - implement
    }

    private void generateScenario3() {
        //TODO - implement
    }

    /**
     *
     * This method generates the events in HDFS and Impala
     *
     * @param user
     * @param computer
     * @param dstMachines
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
    private void createLoginEvents(User user, Computer computer, String[] dstMachines, DataSource dataSource,
            String dc, String computerDomain, String clientAddress, DateTime anomalyDate, String featureName,
            String aggrFeatureName) throws ClassNotFoundException, IllegalAccessException, InstantiationException,
            IOException, HdfsException {
        Random random = new Random();
        DateTime dt = anomalyDate.minusDays(numOfDaysBack);
        while (dt.isBefore(anomalyDate.minusMillis(1))) {
            if (skipWeekend && dt.getDayOfWeek() == DateTimeConstants.SATURDAY) {
                dt = dt.plusDays(2);
            } else if (skipWeekend && dt.getDayOfWeek() == DateTimeConstants.SUNDAY) {
                dt = dt.plusDays(1);
            }
            int numberOfMorningEvents = random.nextInt(numberOfMaxEventsPerTimePeriod - numberOfMinEventsPerTimePeriod)
                    + numberOfMinEventsPerTimePeriod;
            int numberOfAfternoonEvents = random.nextInt(numberOfMaxEventsPerTimePeriod -
                    numberOfMinEventsPerTimePeriod) + numberOfMinEventsPerTimePeriod;
            Map<DateTime, Integer> bucketMap = new HashMap();
            for (int j = 0; j < numberOfMorningEvents; j++) {
                eventGeneratorAux(dt, morningMedianHour, bucketMap, dataSource, user, computer, dstMachines,
                        computerDomain, dc, clientAddress, 0);
            }
            for (int j = 0; j < numberOfAfternoonEvents; j++) {
                eventGeneratorAux(dt, afternoonMedianHour, bucketMap, dataSource, user, computer, dstMachines,
                        computerDomain, dc, clientAddress, 0);
            }
            bucketCreationAux(bucketMap, user, dataSource, featureName, dt, anomalyDate, aggrFeatureName);
            dt = dt.plusDays(1);
        }
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
     * @param dstMachines
     * @param indicatorScore
     * @param eventScore
     * @param dc
     * @param computerDomain
     * @param clientAddress
     * @param reason
     * @param timeframe
     * @param evidenceType
     * @param status
     * @throws HdfsException
     * @throws IOException
     * @return
     */
    private List<Evidence> createLoginAnomalies(DataSource dataSource, DateTime anomalyDate, int minNumberOfAnomalies,
            int maxNumberOfAnomalies, int minHourForAnomaly, int maxHourForAnomaly, User user, Computer computer,
            String[] dstMachines, int indicatorScore, int eventScore, String dc, String computerDomain,
            String clientAddress, EventFailReason reason, EvidenceTimeframe timeframe, EvidenceType evidenceType,
            String anomalyTypeFieldName, String histogramName, String status) throws HdfsException, IOException {
        HDFSProperties hdfsProperties = dataSourceToHDFSProperties.get(dataSource);
        HdfsService service = new HdfsService(hdfsProperties.getHdfsPartition(), hdfsProperties.getFileName(),
                partitionStrategy, splitStrategy, hdfsProperties.getImpalaTable(), 1, 0, SEPARATOR);
        HdfsService service_top = new HdfsService(hdfsProperties.getHdfsPartition() + "_top",
                hdfsProperties.getFileName(), partitionStrategy, splitStrategy, hdfsProperties.getImpalaTable() +
                "_top", 1, 0, SEPARATOR);
        Random random = new Random();
        List<Evidence> indicators = new ArrayList();
        int numberOfAnomalies;
        if (maxNumberOfAnomalies == minNumberOfAnomalies) {
            numberOfAnomalies = maxNumberOfAnomalies;
        } else {
            numberOfAnomalies = random.nextInt(maxNumberOfAnomalies - minNumberOfAnomalies) + minNumberOfAnomalies;
        }
        Map<DateTime, Integer> bucketMap = new HashMap();
        for (int i = 0; i < numberOfAnomalies; i++) {
            DateTime randomDate = generateRandomTimeForAnomaly(anomalyDate, minHourForAnomaly, maxHourForAnomaly);
            String lineToWrite = null;
            String dstMachine = null;
            switch (dataSource) {
                case kerberos_logins: {
                    dstMachine = dstMachines[0];
                    lineToWrite = buildKerberosHDFSLine(randomDate, user, computer, dstMachine, eventScore, reason,
                            computerDomain, dc, clientAddress, status);
                    break;
                }
                case ssh: {
                    dstMachine = dstMachines[i];
                    lineToWrite = buildSshHDFSLine(randomDate, user, computer, dstMachine, eventScore, reason,
                            clientAddress, status);
                    break;
                }
            }
            service.writeLineToHdfs(lineToWrite, randomDate.getMillis());
            service_top.writeLineToHdfs(lineToWrite, randomDate.getMillis());
            addToBucketMap(randomDate, bucketMap);
            //create only one indicator
            if (i == 0) {
                indicatorCreationAux(evidenceType, reason, indicators, user, randomDate, dataSource, indicatorScore,
                        anomalyTypeFieldName, timeframe, numberOfAnomalies, anomalyDate, dstMachine, computer.
                                getName());
            }
        }
        //TODO - try to use the aux method for this as well
        //create hourly buckets
        GenericHistogram dailyHistogram = new GenericHistogram();
        for (Map.Entry<DateTime, Integer> bucket: bucketMap.entrySet()) {
            GenericHistogram genericHistogram = new GenericHistogram();
            genericHistogram.add(bucket.getKey().getHourOfDay(), bucket.getValue() + 0.0);
            dailyHistogram.add(bucket.getKey().getHourOfDay(), bucket.getValue() + 0.0);
            createBucket(user.getUsername(), dataSource.name(), EvidenceTimeframe.Hourly.name().toLowerCase(),
                    bucket.getKey(), bucket.getKey().plusHours(1).minusMillis(1), genericHistogram, histogramName);
            if (evidenceType == EvidenceType.AnomalyAggregatedEvent) {
                createScoredBucket(user.getUsername(), anomalyTypeFieldName + "_" + EvidenceTimeframe.Hourly.name().
                                toLowerCase(), dataSource.name(), EvidenceTimeframe.Hourly.name().toLowerCase(),
                        bucket.getKey(), bucket.getKey().plusHours(1).minusMillis(1), (int)dailyHistogram.
                                getTotalCount());
            }
        }
        //create daily bucket
        createBucket(user.getUsername(), dataSource.name(), EvidenceTimeframe.Daily.name().toLowerCase(), anomalyDate,
                anomalyDate.plusDays(1).minusMillis(1), dailyHistogram, histogramName);
        if (evidenceType == EvidenceType.AnomalyAggregatedEvent) {
            createScoredBucket(user.getUsername(), anomalyTypeFieldName + "_" + EvidenceTimeframe.Daily.name().
                            toLowerCase(), dataSource.name(), EvidenceTimeframe.Daily.name().toLowerCase(), anomalyDate,
                    anomalyDate.plusDays(1).minusMillis(1), (int)dailyHistogram.getTotalCount());
        }
        return indicators;
    }

    /**
     *
     * This method creates the actual csv line to write in HDFS for kerberos
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
     * @param failureCode
     * @return
     */
    private String buildKerberosHDFSLine(DateTime dt, User user, Computer srcMachine, String dstMachine, int score,
            EventFailReason reason, String domain, String dc, String clientAddress, String failureCode) {
        DateTimeFormatter hdfsFolderFormat = DateTimeFormat.forPattern("yyyyMMdd");
        DateTimeFormatter hdfsTimestampFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        String srcClass = "Desktop";
        String dstClass = "Server";
        int dateTimeScore = 0;
        int failureCodeScore = 0;
        int normalizedSrcMachineScore = 0;
        int normalizedDstMachineScore = 0;
        boolean isNat = false;
        switch (reason) {
            case TIME: dateTimeScore = score; break;
            case FAILURE: failureCodeScore = score; break;
            case SOURCE: normalizedSrcMachineScore = score; break;
            case DEST: normalizedDstMachineScore = score; break;
        }
        int eventScore = score;
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
     * This method creates the actual csv line to write in HDFS for ssh
     *
     * @param dt
     * @param user
     * @param srcMachine
     * @param dstMachine
     * @param score
     * @param reason
     * @param clientAddress
     * @param status
     * @return
     */
    private String buildSshHDFSLine(DateTime dt, User user, Computer srcMachine, String dstMachine, int score,
            EventFailReason reason, String clientAddress, String status) {
        DateTimeFormatter hdfsFolderFormat = DateTimeFormat.forPattern("yyyyMMdd");
        DateTimeFormatter hdfsTimestampFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        //TODO - extract this to parameter?
        String authMethod = "password";

        String srcClass = "Desktop";
        String dstClass = "Server";
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
        int eventScore = score;
        long timestamp = new Date().getTime();
        StringBuilder sb = new StringBuilder().append(hdfsTimestampFormat.print(dt)).append(SEPARATOR)
                .append(dt.getMillis() / 1000).append(SEPARATOR)
                .append(dateTimeScore).append(SEPARATOR)
                .append(user.getUsername()).append(SEPARATOR)
                .append(user.getUsername()).append(SEPARATOR)
                .append(user.getAdministratorAccount()).append(SEPARATOR)
                .append(user.getExecutiveAccount()).append(SEPARATOR)
                .append(user.getUserServiceAccount()).append(SEPARATOR)
                .append(status).append(SEPARATOR)
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
     * This method creates the actual csv line to write in HDFS for vpn
     *
     * @param dt
     * @param user
     * @param srcMachine
     * @param localIp
     * @param score
     * @param reason
     * @param country
     * @param status
     * @return
     */
    private String buildVpnHDFSLine(DateTime dt, User user, Computer srcMachine, String localIp, int score,
            EventFailReason reason, String country, String status) {
        DateTimeFormatter hdfsFolderFormat = DateTimeFormat.forPattern("yyyyMMdd");
        DateTimeFormatter hdfsTimestampFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        //TODO - check if this is not the other way around with locaIp, check if should extract as well
        String sourceIp = generateRandomIPAddress();

        String region = "Blantyre";
        String countryCode = "MW";
        String city = "Blantyre";
        String ipUsage = "isp";
        String isp = "Mtlonline.mw";
        String username = user.getUsername().split("@")[0];
        int dateTimeScore = 0;
        int normalizedSrcMachineScore = 0;
        int countryScore = 0;
        switch (reason) {
            case TIME: dateTimeScore = score; break;
            case SOURCE: normalizedSrcMachineScore = score; break;
            case COUNTRY: countryScore = score; break;
        }
        int eventScore = score;
        long timestamp = new Date().getTime();
        StringBuilder sb = new StringBuilder().append(hdfsTimestampFormat.print(dt)).append(SEPARATOR)
                .append(dt.getMillis() / 1000).append(SEPARATOR)
                .append(dateTimeScore).append(SEPARATOR)
                .append(username).append(SEPARATOR)
                .append(user.getUsername()).append(SEPARATOR)
                .append(user.getAdministratorAccount()).append(SEPARATOR)
                .append(user.getExecutiveAccount()).append(SEPARATOR)
                .append(status).append(SEPARATOR)
                .append(sourceIp).append(SEPARATOR)
                .append(srcMachine.getName().toUpperCase()).append(SEPARATOR)
                .append(normalizedSrcMachineScore).append(SEPARATOR)
                .append(localIp).append(SEPARATOR)
                .append(country).append(SEPARATOR)
                .append(countryScore).append(SEPARATOR)
                .append(countryCode).append(SEPARATOR)
                .append(region).append(SEPARATOR)
                .append(city).append(SEPARATOR)
                .append(isp).append(SEPARATOR)
                .append(ipUsage).append(SEPARATOR)
                .append(user.getTags().contains(UserTagEnum.LR.getId())).append(SEPARATOR)
                .append(eventScore).append(SEPARATOR).append(timestamp).append(SEPARATOR)
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
        JSONObject event = aggrFeatureEventBuilderService.buildEvent(dataSource, featureType, aggregatedFeatureName, count + 0.0, additionalInfoMap, bucketConfName, context, startTime, endTime, dataSources, new Date().getTime());
        event.put(AggrEvent.EVENT_FIELD_SCORE, 0.0);
        AggrEvent aggrEvent = aggrFeatureEventBuilderService.buildEvent(event);
        aggregatedEventQueryMongoService.insertAggregatedEvent(collectionName, aggrEvent);
    }

    /**
     *
     * This method is a helper method for the event generators
     *
     * @param dt
     * @param medianHour
     * @param bucketMap
     * @param dataSource
     * @param user
     * @param computer
     * @param computerDomain
     * @param dc
     * @param clientAddress
     * @param score
     * @throws HdfsException
     */
    private void eventGeneratorAux(DateTime dt, int medianHour, Map<DateTime, Integer> bucketMap, DataSource dataSource,
            User user, Computer computer, String[] dstMachines, String computerDomain, String dc, String clientAddress,
            int score) throws HdfsException, IOException {
        Random random = new Random();
        HDFSProperties hdfsProperties = dataSourceToHDFSProperties.get(dataSource);
        HdfsService service = new HdfsService(hdfsProperties.getHdfsPartition(), hdfsProperties.getFileName(),
                partitionStrategy, splitStrategy, hdfsProperties.getImpalaTable(), 1, 0, SEPARATOR);
        DateTime dateTime = generateRandomTimeForDay(dt, standardDeviation, medianHour, maxHourOfWork, minHourOfWork);
        String lineToWrite = null;
        addToBucketMap(dateTime, bucketMap);
        String dstMachine = dstMachines[random.nextInt(dstMachines.length)];
        switch (dataSource) {
            case kerberos_logins: lineToWrite = buildKerberosHDFSLine(dateTime, user, computer, dstMachine, score,
                    EventFailReason.TIME, computerDomain, dc, clientAddress, "0x0"); break;
            case ssh: {
                lineToWrite = buildSshHDFSLine(dateTime, user, computer, dstMachine, score, EventFailReason.TIME,
                        clientAddress, "Accepted");
                break;
            }
        }
        service.writeLineToHdfs(lineToWrite, dateTime.getMillis());
    }

    /**
     *
     * This method is a helper method for bucket creation
     *
     * @param bucketMap
     * @param user
     * @param dataSource
     * @param featureName
     * @param dt
     * @param anomalyDate
     * @param aggrFeatureName
     */
    private void bucketCreationAux(Map<DateTime, Integer> bucketMap, User user, DataSource dataSource,
            String featureName, DateTime dt, DateTime anomalyDate, String aggrFeatureName) {
        //create hourly buckets
        GenericHistogram dailyHistogram = new GenericHistogram();
        for (Map.Entry<DateTime, Integer> bucket: bucketMap.entrySet()) {
            GenericHistogram genericHistogram = new GenericHistogram();
            genericHistogram.add(bucket.getKey().getHourOfDay(), bucket.getValue() + 0.0);
            dailyHistogram.add(bucket.getKey().getHourOfDay(), bucket.getValue() + 0.0);
            createBucket(user.getUsername(), dataSource.name(), EvidenceTimeframe.Hourly.name().toLowerCase(),
                    bucket.getKey(), bucket.getKey().plusHours(1).minusMillis(1), genericHistogram, featureName);
            //TODO - check this logic
            if (!dt.equals(anomalyDate)) {
                createScoredBucket(user.getUsername(), aggrFeatureName + "_" + EvidenceTimeframe.Hourly.name().
                                toLowerCase(), dataSource.name(), EvidenceTimeframe.Hourly.name().toLowerCase(),
                        bucket.getKey(), bucket.getKey().plusDays(1).minusMillis(1), 0);
            }

        }
        //create daily bucket
        createBucket(user.getUsername(), dataSource.name(), EvidenceTimeframe.Daily.name().toLowerCase(), dt, dt.
                plusDays(1).minusMillis(1), dailyHistogram, featureName);
        if (!dt.equals(anomalyDate)) {
            createScoredBucket(user.getUsername(), aggrFeatureName + "_" + EvidenceTimeframe.Daily.name().
                    toLowerCase(), dataSource.name(), EvidenceTimeframe.Daily.name().toLowerCase(), dt, dt.
                    plusDays(1).minusMillis(1), 0);
        }
    }

    /**
     *
     * This method is a helper method for creating indicators
     *
     * @param evidenceType
     * @param reason
     * @param indicators
     * @param user
     * @param randomDate
     * @param dataSource
     * @param indicatorScore
     * @param anomalyTypeFieldName
     * @param timeframe
     * @param numberOfAnomalies
     * @param anomalyDate
     */
    private void indicatorCreationAux(EvidenceType evidenceType, EventFailReason reason, List<Evidence> indicators,
            User user, DateTime randomDate, DataSource dataSource, int indicatorScore, String anomalyTypeFieldName,
            EvidenceTimeframe timeframe, int numberOfAnomalies, DateTime anomalyDate, String dstMachine,
            String srcMachine) {
        if (evidenceType == EvidenceType.AnomalySingleEvent) {
            switch (reason) {
                case TIME: {
                    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.0");
                    indicators.add(createIndicator(user.getUsername(), evidenceType,
                            randomDate.toDate(), randomDate.toDate(), dataSource.name(), indicatorScore + 0.0,
                            anomalyTypeFieldName, dateTimeFormatter.print(randomDate), 1, timeframe));
                    break;
                }
                case DEST: indicators.add(createIndicator(user.getUsername(), evidenceType,
                        randomDate.toDate(), randomDate.toDate(), dataSource.name(), indicatorScore + 0.0,
                        anomalyTypeFieldName, dstMachine, 1, timeframe)); break;
                case SOURCE: indicators.add(createIndicator(user.getUsername(), evidenceType,
                        randomDate.toDate(), randomDate.toDate(), dataSource.name(), indicatorScore + 0.0,
                        anomalyTypeFieldName, srcMachine, 1, timeframe)); break;
                case FAILURE: indicators.add(createIndicator(user.getUsername(), evidenceType,
                        randomDate.toDate(), randomDate.toDate(), dataSource.name(), indicatorScore + 0.0,
                        anomalyTypeFieldName, ((double)numberOfAnomalies) + "", 1, timeframe)); break;
            }
        } else {
            DateTime endDate;
            if (timeframe == EvidenceTimeframe.Hourly) {
                randomDate = randomDate.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
                endDate = randomDate.plusHours(1);
            } else {
                randomDate = anomalyDate;
                endDate = randomDate.plusDays(1);
            }
            indicators.add(createIndicator(user.getUsername(), evidenceType, randomDate.toDate(), endDate.minusMillis(1).toDate(), dataSource.name(), indicatorScore + 0.0, anomalyTypeFieldName + "_" + timeframe.name().toLowerCase(), ((double) numberOfAnomalies) + "", numberOfAnomalies, timeframe));
        }
    }

    /**
     *
     * This method generates a number of random destination machines
     *
     * @param minNumberOfDestMachines
     * @param maxNumberOfDestMachines
     * @param machines
     * @return
     */
    private Set<String> generateRandomDestinationMachines(List<Computer> machines, int minNumberOfDestMachines,
            int maxNumberOfDestMachines) {
        Random random = new Random();
        Set<String> result = new HashSet();
        maxNumberOfDestMachines = Math.min(machines.size(), maxNumberOfDestMachines);
        minNumberOfDestMachines = Math.min(machines.size(), minNumberOfDestMachines);
        int numberOfDestinationMachines;
        if (maxNumberOfDestMachines == minNumberOfDestMachines) {
            numberOfDestinationMachines = maxNumberOfDestMachines;
        } else {
            numberOfDestinationMachines = random.nextInt(maxNumberOfDestMachines - minNumberOfDestMachines) +
                    minNumberOfDestMachines;
        }
        while (result.size() < numberOfDestinationMachines) {
            int index = random.nextInt(machines.size());
            result.add(machines.get(index).getName());
        }
        return result;
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