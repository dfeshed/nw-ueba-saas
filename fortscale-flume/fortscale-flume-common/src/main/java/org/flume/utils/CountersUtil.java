package org.flume.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

public class CountersUtil {

    private static Logger logger = LoggerFactory.getLogger(DateUtils.class);


    public static final String SINK_COUNTERS_FOLDER_NAME = "sink";
    public static final String SOURCE_COUNTERS_FOLDER_NAME = "source";
    public static final String HOUR_IS_READY_MARKER = "READY";

    /**
     * This method attributes the event to the relevant hour {@code time} (for hour-is-ready-detection) - meaning we will add {@code amount} to the counter
     * of events that need to be 'sinked' for this hour {@code time} (by hour ceiling).
     * i.e, all events between 2017-08-03T14:00:00Z---2017-08-03T15:00:00Z will be under 2017-08-03T15:00:00Z
     *
     * @param time                 the time of the event
     * @param schemaName           the schema of the event
     * @param canClosePreviousHour is the hour closed
     * @param amount               the amount of events that were sinked for given {@code time} and {@code schemaName}
     * @throws IOException when the there a problem with the file
     */
    public int addToSourceCounter(Instant time, String schemaName, boolean canClosePreviousHour, int amount) throws IOException {
        return addToCounter(time, schemaName, SOURCE_COUNTERS_FOLDER_NAME, canClosePreviousHour, amount);
    }

    /**
     * This method attributes several events to the relevant hour {@code time} (for hour-is-ready-detection) - meaning we will add {@code amount} to the counter
     * of events that were 'sinked' for this hour {@code time} (by hour ceiling).
     * i.e, all events between 2017-08-03T14:00:00Z---2017-08-03T15:00:00Z will be under 2017-08-03T15:00:00Z
     *
     * @param time       the time of the events
     * @param schemaName the schema of the events
     * @param amount     the amount of events that were sinked for given {@code time} and {@code schemaName}
     * @throws IOException when the there a problem with the file
     */
    public int addToSinkCounter(Instant time, String schemaName, int amount) throws IOException {
        return addToCounter(time, schemaName, SINK_COUNTERS_FOLDER_NAME, false, amount);
    }

    private int addToCounter(Instant timeDetected, String schemaName, String flumeComponentType, boolean canClosePreviousHour, int amount) throws IOException {
        final int newCount;
        FileLock lock = null;
        FileChannel channel = null;
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            /* Get the file stuff */
            File file = createFile(schemaName, flumeComponentType);
            channel = new RandomAccessFile(file, "rw").getChannel();
            lock = channel.lock(); // This method blocks until it can retrieve the lock.

            /* load existing count properties */
            Properties countProperties = new Properties();
            in = new FileInputStream(file);
            countProperties.load(in);

            /* update count properties */
            newCount = updateCountProperties(timeDetected, canClosePreviousHour, countProperties, amount);

            /* save new count properties */
            out = new FileOutputStream(file.getAbsolutePath());
            countProperties.store(out, "hour counters for schema " + schemaName);
            return newCount;
        } finally {
            if (in != null) {
                in.close();
            }

            if (out != null) {
                out.close();
            }

            if (lock != null) {
                lock.release();
            }

            if (channel != null) {
                channel.close();
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File createFile(String schemaName, String flumeComponentType) throws IOException {
        final String presidioHome = System.getenv("PRESIDIO_HOME");
        final String folderPath = presidioHome + File.separator + "flume" + File.separator + "counters" + File.separator + flumeComponentType;
        final String filePath = folderPath + File.separator + schemaName;
        if (!Files.exists(Paths.get(folderPath))) {
            try {
                Files.createDirectories(Paths.get(folderPath));
            } catch (IOException e) {
                final String warnMessage = "Can't create folder " + folderPath;
                logger.warn(warnMessage);
                throw new IOException(warnMessage);
            }
        }
        File file = new File(filePath);
        file.createNewFile();
        return file;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private int updateCountProperties(Instant timeDetected, boolean canClosePreviousHour, Properties properties, int amount) throws IllegalStateException {
        final String endOfHour = DateUtils.ceiling(timeDetected, ChronoUnit.HOURS).toString();
        String currCount = properties.getProperty(endOfHour);
        final boolean firstCountForHour = currCount == null;
        if (firstCountForHour) { // first edit for this hour
            currCount = "0";
        } else {
            if (currCount.startsWith(HOUR_IS_READY_MARKER)) { //for sanity
                final String errorMessage = "Invalid state, can't add to counter of an already done event! " + timeDetected;
                logger.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
            try {
                Integer.parseInt(currCount); //make sure the current is an int, just for sanity
            } catch (NumberFormatException e) {
                final String errorMessage = "Invalid state, counter for event must be an int! " + timeDetected;
                logger.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        }

        String newCount = String.valueOf(Integer.parseInt(currCount) + amount);
        final String toWrite = canClosePreviousHour ? HOUR_IS_READY_MARKER + "_" + newCount : newCount;
        if (firstCountForHour) {
            properties.put(endOfHour, toWrite);
        } else {
            properties.replace(endOfHour, toWrite);
        }

        return Integer.parseInt(newCount);
    }
}
