package fortscale.collection.jobs.demo;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.core.Computer;
import fortscale.domain.core.ComputerUsageType;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.services.UserService;
import fortscale.services.exceptions.HdfsException;
import fortscale.services.impl.HdfsService;
import fortscale.services.impl.SpringService;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.hdfs.split.FileSplitStrategy;
import fortscale.utils.impala.ImpalaPageRequest;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.impala.ImpalaQuery;
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
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;

import java.io.IOException;
import java.sql.Timestamp;
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
            String topics = jobDataMapExtension.getJobDataMapStringValue(map, "topics-" + dataSource);
            String fields = jobDataMapExtension.getJobDataMapStringValue(map, "impalaTableFields-" + dataSource);
            result.put(dataSource, new DataSourceProperties(impalaTable, fileName, hdfsPartition, topics, fields));
        }
        return result;
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
        int eventScore = 98;
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

        //TODO - extract these to json

        //create baseline
        createLoginEvents(user, computer, new String[] { dstMachine }, DemoUtils.DataSource.kerberos_logins,
                computerDomain, dc, clientAddress);
        createLoginEvents(user, computer, baseLineMachines, DemoUtils.DataSource.ssh, computerDomain, dc,clientAddress);
        createLoginEvents(serviceAccount, serviceMachine, new String[] { anomalousMachine }, DemoUtils.DataSource.ssh,
                computerDomain, dc, clientAddress);

        //create anomalies
        createLoginAnomalies(DemoUtils.DataSource.kerberos_logins, minNumberOfAnomaliesIndicator1,
                maxNumberOfAnomaliesIndicator1, minHourForAnomaly, maxHourForAnomaly, user, computer, new String[]
                        { dstMachine }, eventScore, computerDomain, dc, clientAddress, DemoUtils.EventFailReason.TIME,
                "0x0");
        createLoginAnomalies(DemoUtils.DataSource.kerberos_logins, minNumberOfAnomaliesIndicator2,
                maxNumberOfAnomaliesIndicator2, minHourForAnomaly, maxHourForAnomaly, user, computer, new String[]
                        { dstMachine }, eventScore, computerDomain, dc, clientAddress,
                DemoUtils.EventFailReason.FAILURE, "0x12");
        createLoginAnomalies(DemoUtils.DataSource.ssh, minNumberOfAnomaliesIndicator3, maxNumberOfAnomaliesIndicator3,
                anomalousHour, anomalousHour, user, computer, anomalousMachines, 50, computerDomain, dc, clientAddress,
                DemoUtils.EventFailReason.TIME, "Accepted");
        createLoginAnomalies(DemoUtils.DataSource.ssh, minNumberOfAnomaliesIndicator4, maxNumberOfAnomaliesIndicator4,
                minHourForAnomaly, maxHourForAnomaly, user, computer, new String[] { anomalousMachine }, eventScore,
                computerDomain, dc, clientAddress, DemoUtils.EventFailReason.DEST, "Accepted");
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
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     * @throws HdfsException
     */
    private void createLoginEvents(User user, Computer computer, String[] dstMachines, DemoUtils.DataSource dataSource,
            String dc, String computerDomain, String clientAddress) throws ClassNotFoundException,
            IllegalAccessException, InstantiationException, IOException, HdfsException {
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
            for (int j = 0; j < numberOfMorningEvents; j++) {
                baseLineGeneratorAux(dt, morningMedianHour, dataSource, user, computer, dstMachines,
                        computerDomain, dc, clientAddress, 0);
            }
            for (int j = 0; j < numberOfAfternoonEvents; j++) {
                baseLineGeneratorAux(dt, afternoonMedianHour, dataSource, user, computer, dstMachines,
                        computerDomain, dc, clientAddress, 0);
            }
            dt = dt.plusDays(1);
        }
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
     * @throws HdfsException
     * @throws IOException
     */
    private void createLoginAnomalies(DemoUtils.DataSource dataSource, int minNumberOfAnomalies,
            int maxNumberOfAnomalies, int minHourForAnomaly, int maxHourForAnomaly, User user, Computer computer,
            String[] dstMachines, int eventScore, String dc, String computerDomain, String clientAddress,
            DemoUtils.EventFailReason reason, String status) throws HdfsException, IOException {
        DataSourceProperties dataSourceProperties = dataSourceToHDFSProperties.get(dataSource);
        HdfsService service = new HdfsService(dataSourceProperties.getHdfsPartition(),
                dataSourceProperties.getFileName(), partitionStrategy, splitStrategy,
                dataSourceProperties.getImpalaTable(), 1, 0, DemoUtils.SEPARATOR);
        HdfsService service_top = new HdfsService(dataSourceProperties.getHdfsPartition() + "_top",
                dataSourceProperties.getFileName(), partitionStrategy, splitStrategy,
                dataSourceProperties.getImpalaTable() + "_top", 1, 0, DemoUtils.SEPARATOR);
        Random random = new Random();
        int numberOfAnomalies;
        if (maxNumberOfAnomalies == minNumberOfAnomalies) {
            numberOfAnomalies = maxNumberOfAnomalies;
        } else {
            numberOfAnomalies = random.nextInt(maxNumberOfAnomalies - minNumberOfAnomalies) + minNumberOfAnomalies;
        }
        for (int i = 0; i < numberOfAnomalies; i++) {
            DateTime randomDate = demoUtils.generateRandomTimeForAnomaly(anomalyDate, minHourForAnomaly,
                    maxHourForAnomaly);
            String lineToWrite = null;
            String dstMachine;
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
            service.writeLineToHdfs(lineToWrite, randomDate.getMillis());
            service_top.writeLineToHdfs(lineToWrite, randomDate.getMillis());
            JSONObject json = convertLineToJSON(dataSourceProperties, randomDate.getMillis() / 1000,
                    user.getUsername());
            if (json == null) {
                throw new HdfsException("failed to find line in HDFS", new Exception());
            }
            String message = json.toJSONString(JSONStyle.NO_COMPRESS);
            for (String topic: dataSourceProperties.getTopics().split(",")) {
                demoUtils.sendMessage(topic, message);
            }
        }
    }

    private void fillJsonWithFieldValue(JSONObject json, String fieldName, Object val) {
        if (val instanceof Timestamp) {
            json.put(fieldName, val.toString());
        } else {
            json.put(fieldName, val);
        }
    }

    private List<Map<String, Object>> getDataFromImpala(String impalaTableName, long epochTime, String username) {
        ImpalaQuery query = new ImpalaQuery();
        query.select("*").from(impalaTableName);
        query.andEq(DemoUtils.EPOCH_TIME, epochTime);
        query.andEq(DemoUtils.NORMALIZED_USERNAME, username);
        query.limitAndSort(new ImpalaPageRequest(1, new Sort(Sort.Direction.DESC, DemoUtils.EPOCH_TIME)));
        return impalaJdbcTemplate.query(query.toSQL(), new ColumnMapRowMapper());
    }

    private JSONObject convertLineToJSON(DataSourceProperties dataSourceProperties, long epochTime, String username) {
        String[] fieldsName = ImpalaParser.getTableFieldNamesAsArray(dataSourceProperties.getFields());
        List<Map<String, Object>> resultsMap =  getDataFromImpala(dataSourceProperties.getImpalaTable(), epochTime,
                username);
        for (Map<String, Object> result : resultsMap) {
            JSONObject json = new JSONObject();
            for (String fieldName : fieldsName) {
                Object val = result.get(fieldName.toLowerCase());
                fillJsonWithFieldValue(json, fieldName, val);
            }
            return json;
        }
        return null;
    }

    /**
     *
     * This method is a helper method for the event generators
     *
     * @param dt
     * @param medianHour
     * @param dataSource
     * @param user
     * @param computer
     * @param dstMachines
     * @param computerDomain
     * @param dc
     * @param clientAddress
     * @param score
     * @throws HdfsException
     */
    private void baseLineGeneratorAux(DateTime dt, int medianHour, DemoUtils.DataSource dataSource, User user,
            Computer computer, String[] dstMachines, String computerDomain, String dc, String clientAddress, int score)
            throws HdfsException, IOException {
        Random random = new Random();
        DataSourceProperties dataSourceProperties = dataSourceToHDFSProperties.get(dataSource);
        HdfsService service = new HdfsService(dataSourceProperties.getHdfsPartition(),
                dataSourceProperties.getFileName(), partitionStrategy, splitStrategy,
                dataSourceProperties.getImpalaTable(), 1, 0, DemoUtils.SEPARATOR);
        DateTime dateTime = demoUtils.generateRandomTimeForDay(dt, standardDeviation, medianHour, maxHourOfWork,
                minHourOfWork);
        String dstMachine = dstMachines[random.nextInt(dstMachines.length)];
        String lineToWrite = null;
        switch (dataSource) {
            case kerberos_logins: lineToWrite = demoUtils.buildKerberosHDFSLine(dateTime, user, computer, dstMachine,
                    score, DemoUtils.EventFailReason.NONE, computerDomain, dc, clientAddress, "0x0"); break;
            case ssh: lineToWrite = demoUtils.buildSshHDFSLine(dateTime, user, computer, dstMachine, score,
                    DemoUtils.EventFailReason.NONE,
                        clientAddress, "Accepted"); break;
            case vpn: break; //TODO - implement
        }
        service.writeLineToHdfs(lineToWrite, dateTime.getMillis());
        JSONObject json = convertLineToJSON(dataSourceProperties, dateTime.getMillis() / 1000, user.getUsername());
        if (json == null) {
            throw new HdfsException("failed to find line in HDFS", new Exception());
        }
        String message = json.toJSONString(JSONStyle.NO_COMPRESS);
        for (String topic: dataSourceProperties.getTopics().split(",")) {
            demoUtils.sendMessage(topic, message);
        }
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