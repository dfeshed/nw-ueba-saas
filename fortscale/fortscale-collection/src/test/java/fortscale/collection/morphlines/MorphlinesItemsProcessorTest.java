package fortscale.collection.morphlines;

import java.io.IOException;
import org.junit.Test;

public class MorphlinesItemsProcessorTest {

	@Test(expected=IllegalArgumentException.class)
	public void morphline_items_processor_should_throw_exception_when_file_does_not_exists() throws IllegalArgumentException, IOException {
		new MorphlinesItemsProcessor(null);
	}

}
