package fortscale.streaming.service.tagging;

import fortscale.streaming.task.KeyValueStoreMock;
import org.apache.samza.storage.kv.KeyValueStore;
import org.junit.Test;

public class FieldTaggingServiceTest {

	@Test
	public void testEnrichEvent() throws Exception {

		KeyValueStore<String,String> valueList = new KeyValueStoreMock<>();

		//FieldTaggingService fieldTaggingService = new FieldTaggingService("resources/lists/sensitiveMachine.txt");

	}
}
