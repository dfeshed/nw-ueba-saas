package fortscale.collection.jobs.demo;

import fortscale.domain.core.*;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.services.AlertsService;
import fortscale.services.EvidencesService;
import fortscale.services.UserService;
import fortscale.services.impl.HdfsService;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.split.FileSplitStrategy;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.quartz.JobExecutionException;
import org.springframework.jdbc.core.JdbcOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Amir Keren on 17/02/2016.
 */
public class Scenario {

    private static Logger logger = Logger.getLogger(ScenarioGeneratorJob.class);

    private DateTime anomalyDate;
    private ComputerRepository computerRepository;
    private UserService userService;
    private AlertsService alertsService;
    private EvidencesService evidencesService;
    private JdbcOperations impalaJdbcTemplate;
    private boolean skipWeekend;
    private int numOfDaysBack;
    private int numberOfMaxEventsPerTimePeriod;
    private int numberOfMinEventsPerTimePeriod;
    private int morningMedianHour;
    private int standardDeviation;
    private int maxHourOfWork;
    private int minHourOfWork;
    private int afternoonMedianHour;
    private PartitionStrategy partitionStrategy;
    private FileSplitStrategy splitStrategy;
    private Map<DemoUtils.DataSource, DataSourceProperties> dataSourceToHDFSProperties;

    private String alertTitle;
    private int alertScore;
    private int indicatorsScore;
    private Severity alertSeverity;
    private String samaccountname;
    private int eventsScore;
    private List<BaseLineEvents> baseLineEvents;
    private List<AnomaliesEvents> anomalyEvents;
    private int minHourForAnomaly;
    private int maxHourForAnomaly;

    public List<BaseLineEvents> getBaseLineEvents() {
        return baseLineEvents;
    }

    public void setBaseLineEvents(List<BaseLineEvents> baseLineEvents) {
        this.baseLineEvents = baseLineEvents;
    }

    public List<AnomaliesEvents> getAnomalyEvents() {
        return anomalyEvents;
    }

    public void setAnomalyEvents(List<AnomaliesEvents> anomalyEvents) {
        this.anomalyEvents = anomalyEvents;
    }

    public String getAlertTitle() {
        return alertTitle;
    }

    public void setAlertTitle(String alertTitle) {
        this.alertTitle = alertTitle;
    }

    public int getAlertScore() {
        return alertScore;
    }

    public void setAlertScore(int alertScore) {
        this.alertScore = alertScore;
    }

    public int getIndicatorsScore() {
        return indicatorsScore;
    }

    public void setIndicatorsScore(int indicatorsScore) {
        this.indicatorsScore = indicatorsScore;
    }

    public Severity getAlertSeverity() {
        return alertSeverity;
    }

    public void setAlertSeverity(Severity alertSeverity) {
        this.alertSeverity = alertSeverity;
    }

    public String getSamaccountname() {
        return samaccountname;
    }

    public void setSamaccountname(String samaccountname) {
        this.samaccountname = samaccountname;
    }

    public int getEventsScore() {
        return eventsScore;
    }

    public void setEventsScore(int eventsScore) {
        this.eventsScore = eventsScore;
    }

    public int getMinHourForAnomaly() {
        return minHourForAnomaly;
    }

    public void setMinHourForAnomaly(int minHourForAnomaly) {
        this.minHourForAnomaly = minHourForAnomaly;
    }

    public int getMaxHourForAnomaly() {
        return maxHourForAnomaly;
    }

    public void setMaxHourForAnomaly(int maxHourForAnomaly) {
        this.maxHourForAnomaly = maxHourForAnomaly;
    }

    public void setGlobalParams(DateTime anomalyDate, AlertsService alertsService, EvidencesService evidencesService,
            UserService userService, ComputerRepository computerRepository, JdbcOperations impalaJdbcTemplate,
            boolean skipWeekend, int numOfDaysBack, int numberOfMaxEventsPerTimePeriod,
            int numberOfMinEventsPerTimePeriod, int morningMedianHour, int standardDeviation, int maxHourOfWork,
            int minHourOfWork, int afternoonMedianHour, PartitionStrategy partitionStrategy,
            FileSplitStrategy splitStrategy,
            Map<DemoUtils.DataSource, DataSourceProperties> dataSourceToHDFSProperties) {
        this.anomalyDate = anomalyDate;
        this.alertsService = alertsService;
        this.evidencesService = evidencesService;
        this.userService = userService;
        this.computerRepository = computerRepository;
        this.impalaJdbcTemplate = impalaJdbcTemplate;
        this.skipWeekend = skipWeekend;
        this.numOfDaysBack = numOfDaysBack;
        this.numberOfMaxEventsPerTimePeriod = numberOfMaxEventsPerTimePeriod;
        this.numberOfMinEventsPerTimePeriod = numberOfMinEventsPerTimePeriod;
        this.morningMedianHour = morningMedianHour;
        this.standardDeviation = standardDeviation;
        this.maxHourOfWork = maxHourOfWork;
        this.minHourOfWork = minHourOfWork;
        this.afternoonMedianHour = afternoonMedianHour;
        this.partitionStrategy = partitionStrategy;
        this.splitStrategy = splitStrategy;
        this.dataSourceToHDFSProperties = dataSourceToHDFSProperties;
    }

	/**
     *
     * This method generates the scenario
     *
     * @return
     * @throws JobExecutionException
     */
    public List<JSONObject> generateScenario() throws Exception {
        DemoUtils demoUtils = new DemoUtils();
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
        List<JSONObject> records = new ArrayList();
        for (BaseLineEvents baseLineEvent: baseLineEvents) {
            records.addAll(createEvents(baseLineEvent.getDemoEvent(), baseLineEvent.getDataSource()));
        }
        List<Evidence> indicators = new ArrayList();
        for (AnomaliesEvents anomaliesEvent: anomalyEvents) {
            records.addAll(createAnomalies(anomaliesEvent.getDataSource(), anomaliesEvent.getDemoEvent(),
                    anomaliesEvent.getMinNumberOfAnomalies(), anomaliesEvent.getMaxNumberOfAnomalies(),
                    anomaliesEvent.getMinHourForAnomaly(), anomaliesEvent.getMaxHourForAnomaly(),
                    anomaliesEvent.getTimeframe(), anomaliesEvent.getEvidenceType(), indicatorsScore,
                    anomaliesEvent.getAnomalyTypeFieldName(), indicators));
        }
        demoUtils.createAlert(alertTitle, anomalyDate.getMillis(), anomalyDate.plusDays(1).minusMillis(1).getMillis(),
                user, indicators, alertScore, alertSeverity, alertsService);
        return records;
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
    private List<JSONObject> createEvents(DemoGenericEvent configuration, DemoUtils.DataSource dataSource)
            throws Exception {
        Random random = new Random();
        DemoUtils demoUtils = new DemoUtils();
        DateTime dt = anomalyDate.minusDays(numOfDaysBack);
        List<DemoEvent> lines = new ArrayList();
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
                lines.add(demoUtils.baseLineGeneratorAux(dt, configuration, afternoonMedianHour, dataSource,
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
        DemoUtils demoUtils = new DemoUtils();
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
        demoUtils.indicatorCreationAux(evidenceType, configuration, indicators, randomDate, dataSource,
                indicatorsScore, anomalyTypeFieldName, numberOfAnomalies, anomalyDate, timeframe,
                evidencesService);
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

}