package fortscale.utils.hdfs.partition;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class MonthlyPartitionStrategyTest {

	@Test
	public void monthly_partition_should_return_single_partition_when_range_within_month() {
		// arrange
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		long start = 1390711225000L; // 26/1/2014
		long finish = 1390884025000L; // 28/1/2014
		
		// act
		String[] actuals = strategy.getPartitionsForDateRange("/base/path", start, finish);
		
		// assert
		assertArrayEquals(new String[] { "/base/path/yearmonth=201401/" }, actuals);
	}
	
	@Test
	public void monthly_partition_should_return_several_partitions_when_range_across_months() {
		// arrange
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		long start = 1390711225000L; // 26/1/2014
		long finish = 1393562425000L; // 28/2/2014
		
		// act
		String[] actuals = strategy.getPartitionsForDateRange("/base/path", start, finish);
		
		// assert
		assertArrayEquals(new String[] { "/base/path/yearmonth=201401/", "/base/path/yearmonth=201402/" }, actuals);
	}
	
	@Test
	public void monthly_partition_should_normalize_date_range() {
		// arrange
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		long start = 1390711225L; // 26/1/2014
		long finish = 1393562425L; // 28/2/2014
		
		// act
		String[] actuals = strategy.getPartitionsForDateRange("/base/path", start, finish);
		
		// assert
		assertArrayEquals(new String[] { "/base/path/yearmonth=201401/", "/base/path/yearmonth=201402/" }, actuals);
	}
	
	@Test
	public void monthly_partition_should_return_partition_name_according_to_UTC() {
		// arrange
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		long ts = 1388531915000L; // UTC 31/12/2013, Local 1/1/2014
		
		// act
		String actual = strategy.getImpalaPartitionName(ts);
		
		// assert
		assertEquals("yearmonth=201312", actual);
	}
	
	@Test
	public void monthly_partition_should_return_partition_name_should_convert_ts_in_seconds_to_milli() {
		// arrange
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		long ts = 1388531915L; // UTC 31/12/2013, Local 1/1/2014
		
		// act
		String actual = strategy.getImpalaPartitionName(ts);
		
		// assert
		assertEquals("yearmonth=201312", actual);
	} 
	
	@Test
	public void monthly_partition_should_convert_timestamp_from_seconds_to_milli() {
		// arrange
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		long ts = 1388531915L; // UTC 31/12/2013, Local 1/1/2014
		
		// act
		String actual = strategy.getPartitionPath(ts, "/user/cloudera/data/ssh");
		
		// assert
		assertEquals("/user/cloudera/data/ssh/yearmonth=201312/", actual);
	}
	
	@Test
	public void monthly_parition_should_create_directories_according_to_UTC() {
		// arrange
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		long ts = 1388531915000L; // UTC 31/12/2013, Local 1/1/2014
		
		// act
		String actual = strategy.getPartitionPath(ts, "/user/cloudera/data/ssh");
		
		// assert
		assertEquals("/user/cloudera/data/ssh/yearmonth=201312/", actual);
	}
	
	@Test
	public void monthly_parition_should_create_directories_for_year_and_month() {
		// arrange
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1; // month are starting from 0 here
		String expectedPath = String.format("/user/cloudera/data/ssh/yearmonth=%s%02d/", year, month);
		
		// act 
		String actual = strategy.getPartitionPath((new Date()).getTime(), "/user/cloudera/data/ssh/");
		
		// assert
		assertEquals(expectedPath, actual);
	}
	
	@Test
	public void monthly_partition_should_add_path_seperator_to_base_path_suffix() {
		// arrange
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1; // month are starting from 0 here
		String expectedPath = String.format("/user/cloudera/data/ssh/yearmonth=%s%02d/", year, month);
		
		// act 
		String actual = strategy.getPartitionPath(calendar.getTimeInMillis(), "/user/cloudera/data/ssh");
		
		// assert
		assertEquals(expectedPath, actual);
	}

	
	@Test
	public void monthly_partition_should_normalize_back_slashes() {
		// arrange
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1; // month are starting from 0 here
		String expectedPath = String.format("/user/cloudera/data/ssh/yearmonth=%s%02d/", year, month);
		
		// act 
		String actual = strategy.getPartitionPath(calendar.getTimeInMillis(), "\\user\\cloudera\\data\\ssh\\");
		
		// assert
		assertEquals(expectedPath, actual);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void monthly_parition_should_not_accept_nulls() {
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		strategy.getPartitionPath((new Date()).getTime(), null);
	}
	
	@Test
	public void monthly_partition_should_not_create_any_partitions() {
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		long timestamp = (new DateTime(2014, 1, 22, 10, 5)).getMillis();
		
		String actual = strategy.getImpalaPartitionName(timestamp);
		
		assertEquals("yearmonth=201401", actual);
	}
	
	@Test
	@Parameters({
		"/base/yearmonth=201402, 1391674242, 0",
		"/base/yearmonth=201402, 1394092770, 1",
		"/base/yearmonth=201402, 1394092770000, 1",
		"/base/yearmonth=201402, 1388995170, -1"
	})
	public void monthly_partition_compare_to_test(String path, long ts, int expected) {
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		assertEquals(expected, strategy.comparePartitionTo(path, ts));
	}
	
	@Test
	@Parameters({
		"/base/yearmonth=201402, true",
		"/base/yearmonth=20141, false",
		"/base/yearmonth=, false",
		"/base/yearmonth, false",
		"/base/year, false",
		"/base/, false"
	})
	public void monthly_partition_is_partition_test(String path, boolean expected) {
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		assertEquals(expected, strategy.isPartitionPath(path));
	}
}
