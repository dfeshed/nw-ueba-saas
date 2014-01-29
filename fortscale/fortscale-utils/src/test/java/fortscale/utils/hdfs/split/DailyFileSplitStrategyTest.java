package fortscale.utils.hdfs.split;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

public class DailyFileSplitStrategyTest extends CommonFileSplitStrategyTest {

	@Before
	public void setup() { 
		strategy = new DailyFileSplitStrategy();
	}

	@Test
	public void daily_split_should_use_UTC() {
		// arrange
		long ts = 1388531915000L; // UTC 31/12/2013, Local 1/1/2014
		
		// act
		String actual = strategy.getFilePath("/base/path", "a.txt", ts);
		
		// assert
		assertEquals("/base/path/a_20131231.txt", actual);
	}
	
	@Test
	public void daily_split_should_convert_timestamp_in_seconds_to_milli() {
		// arrange
		long ts = 1388531915L; // UTC 31/12/2013, Local 1/1/2014
		
		// act
		String actual = strategy.getFilePath("/base/path", "a.txt", ts);
		
		// assert
		assertEquals("/base/path/a_20131231.txt", actual);
	}
	
	@Test
	public void daily_split_should_add_current_date_to_filename() {
		// arrange
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1; // month are starting from 0 here
		String monthPart = (month>9? String.valueOf(month) : "0" + String.valueOf(month));
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		String dayPart = (day>9? String.valueOf(day) : "0" + String.valueOf(day) );
		String expected = "/base/path/a_" + String.valueOf(year) + monthPart + dayPart + ".txt";
		
		// act
		String actual = strategy.getFilePath("/base/path/", "a.txt", calendar.getTimeInMillis());
		
		// assert
		assertEquals(expected, actual);
	}
	
}
