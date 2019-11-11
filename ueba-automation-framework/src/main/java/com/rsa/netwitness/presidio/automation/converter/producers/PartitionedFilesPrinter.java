package com.rsa.netwitness.presidio.automation.converter.producers;

import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.formatters.EventFormatter;
import fortscale.common.general.Schema;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.rsa.netwitness.presidio.automation.config.AutomationConf.LOG_GEN_OUTPUT;
import static java.util.stream.Collectors.*;

class PartitionedFilesPrinter {
    private static final int MAX_CONCURRENT = 4;
    private static AtomicInteger partitionsCount = new AtomicInteger(0);
    private static final int DEFAULT_PARTITION_SIZE = 250000;
    private final int PARTITION_SIZE;

    private static  ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(PartitionedFilesPrinter.class.getName());
    private static final long GEN_START_TIME = System.currentTimeMillis();
    private Map<Schema, String> logPlayerFolders = Maps.newHashMap();
    private EventFormatter<String> formatter;

    PartitionedFilesPrinter(EventFormatter<String> formatter){
        this(formatter, DEFAULT_PARTITION_SIZE);
    }

    PartitionedFilesPrinter(EventFormatter<String> formatter, int partitionSize){
        this.formatter = Objects.requireNonNull(formatter);
        this.PARTITION_SIZE = partitionSize;
    }


    public Map<Schema, Long> printFiles(Stream<NetwitnessEvent> eventsList) {
        Map<Schema, Long> totalEventsPerSchema = new HashMap<>();
        logPlayerFolders.clear();

        UnmodifiableIterator<List<NetwitnessEvent>> partition = Iterators.partition(eventsList.iterator(), PARTITION_SIZE);

        while (partition.hasNext()) {
            List<NetwitnessEvent> nextPartition = partition.next();

            // add destination file path
            Map<Path, List<NetwitnessEvent>> eventsByFilePath = nextPartition.parallelStream()
                    .collect(groupingBy(e -> eventFilePath(e, partitionsCount.get())));

            partitionsCount.incrementAndGet();
            // create destination files
            eventsByFilePath.keySet().parallelStream()
                    .forEach(this::initFile);

            // flush events
            eventsByFilePath.entrySet().parallelStream()
                    .forEach(e -> writeToFile(e.getKey(), e.getValue()));

            Map<Schema, Long> result = nextPartition.parallelStream().collect(groupingBy(ev -> ev.schema, counting()));

            result.forEach(totalEventsPerSchema::putIfAbsent);
            result.forEach((key1, value) -> totalEventsPerSchema.computeIfPresent(key1, (key, val) -> val + value));
        }

        return totalEventsPerSchema;
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

    private Path eventFilePath(NetwitnessEvent event, int partitionsCount) {
        String eventFolder = eventFilePath.apply(event).toString();
        logPlayerFolders.putIfAbsent(event.schema, eventFolder);
        String fileName = event.schema + "_" + instantToString(getEventTime(event, ChronoUnit.DAYS)) + "_" + partitionsCount;
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


