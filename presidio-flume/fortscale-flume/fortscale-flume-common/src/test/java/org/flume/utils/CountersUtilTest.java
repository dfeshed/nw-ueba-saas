package org.flume.utils;

import fortscale.common.general.Schema;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

public class CountersUtilTest {

    private static final long TIMEOUT = 2419200000L; //4 weeks

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();
    private CountersUtil testSubject = new CountersUtil(TIMEOUT);

    private String mockedPresidioHome;
    private static Instant exampleDate;
    private static String exampleDateHourStart;
    private static String exampleDatePreviousHourStart;

    private static Instant exampleOldDateInsideTimeout;
    private static String exampleOldDateInsideTimeoutHourEnd;

    private static Instant exampleOldDateOutsideTimeout;
    private static String exampleOldDateOutsideTimeoutHourEnd;

    private static String mockedCountersFolder;

    @Before
    public void setUp() throws Exception {
        CountersUtil.USER = System.getProperty("user.name");
        CountersUtil.GROUP = CountersUtil.USER;
        String currentDirectory = Paths.get("").toAbsolutePath().toString();
        mockedPresidioHome = currentDirectory + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator;
        mockedCountersFolder = mockedPresidioHome + File.separator + "flume" + File.separator + "counters" + File.separator;
        environmentVariables.set("PRESIDIO_HOME", mockedPresidioHome);

        exampleDate = Instant.now();
        exampleDateHourStart = DateUtils.floor(exampleDate, ChronoUnit.HOURS).toString();
        exampleDatePreviousHourStart = DateUtils.floor(exampleDate.minus(1, ChronoUnit.HOURS), ChronoUnit.HOURS).toString();

        exampleOldDateInsideTimeout = exampleDate.minus(TIMEOUT, ChronoUnit.MILLIS).plus(1, ChronoUnit.DAYS);
        exampleOldDateInsideTimeoutHourEnd = DateUtils.floor(exampleOldDateInsideTimeout, ChronoUnit.HOURS).toString();

        exampleOldDateOutsideTimeout = DateUtils.floor(exampleOldDateInsideTimeout, ChronoUnit.HOURS).minus(1, ChronoUnit.DAYS); //to assure previous hour
        exampleOldDateOutsideTimeoutHourEnd = DateUtils.floor(exampleOldDateOutsideTimeout, ChronoUnit.HOURS).toString();

    }

    @After
    public void tearDown() throws Exception {
        try {
            FileUtils.deleteDirectory(new File(Paths.get(mockedPresidioHome + File.separator + "flume").toUri()));
        } catch (IOException e) {
            System.out.println("tearDown failed. Failed to delete " + new File(Paths.get(mockedPresidioHome + "flume").toUri()));
        }
    }

    @Test
    public void addToCounterSourceCanClosePreviousHour() throws Exception {
        testSubject.addToSourceCounter(exampleDate, Schema.ACTIVE_DIRECTORY, Instant.parse(exampleDatePreviousHourStart), 1);

        final Properties properties = getProperties(CountersUtil.SOURCE_COUNTERS_FOLDER_NAME, Schema.ACTIVE_DIRECTORY);
        Assert.assertEquals(properties.getProperty(exampleDateHourStart), "1");
        Assert.assertEquals(properties.getProperty(CountersUtil.LATEST_READY_HOUR_MARKER), exampleDatePreviousHourStart);
    }

    @Test
    public void addToCounterSourceCannotClosePreviousHour() throws Exception {
        testSubject.addToSourceCounter(exampleDate, Schema.ACTIVE_DIRECTORY, null, 1);

        final Properties properties = getProperties(CountersUtil.SOURCE_COUNTERS_FOLDER_NAME, Schema.ACTIVE_DIRECTORY);
        Assert.assertEquals(properties.getProperty(exampleDateHourStart), "1");
        Assert.assertEquals(properties.getProperty(CountersUtil.LATEST_READY_HOUR_MARKER), null);
    }

    @Test
    public void addToCounterClean() throws Exception {
        final Instant latestReadyHour = Instant.parse(exampleDatePreviousHourStart);
        testSubject.addToSourceCounter(exampleDate, Schema.ACTIVE_DIRECTORY, latestReadyHour, 1);
        testSubject.addToSourceCounter(exampleOldDateInsideTimeout, Schema.ACTIVE_DIRECTORY, latestReadyHour, 1);
        testSubject.addToSourceCounter(exampleOldDateOutsideTimeout, Schema.ACTIVE_DIRECTORY, latestReadyHour, 1);

        final Properties properties = getProperties(CountersUtil.SOURCE_COUNTERS_FOLDER_NAME, Schema.ACTIVE_DIRECTORY);
        Assert.assertEquals(properties.getProperty(exampleDateHourStart), "1");
        Assert.assertEquals(properties.getProperty(exampleOldDateInsideTimeoutHourEnd), "1");
        Assert.assertEquals(properties.getProperty(exampleOldDateOutsideTimeoutHourEnd), null);
    }

    @Test
    public void addToCounterSinkSimple() throws Exception {
        testSubject.addToSinkCounter(exampleDate, Schema.ACTIVE_DIRECTORY, 1);

        final Properties properties = getProperties(CountersUtil.SINK_COUNTERS_FOLDER_NAME, Schema.ACTIVE_DIRECTORY);
        Assert.assertEquals(properties.getProperty(exampleDateHourStart), "1");
        Assert.assertEquals(properties.getProperty(CountersUtil.LATEST_READY_HOUR_MARKER), null);

    }

    private Properties getProperties(String flumeComponentType, Schema schema) throws IOException {
        /* Get the file stuff */
        File file = new File(mockedCountersFolder + flumeComponentType + File.separator + schema.getName());

        /* load existing count properties */
        Properties countProperties = new CountersUtil.OrderedProperties<>(String.class);
        final FileInputStream in = new FileInputStream(file);
        countProperties.load(in);
        return countProperties;
    }


}