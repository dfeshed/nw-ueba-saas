package org.flume.utils;


import fortscale.common.general.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class CountersUtil {

    private static Logger logger = LoggerFactory.getLogger(DateUtils.class);


    public static final String LATEST_READY_HOUR_MARKER = "LATEST_READY_HOUR";
    public static final String SINK_COUNTERS_FOLDER_NAME = "sink";
    public static final String SOURCE_COUNTERS_FOLDER_NAME = "source";

    private static final Object sourceLock = new Object();
    private static final Object sinkLock = new Object();
    private long propertyTimeout;

    public CountersUtil() {
        propertyTimeout = 12L * 30 * 24 * 60 * 60 * 1000; //1 year
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
     * @param time            the time of the event
     * @param schema          the {@link Schema} of the event
     * @param latestReadyHour the latest hour ready
     * @param amount          the amount of events that were sinked for given {@code time} and {@code schema}
     * @throws IOException when the there a problem with the file
     */
    public int addToSourceCounter(Instant time, Schema schema, Instant latestReadyHour, int amount) throws IOException {
        synchronized (sourceLock) {
            return addToCounter(time, schema, SOURCE_COUNTERS_FOLDER_NAME, latestReadyHour, amount);
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
            return addToCounter(time, schema, SINK_COUNTERS_FOLDER_NAME, null, amount);
        }
    }


    /**
     * This method get the latest ready hour for {@link Schema} name {@code schemaName}
     *
     * @param schemaName the name of the {@link Schema} whose latest ready hour we return
     * @return latest ready hour for {@code schemaName}, null if it doesn't exist or we failed to parse it
     * @throws IOException
     */
    public Instant getLatestReadyHour(String schemaName) throws IOException {
        synchronized (sourceLock) {
            FileLock lock = null;
            FileChannel channel = null;
            FileInputStream in = null;
            try {
                /* Get the file stuff */
                File file = createFile(schemaName, SOURCE_COUNTERS_FOLDER_NAME);
                channel = new RandomAccessFile(file, "rw").getChannel();
                lock = channel.lock(); // This method blocks until it can retrieve the lock.

                /* load existing count properties */
                Properties countProperties = new OrderedProperties<>(String.class);
                in = new FileInputStream(file);
                countProperties.load(in);

                /* get latest ready hour properties */
                final String latestReadyHourProperty = countProperties.getProperty(LATEST_READY_HOUR_MARKER);
                Instant latestReadyHourPropertyAsInstant = null;
                try {
                    latestReadyHourPropertyAsInstant = Instant.parse(latestReadyHourProperty);
                } catch (Exception e) {
                    logger.warn("Can't get latest ready hour for schema {}. Failed to parse latestReadyHourProperty {}.", schemaName, latestReadyHourProperty, e);
                }
                return latestReadyHourPropertyAsInstant;
            } finally {
                if (in != null) {
                    in.close();
                }

                if (lock != null) {
                    lock.release();
                }

                if (channel != null) {
                    channel.close();
                }
            }

        }
    }

    /**
     * This method updates the latest ready hour for {@link Schema} name {@code schemaName} to {@code newLatestReadyHour}
     *
     * @param schemaName         the name of the {@link Schema} whose latest ready hour we are updating
     * @param newLatestReadyHour the new (end of) hour as {@link Instant}
     * @throws IOException
     */
    public void updateLatestReadyHour(String schemaName, Instant newLatestReadyHour) throws IOException {
        synchronized (sourceLock) {
            FileLock lock = null;
            FileChannel channel = null;
            FileInputStream in = null;
            FileOutputStream out = null;
            try {
                /* Get the file stuff */
                File file = createFile(schemaName, SOURCE_COUNTERS_FOLDER_NAME);
                final String filePath = file.getAbsolutePath();
                channel = new RandomAccessFile(file, "rw").getChannel();
                lock = channel.lock(); // This method blocks until it can retrieve the lock.

                /* load existing count properties */
                Properties countProperties = new OrderedProperties<>(String.class);
                in = new FileInputStream(file);
                countProperties.load(in);

                /* update count properties */
                final String latestReadyHourProperty = countProperties.getProperty(LATEST_READY_HOUR_MARKER);
                final boolean hasLatestReadyHourProperty = latestReadyHourProperty != null;
                if (hasLatestReadyHourProperty) {
                    countProperties.replace(LATEST_READY_HOUR_MARKER, newLatestReadyHour.toString());
                } else {
                    countProperties.setProperty(LATEST_READY_HOUR_MARKER, newLatestReadyHour.toString());
                }

                /* save new count properties */
                out = new FileOutputStream(filePath);
                countProperties.store(out, "hour counters for schema " + schemaName);
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

    }


    private int addToCounter(Instant timeDetected, Schema schema, String flumeComponentType, Instant latestReadyHour, int amount) throws IOException {
        final int newCount;
        FileLock lock = null;
        FileChannel channel = null;
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            /* Get the file stuff */
            File file = createFile(schema.getName(), flumeComponentType);
            final String filePath = file.getAbsolutePath();
            channel = new RandomAccessFile(file, "rw").getChannel();
            lock = channel.lock(); // This method blocks until it can retrieve the lock.

            /* load existing count properties */
            Properties countProperties = new OrderedProperties<>(String.class);
            in = new FileInputStream(file);
            countProperties.load(in);

            /* update count properties */
            newCount = updateCountProperties(timeDetected, latestReadyHour, countProperties, amount, flumeComponentType);

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
                if (key instanceof String) {
                    propertyAsString = (String) key;
                    if (!propertyAsString.equals(LATEST_READY_HOUR_MARKER)) {
                        propertyAsTime = Instant.parse(propertyAsString);
                    } else {
                        continue;
                    }
                } else {
                    propertyAsTime = (Instant) key;
                    propertyAsString = propertyAsTime.toString();
                }
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
        setOwner(Paths.get(folderPath), "presidio");
        return file;
    }

    private void setOwner(Path folderPath, String user) throws IOException {
        UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
        UserPrincipal userPrincipal = lookupService.lookupPrincipalByName(user);
        Files.setOwner(folderPath, userPrincipal);
    }


    private int updateCountProperties(Instant timeDetected, Instant latestReadyHour, Properties properties, int amount, String flumeComponentType) throws IllegalStateException {
        final Instant endOfHour = DateUtils.ceiling(timeDetected, ChronoUnit.HOURS);
        String newCount = updateHourProperty(timeDetected, properties, amount, endOfHour);

        if (flumeComponentType.equals(SOURCE_COUNTERS_FOLDER_NAME) && latestReadyHour != null) {
            updateLatestReadyHourProperty(properties, latestReadyHour);
        }


        return Integer.parseInt(newCount);
    }


    private void updateLatestReadyHourProperty(Properties properties, Instant latestReadyHour) {
        final String currLatestReadyHourProperty = properties.getProperty(LATEST_READY_HOUR_MARKER);
        if (currLatestReadyHourProperty != null && latestReadyHour.isAfter(Instant.parse(currLatestReadyHourProperty))) {
            properties.replace(LATEST_READY_HOUR_MARKER, latestReadyHour.toString());
        } else {
            properties.setProperty(LATEST_READY_HOUR_MARKER, latestReadyHour.toString());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private String updateHourProperty(Instant timeDetected, Properties properties, int amount, Instant endOfHour) {
        String currCount = properties.getProperty(endOfHour.toString());
        final boolean firstCountForHour = currCount == null;
        if (firstCountForHour) { // first edit for this hour
            currCount = "0";
        } else {
            try {
                Integer.parseInt(currCount); //make sure the current is an int, just for sanity
            } catch (NumberFormatException e) {
                final String errorMessage = "Invalid state, counter for event must be an int! " + timeDetected;
                logger.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        }

        String newCount = String.valueOf(Integer.parseInt(currCount) + amount);
        if (firstCountForHour) {
            properties.put(endOfHour.toString(), newCount);
        } else {
            properties.replace(endOfHour.toString(), newCount);
        }
        return newCount;
    }

    /**
     * This class is a {@link Properties} implementation that reads and writes in a sorted manner (according to the <i>natural ordering</i> of its
     * elements).
     */
    @SuppressWarnings("unused")
    protected static class OrderedProperties<T extends Comparable> extends Properties {

        private final Class<T> keyType; //in order to keep the key type a comparable (POLA principle)

        protected OrderedProperties(Class<T> keyType) {
            this.keyType = keyType;
        }

        @Override
        @SuppressWarnings("NullableProblems") //due to intellij bug
        public Set<Object> keySet() {
            final TreeSet<Object> keys = new TreeSet<>(Collections.reverseOrder());
            keys.addAll(super.keySet());
            return Collections.unmodifiableSet(keys);
        }

        @Override
        public synchronized Enumeration<Object> keys() {
            final TreeSet<Object> keys = new TreeSet<>(Collections.reverseOrder());
            keys.addAll(super.keySet());
            return Collections.enumeration(keys);
        }
    }
}
