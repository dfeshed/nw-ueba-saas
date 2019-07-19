package com.rsa.netwitness.presidio.automation.utils.adapter.log_player.producers;

import fortscale.common.general.Schema;
import org.slf4j.LoggerFactory;
import org.testng.collections.Maps;
import com.rsa.netwitness.presidio.automation.utils.adapter.log_player.events.ConverterEventBase;
import com.rsa.netwitness.presidio.automation.utils.adapter.log_player.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.utils.adapter.log_player.formatters.CefFormatterImpl;
import com.rsa.netwitness.presidio.automation.utils.adapter.log_player.formatters.NetwitnessEventFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.rsa.netwitness.presidio.automation.utils.adapter.log_player.utils.Context.LOG_GEN_PATH;
import static com.rsa.netwitness.presidio.automation.utils.adapter.log_player.utils.LogPlayerResultUtils.runLogPlayerAndGetRecordsCountResult;
import static java.util.stream.Collectors.*;
class CefFilesPrinter {

    private static  ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(CefFilesPrinter.class.getName());

    public static final long GEN_START_TIME = System.currentTimeMillis();

    Map<Schema, String> logPlayerFolders = Maps.newHashMap();

    Map<Schema, Long> printDailyFiles(List<ConverterEventBase> eventsList) {
        return printFiles(eventsList, ChronoUnit.DAYS);
    }

    Map<Schema, Long>  printHourlyFiles(List<ConverterEventBase> eventsList) {
        return printFiles(eventsList, ChronoUnit.HOURS);
    }

    private Map<Schema, Long> printFiles(List<ConverterEventBase> eventsList, ChronoUnit truncatedTo) {
        logPlayerFolders.clear();

        // add destination file path
        Map<Path, List<NetwitnessEvent>> eventsByFilePath = eventsList.parallelStream()
                .map(ConverterEventBase::getAsNetwitnessEvent)
                .collect(groupingBy(e -> eventFilePath(e, truncatedTo)));

        // create destination files
        eventsByFilePath.keySet().parallelStream()
                .forEach(this::initFile);

        // flush events
        eventsByFilePath.entrySet().parallelStream()
                .forEach(e -> writeToFile(e.getKey(), e.getValue()));

        return eventsList.parallelStream().collect(groupingBy(ConverterEventBase::mongoSchema, counting()));
    }

    // send by NWLogPlayer
    Map<Schema, Long> sendToBroker() {
        return logPlayerFolders.entrySet().stream().collect(toMap(entry-> entry.getKey(),
                entity -> runLogPlayerAndGetRecordsCountResult.apply(entity.getValue())));
    }

    private void writeToFile(Path path, List<NetwitnessEvent> events) {
        try {
            NetwitnessEventFormatter<String> cefFormatter = new CefFormatterImpl();

            List<String> stringStream = events.parallelStream()
                    .map(e -> cefFormatter.format(e).concat("\n"))
                    .collect(toList());

            Path result = Files.write(path, stringStream, StandardOpenOption.APPEND);
            LOGGER.info("Created:  [" + result.toAbsolutePath() + "]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeLineByLine(NetwitnessEvent event, ChronoUnit truncatedTo) {
        try {
            CefFormatterImpl formatter = new CefFormatterImpl();
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
            Files.createFile(distinctPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Instant getEventTime(NetwitnessEvent event, ChronoUnit truncatedTo) {
        return event.getEventTimeEpoch().truncatedTo(truncatedTo);
    }

    private Path eventFilePath(NetwitnessEvent event, ChronoUnit truncatedTo) {
        String eventFolder = eventFilePath.apply(event);
        logPlayerFolders.putIfAbsent(event.mongoSchema(), eventFolder);
        String fileName = event.mongoSchema() + "_" + getEventTime(event, truncatedTo);
        return Paths.get(eventFolder + fileName);
    }

    private Function<NetwitnessEvent, String> eventFilePath = event -> LOG_GEN_PATH.toAbsolutePath().toString()
            .concat("/")
            .concat(event.mongoSchema().getName())
            .concat("_")
            .concat(String.valueOf(GEN_START_TIME))
            .concat("/");
}
