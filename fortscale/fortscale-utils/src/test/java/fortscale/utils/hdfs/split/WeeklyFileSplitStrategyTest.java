package fortscale.utils.hdfs.split;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class WeeklyFileSplitStrategyTest extends CommonFileSplitStrategyTest  {

	@Before
	public void setup() { 
		strategy = new WeeklyFileSplitStrategy();
	}

	@Test
	public void weekly_split_should_use_UTC_timestamp() {
		// arrange
		long ts = 1388531915000L; // UTC 31/12/2013, Local 1/1/2014
		
		// act
		String actual = strategy.getFilePath("/base/path", "a.txt", ts);
		
		// assert
		assertEquals("/base/path/a_2013125.txt", actual);
	}
		
	@Test
	public void weekly_split_should_convert_timestamp_in_seconds_to_milli() {
		// arrange
		long ts = 1388531915L; // UTC 31/12/2013, Local 1/1/2014
		
		// act
		String actual = strategy.getFilePath("/base/path", "a.txt", ts);
		
		// assert
		assertEquals("/base/path/a_2013125.txt", actual);
	}
	
	@Test
	public void weekly_split_should_add_first_day_of_the_week_to_filename() {
		// arrange
		long ts = 1390718425;
		String expected = "/base/path/a_2014014.txt";
		
		// act
		String actual = strategy.getFilePath("/base/path/", "a.txt", ts);
		
		// assert
		assertEquals(expected, actual);
	}
	
}
