package com.rsa.netwitness.presidio.automation.log_player;

import com.rsa.netwitness.presidio.automation.domain.repository.AdapterAbstractStoredDataRepository;
import com.rsa.netwitness.presidio.automation.jdbc.AirflowDagsPostgres;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;

public class MongoCollectionsMonitor {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(MongoCollectionsMonitor.class.getName());

    private List<AdapterAbstractStoredDataRepository> collectiontToMonitor = new LinkedList<>();
    private ScheduledExecutorService scheduler;
    private int corePoolSize = 5;
    private long TASK_FREQUENCY_MINUTES = 15;
    private long ADDITIONAL_DELAY_BEFORE_FIRST_TIME_STATUS_CHECK = 10;
    private long TASK_STATUS_CHECK_FREQUENCY = TASK_FREQUENCY_MINUTES;
    private long DELAY_BEFORE_FIRST_TASK_STARTED = TASK_FREQUENCY_MINUTES + ADDITIONAL_DELAY_BEFORE_FIRST_TIME_STATUS_CHECK; // 25 min
    private List<MongoProgressTask> tasks;
    private int TIME_BUCKETES_TO_CHECK = 6;
    private TimeUnit TIME_UNITS = TimeUnit.MINUTES;
    private Supplier<ChronoUnit> TIME_UNITS_CHRONO = () -> ChronoUnit.valueOf(TIME_UNITS.name());

    public MongoCollectionsMonitor(List<? extends AdapterAbstractStoredDataRepository> collectionToMonitor) {
        if (collectionToMonitor != null && !collectionToMonitor.isEmpty())
            this.collectiontToMonitor.addAll(collectionToMonitor);
        else throw new RuntimeException("Empty collections list");

    }


    public void createTasks(Instant startDate, Instant endDate) {
        scheduler = Executors.newScheduledThreadPool(corePoolSize);

        tasks = collectiontToMonitor.stream()
                .map(abstractRepository -> new MongoProgressTask(abstractRepository, startDate, endDate))
                .collect(Collectors.toList());
    }

    public List<ScheduledFuture> execute() {
        return tasks.stream()
                .map(mongoProgressTask -> scheduler.scheduleAtFixedRate(mongoProgressTask, DELAY_BEFORE_FIRST_TASK_STARTED, TASK_FREQUENCY_MINUTES, TIME_UNITS))
                .collect(Collectors.toList());
    }

    public void shutdown() {
        scheduler.shutdown();
    }


    public boolean waitForResult(Instant hourlyEntityFlowsEndDate) throws InterruptedException {
        boolean dataProcessingStillInProgress = true;

        LOGGER.info("First check time: " + Instant.now().plus(DELAY_BEFORE_FIRST_TASK_STARTED + 1, TIME_UNITS_CHRONO.get()));
        TIME_UNITS.sleep(DELAY_BEFORE_FIRST_TASK_STARTED + 1); // 26 min
        LOGGER.info("Going to check if data processing started.");
        boolean isDataProcessingStarted = tasks.stream()
                .map(MongoProgressTask::isProcessingStarted)
                .reduce(Boolean::logicalOr).orElse(false);

        LOGGER.info("isDataProcessingStarted=" + isDataProcessingStarted);
        String errorMessage = "Not a single event reached any input collection after " +
                DELAY_BEFORE_FIRST_TASK_STARTED + " minutes. Aborting wait.";

        assertThat(isDataProcessingStarted)
                .overridingErrorMessage(errorMessage)
                .isTrue();

        AirflowDagsPostgres airflowDagsPostgres = new AirflowDagsPostgres();

        while (dataProcessingStillInProgress && !airflowDagsPostgres.allHourlyEntityFlowsExceeded(hourlyEntityFlowsEndDate)) {
            LOGGER.info("Next check: " + Instant.now().plus(TASK_STATUS_CHECK_FREQUENCY, TIME_UNITS_CHRONO.get()));
            TIME_UNITS.sleep(TASK_STATUS_CHECK_FREQUENCY);

            dataProcessingStillInProgress = tasks.stream()
                    .map(mongoProgressTask -> mongoProgressTask.isProcessingStillInProgress(TIME_BUCKETES_TO_CHECK))
                    .reduce(Boolean::logicalOr).orElse(false);
            LOGGER.info("dataProcessingStillInProgress=" + dataProcessingStillInProgress);
        }

        boolean allCollectionsHaveFinalDaySamples = tasks.stream()
                .map(mongoProgressTask -> mongoProgressTask.isFinalDaySampleExist(13, HOURS))
                .reduce(Boolean::logicalAnd).orElse(false);

        LOGGER.info("waitForResult has finished with " + allCollectionsHaveFinalDaySamples);
        return allCollectionsHaveFinalDaySamples && airflowDagsPostgres.allHourlyEntityFlowsExceeded(hourlyEntityFlowsEndDate);
    }
    
}
