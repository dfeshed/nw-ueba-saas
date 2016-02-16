package fortscale.collection.jobs.demo;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.core.*;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.services.AlertsService;
import fortscale.services.EvidencesService;
import fortscale.services.UserService;
import fortscale.services.exceptions.HdfsException;
import fortscale.services.impl.HdfsService;
import fortscale.services.impl.SpringService;
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
    private UserService userService;
    @Autowired
    private ComputerRepository computerRepository;
    @Autowired
    private JdbcOperations impalaJdbcTemplate;
    @Autowired
    private AlertsService alertsService;
    @Autowired
    private EvidencesService evidencesService;

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
     * @throws Exception
     */
    private void generateScenario1() throws Exception {

        //TODO - extract these general scenario fields
        String title = "Suspicious Daily User Activity";
        int alertScore = 90;
        int indicatorsScore = 90;
        Severity alertSeverity = Severity.Critical;
        String samaccountname = "alrusr51";
        String domain = "somebigcompany.com";
        int eventsScore = 98;
        String computerDomain = "FORTSCALE";
        String dc = "FS-DC-01$";
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
        Set<String> baseLineMachinesSet = demoUtils.generateRandomDestinationMachines(machines, minNumberOfDestMachines,
                maxNumberOfDestMachines);
        String[] baseLineMachines = baseLineMachinesSet.toArray(new String[baseLineMachinesSet.size()]);
        Set<String> anomalousMachinesSet = demoUtils.generateRandomDestinationMachines(machines,
                minNumberOfAnomaliesIndicator3, maxNumberOfAnomaliesIndicator3);
        String[] anomalousMachines = anomalousMachinesSet.toArray(new String[anomalousMachinesSet.size()]);
        List<JSONObject> records = new ArrayList();

        //TODO - extract these to json
        //create baseline
        records.addAll(createLoginEvents(user, computer, new String[] { dstMachine },
                DemoUtils.DataSource.kerberos_logins, computerDomain, dc, clientAddress));
        records.addAll(createLoginEvents(user, computer, baseLineMachines, DemoUtils.DataSource.ssh, computerDomain, dc,
                clientAddress));
        records.addAll(createLoginEvents(serviceAccount, serviceMachine, new String[] { anomalousMachine },
                DemoUtils.DataSource.ssh, computerDomain, dc, clientAddress));

        List<Evidence> indicators = new ArrayList();

        //create anomalies
        records.addAll(createLoginAnomalies(DemoUtils.DataSource.kerberos_logins, minNumberOfAnomaliesIndicator1,
                maxNumberOfAnomaliesIndicator1, minHourForAnomaly, maxHourForAnomaly, user, computer, new String[]
                        { dstMachine }, eventsScore, computerDomain, dc, clientAddress, DemoUtils.EventFailReason.TIME,
                "0x0", null, EvidenceType.AnomalySingleEvent, indicatorsScore, "event_time", indicators));
        records.addAll(createLoginAnomalies(DemoUtils.DataSource.kerberos_logins, minNumberOfAnomaliesIndicator2,
                maxNumberOfAnomaliesIndicator2, minHourForAnomaly, maxHourForAnomaly, user, computer, new String[]
                        { dstMachine }, eventsScore, computerDomain, dc, clientAddress,
                DemoUtils.EventFailReason.FAILURE, "0x12", EvidenceTimeframe.Daily, EvidenceType.AnomalyAggregatedEvent,
                indicatorsScore, "number_of_failed_" + DemoUtils.DataSource.kerberos_logins, indicators));
        records.addAll(createLoginAnomalies(DemoUtils.DataSource.ssh, minNumberOfAnomaliesIndicator3,
                maxNumberOfAnomaliesIndicator3, anomalousHour, anomalousHour, user, computer, anomalousMachines,
                eventsScore, computerDomain, dc, clientAddress, DemoUtils.EventFailReason.TIME, "Accepted",
                EvidenceTimeframe.Hourly, EvidenceType.AnomalyAggregatedEvent, indicatorsScore,
                "distinct_number_of_dst_machines_" + DemoUtils.DataSource.ssh, indicators));
        records.addAll(createLoginAnomalies(DemoUtils.DataSource.ssh, minNumberOfAnomaliesIndicator4,
                maxNumberOfAnomaliesIndicator4, minHourForAnomaly, maxHourForAnomaly, user, computer, new String[]
                        { anomalousMachine }, eventsScore, computerDomain, dc, clientAddress,
                DemoUtils.EventFailReason.DEST, "Accepted", null, EvidenceType.AnomalySingleEvent, indicatorsScore,
                "destination_machine", indicators));

        //forward events to create buckets
        KafkaEventsWriter streamWriter = new KafkaEventsWriter(DemoUtils.AGGREGATION_TOPIC);
        Collections.sort(records, new JSONComparator());
        for (JSONObject record: records) {
            streamWriter.send(null, record.toJSONString(JSONStyle.NO_COMPRESS));
        }
        long endTime = (Long)records.get(records.size() - 1).get(DemoUtils.EPOCH_TIME_FIELD) + 60 * 60 * 24;
        String dummyEvent = "{\"date_time_unix\":" + endTime + ",\"data_source\":\"dummy\"}";
        streamWriter.send(null, dummyEvent);
        streamWriter.close();

        //TODO - stop alert creation task and delete topic?

        //create alert
        demoUtils.createAlert(title, anomalyDate.getMillis(), anomalyDate.plusDays(1).minusMillis(1).getMillis(), user,
                indicators, alertScore, alertSeverity, alertsService);
    }

    /**
     *
     * This method generates scenario2 as described here:
     * https://fortscale.atlassian.net/browse/FV-9288
     *
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws HdfsException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    /*private void generateScenario2()
            throws IOException, HdfsException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        //TODO - extract these general scenario fields
        String samaccountname = "adminusr25fs";
        String domain = "somebigcompany.com";
        int indicatorScore = 98;
        String title = "Suspicious Hourly Privileged Account Activity";
        int alertScore = 90;
        Severity alertSeverity = Severity.Critical;
        String computerDomain = "FORTSCALE";
        String dc = "FS-DC-01$";
        int minHourForAnomaly = 9;
        int maxHourForAnomaly = 5;
        int minNumberOfDestMachines = 10;
        int maxNumberOfDestMachines = 30;
        numberOfMinEventsPerTimePeriod = 10;
        numberOfMaxEventsPerTimePeriod = 30;
        //TODO - extract these specific indicator fields
        int numberOfAnomaliesIndicator1 = 60;

        String clientAddress = demoUtils.generateRandomIPAddress();
        String username = samaccountname + "@" + domain;
        String srcMachine = samaccountname + "_PC";
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
        List<Computer> machines = computerRepository.getComputersOfType(ComputerUsageType.Desktop,
        	limitNumberOfDestinationMachines);
        if (machines.isEmpty()) {
            logger.error("no desktop machines found");
            return;
        }
        Set<String> baseLineMachinesSet = demoUtils.generateRandomDestinationMachines(machines, minNumberOfDestMachines,
                maxNumberOfDestMachines);
        String[] baseLineMachines = baseLineMachinesSet.toArray(new String[baseLineMachinesSet.size()]);
        Set<String> anomalousMachinesSet = demoUtils.generateRandomDestinationMachines(machines,
                numberOfAnomaliesIndicator1, numberOfAnomaliesIndicator1);
        String[] anomalousMachines = anomalousMachinesSet.toArray(new String[anomalousMachinesSet.size()]);
        //generate scenario
        List<Evidence> indicators = new ArrayList();

        createLoginEvents(user, computer, baseLineMachines, DemoUtils.DataSource.kerberos_logins, computerDomain, dc,
                clientAddress, DemoUtils.HOURLY_HISTOGRAM, "number_of_failed_" + DemoUtils.DataSource.kerberos_logins);

        //create anomalies
        indicators.add(demoUtils.createIndicator(user.getUsername(), EvidenceType.Tag, anomalyDate.toDate(),
                anomalyDate.plusDays(1).minusMillis(1).toDate(), DemoUtils.NORMALIZED_USERNAME, 50.0, "tag", "admin", 1,
                EvidenceTimeframe.Daily));
        indicators.addAll(createLoginAnomalies(DemoUtils.DataSource.kerberos_logins, numberOfAnomaliesIndicator1,
                numberOfAnomaliesIndicator1, minHourForAnomaly, maxHourForAnomaly, user, computer, anomalousMachines,
                indicatorScore, 50, computerDomain, dc, clientAddress, DemoUtils.EventFailReason.TIME,
                EvidenceTimeframe.Daily, EvidenceType.AnomalyAggregatedEvent, "distinct_number_of_dst_machines_" +
                        DemoUtils.DataSource.kerberos_logins, DemoUtils.HOURLY_HISTOGRAM, "0x0"));

        //create alert
        demoUtils.createAlert(title, anomalyDate.getMillis(), anomalyDate.plusDays(1).minusMillis(1).getMillis(), user,
                indicators, alertScore, alertSeverity);
    }*/

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
        finishStep();
	}

    /**
     *
     * This method generates the login events
     *
     * @param user
     * @param computer
     * @param dstMachines
     * @param dataSource
     * @param dc
     * @param computerDomain
     * @param clientAddress
     * @return
     * @throws Exception
     */
    private List<JSONObject> createLoginEvents(User user, Computer computer, String[] dstMachines,
            DemoUtils.DataSource dataSource, String dc, String computerDomain, String clientAddress) throws Exception {
        Random random = new Random();
        DateTime dt = anomalyDate.minusDays(numOfDaysBack);
        List<LineAux> lines = new ArrayList();
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
                lines.add(demoUtils.baseLineGeneratorAux(dt, morningMedianHour, dataSource, user, computer, dstMachines,
                        computerDomain, dc, clientAddress, 0, standardDeviation, maxHourOfWork, minHourOfWork));
            }
            for (int j = 0; j < numberOfAfternoonEvents; j++) {
                lines.add(demoUtils.baseLineGeneratorAux(dt, afternoonMedianHour, dataSource, user, computer,
                        dstMachines, computerDomain, dc, clientAddress, 0, standardDeviation, maxHourOfWork,
                        minHourOfWork));
            }
            dt = dt.plusDays(1);
        }
        List<HdfsService> hdfsServices = new ArrayList();
        DataSourceProperties dataSourceProperties = dataSourceToHDFSProperties.get(dataSource);
        hdfsServices.add(new HdfsService(dataSourceProperties.getHdfsPartition(),
                dataSourceProperties.getFileName(), partitionStrategy, splitStrategy,
                dataSourceProperties.getImpalaTable(), lines.size(), 0, DemoUtils.SEPARATOR));
        List<JSONObject> records = demoUtils.saveEvents(user, dataSourceProperties, lines,
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
     * @param user
     * @param computer
     * @param dstMachines
     * @param eventScore
     * @param dc
     * @param computerDomain
     * @param clientAddress
     * @param reason
     * @param status
     * @param timeframe
     * @param evidenceType
     * @param indicatorScore
     * @param anomalyTypeFieldName
     * @param indicators
     * @return
     * @throws Exception
     */
    private List<JSONObject> createLoginAnomalies(DemoUtils.DataSource dataSource, int minNumberOfAnomalies,
            int maxNumberOfAnomalies, int minHourForAnomaly, int maxHourForAnomaly, User user, Computer computer,
            String[] dstMachines, int eventScore, String dc, String computerDomain, String clientAddress,
            DemoUtils.EventFailReason reason, String status, EvidenceTimeframe timeframe, EvidenceType evidenceType,
            int indicatorScore, String anomalyTypeFieldName, List<Evidence> indicators)
            throws Exception {
        DataSourceProperties dataSourceProperties = dataSourceToHDFSProperties.get(dataSource);
        Random random = new Random();
        int numberOfAnomalies;
        if (maxNumberOfAnomalies == minNumberOfAnomalies) {
            numberOfAnomalies = maxNumberOfAnomalies;
        } else {
            numberOfAnomalies = random.nextInt(maxNumberOfAnomalies - minNumberOfAnomalies) + minNumberOfAnomalies;
        }
        List<LineAux> lines = new ArrayList();
        for (int i = 0; i < numberOfAnomalies; i++) {
            DateTime randomDate = demoUtils.generateRandomTimeForAnomaly(anomalyDate, minHourForAnomaly,
                    maxHourForAnomaly);
            String lineToWrite = null;
            String dstMachine = null;
            switch (dataSource) {
                case kerberos_logins: {
                    dstMachine = dstMachines[0];
                    lineToWrite = demoUtils.buildKerberosHDFSLine(randomDate, user, computer, dstMachine, eventScore,
                            reason, computerDomain, dc, clientAddress, status);
                    break;
                } case ssh: {
                    dstMachine = dstMachines[i];
                    lineToWrite = demoUtils.buildSshHDFSLine(randomDate, user, computer, dstMachine, eventScore, reason,
                            clientAddress, status);
                    break;
                } case vpn: break; //TODO - implement
            }
            lines.add(new LineAux(lineToWrite, randomDate));
            //create just one indicator
            if (i == 0) {
                demoUtils.indicatorCreationAux(evidenceType, reason, indicators, user, randomDate, dataSource,
                        indicatorScore, anomalyTypeFieldName, numberOfAnomalies, anomalyDate, dstMachine,
                        computer.getName(), timeframe, evidencesService);
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