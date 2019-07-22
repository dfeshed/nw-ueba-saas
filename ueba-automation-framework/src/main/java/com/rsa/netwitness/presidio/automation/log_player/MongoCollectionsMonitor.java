package com.rsa.netwitness.presidio.automation.log_player;

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

public class MongoCollectionsMonitor {

    private List<MongoRepository> collectiontToMonitor = new LinkedList<>();
    private ScheduledExecutorService scheduler;
    private int corePoolSize = 2;
    private long DELAY_BETWEEN_TASKS = 1;
    private long DELAY_BEFORE_FIRST_TASK_STARTED = 15;
    private long TASK_FREQUENCY_MINUTES = 15;
    private long ADDITIONAL_DELAY_BEFORE_FIRST_TIME_STATUS_CHECK = 5;
    private long TASK_STATUS_CHECK_FREQUENCY = TASK_FREQUENCY_MINUTES;
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
                .map(e ->  scheduler.scheduleAtFixedRate(e, DELAY_BEFORE_FIRST_TASK_STARTED, TASK_FREQUENCY_MINUTES, TIME_UNITS))
                .collect(Collectors.toList());
    }

    public void shutdown() {
        scheduler.shutdown();
    }


    public boolean waitForResult() throws InterruptedException {

        boolean stillWaitingForTheLastDayData = true;
        boolean dataExistAtLeastInOneBucket = true;
        TIME_UNITS.sleep(ADDITIONAL_DELAY_BEFORE_FIRST_TIME_STATUS_CHECK);

        while (dataExistAtLeastInOneBucket && stillWaitingForTheLastDayData) {

            TIME_UNITS.sleep(TASK_STATUS_CHECK_FREQUENCY);

            dataExistAtLeastInOneBucket = tasks.stream()
                    .map(e -> e.dataExistAtLeastInOneBucket(TIME_BUCKETES_TO_CHECK))
                    .reduce(false, (agg,e) -> agg | e);

            stillWaitingForTheLastDayData = !tasks.stream()
                    .map(e -> e.dataFromTheLastDayArrived(1, ChronoUnit.DAYS))
                    .reduce(false, (agg,e) -> agg & e);
        }

        return !stillWaitingForTheLastDayData;

    }
}
