package fortscale.collection.jobs.demo;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.core.*;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.services.AlertsService;
import fortscale.services.EvidencesService;
import fortscale.services.UserService;
import fortscale.services.impl.HdfsService;
import fortscale.services.impl.SpringService;
import fortscale.utils.cloudera.ClouderaUtils;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.hdfs.split.FileSplitStrategy;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcOperations;

import java.util.*;

/**
 * Created by Amir Keren on 14/12/2015.
 *
 * This job generates demo scenarios
 *
 */
public class ScenarioGeneratorJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(ScenarioGeneratorJob.class);

    @Autowired
    private ClouderaUtils clouderaUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private ComputerRepository computerRepository;
    @Autowired
    private JdbcOperations impalaJdbcTemplate;
    @Autowired
    private AlertsService alertsService;
    @Autowired
    private EvidencesService evidencesService;

    @Value("${streaming.service.name}")
    private String streamingService;

    private DemoUtils demoUtils;
    private FileSplitStrategy splitStrategy;
    private PartitionStrategy partitionStrategy;
    private Map<DemoUtils.DataSource, DataSourceProperties> dataSourceToHDFSProperties;
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
    private DateTime anomalyDate;
    private String domain;
    private String computerDomain;
    private String dc;

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
        SpringService.init(DemoUtils.CONTEXT);
        try {
            splitStrategy = (FileSplitStrategy)Class.forName(DemoUtils.SPLIT_STRATEGY).newInstance();
        } catch (Exception ex) {
            logger.error("failed to find split strategy");
            throw new JobExecutionException(ex);
        }
        demoUtils = new DemoUtils();
        partitionStrategy = PartitionsUtils.getPartitionStrategy("daily");
        dataSourceToHDFSProperties = buildDataSourceToHDFSPropertiesMap(map);
        domain = jobDataMapExtension.getJobDataMapStringValue(map, "domain");
        dc = jobDataMapExtension.getJobDataMapStringValue(map, "computerDomain");
        computerDomain = jobDataMapExtension.getJobDataMapStringValue(map, "domain");
        numOfDaysBack = jobDataMapExtension.getJobDataMapIntValue(map, "numOfDaysBack");
        maxHourOfWork = jobDataMapExtension.getJobDataMapIntValue(map, "maxHourOfWork");
        minHourOfWork = jobDataMapExtension.getJobDataMapIntValue(map, "minHourOfWork");
        limitNumberOfDestinationMachines = jobDataMapExtension.getJobDataMapIntValue(map,
                "limitNumberOfDestinationMachines");
        standardDeviation = jobDataMapExtension.getJobDataMapIntValue(map, "standardDeviation");
        morningMedianHour = jobDataMapExtension.getJobDataMapIntValue(map, "morningMedianHour");
        afternoonMedianHour = jobDataMapExtension.getJobDataMapIntValue(map, "afternoonMedianHour");
        skipWeekend = jobDataMapExtension.getJobDataMapBooleanValue(map, "skipWeekend", true);
        anomalyDate = new DateTime().withZone(DateTimeZone.UTC)
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        if (skipWeekend && anomalyDate.getDayOfWeek() == DateTimeConstants.SATURDAY) {
            anomalyDate = anomalyDate.minusDays(1);
        } else if (skipWeekend && anomalyDate.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            anomalyDate = anomalyDate.minusDays(2);
        }
        logger.info("Job initialized");
	}

    /**http://127.0.0.1:7180/cmf/services/11/instances/55/status
     *
     * This method builds the map required to save data to HDFS
     *
     * @param map
     * @return
     * @throws JobExecutionException
     */
    private Map<DemoUtils.DataSource, DataSourceProperties> buildDataSourceToHDFSPropertiesMap(JobDataMap map)
            throws JobExecutionException {
        Map<DemoUtils.DataSource, DataSourceProperties> result = new HashMap();
        for (String dataSourceStr: jobDataMapExtension.getJobDataMapStringValue(map, "dataEntities").split(",")) {
            DemoUtils.DataSource dataSource = DemoUtils.DataSource.valueOf(dataSourceStr);
            String impalaTable = jobDataMapExtension.getJobDataMapStringValue(map, "impalaTableName-" + dataSource);
            String hdfsPartition = jobDataMapExtension.getJobDataMapStringValue(map, "hdfsPartition-" + dataSource);
            String fileName = jobDataMapExtension.getJobDataMapStringValue(map, "fileName-" + dataSource);
            String fields = jobDataMapExtension.getJobDataMapStringValue(map, "impalaTableFields-" + dataSource);
            result.put(dataSource, new DataSourceProperties(impalaTable, fileName, hdfsPartition, fields, dataSource));
        }
        return result;
    }

    /**
     *
     * This method generates scenario1 as described here:
     * https://fortscale.atlassian.net/browse/FV-9286
     *
     * @return
     * @throws Exception
     *
     */
    private List<JSONObject> generateScenario1() throws Exception {

        //TODO - extract these general scenario fields
        String title = "Suspicious Daily User Activity";
        int alertScore = 90;
        int indicatorsScore = 90;
        Severity alertSeverity = Severity.Critical;
        String samaccountname = "alrusr51";
        int eventsScore = 98;
        int minHourForAnomaly = 3;
        int maxHourForAnomaly = 5;
        int minNumberOfDestMachines = 2;
        int maxNumberOfDestMachines = 3;
        numberOfMinEventsPerTimePeriod = 2;
        numberOfMaxEventsPerTimePeriod = 5;
        //TODO - extract these specific indicator fields
        int minNumberOfAnomaliesIndicator1 = 2;
        int maxNumberOfAnomaliesIndicator1 = 3;
        int minNumberOfAnomaliesIndicator2 = 5;
        int maxNumberOfAnomaliesIndicator2 = 6;
        int minNumberOfAnomaliesIndicator3 = 8;
        int maxNumberOfAnomaliesIndicator3 = 10;
        int minNumberOfAnomaliesIndicator4 = 1;
        int maxNumberOfAnomaliesIndicator4 = 1;

        int anomalousHour = demoUtils.generateRandomTimeForAnomaly(anomalyDate, minHourForAnomaly, maxHourForAnomaly).
                getHourOfDay();
        String clientAddress = demoUtils.generateRandomIPAddress();
        String username = samaccountname + "@" + domain;
        String srcMachine = samaccountname + DemoUtils.COMPUTER_SUFFIX;
        String dstMachine = samaccountname + DemoUtils.SERVER_SUFFIX;
        Computer computer = computerRepository.findByName(srcMachine.toUpperCase());
        if (computer == null) {
            logger.error("computer {} not found - exiting", srcMachine.toUpperCase());
            throw new JobExecutionException();
        }
        User user = userService.findByUsername(username);
        if (user == null) {
            logger.error("user {} not found - exiting", username);
            throw new JobExecutionException();
        }
        List<Computer> machines = computerRepository.getComputersOfType(ComputerUsageType.Server,
                limitNumberOfDestinationMachines);
        if (machines.isEmpty()) {
            logger.error("no server machines found");
            throw new JobExecutionException();
        }
        String service = "sausr29fs";
        Computer serviceMachine = new Computer();
        serviceMachine.setName(service.toUpperCase() + DemoUtils.COMPUTER_SUFFIX);
        String anomalousMachine = service.toUpperCase() + DemoUtils.SERVER_SUFFIX;
        User serviceAccount = new User();
        serviceAccount.setUsername(service + "@" + domain);
        serviceAccount.setUserServiceAccount(true);
        Set<String> baseLineMachinesSet = demoUtils.generateRandomDestinationMachines(machines, minNumberOfDestMachines,
                maxNumberOfDestMachines);
        String[] baseLineMachines = baseLineMachinesSet.toArray(new String[baseLineMachinesSet.size()]);
        Set<String> anomalousMachinesSet = demoUtils.generateRandomDestinationMachines(machines,
                minNumberOfAnomaliesIndicator3, maxNumberOfAnomaliesIndicator3);
        String[] anomalousMachines = anomalousMachinesSet.toArray(new String[anomalousMachinesSet.size()]);
        List<JSONObject> records = new ArrayList();

        //TODO - extract these to json
        //create baseline
        DemoEvent demoEvent = new DemoKerberosEvent(user, 0, DemoUtils.EventFailReason.NONE, computer, dstMachine,
                domain, dc, clientAddress, "0x0");
        records.addAll(createEvents(demoEvent, DemoUtils.DataSource.kerberos_logins));
        demoEvent = new DemoSSHEvent(user, 0, DemoUtils.EventFailReason.NONE, computer, baseLineMachines, clientAddress,
                "Accepted", "password");
        records.addAll(createEvents(demoEvent, DemoUtils.DataSource.ssh));
        demoEvent = new DemoSSHEvent(serviceAccount, 0, DemoUtils.EventFailReason.NONE, serviceMachine,
                anomalousMachine, clientAddress, "Accepted", "password");
        records.addAll(createEvents(demoEvent, DemoUtils.DataSource.ssh));

        List<Evidence> indicators = new ArrayList();

        //create anomalies
        demoEvent = new DemoKerberosEvent(user, eventsScore, DemoUtils.EventFailReason.TIME, computer, dstMachine,
                domain, dc, clientAddress, "0x0");
        records.addAll(createAnomalies(DemoUtils.DataSource.kerberos_logins, demoEvent, minNumberOfAnomaliesIndicator1,
                maxNumberOfAnomaliesIndicator1, minHourForAnomaly, maxHourForAnomaly, null,
                EvidenceType.AnomalySingleEvent, indicatorsScore, "event_time", indicators));
        demoEvent = new DemoKerberosEvent(user, eventsScore, DemoUtils.EventFailReason.NONE, computer, dstMachine,
                domain, dc, clientAddress, "0x12");
        records.addAll(createAnomalies(DemoUtils.DataSource.kerberos_logins, demoEvent, minNumberOfAnomaliesIndicator2,
                maxNumberOfAnomaliesIndicator2, minHourForAnomaly, maxHourForAnomaly, EvidenceTimeframe.Daily,
                EvidenceType.AnomalyAggregatedEvent, indicatorsScore, "number_of_failed_" +
                        DemoUtils.DataSource.kerberos_logins, indicators));
        demoEvent = new DemoSSHEvent(user, eventsScore, DemoUtils.EventFailReason.NONE, computer, anomalousMachines,
                clientAddress, "Accepted", "password");
        records.addAll(createAnomalies(DemoUtils.DataSource.ssh, demoEvent, minNumberOfAnomaliesIndicator3,
                maxNumberOfAnomaliesIndicator3, anomalousHour, anomalousHour, EvidenceTimeframe.Hourly,
                EvidenceType.AnomalyAggregatedEvent, indicatorsScore, "distinct_number_of_dst_machines_" +
                        DemoUtils.DataSource.ssh, indicators));
        demoEvent = new DemoSSHEvent(user, eventsScore, DemoUtils.EventFailReason.NONE, computer, anomalousMachine,
                clientAddress, "Accepted", "password");
        records.addAll(createAnomalies(DemoUtils.DataSource.ssh, demoEvent, minNumberOfAnomaliesIndicator4,
                maxNumberOfAnomaliesIndicator4, minHourForAnomaly, maxHourForAnomaly, null,
                EvidenceType.AnomalySingleEvent, indicatorsScore, "destination_machine", indicators));

        //create alert
        demoUtils.createAlert(title, anomalyDate.getMillis(), anomalyDate.plusDays(1).minusMillis(1).getMillis(), user,
                indicators, alertScore, alertSeverity, alertsService);

        return records;
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
        List<String> tasks = new ArrayList();
        tasks.add(DemoUtils.ALERT_GENERATOR_TASK);
        if (clouderaUtils.validateServiceRoles(streamingService, tasks, false, false)) {
            clouderaUtils.startStopTask(streamingService, tasks, true, false);
        }
        List<JSONObject> records = new ArrayList();
        records.addAll(generateScenario1());
        //forward events to create buckets
        KafkaEventsWriter streamWriter = new KafkaEventsWriter(DemoUtils.AGGREGATION_TOPIC);
        Collections.sort(records, new JSONComparator());
        for (JSONObject record: records) {
            streamWriter.send(null, record.toJSONString(JSONStyle.NO_COMPRESS));
        }
        long endTime = anomalyDate.plusDays(1).plusSeconds(1).getMillis() / 1000;
        String dummyEvent = "{\"date_time_unix\":" + endTime + ",\"data_source\":\"dummy\"}";
        streamWriter.send(null, dummyEvent);
        streamWriter.close();
        logger.info("finished generating scenarios, going to sleep for bucket creation");
        Thread.sleep(DemoUtils.SLEEP_TIME);
        logger.info("finished waiting for buckets creation, finished generating demo scenarios");
        finishStep();
	}

    /**
     *
     * This method generates events
     *
     * @param configuration
     * @param dataSource
     * @return
     * @throws Exception
     */
    private List<JSONObject> createEvents(DemoEvent configuration, DemoUtils.DataSource dataSource)
            throws Exception {
        Random random = new Random();
        DateTime dt = anomalyDate.minusDays(numOfDaysBack);
        List<DemoEventAux> lines = new ArrayList();
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
            for (int j = 0; j < numberOfMorningEvents; j++) {
                lines.add(demoUtils.baseLineGeneratorAux(dt, configuration, morningMedianHour, dataSource,
                        standardDeviation, maxHourOfWork, minHourOfWork));
            }
            for (int j = 0; j < numberOfAfternoonEvents; j++) {
                lines.add(demoUtils.baseLineGeneratorAux(dt, configuration, morningMedianHour, dataSource,
                        standardDeviation, maxHourOfWork, minHourOfWork));
            }
            dt = dt.plusDays(1);
        }
        List<HdfsService> hdfsServices = new ArrayList();
        DataSourceProperties dataSourceProperties = dataSourceToHDFSProperties.get(dataSource);
        hdfsServices.add(new HdfsService(dataSourceProperties.getHdfsPartition(),
                dataSourceProperties.getFileName(), partitionStrategy, splitStrategy,
                dataSourceProperties.getImpalaTable(), lines.size(), 0, DemoUtils.SEPARATOR));
        List<JSONObject> records = demoUtils.saveEvents(configuration.getUser(), dataSourceProperties, lines,
                hdfsServices, impalaJdbcTemplate);
        return records;
    }

    /**
     *
     * This method generates a random number of time login anomalies
     *
     * @param dataSource
     * @param minNumberOfAnomalies
     * @param maxNumberOfAnomalies
     * @param minHourForAnomaly
     * @param maxHourForAnomaly
     * @param timeframe
     * @param evidenceType
     * @param indicatorScore
     * @param anomalyTypeFieldName
     * @param indicators
     * @return
     * @throws Exception
     */
    private List<JSONObject> createAnomalies(DemoUtils.DataSource dataSource, DemoEvent configuration,
            int minNumberOfAnomalies, int maxNumberOfAnomalies, int minHourForAnomaly, int maxHourForAnomaly,
            EvidenceTimeframe timeframe, EvidenceType evidenceType, int indicatorScore, String anomalyTypeFieldName,
            List<Evidence> indicators)
            throws Exception {
        DataSourceProperties dataSourceProperties = dataSourceToHDFSProperties.get(dataSource);
        User user = configuration.getUser();
        Random random = new Random();
        int numberOfAnomalies;
        if (maxNumberOfAnomalies == minNumberOfAnomalies) {
            numberOfAnomalies = maxNumberOfAnomalies;
        } else {
            numberOfAnomalies = random.nextInt(maxNumberOfAnomalies - minNumberOfAnomalies) + minNumberOfAnomalies;
        }
        List<DemoEventAux> lines = new ArrayList();
        for (int i = 0; i < numberOfAnomalies; i++) {
            DateTime randomDate = demoUtils.generateRandomTimeForAnomaly(anomalyDate, minHourForAnomaly,
                    maxHourForAnomaly);
            String lineToWrite = demoUtils.generateEvent(configuration, dataSource, randomDate);
            lines.add(new DemoEventAux(lineToWrite, randomDate));
            //create just one indicator
            if (i == 0) {
                demoUtils.indicatorCreationAux(evidenceType, configuration, indicators, randomDate, dataSource,
                        indicatorScore, anomalyTypeFieldName, numberOfAnomalies, anomalyDate, timeframe,
                        evidencesService);
            }
        }
        List<HdfsService> hdfsServices = new ArrayList();
        hdfsServices.add(new HdfsService(dataSourceProperties.getHdfsPartition(),
                dataSourceProperties.getFileName(), partitionStrategy, splitStrategy,
                dataSourceProperties.getImpalaTable(), lines.size(), 0, DemoUtils.SEPARATOR));
        hdfsServices.add(new HdfsService(dataSourceProperties.getHdfsPartition() + "_top",
                dataSourceProperties.getFileName(), partitionStrategy, splitStrategy,
                dataSourceProperties.getImpalaTable() + "_top", lines.size(), 0, DemoUtils.SEPARATOR));
        List<JSONObject> records = demoUtils.saveEvents(user, dataSourceProperties, lines,
                hdfsServices, impalaJdbcTemplate);
        return records;
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

}