package fortscale.collection.jobs.demo;

import com.google.common.collect.Lists;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.core.*;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.services.AlertsService;
import fortscale.services.EvidencesService;
import fortscale.services.UserService;
import fortscale.services.exceptions.HdfsException;
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
        numberOfMinEventsPerTimePeriod = 2;
        numberOfMaxEventsPerTimePeriod = 5;
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
    /*private List<JSONObject> generateScenario1() throws Exception {
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
        String username = samaccountname + "@" + DemoUtils.DOMAIN;
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
        serviceAccount.setUsername(service + "@" + DemoUtils.DOMAIN);
        serviceAccount.setUserServiceAccount(true);
        Set<String> baseLineMachinesSet = demoUtils.generateRandomDestinationMachines(machines, minNumberOfDestMachines,
                maxNumberOfDestMachines);
        String[] baseLineMachines = baseLineMachinesSet.toArray(new String[baseLineMachinesSet.size()]);
        Set<String> anomalousMachinesSet = demoUtils.generateRandomDestinationMachines(machines,
                minNumberOfAnomaliesIndicator3, maxNumberOfAnomaliesIndicator3);
        String[] anomalousMachines = anomalousMachinesSet.toArray(new String[anomalousMachinesSet.size()]);
        List<JSONObject> records = new ArrayList();

        //create baseline
        DemoGenericEvent demoEventGeneric = new DemoKerberosEvent(user, DemoUtils.DEFAULT_SCORE,
                DemoUtils.EventFailReason.NONE, computer, dstMachine, clientAddress, DemoUtils.CODE_SUCCESS);
        records.addAll(createBaseline(demoEventGeneric, DemoUtils.DataSource.kerberos_logins));
        demoEventGeneric = new DemoSSHEvent(user, DemoUtils.DEFAULT_SCORE, DemoUtils.EventFailReason.NONE, computer,
                baseLineMachines, clientAddress, DemoUtils.SSH_SUCCESS, DemoUtils.SSH_DEFAULT_AUTH_METHOD);
        records.addAll(createBaseline(demoEventGeneric, DemoUtils.DataSource.ssh));
        demoEventGeneric = new DemoSSHEvent(serviceAccount, DemoUtils.DEFAULT_SCORE, DemoUtils.EventFailReason.NONE,
                serviceMachine, anomalousMachine, clientAddress, DemoUtils.SSH_SUCCESS,
                DemoUtils.SSH_DEFAULT_AUTH_METHOD);
        records.addAll(createBaseline(demoEventGeneric, DemoUtils.DataSource.ssh));

        //create anomalies
        List<Evidence> indicators = new ArrayList();
        demoEventGeneric = new DemoKerberosEvent(user, eventsScore, DemoUtils.EventFailReason.TIME, computer,
                dstMachine, clientAddress, DemoUtils.CODE_SUCCESS);
        records.addAll(createAnomalies(DemoUtils.DataSource.kerberos_logins, demoEventGeneric,
                minNumberOfAnomaliesIndicator1, maxNumberOfAnomaliesIndicator1, minHourForAnomaly, maxHourForAnomaly,
                null, EvidenceType.AnomalySingleEvent, indicatorsScore, DemoUtils.AnomalyType.TIME.text, indicators));
        demoEventGeneric = new DemoKerberosEvent(user, eventsScore, DemoUtils.EventFailReason.NONE, computer,
                dstMachine, clientAddress, DemoUtils.CODE_FAILURE);
        records.addAll(createAnomalies(DemoUtils.DataSource.kerberos_logins, demoEventGeneric,
                minNumberOfAnomaliesIndicator2, maxNumberOfAnomaliesIndicator2, minHourForAnomaly, maxHourForAnomaly,
                EvidenceTimeframe.Daily, EvidenceType.AnomalyAggregatedEvent, indicatorsScore,
                DemoUtils.NUMBER_OF_FAILED_PREFIX + DemoUtils.DataSource.kerberos_logins, indicators));
        demoEventGeneric = new DemoSSHEvent(user, eventsScore, DemoUtils.EventFailReason.NONE, computer,
                anomalousMachines, clientAddress, DemoUtils.SSH_SUCCESS, DemoUtils.SSH_DEFAULT_AUTH_METHOD);
        records.addAll(createAnomalies(DemoUtils.DataSource.ssh, demoEventGeneric, minNumberOfAnomaliesIndicator3,
                maxNumberOfAnomaliesIndicator3, anomalousHour, anomalousHour, EvidenceTimeframe.Hourly,
                EvidenceType.AnomalyAggregatedEvent, indicatorsScore, DemoUtils.DISTINCT_NUMBER_OF_DST_PREFIX +
                        DemoUtils.DataSource.ssh, indicators));
        demoEventGeneric = new DemoSSHEvent(user, eventsScore, DemoUtils.EventFailReason.DEST, computer,
                anomalousMachine, clientAddress, DemoUtils.SSH_SUCCESS, DemoUtils.SSH_DEFAULT_AUTH_METHOD);
        records.addAll(createAnomalies(DemoUtils.DataSource.ssh, demoEventGeneric, minNumberOfAnomaliesIndicator4,
                maxNumberOfAnomaliesIndicator4, minHourForAnomaly, maxHourForAnomaly, null,
                EvidenceType.AnomalySingleEvent, indicatorsScore, DemoUtils.AnomalyType.DEST.text, indicators));

        //create alert
        demoUtils.createAlert(title, anomalyDate.getMillis(), anomalyDate.plusDays(1).minusMillis(1).getMillis(), user,
                indicators, alertScore, alertSeverity, alertsService);

        return records;
    }*/

    /**
     *
     * This method generates scenario2 as described here:
     * https://fortscale.atlassian.net/browse/FV-9288
     *
     * @throws Exception
     */
    /*private List<JSONObject> generateScenario2() throws Exception {
        String samaccountname = "adminusr26fs";
        int indicatorsScore = 98;
        String title = "Suspicious Hourly Privileged Account Activity";
        int alertScore = 90;
        Severity alertSeverity = Severity.Critical;
        int eventsScore = 98;
        int minHourForAnomaly = 9;
        int maxHourForAnomaly = 5;
        int minNumberOfDestMachines = 10;
        int maxNumberOfDestMachines = 30;
        numberOfMinEventsPerTimePeriod = 10;
        numberOfMaxEventsPerTimePeriod = 30;
        int numberOfAnomaliesIndicator1 = 60;
        String clientAddress = demoUtils.generateRandomIPAddress();
        String username = samaccountname + "@" + DemoUtils.DOMAIN;
        String srcMachine = samaccountname + DemoUtils.COMPUTER_SUFFIX;
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
        List<Computer> machines = computerRepository.getComputersOfType(ComputerUsageType.Desktop,
                limitNumberOfDestinationMachines);
        if (machines.isEmpty()) {
            logger.error("no desktop machines found");
            throw new JobExecutionException();
        }
        Set<String> baseLineMachinesSet = demoUtils.generateRandomDestinationMachines(machines, minNumberOfDestMachines,
                maxNumberOfDestMachines);
        String[] baseLineMachines = baseLineMachinesSet.toArray(new String[baseLineMachinesSet.size()]);
        Set<String> anomalousMachinesSet = demoUtils.generateRandomDestinationMachines(machines,
                numberOfAnomaliesIndicator1, numberOfAnomaliesIndicator1);
        String[] anomalousMachines = anomalousMachinesSet.toArray(new String[anomalousMachinesSet.size()]);
        List<JSONObject> records = new ArrayList();

        //create baseline
        DemoGenericEvent demoEventGeneric = new DemoKerberosEvent(user, DemoUtils.DEFAULT_SCORE,
                DemoUtils.EventFailReason.NONE, computer, baseLineMachines, clientAddress, DemoUtils.CODE_SUCCESS);
        records.addAll(createBaseline(demoEventGeneric, DemoUtils.DataSource.kerberos_logins));

        //create anomalies
        List<Evidence> indicators = new ArrayList();
        demoEventGeneric = new DemoKerberosEvent(user, eventsScore, DemoUtils.EventFailReason.NONE, computer,
                anomalousMachines, clientAddress, DemoUtils.CODE_SUCCESS);
        records.addAll(createAnomalies(DemoUtils.DataSource.kerberos_logins, demoEventGeneric,
                numberOfAnomaliesIndicator1, numberOfAnomaliesIndicator1, minHourForAnomaly, maxHourForAnomaly,
                EvidenceTimeframe.Daily, EvidenceType.AnomalyAggregatedEvent, indicatorsScore,
                DemoUtils.AnomalyType.TIME.text, indicators));

        //create alert
        demoUtils.createAlert(title, anomalyDate.getMillis(), anomalyDate.plusDays(1).minusMillis(1).getMillis(), user,
                indicators, alertScore, alertSeverity, alertsService);

        return records;

    }*/

    /**
     *
     * This method generates scenario4 as described here:
     * https://fortscale.atlassian.net/browse/FV-10278
     *
     * @return
     * @throws Exception
     *
     */
    private List<JSONObject> generateScenario4() throws Exception {
        String title = "Suspicious Daily User Activity";
        int alertScore = 90;
        int indicatorsScore = 90;
        Severity alertSeverity = Severity.Critical;
        String samaccountname = "adminusr26fs";
        String targetUsername = "execgr1usr1fs";
        int eventsScore = 98;
        int numberOfWAMEEvents = 1;
        int numberOfNTLMEvents = 5;
        int minHourForAnomaly = 9;
        int maxHourForAnomaly = 12;
        String username = samaccountname + "@" + DemoUtils.DOMAIN;
        String srcMachine = samaccountname + DemoUtils.COMPUTER_SUFFIX;
        String service = "sausr29fs";
        Computer anomalousMachine = new Computer();
        anomalousMachine.setName(service.toUpperCase() + DemoUtils.COMPUTER_SUFFIX);
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
        List<JSONObject> records = new ArrayList();

        //create baseline
        DemoGenericEvent baseLineConfiguration = DemoNTLMEvent.createBaseLineConfiguration(user, computer);
        records.addAll(createBaseline(baseLineConfiguration, DemoUtils.DataSource.ntlm));

        //create anomalies - NTLM
        List<Evidence> indicators = new ArrayList();
        DemoGenericEvent anomalyConfiguration = DemoNTLMEvent.createAnomalyConfiguration(user, computer, eventsScore,
                DemoUtils.EventFailReason.FAILURE, "0x12");
        records.addAll(createAnomalies(DemoUtils.DataSource.ntlm, anomalyConfiguration, numberOfNTLMEvents,
                numberOfNTLMEvents, minHourForAnomaly, maxHourForAnomaly, null, EvidenceType.AnomalySingleEvent,
                indicatorsScore, DemoUtils.AnomalyType.FAILURE_CODE.text, indicators));
        DateTime dt = new DateTime(indicators.get(0).getStartDate());
        anomalyConfiguration = DemoNTLMEvent.createAnomalyConfiguration(user, anomalousMachine, eventsScore,
                DemoUtils.EventFailReason.SOURCE, "0x12");
        demoUtils.indicatorCreationAux(EvidenceType.AnomalySingleEvent, anomalyConfiguration, indicators, dt,
                DemoUtils.DataSource.ntlm, indicatorsScore, DemoUtils.AnomalyType.SOURCE.text, 1, anomalyDate, null,
                evidencesService);
        demoUtils.indicatorCreationAux(EvidenceType.AnomalyAggregatedEvent, anomalyConfiguration, indicators, null,
                DemoUtils.DataSource.ntlm, indicatorsScore, DemoUtils.NUMBER_OF_FAILED_PREFIX +
                        DemoUtils.DataSource.ntlm, numberOfNTLMEvents, anomalyDate,
                EvidenceTimeframe.Daily, evidencesService);
        maxHourForAnomaly++;
        records.addAll(createSingleEvent(baseLineConfiguration, DemoUtils.DataSource.ntlm, anomalyDate,
                maxHourForAnomaly, maxHourForAnomaly));
        maxHourForAnomaly++;
        //create anomalies - WAME
        anomalyConfiguration = DemoWAMEEvent.createAnomalyConfiguration(user, eventsScore,
                DemoUtils.EventFailReason.ACTION_TYPE, DemoWAMEEvent.ActionType.RESET.text, DemoWAMEEvent.SUCCESS_CODE,
                targetUsername);
        records.addAll(createAnomalies(DemoUtils.DataSource.wame, anomalyConfiguration, numberOfWAMEEvents,
                numberOfWAMEEvents, maxHourForAnomaly, maxHourForAnomaly, null, EvidenceType.AnomalySingleEvent,
                indicatorsScore, DemoUtils.AnomalyType.ACTION_TYPE.text, indicators));
        maxHourForAnomaly++;
        anomalyConfiguration = DemoWAMEEvent.createAnomalyConfiguration(user, eventsScore,
                DemoUtils.EventFailReason.ACTION_TYPE, DemoWAMEEvent.ActionType.UNLOCKED.text,
                DemoWAMEEvent.SUCCESS_CODE, targetUsername);
        records.addAll(createAnomalies(DemoUtils.DataSource.wame, anomalyConfiguration, numberOfWAMEEvents,
                numberOfWAMEEvents, maxHourForAnomaly, maxHourForAnomaly, null, EvidenceType.AnomalySingleEvent,
                indicatorsScore, DemoUtils.AnomalyType.ACTION_TYPE.text, indicators));
        maxHourForAnomaly++;
        anomalyConfiguration = DemoWAMEEvent.createAnomalyConfiguration(user, eventsScore,
                DemoUtils.EventFailReason.ACTION_TYPE, DemoWAMEEvent.ActionType.ENABLED.text,
                DemoWAMEEvent.SUCCESS_CODE, targetUsername);
        records.addAll(createAnomalies(DemoUtils.DataSource.wame, anomalyConfiguration, numberOfWAMEEvents,
                numberOfWAMEEvents, maxHourForAnomaly, maxHourForAnomaly, null, EvidenceType.AnomalySingleEvent,
                indicatorsScore, DemoUtils.AnomalyType.ACTION_TYPE.text, indicators));
        demoUtils.indicatorCreationAux(EvidenceType.AnomalyAggregatedEvent, anomalyConfiguration, indicators, null,
                DemoUtils.DataSource.ntlm, indicatorsScore, DemoUtils.NUMBER_OF_EVENTS_PREFIX +
                        DemoUtils.DataSource.wame, 3, anomalyDate, EvidenceTimeframe.Daily,
                evidencesService);
        indicators = Lists.reverse(indicators);

        //create alert
        demoUtils.createAlert(title, anomalyDate.getMillis(), anomalyDate.plusDays(1).minusMillis(1).getMillis(), user,
                indicators, alertScore, alertSeverity, alertsService);

        return records;
    }

    /**
     *
     * This method generates scenario4 as described here:
     * https://fortscale.atlassian.net/browse/FV-10279
     *
     * @return
     * @throws Exception
     *
     */
    private List<JSONObject> generateScenario5() throws Exception {
        String title = "Suspicious Daily User Activity";
        int alertScore = 90;
        int indicatorsScore = 90;
        Severity alertSeverity = Severity.Critical;
        String samaccountname = "alrusr52";
        int eventsScore = 98;
        int numberOfOraclEvents = 4;
        int numberOfPrintingEvents = 1;
        int minHourForAnomaly = 11;
        int maxHourForAnomaly = 11;
        int normalMinPages = 3;
        int normalMaxPages = 5;
        int normalPrintSize = 200;
        int anomalyPrintSize = 15000;
        int anomalyPages = 5;
        String returnCode = "1";
        String tableName = "all_contacts";
        String username = samaccountname + "@" + DemoUtils.DOMAIN;
        String srcMachine = samaccountname + DemoUtils.COMPUTER_SUFFIX;
        String targetMachine = "SAUSR29FS_SRV";
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
        List<JSONObject> records = new ArrayList();

        //create baseline
        DemoGenericEvent baseLineConfiguration = DemoPrintLogEvent.createBaseLineConfiguration(user, computer,
                new String[] { targetMachine }, normalPrintSize, normalPrintSize, normalMinPages, normalMaxPages);
        records.addAll(createBaseline(baseLineConfiguration, DemoUtils.DataSource.prnlog));
        baseLineConfiguration = DemoSalesForceEvent.createBaseLineConfiguration(user);
        records.addAll(createBaseline(baseLineConfiguration, DemoUtils.DataSource.crmsf));

        //create anomalies
        List<Evidence> indicators = new ArrayList();
        DemoGenericEvent anomalyConfiguration = DemoOracleEvent.createAnomalyConfiguration(user, computer,
                new String[] { targetMachine }, tableName, samaccountname, DemoOracleEvent.DEFAULT_ACTION,
                eventsScore, returnCode, DemoUtils.EventFailReason.OBJECT);
        records.addAll(createAnomalies(DemoUtils.DataSource.oracle, anomalyConfiguration, numberOfOraclEvents,
                numberOfOraclEvents, minHourForAnomaly, maxHourForAnomaly, null, EvidenceType.AnomalySingleEvent,
                indicatorsScore, DemoUtils.AnomalyType.FAILURE_CODE.text, indicators));
        DateTime dt = new DateTime(indicators.get(0).getStartDate());
        demoUtils.indicatorCreationAux(EvidenceType.AnomalyAggregatedEvent, anomalyConfiguration, indicators, dt,
                DemoUtils.DataSource.oracle, indicatorsScore, DemoUtils.AnomalyType.SOURCE.text, numberOfOraclEvents,
                anomalyDate, EvidenceTimeframe.Hourly, evidencesService);
        anomalyConfiguration = DemoPrintLogEvent.createAnomalyConfiguration(user, computer,
                new String[] { targetMachine }, anomalyPrintSize, anomalyPrintSize, anomalyPages, anomalyPages,
                DemoUtils.EventFailReason.NONE, eventsScore);
        records.addAll(createAnomalies(DemoUtils.DataSource.prnlog, anomalyConfiguration, numberOfPrintingEvents,
                numberOfPrintingEvents, minHourForAnomaly, maxHourForAnomaly, EvidenceTimeframe.Daily,
                EvidenceType.AnomalyAggregatedEvent, indicatorsScore, DemoUtils.AnomalyType.FAILURE_CODE.text,
                indicators));
        demoUtils.indicatorCreationAux(EvidenceType.AnomalyAggregatedEvent, anomalyConfiguration, indicators, null,
                DemoUtils.DataSource.prnlog, indicatorsScore, DemoUtils.AnomalyType.SOURCE.text, 1, anomalyDate,
                EvidenceTimeframe.Daily, evidencesService);

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
        //TODO - add missing scenarios
        records.addAll(generateScenario4());
        records.addAll(generateScenario5());
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
    private List<JSONObject> createBaseline(DemoGenericEvent configuration, DemoUtils.DataSource dataSource)
            throws Exception {
        Random random = new Random();
        DateTime dt = anomalyDate.minusDays(numOfDaysBack);
        List<DemoEvent> lines = new ArrayList();
        while (dt.isBefore(anomalyDate.minusMillis(1))) {
            if (skipWeekend && dt.getDayOfWeek() == DateTimeConstants.SATURDAY) {
                dt = dt.plusDays(2);
            } else if (skipWeekend && dt.getDayOfWeek() == DateTimeConstants.SUNDAY) {
                dt = dt.plusDays(1);
            }
            if (numberOfMaxEventsPerTimePeriod == numberOfMinEventsPerTimePeriod) {
                lines.add(demoUtils.baseLineGeneratorAux(dt, configuration, morningMedianHour, dataSource,
                        standardDeviation, maxHourOfWork, minHourOfWork));
            } else {
                int numberOfMorningEvents = random.nextInt(numberOfMaxEventsPerTimePeriod -
                        numberOfMinEventsPerTimePeriod) + numberOfMinEventsPerTimePeriod;
                int numberOfAfternoonEvents = random.nextInt(numberOfMaxEventsPerTimePeriod -
                        numberOfMinEventsPerTimePeriod) + numberOfMinEventsPerTimePeriod;
                for (int j = 0; j < numberOfMorningEvents; j++) {
                    lines.add(demoUtils.baseLineGeneratorAux(dt, configuration, morningMedianHour, dataSource,
                            standardDeviation, maxHourOfWork, minHourOfWork));
                }
                for (int j = 0; j < numberOfAfternoonEvents; j++) {
                    lines.add(demoUtils.baseLineGeneratorAux(dt, configuration, afternoonMedianHour, dataSource,
                            standardDeviation, maxHourOfWork, minHourOfWork));
                }
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
     * This method generates a single event
     *
     * @param configuration
     * @param dataSource
     * @param dt
     * @param minHour
     * @param maxHour
     * @return
     * @throws Exception
	 */
    public List<JSONObject> createSingleEvent(DemoGenericEvent configuration, DemoUtils.DataSource dataSource,
            DateTime dt, int minHour, int maxHour) throws Exception {
        List<DemoEvent> lines = new ArrayList();
        lines.add(demoUtils.generateEvent(configuration, dataSource, dt, minHour, maxHour));
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
     * @param indicatorsScore
     * @param anomalyTypeFieldName
     * @param indicators
     * @return
     * @throws Exception
     */
    private List<JSONObject> createAnomalies(DemoUtils.DataSource dataSource, DemoGenericEvent configuration,
            int minNumberOfAnomalies, int maxNumberOfAnomalies, int minHourForAnomaly, int maxHourForAnomaly,
            EvidenceTimeframe timeframe, EvidenceType evidenceType, int indicatorsScore, String anomalyTypeFieldName,
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
        List<DemoEvent> lines = new ArrayList();
        DateTime randomDate = demoUtils.generateRandomTimeForAnomaly(anomalyDate, minHourForAnomaly,
                maxHourForAnomaly);
        demoUtils.indicatorCreationAux(evidenceType, configuration, indicators, randomDate, dataSource, indicatorsScore,
                anomalyTypeFieldName, numberOfAnomalies, anomalyDate, timeframe, evidencesService);
        for (int i = 0; i < numberOfAnomalies; i++) {
            String lineToWrite = demoUtils.generateEvent(configuration, dataSource, randomDate);
            lines.add(new DemoEvent(lineToWrite, randomDate));
            randomDate = demoUtils.generateRandomTimeForAnomaly(anomalyDate, minHourForAnomaly,
                    maxHourForAnomaly);
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