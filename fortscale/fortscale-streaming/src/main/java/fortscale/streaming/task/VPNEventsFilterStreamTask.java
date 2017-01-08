package fortscale.streaming.task;

import fortscale.streaming.task.message.ProcessMessageContext;
import fortscale.streaming.task.message.StreamingProcessMessageContext;
import net.minidev.json.JSONObject;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.TaskContext;

public class VPNEventsFilterStreamTask extends EventsFilterStreamTask {
	private String statusFieldName;
	private String statusValueClosed;
	private String dataSourceFieldName;
	private String dataSourceValueVpnSession;

	@Override
	protected void processInit(Config config, TaskContext context) throws Exception {
		super.processInit(config, context);
		statusFieldName = resolveStringValue(config, "fortscale.vpn.field.name.status", res);
		statusValueClosed = resolveStringValue(config, "fortscale.vpn.status.value.closed", res);
		dataSourceFieldName = resolveStringValue(config, "fortscale.vpn.field.name.data_source", res);
		dataSourceValueVpnSession = resolveStringValue(config, "fortscale.vpn.data_source.value.vpn_session", res);
	}

	@Override
	public void processMessage(ProcessMessageContext messageContext) throws Exception {

		JSONObject message = messageContext.getMessageAsJson();
		String status = message.getAsString(statusFieldName);

		if (statusValueClosed.equals(status)) {
			++taskMetrics.vpnCloseMessages;
			message.put(dataSourceFieldName, dataSourceValueVpnSession);
		}
		else {
			++taskMetrics.vpnNonCloseMessages;
		}

		StreamingProcessMessageContext processMessageContext = (StreamingProcessMessageContext) messageContext;
		IncomingMessageEnvelope newEnvelope = new IncomingMessageEnvelope(
				processMessageContext.getIncomingMessageEnvelope().getSystemStreamPartition(),
				processMessageContext.getIncomingMessageEnvelope().getOffset(),
				processMessageContext.getIncomingMessageEnvelope().getKey(),
				message.toJSONString());
		ProcessMessageContext newMessage =
				new StreamingProcessMessageContext(newEnvelope,
						processMessageContext.getCollector(), processMessageContext.getCoordinator(),this);
		super.processMessage(newMessage);
	}
}
