package org.flume.utils;


import fortscale.common.general.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class CountersUtil {

    private static Logger logger = LoggerFactory.getLogger(DateUtils.class);

    private static final Object sourceLock = new Object();
    private static final Object sinkLock = new Object();

    private long propertyTimeout;

    public static final String SINK_COUNTERS_FOLDER_NAME = "sink";
    public static final String SOURCE_COUNTERS_FOLDER_NAME = "source";
    public static final String HOUR_IS_READY_MARKER = "READY";

    public CountersUtil() {
        propertyTimeout = 7L * 24 * 60 * 60 * 1000; //1 week
    }


    /**
     * @param propertyTimeout if propertyTimeout>0 then properties older than propertyTimeout will ve deleted.
     *                        propertyTimeout<=0 means no timeout.
     *                        in Milliseconds
     */
    public CountersUtil(long propertyTimeout) {
        this.propertyTimeout = propertyTimeout;
    }

    /**
     * This method attributes the event to the relevant hour {@code time} (for hour-is-ready-detection) - meaning we will add {@code amount} to the counter
     * of events that need to be 'sinked' for this hour {@code time} (by hour ceiling).
     * i.e, all events between 2017-08-03T14:00:00Z---2017-08-03T15:00:00Z will be under 2017-08-03T15:00:00Z
     *
     * @param time                 the time of the event
     * @param schema               the {@link Schema} of the event
     * @param canClosePreviousHour is the hour closed
     * @param amount               the amount of events that were sinked for given {@code time} and {@code schema}
     * @throws IOException when the there a problem with the file
     */
    public int addToSourceCounter(Instant time, Schema schema, boolean canClosePreviousHour, int amount) throws IOException {
        synchronized (sourceLock) {
            return addToCounter(time, schema, SOURCE_COUNTERS_FOLDER_NAME, canClosePreviousHour, amount);
        }
    }

    /**
     * This method attributes several events to the relevant hour {@code time} (for hour-is-ready-detection) - meaning we will add {@code amount} to the counter
     * of events that were 'sinked' for this hour {@code time} (by hour ceiling).
     * i.e, all events between 2017-08-03T14:00:00Z---2017-08-03T15:00:00Z will be under 2017-08-03T15:00:00Z
     *
     * @param time   the time of the events
     * @param schema the {@link Schema} of the event
     * @param amount the amount of events that were sinked for given {@code time} and {@code schema}
     * @throws IOException when the there a problem with the file
     */
    public int addToSinkCounter(Instant time, Schema schema, int amount) throws IOException {
        synchronized (sinkLock) {
            return addToCounter(time, schema, SINK_COUNTERS_FOLDER_NAME, false, amount);
        }
    }

    private int addToCounter(Instant timeDetected, Schema schema, String flumeComponentType, boolean canClosePreviousHour, int amount) throws IOException {
        final int newCount;
        FileLock lock = null;
        FileChannel channel = null;
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            /* Get the file stuff */
            File file = createFile(schema, flumeComponentType);
            final String filePath = file.getAbsolutePath();
            channel = new RandomAccessFile(file, "rw").getChannel();
            lock = channel.lock(); // This method blocks until it can retrieve the lock.

            /* load existing count properties */
            Properties countProperties = new OrderedProperties<>(String.class);
            in = new FileInputStream(file);
            countProperties.load(in);

            /* update count properties */
            newCount = updateCountProperties(timeDetected, canClosePreviousHour, countProperties, amount);

            if (propertyTimeout > 0) {
                countProperties = removeTimedOutProperties(flumeComponentType, countProperties);
            }

            /* save new count properties */
            out = new FileOutputStream(filePath);
            countProperties.store(out, "hour counters for schema " + schema.getName());
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

    private Properties removeTimedOutProperties(String flumeComponentType, Properties countProperties) {
        for (Object key : countProperties.keySet()) {
            final Instant propertyAsTime;
            final String propertyAsString;
            try {
                propertyAsString = (String) key;
                propertyAsTime = Instant.parse(propertyAsString);
            } catch (Exception e) {
                logger.warn("Invalid property {}. This is an invalid state but the system can keep working."); //should not happen
                return countProperties;
            }
            if (propertyAsTime.isBefore(Instant.now().minus(propertyTimeout, ChronoUnit.MILLIS))) {
                logger.info("Remove timed out hour {} from {} counter file. timeout: {}", propertyAsString, flumeComponentType, propertyTimeout);
                countProperties.remove(propertyAsString);
            }
        }

        return countProperties;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File createFile(Schema schema, String flumeComponentType) throws IOException {
        final String presidioHome = System.getenv("PRESIDIO_HOME");
        final String folderPath = presidioHome + File.separator + "flume" + File.separator + "counters" + File.separator + flumeComponentType;
        final String filePath = folderPath + File.separator + schema.getName();
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

    /**
     * This class is a {@link Properties} implementation that reads and writes in a sorted manner (according to the <i>natural ordering</i> of its
     * elements).
     */
    @SuppressWarnings("unused")
    private static class OrderedProperties<T extends Comparable> extends Properties {

        private final Class<T> keyType; //in order to keep the key type a comparable (POLA principle)

        private OrderedProperties(Class<T> keyType) {
            this.keyType = keyType;
        }

        @Override
        @SuppressWarnings("NullableProblems") //due to intellij bug
        public Set<Object> keySet() {
            return Collections.unmodifiableSet(new TreeSet<>(super.keySet()));
        }

        @Override
        public synchronized Enumeration<Object> keys() {
            return Collections.enumeration(new TreeSet<>(super.keySet()));
        }
    }
}
