package fortscale.utils.hdfs.partition;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


@RunWith(JUnitParamsRunner.class)
public class DailyPartitionStrategyTest {

    @Test
    public void daily_partition_should_return_single_partition_when_range_within_day() {

        // arrange
        DailyPartitionStrategy strategy = new DailyPartitionStrategy();

        long start = 1390711225000L; // 26/1/2014 04:40:25
        long finish = 1390721325000L; // 26/1/2014 07:28:45

        // act
        String[] actuals = strategy.getPartitionsForDateRange("/base/path", start, finish);

        // assert
        assertArrayEquals(new String[] { "/base/path/yearmonthday=20140126/" }, actuals);
    }

    @Test
    public void daily_partition_should_return_several_partitions_when_range_across_days() {
        // arrange
        DailyPartitionStrategy strategy = new DailyPartitionStrategy();
        long start = 1390711225000L; // 26/1/2014
        long finish = 1390900225000L; // 28/1/2014

        // act
        String[] actuals = strategy.getPartitionsForDateRange("/base/path", start, finish);

        // assert
        assertArrayEquals(new String[] { "/base/path/yearmonthday=20140126/", "/base/path/yearmonthday=20140127/","/base/path/yearmonthday=20140128/" }, actuals);
    }


    @Test
    public void daily_partition_should_return_partition_name_according_to_UTC() {
        // arrange
        DailyPartitionStrategy strategy = new DailyPartitionStrategy();
        long ts = 1388531915000L; // UTC 31/12/2013, Local 1/1/2014

        // act
        String actual = strategy.getImpalaPartitionName(ts);

        // assert
        assertEquals("yearmonthday=20131231", actual);
    }

    @Test
    public void daily_partition_should_return_partition_name_should_convert_ts_in_seconds_to_milli() {
        // arrange
        DailyPartitionStrategy strategy = new DailyPartitionStrategy();
        long ts = 1388531915L; // UTC 31/12/2013, Local 1/1/2014

        // act
        String actual = strategy.getImpalaPartitionName(ts);

        // assert
        assertEquals("yearmonthday=20131231", actual);
    }

    @Test
    public void daily_partition_should_convert_timestamp_from_seconds_to_milli() {
        // arrange
        DailyPartitionStrategy strategy = new DailyPartitionStrategy();
        long ts = 1388531915L; // UTC 31/12/2013, Local 1/1/2014

        // act
        String actual = strategy.getPartitionPath(ts, "/user/cloudera/data/ssh");

        // assert
        assertEquals("/user/cloudera/data/ssh/yearmonthday=20131231/", actual);
    }


    @Test
    public void daily_parition_should_create_directories_for_year_month_and_day() {
        // arrange
        DailyPartitionStrategy strategy = new DailyPartitionStrategy();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // month are starting from 0 here
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String expectedPath = String.format("/user/cloudera/data/ssh/yearmonthday=%s%02d%02d/", year, month,day);

        // act
        String actual = strategy.getPartitionPath((new Date()).getTime(), "/user/cloudera/data/ssh/");

        // assert
        assertEquals(expectedPath, actual);
    }

    @Test
    public void daily_partition_should_add_path_seperator_to_base_path_suffix() {
        // arrange
        DailyPartitionStrategy strategy = new DailyPartitionStrategy();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // month are starting from 0 here
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String expectedPath = String.format("/user/cloudera/data/ssh/yearmonthday=%s%02d%02d/", year, month,day);

        // act
        String actual = strategy.getPartitionPath(calendar.getTimeInMillis(), "/user/cloudera/data/ssh");

        // assert
        assertEquals(expectedPath, actual);
    }


    @Test
    public void daily_partition_should_normalize_back_slashes() {
        // arrange
        DailyPartitionStrategy strategy = new DailyPartitionStrategy();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // month are starting from 0 here
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String expectedPath = String.format("/user/cloudera/data/ssh/yearmonthday=%s%02d%02d/", year, month,day);

        // act
        String actual = strategy.getPartitionPath(calendar.getTimeInMillis(), "\\user\\cloudera\\data\\ssh\\");

        // assert
        assertEquals(expectedPath, actual);
    }

    @Test(expected=IllegalArgumentException.class)
    public void daily_parition_should_not_accept_nulls() {
        DailyPartitionStrategy strategy = new DailyPartitionStrategy();
        strategy.getPartitionPath((new Date()).getTime(), null);
    }

    @Test
    public void daily_partition_should_not_create_any_partitions() {
        DailyPartitionStrategy strategy = new DailyPartitionStrategy();
        long timestamp = (new DateTime(2014, 1, 22, 10, 5)).getMillis();

        String actual = strategy.getImpalaPartitionName(timestamp);

        assertEquals("yearmonthday=20140122", actual);
    }

    @Test
    @Parameters({
            "/base/yearmonthday=20140206, 1391674242, 0",
            "/base/yearmonthday=20140206, 1394092770, 1",
            "/base/yearmonthday=20140206, 1394092770000, 1",
            "/base/yearmonthday=20140206, 1388995170, -1",
            "/base/yearmonthday=20131206, 1394092770000, 1"
    })
    public void daily_partition_compare_to_test(String path, long ts, int expected) {
        DailyPartitionStrategy strategy = new DailyPartitionStrategy();
        assertEquals(expected, strategy.comparePartitionTo(path, ts));
    }

    @Test
    @Parameters({
            "/base/yearmonthday=20140206, true",
            "/base/yearmonthday=20141, false",
            "/base/yearmonthday=2014011, false",
            "/base/yearmonthday=, false",
            "/base/yearmonthday, false",
            "/base/year, false",
            "/base/, false"
    })
    public void daily_partition_is_partition_test(String path, boolean expected) {
        DailyPartitionStrategy strategy = new DailyPartitionStrategy();
        assertEquals(expected, strategy.isPartitionPath(path));
    }

    @Test
    @Parameters({
            "/base/yearmonthday=20140201, yearmonthday=20140201",
            "/base/yearmonthday=20140201/, yearmonthday=20140201",
            "/base/path/yearmonthday=20140228, yearmonthday=20140228"
    })
    public void daily_partition_get_partition_from_path_test(String path, String partition) {
        DailyPartitionStrategy strategy = new DailyPartitionStrategy();
        assertEquals(partition, strategy.getImpalaPartitionNameFromPath(path));
    }

}
