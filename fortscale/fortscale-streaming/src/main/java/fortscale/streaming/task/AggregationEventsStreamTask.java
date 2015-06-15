package fortscale.streaming.task;

import fortscale.streaming.service.aggregation.AggregationEventsManager;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;

public class AggregationEventsStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {
	private boolean skipModel;
	private boolean skipScore;

	private AggregationEventsManager aggregationEventsManager;

	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		skipModel = config.getBoolean("fortscale.skip.model", false);
		skipScore = config.getBoolean("fortscale.skip.score", false);

		aggregationEventsManager = new AggregationEventsManager(config);
	}

	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		if (!skipModel) {
			aggregationEventsManager.processEvent(envelope);
		}

		if (!skipScore) {
			// TODO implement
		}
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
