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
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class CountersUtil {

    private static Logger logger = LoggerFactory.getLogger(DateUtils.class);


    public static String USER = "presidio";
    public static String GROUP = "presidio";
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
     * of events that need to be 'sinked' for this hour {@code time} (by hour floor).
     * i.e, all events between 2017-08-03T14:00:00Z---2017-08-03T15:00:00Z will be under 2017-08-03T14:00:00Z
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
     * of events that were 'sinked' for this hour {@code time} (by hour floor).
     * i.e, all events between 2017-08-03T14:00:00Z---2017-08-03T15:00:00Z will be under 2017-08-03T14:00:00Z
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
     * @param schema the {@link Schema} whose latest ready hour we return
     * @return latest ready hour for {@code schemaName}, null if it doesn't exist or we failed to parse it
     * @throws IOException
     */
    public Instant getLatestReadyHour(Schema schema) throws IOException {
        synchronized (sourceLock) {
            FileLock lock = null;
            FileChannel channel = null;
            FileInputStream in = null;
            try {
                /* Get the file stuff */
                File file = createFile(schema, SOURCE_COUNTERS_FOLDER_NAME);
                channel = new RandomAccessFile(file, "rw").getChannel();
                lock = channel.lock(); // This method blocks until it can retrieve the lock.

                /* load existing count properties */
                Properties countProperties = new OrderedProperties<>(String.class);
                in = new FileInputStream(file);
                countProperties.load(in);

                /* get latest ready hour properties */
                final String latestReadyHourProperty = countProperties.getProperty(LATEST_READY_HOUR_MARKER);
                if (latestReadyHourProperty == null) {
                    return null;
                }
                Instant latestReadyHourPropertyAsInstant = null;
                try {
                    latestReadyHourPropertyAsInstant = Instant.parse(latestReadyHourProperty);
                } catch (Exception e) {
                    logger.warn("Can't get latest ready hour for schema {}. Failed to parse latestReadyHourProperty {}.", schema.getName(), latestReadyHourProperty, e);
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


    private int addToCounter(Instant timeDetected, Schema schema, String flumeComponentType, Instant latestReadyHour, int amount) throws IOException {
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
            newCount = updateProperties(timeDetected, latestReadyHour, countProperties, amount, flumeComponentType);

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
    private File createFile(Schema schema, String flumeComponentType) throws IOException {
        final String presidioHome = System.getenv("PRESIDIO_HOME");
        final String countersFolderPath = presidioHome + File.separator + "flume" + File.separator + "counters" + File.separator;
        final String folderPath = countersFolderPath + flumeComponentType;
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
        ownCounterFolderAndFiles(countersFolderPath, folderPath, filePath);
        return file;
    }

    private void ownCounterFolderAndFiles(String countersFolderPath, String folderPath, String filePath) throws IOException {
        setOwnership(Paths.get(folderPath), USER, GROUP);
        setOwnership(Paths.get(countersFolderPath), USER, GROUP);
        setOwnership(Paths.get(filePath), USER, GROUP);
    }

    private void setOwnership(Path path, String user, String group) throws IOException {
        final UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();

        final UserPrincipal userPrincipal = lookupService.lookupPrincipalByName(user);
        Files.setOwner(path, userPrincipal);

        final GroupPrincipal groupPrincipal = lookupService.lookupPrincipalByGroupName(group);
        final PosixFileAttributeView fileAttributeView = Files.getFileAttributeView(path, PosixFileAttributeView.class);
        fileAttributeView.setGroup(groupPrincipal);
    }


    private int updateProperties(Instant timeDetected, Instant latestReadyHour, Properties properties, int amount, String flumeComponentType) throws IllegalStateException {
        final Instant startOfHour = DateUtils.floor(timeDetected, ChronoUnit.HOURS);
        String newCount = updateHourCountProperty(timeDetected, properties, amount, startOfHour);

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
    private String updateHourCountProperty(Instant timeDetected, Properties properties, int amount, Instant startOfHour) {
        String currCount = properties.getProperty(startOfHour.toString());
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
            properties.put(startOfHour.toString(), newCount);
        } else {
            properties.replace(startOfHour.toString(), newCount);
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
