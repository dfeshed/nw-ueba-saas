package fortscale.streaming.task;

import fortscale.streaming.task.message.ProcessMessageContext;
import fortscale.streaming.task.message.SamzaProcessMessageContext;
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
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		super.wrappedInit(config, context);
		statusFieldName = resolveStringValue(config, "fortscale.vpn.field.name.status", res);
		statusValueClosed = resolveStringValue(config, "fortscale.vpn.status.value.closed", res);
		dataSourceFieldName = resolveStringValue(config, "fortscale.vpn.field.name.data_source", res);
		dataSourceValueVpnSession = resolveStringValue(config, "fortscale.vpn.data_source.value.vpn_session", res);
	}

	@Override
	public void ProcessMessage(ProcessMessageContext contextualMessage) throws Exception {

		JSONObject message = contextualMessage.getMessageAsJson();
		String status = message.getAsString(statusFieldName);

		if (statusValueClosed.equals(status)) {
			++taskMetrics.vpnCloseMessages;
			message.put(dataSourceFieldName, dataSourceValueVpnSession);
		}
		else {
			++taskMetrics.vpnNonCloseMessages;
		}

		SamzaProcessMessageContext processMessageContext = (SamzaProcessMessageContext) contextualMessage;
		IncomingMessageEnvelope newEnvelope = new IncomingMessageEnvelope(
				processMessageContext.getIncomingMessageEnvelope().getSystemStreamPartition(),
				processMessageContext.getIncomingMessageEnvelope().getOffset(),
				processMessageContext.getIncomingMessageEnvelope().getKey(),
				message.toJSONString());
		ProcessMessageContext newMessage =
				new SamzaProcessMessageContext(newEnvelope,messageShouldContainDataSourceField(),
						processMessageContext.getCollector(), processMessageContext.getCoordinator());
		super.ProcessMessage(newMessage);
	}
}
