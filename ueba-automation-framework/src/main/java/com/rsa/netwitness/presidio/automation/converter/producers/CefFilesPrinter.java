package com.rsa.netwitness.presidio.automation.converter.producers;

import com.rsa.netwitness.presidio.automation.converter.events.ConverterEventBase;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.formatters.CefFormatterImpl;
import com.rsa.netwitness.presidio.automation.converter.formatters.NetwitnessEventFormatter;
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
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.rsa.netwitness.presidio.automation.context.AutomationConf.LOG_GEN_OUTPUT;
import static com.rsa.netwitness.presidio.automation.log_player.LogPlayerResultUtils.runLogPlayerAndGetRecordsCountResult;
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
            LOGGER.debug("Created:  [" + result.toAbsolutePath() + "]");
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
        String eventFolder = eventFilePath.apply(event).toString();
        logPlayerFolders.putIfAbsent(event.mongoSchema(), eventFolder);
        String fileName = event.mongoSchema() + "_" + instantToString(getEventTime(event, truncatedTo));
        return Paths.get(eventFolder,fileName.concat(".cef"));
    }

    private String instantToString(Instant instant){
        return instant.toString().replaceAll(":","_");
    }

    private Function<NetwitnessEvent, String> eventFolderName = event ->
            event.mongoSchema().getName().concat("_").concat(instantToString(Instant.ofEpochMilli(GEN_START_TIME)));

    private Function<NetwitnessEvent, Path> eventFilePath = event ->
            Paths.get(LOG_GEN_OUTPUT.toAbsolutePath().toString(), eventFolderName.apply(event)).toAbsolutePath();
}


