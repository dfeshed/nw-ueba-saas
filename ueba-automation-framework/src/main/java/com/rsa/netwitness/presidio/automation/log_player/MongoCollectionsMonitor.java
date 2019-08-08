package com.rsa.netwitness.presidio.automation.log_player;

import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class MongoCollectionsMonitor {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(MongoCollectionsMonitor.class.getName());

    private List<MongoRepository> collectiontToMonitor = new LinkedList<>();
    private ScheduledExecutorService scheduler;
    private int corePoolSize = 2;
    private long DELAY_BETWEEN_TASKS = 1;
    private long TASK_FREQUENCY_MINUTES = 15;
    private long ADDITIONAL_DELAY_BEFORE_FIRST_TIME_STATUS_CHECK = 5;
    private long TASK_STATUS_CHECK_FREQUENCY = TASK_FREQUENCY_MINUTES;
    private long DELAY_BEFORE_FIRST_TASK_STARTED = TASK_FREQUENCY_MINUTES + ADDITIONAL_DELAY_BEFORE_FIRST_TIME_STATUS_CHECK - 1;
    private final TimeUnit TIME_UNITS = TimeUnit.MINUTES;
    private List<MongoProgressTask> tasks;
    private int TIME_BUCKETES_TO_CHECK = 6;

    public MongoCollectionsMonitor(List<? extends MongoRepository> collectiontToMonitor) {
        if (collectiontToMonitor != null && !collectiontToMonitor.isEmpty())
            this.collectiontToMonitor.addAll(collectiontToMonitor);
        else throw new RuntimeException("Empty collections list");

    }


    public void createTasks(Instant startDate, Instant endDate) {
        scheduler = Executors.newScheduledThreadPool(corePoolSize);

        tasks = collectiontToMonitor.stream()
                .map(e -> new MongoProgressTask(e, startDate, endDate))
                .collect(Collectors.toList());

    }

    public List<ScheduledFuture> execute() {
        return tasks.stream()
                .map(e -> scheduler.scheduleAtFixedRate(e, DELAY_BEFORE_FIRST_TASK_STARTED, TASK_FREQUENCY_MINUTES, TIME_UNITS))
                .collect(Collectors.toList());
    }

    public void shutdown() {
        scheduler.shutdown();
    }


    public boolean waitForResult() throws InterruptedException {

        boolean allCollectionsAreEmptyAfterInitiateWait = true;
        boolean stillWaitingForTheLastDayData = true;
        boolean dataProcessingStillBeInProgress = true;

        TIME_UNITS.sleep(ADDITIONAL_DELAY_BEFORE_FIRST_TIME_STATUS_CHECK);

        while (allCollectionsAreEmptyAfterInitiateWait && dataProcessingStillBeInProgress && stillWaitingForTheLastDayData) {

            TIME_UNITS.sleep(TASK_STATUS_CHECK_FREQUENCY);

            allCollectionsAreEmptyAfterInitiateWait = tasks.stream()
                    .map(MongoProgressTask::isEventTimeHistoryQueueEmpty)
                    .reduce(true, (agg, e) -> agg & e);

            dataProcessingStillBeInProgress = tasks.stream()
                    .map(e -> e.shouldWaitingForNewSample(TIME_BUCKETES_TO_CHECK))
                    .reduce(false, (agg, e) -> agg | e);

            stillWaitingForTheLastDayData = !tasks.stream()
                    .map(e -> e.hasDataProcessingReachedTheFinalDay(0, ChronoUnit.DAYS))
                    .reduce(false, (agg, e) -> agg & e);
        }

        assertThat(allCollectionsAreEmptyAfterInitiateWait)
                .overridingErrorMessage("Not a single event reached any input collection after " +
                        DELAY_BEFORE_FIRST_TASK_STARTED + " minutes wait.\nAborting the job.\n")
                .isFalse();

        if (dataProcessingStillBeInProgress) {
            LOGGER.warn("Collections data processing still in progress");
        } else{
            LOGGER.info("Collections data processing has finished");
        }

        return !stillWaitingForTheLastDayData;

    }
}
