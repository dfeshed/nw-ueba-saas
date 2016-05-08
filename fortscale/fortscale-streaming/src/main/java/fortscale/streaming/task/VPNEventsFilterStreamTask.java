package fortscale.streaming.task;

import net.minidev.json.JSONObject;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

public class VPNEventsFilterStreamTask extends EventsFilterStreamTask {
	private String statusFieldName;
	private String statusValueClosed;
	private String dataSourceFieldName;
	private String dataSourceValueVpnSession;

	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		super.wrappedInit(config, context);
		statusFieldName = resolveStringValue(config, "fortscale.vpn.field.name.status", res);
		statusValueClosed = resolveStringValue(config, "fortscale.vpn.status.value.closed", res);
		dataSourceFieldName = resolveStringValue(config, "fortscale.vpn.field.name.data_source", res);
		dataSourceValueVpnSession = resolveStringValue(config, "fortscale.vpn.data_source.value.vpn_session", res);
	}

	@Override
	public void wrappedProcess(
			IncomingMessageEnvelope envelope,
			MessageCollector collector,
			TaskCoordinator coordinator) throws Exception {

		JSONObject message = parseJsonMessage(envelope);
		String status = message.getAsString(statusFieldName);

		if (statusValueClosed.equals(status)) {
			message.put(dataSourceFieldName, dataSourceValueVpnSession);
		}

		IncomingMessageEnvelope newEnvelope = new IncomingMessageEnvelope(
				envelope.getSystemStreamPartition(),
				envelope.getOffset(),
				envelope.getKey(),
				message.toJSONString());
		super.wrappedProcess(newEnvelope, collector, coordinator);
	}
}
