package fortscale.streaming.task;

import fortscale.streaming.task.message.FSProcessContextualMessage;
import fortscale.streaming.task.message.SamzaProcessContextualMessage;
import fortscale.streaming.task.message.UnsupportedMessageTypeException;
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
			FSProcessContextualMessage contextualMessage,
			MessageCollector collector,
			TaskCoordinator coordinator) throws Exception {

		JSONObject message = contextualMessage.getMessageAsJson();
		String status = message.getAsString(statusFieldName);

		if (statusValueClosed.equals(status)) {
			++taskMetrics.vpnCloseMessages;
			message.put(dataSourceFieldName, dataSourceValueVpnSession);
		}
		else {
			++taskMetrics.vpnNonCloseMessages;
		}

		if(!(contextualMessage instanceof SamzaProcessContextualMessage))
		{
			throw new UnsupportedMessageTypeException(contextualMessage);
		}
		IncomingMessageEnvelope newEnvelope = new IncomingMessageEnvelope(
				((SamzaProcessContextualMessage) contextualMessage).getIncomingMessageEnvelope().getSystemStreamPartition(),
				((SamzaProcessContextualMessage) contextualMessage).getIncomingMessageEnvelope().getOffset(),
				((SamzaProcessContextualMessage) contextualMessage).getIncomingMessageEnvelope().getKey(),
				message.toJSONString());
		FSProcessContextualMessage newMessage = new SamzaProcessContextualMessage(newEnvelope,messageShouldContainDataSourceField());
		super.wrappedProcess(newMessage, collector, coordinator);
	}
}
