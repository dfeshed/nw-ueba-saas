package fortscale.streaming.task.enrichment;

import fortscale.streaming.task.AbstractStreamTask;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

/**
 * Update User's last activity:
 * 1. In level DB (state) during process
 * 2. In Mongo from time to time
 * Date: 1/11/2015.
 */
public class UserLastActivityTask extends AbstractStreamTask {

	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector,
			TaskCoordinator coordinator) throws Exception {

	}

	@Override
	protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {

	}

	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {

	}

	@Override
	protected void wrappedClose() throws Exception {

	}
}
