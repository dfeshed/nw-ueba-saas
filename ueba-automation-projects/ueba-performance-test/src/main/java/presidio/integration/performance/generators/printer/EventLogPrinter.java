package presidio.integration.performance.generators.printer;

import presidio.data.domain.event.Event;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

/**
 * Prints scenario events into files in log decoder format.
 * Two public methods available:
 *  - print to files by day (for output indicators scenario)
 *  - print to hourly files (for performance scenario)
 *
 * **/
public abstract class EventLogPrinter {
    PrintWriter writer = null;
    String currentDir = "";
    String currentFile = "";
    Instant currentFileHour = null;
    Instant nextFileHour = null;
    String logDecoderIp = null;

    String schema;
    String logsPath;

    public DateTimeFormatter dateShortFormatter = DateTimeFormatter.ofPattern("MMM dd HH:mm:ss").withLocale( Locale.getDefault() ).withZone( ZoneId.of("UTC"));
    public DateTimeFormatter dateLongFormatter = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss yyyy").withLocale( Locale.getDefault() ).withZone( ZoneId.of("UTC"));

    abstract void print(Event event, PrintWriter writer);



    public void printHourlyFiles(List<? extends Event> events) {
        /** printDailyFiles events to files in chunks of generator, and split by hour also **/
        if (events == null || events.size() == 0) return;

        try {
            for (Event event : events) {
                if (event == null) continue;
                PrintWriter writer = getHourlyPrintWriter(event.getDateTime());
                print(event, writer);
            }
            System.out.println(events.size() + " events where written to file: " + currentFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            // reset state at end of chunk
            writer.close();
            writer = null;
            currentFile = "";
            currentFileHour = null;
            nextFileHour = null;
        }
    }

    PrintWriter getHourlyPrintWriter(Instant dateTime) throws FileNotFoundException, UnsupportedEncodingException {
        // while in current hour - printDailyFiles to same current file
        if (dateTime.truncatedTo(ChronoUnit.HOURS).equals(currentFileHour)) return writer;

        // Switch directory and file:
        // For new day - create new directory
        String dirName = logsPath + dateTime.truncatedTo(ChronoUnit.DAYS);
        if (!currentDir.equalsIgnoreCase(dirName)) {
            // TODO: load logs directory to decoder
            currentDir = dirName;

            File directory = new File(currentDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }

        // For new hour - create new file
        if (currentFileHour == null || !dateTime.isBefore(nextFileHour)) {
            currentFileHour = dateTime.truncatedTo(ChronoUnit.HOURS);
            nextFileHour = currentFileHour.plus(1, ChronoUnit.HOURS);

            // name file using time of current event
            currentFile = currentDir + "/" + schema + "_" + dateTime;

            // reopen writer for new file
            if (writer != null) writer.close();
            writer = new PrintWriter(currentFile, "UTF-8");
        }

        return writer;
    }

    public StringBuilder buildCommonPart(String referenceId, Instant eventTime, String computer, String user, String result) {
        String datetimeShort = dateShortFormatter.format(eventTime);         //Jan 01 00:00:00
        String datetimeLong  = dateLongFormatter.format(eventTime);          //Tue Jan 01 00:00:00 2019

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(datetimeShort);
        stringBuilder.append(" ");
        stringBuilder.append(computer);
        stringBuilder.append(" MSWinEventLog,1,Security,28346715,");
        stringBuilder.append(datetimeLong);
        stringBuilder.append(",");
        stringBuilder.append(referenceId);
        stringBuilder.append(",Microsoft-Windows-Security-Auditing,RSA\\");
        stringBuilder.append(user);
        stringBuilder.append(",N/A,");
        stringBuilder.append(result);
        stringBuilder.append(" Audit,");
        stringBuilder.append(computer);
        return stringBuilder;
    }

    public void setLogDecoderIp(String logDecoderIp) {
        this.logDecoderIp = logDecoderIp;
    }
}
