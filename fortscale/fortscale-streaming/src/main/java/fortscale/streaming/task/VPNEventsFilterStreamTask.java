package fortscale.streaming.task;

import fortscale.geoip.GeoIPInfo;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

public class VPNEventsFilterStreamTask extends EventsFilterStreamTask {
	private String countryFiledName;
	private String statusFieldName;
	private String countryToScoreFieldName;
	private String statusValueClosed;
	private String dataSourceFieldName;
	private String dataSourceValueVpnSession;

	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		super.wrappedInit(config, context);
		countryFiledName = resolveStringValue(config, "fortscale.vpn.field.name.country", res);
		statusFieldName = resolveStringValue(config, "fortscale.vpn.field.name.status", res);
		countryToScoreFieldName = resolveStringValue(config, "fortscale.vpn.field.name.country_to_score", res);
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
		String country = message.getAsString(countryFiledName);
		String status = message.getAsString(statusFieldName);

		if (GeoIPInfo.RESERVED_RANGE.equals(country)) {
			message.put(countryToScoreFieldName, StringUtils.EMPTY);
		} else {
			message.put(countryToScoreFieldName, country);
		}

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
