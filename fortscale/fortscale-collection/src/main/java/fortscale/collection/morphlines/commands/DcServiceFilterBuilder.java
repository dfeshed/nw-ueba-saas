package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import fortscale.collection.monitoring.CollectionMessages;
import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import fortscale.services.ServersListConfiguration;
import fortscale.utils.logging.Logger;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * A command that filters out records whose service name matches the Domain Controller regex.
 *
 * @author Lior Govrin
 */
public class DcServiceFilterBuilder implements CommandBuilder {
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList(DcServiceFilter.class.getSimpleName());
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new DcServiceFilter(this, config, parent, child, context);
	}

	@Configurable(preConstruction = true)
	public static final class DcServiceFilter extends AbstractCommand {
		private static final Logger logger = Logger.getLogger(DcServiceFilter.class);
		private static final String FIELD_NAME_KEY = "fieldName";

		@Autowired
		private ServersListConfiguration serversListConfiguration;

		private final Pattern dcRegex;
		private final MorphlineCommandMonitoringHelper morphlineCommandMonitoringHelper;
		private final String fieldName;
		private final String renderedConfig;

		public DcServiceFilter(
				CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {

			super(builder, config, parent, child, context);
			String loginServiceRegex = serversListConfiguration.getLoginServiceRegex();
			dcRegex = isNotBlank(loginServiceRegex) ? compile(loginServiceRegex, Pattern.CASE_INSENSITIVE) : null;
			morphlineCommandMonitoringHelper = new MorphlineCommandMonitoringHelper();
			fieldName = getConfigs().getString(config, FIELD_NAME_KEY);
			renderedConfig = config.root().render();
		}

		@Override
		protected boolean doProcess(Record record) {
			if (dcRegex != null) {
				MorphlineMetrics morphlineMetrics = morphlineCommandMonitoringHelper.getMorphlineMetrics(record);
				List fieldValues = record.get(fieldName);

				for (Object fieldValue : fieldValues) {
					if (fieldValue != null && dcRegex.matcher((String)fieldValue).matches()) {
						if (morphlineMetrics != null) morphlineMetrics.dcRegexMatches++;
						morphlineCommandMonitoringHelper.addFilteredEventToMonitoring(
								record, CollectionMessages.SERVICE_NAME_MATCHES_DC_REGEX,
								(String)fieldValue, fieldName);
						logger.debug(
								"The {} command dropped the record because the value {} of field {} " +
								"matched the Domain Controller regex. Command: {}, Record: {}",
								DcServiceFilter.class.getSimpleName(), fieldValue, fieldName,
								renderedConfig, record.toString());
						return true;
					}
				}
			}

			return super.doProcess(record);
		}
	}
}
