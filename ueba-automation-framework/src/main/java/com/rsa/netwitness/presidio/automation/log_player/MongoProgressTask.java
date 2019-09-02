package com.rsa.netwitness.presidio.automation.log_player;


import com.rsa.netwitness.presidio.automation.domain.activedirectory.AdapterActiveDirectoryStoredData;
import com.rsa.netwitness.presidio.automation.domain.authentication.AdapterAuthenticationStoredData;
import com.rsa.netwitness.presidio.automation.domain.file.AdapterFileStoredData;
import com.rsa.netwitness.presidio.automation.domain.process.AdapterRegistryStoredData;
import com.rsa.netwitness.presidio.automation.domain.repository.*;
import com.rsa.netwitness.presidio.automation.domain.tls.AdapterTlsStoredData;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;

public class MongoProgressTask implements Runnable {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(MongoProgressTask.class.getName());

    private String collectionName;
    private MongoRepository obj;
    private Instant start;
    private final Instant end;
    private Instant lastEventTime;
    private LinkedList<Instant> eventTimeHistory = new LinkedList<>();

    MongoProgressTask(MongoRepository obj, Instant start, Instant end) {
        LOGGER.info("New task created. start = " + start + " end = " + end);
        this.obj = obj;
        this.start = start;
        this.end = end;
    }

    public void run() {
        Optional<Instant> result = findByTimeAppliedOnObj(obj, start, end);
        LOGGER.info("#### [" + collectionName + "] - Query result from start_time=" + start + " is " + result);

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
        LOGGER.info("[" + collectionName + "] - Last samples: " + eventTimeHistory);

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


    private Optional<Instant> findByTimeAppliedOnObj(MongoRepository obj, Instant start, Instant end) {
        Sort sort = new Sort(Sort.Direction.DESC, "dateTime");

        if (obj instanceof AdapterActiveDirectoryStoredDataRepository) {
            collectionName = "ActiveDirectory";
            LOGGER.debug("[" + collectionName + "] - Going to execute query: start = " + start + " end = " + end);
            AdapterActiveDirectoryStoredData result = ((AdapterActiveDirectoryStoredDataRepository) obj).findTopByDateTimeBetween(start, end, sort);
            if (result == null) return Optional.empty();
            else return Optional.of(result.getDateTime());
        }
        if (obj instanceof AdapterAuthenticationStoredDataRepository) {
            collectionName = "Authentication";
            LOGGER.debug("[" + collectionName + "] - Going to execute query: start = " + start + " end = " + end);
            AdapterAuthenticationStoredData result = ((AdapterAuthenticationStoredDataRepository) obj).findTopByDateTimeBetween(start, end, sort);
            if (result == null) return Optional.empty();
            else return Optional.of(result.getDateTime());
        }
        if (obj instanceof AdapterFileStoredDataRepository) {
            collectionName = "File";
            LOGGER.debug("[" + collectionName + "] - Going to execute query: start = " + start + " end = " + end);
            AdapterFileStoredData result = ((AdapterFileStoredDataRepository) obj).findTopByDateTimeBetween(start, end, sort);
            if (result == null) return Optional.empty();
            else return Optional.of(result.getDateTime());
        }
        if (obj instanceof AdapterRegistryStoredDataRepository) {
            collectionName = "Registry";
            LOGGER.debug("[" + collectionName + "] - Going to execute query: start = " + start + " end = " + end);
            AdapterRegistryStoredData result = ((AdapterRegistryStoredDataRepository) obj).findTopByDateTimeBetween(start, end, sort);
            if (result == null) return Optional.empty();
            else return Optional.of(result.getDateTime());
        }
        if (obj instanceof AdapterTlsStoredDataRepository) {
            collectionName = "TLS";
            LOGGER.debug("[" + collectionName + "] - Going to execute query: start = " + start + " end = " + end);
            AdapterTlsStoredData result = ((AdapterTlsStoredDataRepository) obj).findTopByDateTimeBetween(start, end, sort);
            if (result == null) return Optional.empty();
            else return Optional.of(result.getDateTime());
        }

        LOGGER.error("No collection name mapping found for AdapterRepository class: " + obj);
        return Optional.empty();
    }
}