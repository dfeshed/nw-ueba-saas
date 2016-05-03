package fortscale.streaming.task.evidence.pre.process;

import fortscale.streaming.task.EvidenceCreationTask;
import fortscale.streaming.task.EvidenceProcessor;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;

import static fortscale.utils.ConversionUtils.convertToLong;

/**
 * Created by tomerd on 17/08/2015.
 */
public class VpnSessionEvidencePreProcess extends AbstractEvidencePreProcess implements EvidenceProcessor {

	static final String DURATION_FILED_NAME = "duration";

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(VpnSessionEvidencePreProcess.class);

	/**
	 * When receiving a VPN close event, calculate the start of event time
	 * @param message
	 * @return
	 */
	@Override public void run(JSONObject message, EvidenceCreationTask.DataSourceConfiguration dataSourceConfiguration) {

		try {
			long endTime = convertToLong(validateFieldExistsAndGetValue(message, dataSourceConfiguration.endTimestampField));
			long duration = convertToLong(validateFieldExistsAndGetValue(message, DURATION_FILED_NAME));
			String startTimeFieldName = dataSourceConfiguration.startTimestampField;

			message.put(startTimeFieldName, endTime - duration);
		}
		catch (Exception ex) {
			logger.error("Error in VpnSessionEvidencePreProcess - unable to read data from message", ex.getMessage());
		}
	}
}
