package fortscale.streaming.task;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.aggregation.AggregatorManager;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;

public class AggregationEventsStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {
	private AggregatorManager aggregationEventsManager;

	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		aggregationEventsManager = new AggregatorManager(config, new ExtendedSamzaTaskContext(context));
	}

	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		aggregationEventsManager.processEvent(envelope);
	}

	@Override
	protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		if (aggregationEventsManager != null) {
			aggregationEventsManager.window(collector, coordinator);
		}
	}

	@Override
	protected void wrappedClose() throws Exception {
		if (aggregationEventsManager != null) {
			aggregationEventsManager.close();
			aggregationEventsManager = null;
		}
	}
}
