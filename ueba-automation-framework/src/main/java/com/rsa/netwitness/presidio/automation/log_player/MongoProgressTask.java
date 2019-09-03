package com.rsa.netwitness.presidio.automation.log_player;


import com.rsa.netwitness.presidio.automation.domain.repository.AdapterAbstractStoredDataRepository;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;

public class MongoProgressTask implements Runnable {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(MongoProgressTask.class.getName());

    private final String collectionName;
    private final AdapterAbstractStoredDataRepository dataRepo;
    private final Instant end;
    private Instant start;
    private Instant lastEventTime;
    private LinkedList<Instant> eventTimeHistory = new LinkedList<>();

    MongoProgressTask(AdapterAbstractStoredDataRepository dataRepo, Instant start, Instant end) {
        this.collectionName = dataRepo.getName();
        LOGGER.info("[" + collectionName + "] - New task created. start = " + start + " end = " + end);
        this.dataRepo = dataRepo;
        this.start = start;
        this.end = end;
    }

    public void run() {
        Optional<Instant> result = Optional.ofNullable(dataRepo.maxDateTimeBetween(start, end));
        LOGGER.info("[" + collectionName + "] - Max dateTime = " + result + " (start_time=" + start + ")");

        if (result.isPresent() && result.get().isAfter(start)) start = result.get().minusSeconds(10);
        result.ifPresent(e -> eventTimeHistory.push(e));
        result.ifPresent(e -> lastEventTime = e);
    }


    boolean isProcessingStarted() {
        boolean isStarted = !eventTimeHistory.isEmpty();
        if (!isStarted) {
            LOGGER.warn("[" + collectionName + "] - Collection still empty after initial wait.");
        }
        return isStarted;
    }


    boolean isProcessingStillInProgress(int bucketsToCheck) {
        LOGGER.info("[" + collectionName + "] - dateTime history: " + eventTimeHistory);

        if (eventTimeHistory.isEmpty()) return false;

        // Queue size is not enough for decision
        if (eventTimeHistory.size() < bucketsToCheck) return true;

        Instant pollLast = eventTimeHistory.pollLast();
        LOGGER.debug("[" + collectionName + "] - Removed sample: " + pollLast);

        boolean hasANewSample = isQueueContainsA_OneNewSample();

        if (hasANewSample) {
            LOGGER.debug("[" + collectionName + "] - New sample was detected.");
        } else {
            LOGGER.warn("[" + collectionName + "] - No new samples from " + eventTimeHistory.getLast() + " to " + eventTimeHistory.getFirst());
        }
        return hasANewSample;
    }

    private boolean isQueueContainsA_OneNewSample() {
        boolean result = false;
        Iterator<Instant> iterator = eventTimeHistory.iterator();
        Iterator<Instant> nextIterator = eventTimeHistory.iterator();
        if (iterator.hasNext()) nextIterator.next();

        while (iterator.hasNext() && nextIterator.hasNext()) {
            boolean currentBiggerThenPrevious = iterator.next().isAfter(nextIterator.next());
            result |= currentBiggerThenPrevious;
        }
        return result;
    }

    boolean isFinalDaySampleExist(long timeFromJobEndTime, ChronoUnit units) {
        Instant finalDay = end.minus(timeFromJobEndTime, units).truncatedTo(units);
        if (lastEventTime == null) {
            LOGGER.error("[" + collectionName + "] - Collection has no samples.");
            return false;
        }

        boolean result = lastEventTime.isAfter(finalDay);

        if (result) {
            LOGGER.info("[" + collectionName + "] - Processing reached the final day=" + finalDay);
        } else {
            LOGGER.error("[" + collectionName + "] - Last sample arrived on " + lastEventTime + " which is before the finalDay=" + finalDay);
            LOGGER.error("[" + collectionName + "] - eventTimeHistory=" + eventTimeHistory);
        }
        return result;
    }
}