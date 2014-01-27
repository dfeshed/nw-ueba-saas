package fortscale.utils.hdfs.partition;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class DefaultPartitionStrategyTest {

	@Test
	public void default_partition_should_return_base_path_as_partition_for_any_date_range() {
		// arrange
		DefaultPartitionStrategy strategy = new DefaultPartitionStrategy();
		long start = 1390711225000L; // 26/1/2014
		long finish = 1390884025000L; // 28/1/2014
		
		// act
		String[] actuals = strategy.getPartitionsForDateRange("/base/path", start, finish);
		
		// assert
		assertArrayEquals(new String[] { "/base/path/" }, actuals);
	}
	
	@Test
	public void default_partition_should_not_add_directories_to_a_given_path() {
		// arrange
		DefaultPartitionStrategy strategy = new DefaultPartitionStrategy();
		
		// act
		String actual = strategy.getPartitionPath((new Date()).getTime(), "/user/cloudera/data/vpn/");
		
		// assert
		assertEquals("/user/cloudera/data/vpn/", actual);
	}
	
	@Test
	public void default_parition_should_add_path_separator_to_base_path() {
		// arrange
		DefaultPartitionStrategy strategy = new DefaultPartitionStrategy();
		
		// act
		String actual = strategy.getPartitionPath((new Date()).getTime(), "/user/cloudera/data/vpn");
		
		// assert
		assertEquals("/user/cloudera/data/vpn/", actual);
	}
	
	
	@Test
	public void default_partition_should_normalize_back_slashes() {
		// arrange
		DefaultPartitionStrategy strategy = new DefaultPartitionStrategy();
		
		// act
		String actual = strategy.getPartitionPath((new Date()).getTime(), "\\user\\cloudera\\data\\vpn");
		
		// assert
		assertEquals("/user/cloudera/data/vpn/", actual);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void deafult_parition_should_not_accept_nulls() {
		DefaultPartitionStrategy strategy = new DefaultPartitionStrategy();
		strategy.getPartitionPath((new Date()).getTime(), null);
	}

	@Test
	public void default_partition_should_not_create_any_partitions() {
		DefaultPartitionStrategy strategy = new DefaultPartitionStrategy();
		assertNull(strategy.getImpalaPartitionName((new Date()).getTime()));
	}
}
