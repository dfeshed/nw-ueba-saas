package fortscale.utils.hdfs.partition;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Test;

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
		assertArrayEquals(new String[] { "/base/path/year=2014/month=1/" }, actuals);
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
		assertArrayEquals(new String[] { "/base/path/year=2014/month=1/", "/base/path/year=2014/month=2/" }, actuals);
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
		assertArrayEquals(new String[] { "/base/path/year=2014/month=1/", "/base/path/year=2014/month=2/" }, actuals);
	}
	
	@Test
	public void monthly_partition_should_return_partition_name_according_to_UTC() {
		// arrange
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		long ts = 1388531915000L; // UTC 31/12/2013, Local 1/1/2014
		
		// act
		String actual = strategy.getImpalaPartitionName(ts);
		
		// assert
		assertEquals("year=2013,month=12", actual);
	}
	
	@Test
	public void monthly_partition_should_return_partition_name_should_convert_ts_in_seconds_to_milli() {
		// arrange
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		long ts = 1388531915L; // UTC 31/12/2013, Local 1/1/2014
		
		// act
		String actual = strategy.getImpalaPartitionName(ts);
		
		// assert
		assertEquals("year=2013,month=12", actual);
	} 
	
	@Test
	public void monthly_partition_should_convert_timestamp_from_seconds_to_milli() {
		// arrange
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		long ts = 1388531915L; // UTC 31/12/2013, Local 1/1/2014
		
		// act
		String actual = strategy.getPartitionPath(ts, "/user/cloudera/data/ssh");
		
		// assert
		assertEquals("/user/cloudera/data/ssh/year=2013/month=12/", actual);
	}
	
	@Test
	public void monthly_parition_should_create_directories_according_to_UTC() {
		// arrange
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		long ts = 1388531915000L; // UTC 31/12/2013, Local 1/1/2014
		
		// act
		String actual = strategy.getPartitionPath(ts, "/user/cloudera/data/ssh");
		
		// assert
		assertEquals("/user/cloudera/data/ssh/year=2013/month=12/", actual);
	}
	
	@Test
	public void monthly_parition_should_create_directories_for_year_and_month() {
		// arrange
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1; // month are starting from 0 here
		String expectedPath = String.format("/user/cloudera/data/ssh/year=%s/month=%s/", year, month);
		
		// act 
		String actual = strategy.getPartitionPath((new Date()).getTime(), "/user/cloudera/data/ssh/");
		
		// assert
		assertEquals(expectedPath, actual);
	}
	
	@Test
	public void monthly_partition_should_add_path_seperator_to_base_path_suffix() {
		// arrange
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1; // month are starting from 0 here
		String expectedPath = String.format("/user/cloudera/data/ssh/year=%s/month=%s/", year, month);
		
		// act 
		String actual = strategy.getPartitionPath((new Date()).getTime(), "/user/cloudera/data/ssh");
		
		// assert
		assertEquals(expectedPath, actual);
	}

	
	@Test
	public void monthly_partition_should_normalize_back_slashes() {
		// arrange
		MonthlyPartitionStrategy strategy = new MonthlyPartitionStrategy();
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1; // month are starting from 0 here
		String expectedPath = String.format("/user/cloudera/data/ssh/year=%s/month=%s/", year, month);
		
		// act 
		String actual = strategy.getPartitionPath((new Date()).getTime(), "\\user\\cloudera\\data\\ssh\\");
		
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
		
		assertEquals("year=2014,month=1", actual);
	}
}
