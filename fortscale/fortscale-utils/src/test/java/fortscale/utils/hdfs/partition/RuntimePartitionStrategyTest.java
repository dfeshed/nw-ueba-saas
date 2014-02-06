package fortscale.utils.hdfs.partition;

import static org.junit.Assert.*;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class RuntimePartitionStrategyTest {

	RuntimePartitionStrategy strategy = new RuntimePartitionStrategy();
	
	@Test
	public void runtime_partition_strategy_should_set_path_correct() {
		String actual = strategy.getPartitionPath(111, "/base");
		assertEquals("/base/runtime=111/", actual);
	}
	
	@Test
	public void runtime_partition_strategy_should_return_all_numbers_between_range() {
		String[] expected = new String[] { "/base/runtime=1/", "/base/runtime=2/", "/base/runtime=3/" };
		
		String[] actual = strategy.getPartitionsForDateRange("/base", 1, 3);
		
		assertArrayEquals(expected, actual);
	}
	
	@Test
	@Parameters({
		"/base/runtime=1, true",
		"/base/yearmonth=201402, false",
		"/base/, false",
		"/base/runtime=1/, true"
	})
	public void is_partition_path_tests(String path, boolean expected) {
		assertEquals(expected, strategy.isPartitionPath(path));
	}
	
	@Test
	@Parameters({
		"/base/runtime=1388995170, 1388995170000, 0",
		"/base/runtime=1388995170, 1388995170, 0",
		"/base/runtime=1388995170, 1388995180, 1",
		"/base/runtime=1388995170, 1388995160, -1",
		"/base/runti, 201402, 0"
	})
	public void compare_tests(String path, long runtime, int expected) {
		assertEquals(expected, strategy.comparePartitionTo(path, runtime));
	}
}
