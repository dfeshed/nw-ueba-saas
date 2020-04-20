package com.rsa.netwitness.presidio.automation.converter.producers;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.formatters.EventFormatter;
import fortscale.common.general.Schema;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.LoggerFactory;
import org.testng.collections.Maps;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.rsa.netwitness.presidio.automation.config.AutomationConf.LOG_GEN_OUTPUT;
import static com.rsa.netwitness.presidio.automation.data.processing.broker.LogPlayerHelper.runLogPlayerAndGetRecordsCountResult;
import static java.util.stream.Collectors.*;

class CefFilesPrinter {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(CefFilesPrinter.class);

    private static final long GEN_START_TIME = System.currentTimeMillis();
    private Map<Schema, String> logPlayerFolders = Maps.newHashMap();
    private EventFormatter<NetwitnessEvent, String> formatter;
    private static final int EVENTS_CHUNK = 50000;

    CefFilesPrinter(EventFormatter<NetwitnessEvent, String> formatter){
        this.formatter = Objects.requireNonNull(formatter);
    }


    Map<Schema, Long> printDailyFiles(Stream<NetwitnessEvent> eventsList) {
        return printFiles(eventsList, ChronoUnit.DAYS);
    }

    Map<Schema, Long>  printHourlyFiles(Stream<NetwitnessEvent> eventsList) {
        return printFiles(eventsList, ChronoUnit.HOURS);
    }

    private Map<Schema, Long> printFiles(Stream<NetwitnessEvent> eventsList, ChronoUnit truncatedTo) {
        StopWatch tlsStopWatch = new StopWatch();
        UnmodifiableIterator<List<NetwitnessEvent>> partition = Iterators.partition(eventsList.iterator(), EVENTS_CHUNK);
        Map<Schema, Long> totalResult = new HashMap<>();
        tlsStopWatch.start();

        while (partition.hasNext()) {
            LOGGER.debug("Going to collect next bucket");
            List<NetwitnessEvent> nextBucket = partition.next();
            LOGGER.debug("Next bucket is collected");

            // add destination file path
            LOGGER.debug("Going to group bucket by schema and add file path");
            Map<Path, List<NetwitnessEvent>> eventsByFilePath = nextBucket.parallelStream()
                    .collect(groupingBy(e -> eventFilePath(e, truncatedTo)));
            LOGGER.debug("Done grouping bucket by schema and add file path");

            // create destination files
            eventsByFilePath.keySet().parallelStream().forEach(this::initFile);

            // flush events
            LOGGER.debug("Going to insert " + nextBucket.size() + " events");
            eventsByFilePath.entrySet().parallelStream().forEach(e -> writeToFile(e.getKey(), e.getValue()));
            LOGGER.debug("Finished to insert " + nextBucket.size() + " events");

            Map<Schema, Long> update = nextBucket.parallelStream().collect(groupingBy(ev -> ev.schema, counting()));
            update.forEach((key, value) -> totalResult.compute(key, (k, v) -> totalResult.getOrDefault(key, 0L) + value));

            tlsStopWatch.split();
            if (Instant.ofEpochMilli(tlsStopWatch.getSplitTime()).minusSeconds(30).toEpochMilli() > 0) {
                LOGGER.info("  >>>>>>> Intermediate result <<<<<<<");
                totalResult.forEach((k, v) -> System.out.println(k + ": " + v));
                LOGGER.info("  >>>>>>>>>>>>>>>>  <<<<<<<<<<<<<<<<<");
                tlsStopWatch.reset();
                tlsStopWatch.start();
            }
        }

        return totalResult;
    }

    // send by NWLogPlayer
    Map<Schema, Long> sendToBroker() {
        return logPlayerFolders.entrySet().stream().collect(toMap(Map.Entry::getKey,
                entity -> runLogPlayerAndGetRecordsCountResult.apply(entity.getValue())));
    }

    private void writeToFile(Path path, List<NetwitnessEvent> events) {
        try {
            List<String> stringStream = events.parallelStream()
                    .map(e -> formatter.format(e).concat("\n"))
                    .collect(toList());

            Path result = Files.write(path, stringStream, StandardOpenOption.APPEND);
            LOGGER.debug("Created:  [" + result.toAbsolutePath() + "]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeLineByLine(NetwitnessEvent event, ChronoUnit truncatedTo) {
        try {
            Path path = eventFilePath(event, truncatedTo);
            Files.write(path, formatter.format(event).concat("\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initFile(Path distinctPath) {
        try {
            if (!distinctPath.getParent().toFile().exists())
                Files.createDirectories(distinctPath.getParent());
            if (!distinctPath.toFile().exists()) {
                Files.createFile(distinctPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Instant getEventTime(NetwitnessEvent event, ChronoUnit truncatedTo) {
        return event.eventTimeEpoch.truncatedTo(truncatedTo);
    }

    private Path eventFilePath(NetwitnessEvent event, ChronoUnit truncatedTo) {
        String eventFolder = eventFilePath.apply(event).toString();
        logPlayerFolders.putIfAbsent(event.schema, eventFolder);
        String fileName = event.schema + "_" + instantToString(getEventTime(event, truncatedTo));
        return Paths.get(eventFolder,fileName.concat(".cef"));
    }

    private String instantToString(Instant instant){
        return instant.toString().replaceAll(":","_");
    }

    private Function<NetwitnessEvent, String> eventFolderName = event ->
            event.schema.getName().concat("_").concat(instantToString(Instant.ofEpochMilli(GEN_START_TIME)));

    private Function<NetwitnessEvent, Path> eventFilePath = event ->
            Paths.get(LOG_GEN_OUTPUT.toAbsolutePath().toString(), eventFolderName.apply(event)).toAbsolutePath();
}


