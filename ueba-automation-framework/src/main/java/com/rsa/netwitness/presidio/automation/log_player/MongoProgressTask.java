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
    private static  ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
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
        Optional<Instant> result = findByTimeAppliedOnObj(obj,start,end);
        LOGGER.info("#### Query result for ["+ collectionName + "] == " + result);

        if (result.isPresent() && result.get().isAfter(start)) start = result.get().minusSeconds(10);
        result.ifPresent(e -> eventTimeHistory.push(e));
        result.ifPresent(e -> lastEventTime = e);
    }


    boolean dataExistAtLeastInOneBucket(int bucketsToCheck) {
        LOGGER.info("---> [" +collectionName + "] eventTimeHistory array: " + eventTimeHistory);

        if (eventTimeHistory.isEmpty()) {
            LOGGER.error("[" +collectionName + "] Collection is empty after initial wait");
            return false;
        }

        if (eventTimeHistory.size() <  bucketsToCheck) return true;

        eventTimeHistory = new LinkedList<>(eventTimeHistory.subList(0, bucketsToCheck));

        boolean result = false;
        Iterator<Instant> iterator = eventTimeHistory.iterator();
        Iterator<Instant> nextIterator = eventTimeHistory.iterator();
        if (iterator.hasNext()) nextIterator.next();

        while (iterator.hasNext() && nextIterator.hasNext()) {
            boolean currentBiggerThenPrevious = iterator.next().isAfter(nextIterator.next());
            result |= currentBiggerThenPrevious;
        }

        if (result) {
            LOGGER.debug("[" +collectionName + "]: result " + result);
        } else {
            LOGGER.warn("[" +collectionName + "]: no updates for the last " + (bucketsToCheck-1) + " checks.");
        }
        return result;
    }

    boolean dataFromTheLastDayArrived(long amountToSubtract, ChronoUnit units) {
        Instant stopTime = end.minus(amountToSubtract, units).truncatedTo(units);
        if (lastEventTime == null) return false;

        boolean result = lastEventTime.isAfter(stopTime);

        if (result) {
            LOGGER.info(" *** Collection [" + collectionName + "]: processing reached the final day=" + stopTime);
        } else {
            LOGGER.info("["+collectionName+"]:"+ " data processing still in progress. stopTime="+stopTime+" lastEventTime="+ lastEventTime);
        }
        return result;
    }

    private Optional<Instant> findByTimeAppliedOnObj(MongoRepository obj, Instant start, Instant end) {
        Sort sort = new Sort(Sort.Direction.DESC, "dateTime");

        if (obj instanceof AdapterActiveDirectoryStoredDataRepository) {
            collectionName = "AdapterActiveDirectory";
            LOGGER.debug("[" + collectionName + "] - Going to execute query: start = " + start + " end = " + end);
            AdapterActiveDirectoryStoredData result = ((AdapterActiveDirectoryStoredDataRepository) obj).findTopByDateTimeBetween(start, end, sort);
            if (result == null) return Optional.empty();
                else return Optional.of(result.getDateTime());
        }
        if (obj instanceof AdapterAuthenticationStoredDataRepository) {
            collectionName = "AdapterAuthentication";
            LOGGER.debug("[" + collectionName + "] - Going to execute query: start = " + start + " end = " + end);
            AdapterAuthenticationStoredData result = ((AdapterAuthenticationStoredDataRepository) obj).findTopByDateTimeBetween(start, end, sort);
            if (result == null) return Optional.empty();
                else return Optional.of(result.getDateTime());
        }
        if (obj instanceof AdapterFileStoredDataRepository) {
            collectionName = "AdapterFile";
            LOGGER.debug("[" + collectionName + "] - Going to execute query: start = " + start + " end = " + end);
            AdapterFileStoredData result = ((AdapterFileStoredDataRepository) obj).findTopByDateTimeBetween(start, end, sort);
            if (result == null) return Optional.empty();
                else return Optional.of(result.getDateTime());
        }
        if (obj instanceof AdapterRegistryStoredDataRepository){
            collectionName = "AdapterRegistry";
            LOGGER.debug("[" + collectionName + "] - Going to execute query: start = " + start + " end = " + end);
            AdapterRegistryStoredData result = ((AdapterRegistryStoredDataRepository) obj).findTopByDateTimeBetween(start, end, sort);
            if (result == null) return Optional.empty();
                else return Optional.of(result.getDateTime());
        }
        if (obj instanceof AdapterTlsStoredDataRepository){
            collectionName = "TlsRegistry";
            LOGGER.debug("[" + collectionName + "] - Going to execute query: start = " + start + " end = " + end);
            AdapterTlsStoredData result = ((AdapterTlsStoredDataRepository) obj).findTopByDateTimeBetween(start, end, sort);
            if (result == null) return Optional.empty();
            else return Optional.of(result.getDateTime());
        }

        LOGGER.error("No collection name mapping found for AdapterRepository class: " + obj.getClass().getTypeName());
        return Optional.empty();
    }
}