package com.rsa.netwitness.presidio.automation.common.dlpfile;

import org.slf4j.LoggerFactory;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.dlpfile.DLPFileEvent;

import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// FOR DEMO ONLY
public class Fortscale4SOCEventsInserter {
    public static final String DLPFILE_CSV = "/home/presidio/some_input_folder/DLPFILE_0000000000001.csv";

    static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(Fortscale4SOCEventsInserter.class.getName());

    public void insert(List<? extends Event> evList) {
       try {
           FileWriter writer = new FileWriter(DLPFILE_CSV);

           List<String> dlpFile = new ArrayList<>();
           for (int i = 0; i < evList.size(); i++) {
               dlpFile.add(toCSV31((DLPFileEvent) evList.get(i)));
           }

           String collect = dlpFile.stream().collect(Collectors.joining("\n"));

           writer.write(collect);
           writer.close();

       } catch (IOException e) {
            LOGGER.error("Exception occured in Insert: " + e.getMessage());
       }
    }

    private String toCSV31(DLPFileEvent ev) {
        final DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"));

        return ",," +
                // For 3.1 date should be in format: 2017-06-30T10:29:00Z
                formatter.format(ev.getDateTime()) + ',' +
                ev.getExecutingApplication() + ',' +
                ev.getSrcMachine() + ',' +
                ",,,,,," +
                ev.getFirstName() + ',' +
                ev.getLastName() + ',' +
                "," +
                ev.getUsername() + ',' +
                ",,,,,,,," +
                ev.getMalwareScanResult() + ',' +
                ",,,,,,,," +
                ev.getEventId() + ',' +
                ev.getSourceIp() + ',' +
                ",,,," +
                ev.getEventType() + ',' +
                ",,," +
                ev.getWasBlocked() + ',' +
                ev.getWasClassified() + ',' +
                ",,,,,,,,,,,,,,,,,,,,,,,,," +
                ev.getDestinationPath() + ',' +
                ev.getDestinationFileName() + ',' +
                ",,," +
                ev.getFileSize() + ',' +
                ",,,,,,," +
                ev.getSourcePath() + ',' +
                ev.getSourceFileName() + ',' +
                ",,,,,,,,,,," +
                ev.getSourceDriveType() + ',' +
                "," +
                ev.getDestinationDriveType() +
                ",,,,,,,";
    }

}
