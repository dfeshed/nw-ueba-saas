package fortscale.utils.hdfs.split;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

public abstract class CommonFileSplitStrategyTest {

	protected FileSplitStrategy strategy;
	
	
	@Test(expected=IllegalArgumentException.class)
	public void split_should_not_accept_null_path() {
		strategy.getFilePath(null, "a.txt", (new Date()).getTime());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void split_should_not_accept_null_filename() {
		strategy.getFilePath("/user/base/", null, (new Date()).getTime());
	}

	@Test(expected=IllegalArgumentException.class)
	public void split_should_not_accept_filename_with_extension() {
		strategy.getFilePath("/user/base/", "a", (new Date()).getTime());
	}
	
	@Test
	public void split_should_normalize_path_seperator() {
		// act
		String actual = strategy.getFilePath("\\user\\base", "a.txt", (new Date()).getTime());
		
		// assert
		assertTrue(actual.startsWith("/user/base/"));
	}
	
}
