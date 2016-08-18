package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fortscale.collection.monitoring.CollectionMessages;
import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.kitesdk.morphline.base.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import com.typesafe.config.Config;

public class OverFlowFilterCmdBuilder implements CommandBuilder {
	private static Logger logger = LoggerFactory
			.getLogger(OverFlowFilterCmdBuilder.class);

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("OverFlowFilter");
	}

	@Override
	public Command build(Config config, Command parent, Command child,
			MorphlineContext context) {
		return new OverFlowFilter(this, config, parent, child, context);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	@Configurable(preConstruction = true)
	public class OverFlowFilter extends AbstractCommand {
		HashMap<String, Integer> counterMap = new HashMap<String, Integer>();
		HashMap<String, Long> passedThreshold = new HashMap<String, Long>();
		List<String> criteriaFields = null;
		Integer threshold = Integer.MAX_VALUE;
		String eventType = "";
		@Value("${mophline.cmd.overflow:false}")
		private boolean runCmd;

		MorphlineCommandMonitoringHelper commandMonitoringHelper = new MorphlineCommandMonitoringHelper();


		protected OverFlowFilter(CommandBuilder builder, Config config,
				Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			criteriaFields = getConfigs().getStringList(config, "criteria");
			threshold = getConfigs().getInt(config, "threshold");
			eventType = getConfigs().getString(config, "eventsType");
		}

		@Override
		protected boolean doProcess(Record inputRecord) {

			//The specific Morphline metric
			MorphlineMetrics morphlineMetrics = commandMonitoringHelper.getMorphlineMetrics(inputRecord);

			if(runCmd == false){
				return super.doProcess(inputRecord);
			}
			String key = "/";
			if (criteriaFields.size() != 0) {
				StringBuilder strBuilder = new StringBuilder("");
				for (String field : criteriaFields) {
					String fieldValue = (String) inputRecord
							.getFirstValue(field);
					if (fieldValue != null) {
						strBuilder.append("/").append(fieldValue);
					} else {
						logger.warn("input record do not contain {}", field);
					}
				}
				key = strBuilder.toString();
			}
			Long counter = new Long(1);
			if (counterMap.containsKey(key)) {
				counter = new Long(counterMap.get(key) + 1);
				if (counter > threshold) {
					counterMap.remove(key);
					passedThreshold.put(key, counter);
				} else {
					counterMap.put(key, counter.intValue());
				}
			} else if (passedThreshold.containsKey(key)) {
				counter = passedThreshold.get(key) + 1;
				passedThreshold.put(key, counter);
			} else { // new key
				counterMap.put(key, 1);
			}
			if (counter > threshold) {
				// drop record
				commandMonitoringHelper.addFilteredEventToMonitoring(inputRecord,
						CollectionMessages.OVERFLOW_THRESHOLD_WAS_REACHED);

				if (morphlineMetrics != null) {
					morphlineMetrics.overflowThresholdReached++;
				}

				return true;
			}
			return super.doProcess(inputRecord);
		}

		@Override
		protected void doNotify(Record notification) {

			for (Object event : Notifications.getLifecycleEvents(notification)) {
				if (event == Notifications.LifecycleEvent.SHUTDOWN) {
					for (Map.Entry<String, Long> entry : passedThreshold
							.entrySet()) {
						logger.info(
								"Event type {} with criteria {} passed the threshold. Number of records {} : threshold {}",
								eventType, entry.getKey(), entry.getValue(),
								threshold);
					}
				}
			}
			super.doNotify(notification);
		}
	}
}
