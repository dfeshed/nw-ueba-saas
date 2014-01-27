package fortscale.utils.hdfs.split;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class DefaultFileSplitStrategyTest  extends CommonFileSplitStrategyTest {

	@Before
	public void setup() { 
		strategy = new DefaultFileSplitStrategy();
	}

	@Test
	public void default_file_split_strategy_should_not_change_filename() {
		String actual = strategy.getFilePath("/base/path/", "a.txt", (new Date()).getTime());
				
		assertEquals("/base/path/a.txt", actual);
	}

	
}
