package fortscale.streaming.task;

import fortscale.streaming.service.aggregation.AggregationEventsManager;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;

public class AggregationEventsStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {
	private boolean skipModel;
	private AggregationEventsManager manager;

	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		skipModel = config.getBoolean("fortscale.skip.model", false);
		manager = new AggregationEventsManager(config);
	}

	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		if (!skipModel) {
			manager.processEvent(envelope);
		}
	}

	@Override
	protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {}

	@Override
	protected void wrappedClose() throws Exception {}
}
