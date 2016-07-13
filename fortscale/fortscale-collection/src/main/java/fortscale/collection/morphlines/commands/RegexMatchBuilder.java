package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import fortscale.collection.monitoring.CollectionMessages;
import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;

import com.typesafe.config.Config;

import fortscale.collection.morphlines.RecordExtensions;

/**
 * Command that match a string property in a record to a regular expression
 */
public class RegexMatchBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("regexMatch");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new RegexMatch(this, config, parent, child, context);
	}
	
	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	private static final class RegexMatch extends AbstractCommand {

		private String fieldName;
		private Pattern pattern;
		private boolean dropOnMatch;
		private boolean dropOnMissMatch;

		MorphlineCommandMonitoringHelper commandMonitoringHelper = new MorphlineCommandMonitoringHelper();


		public RegexMatch(CommandBuilder builder, Config config, Command parent,
				Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.fieldName = getConfigs().getString(config, "field");
			this.dropOnMatch = getConfigs().getBoolean(config, "dropOnMatch", true);
			this.dropOnMissMatch = getConfigs().getBoolean(config, "dropOnMissMatch", false);
			String regex = getConfigs().getString(config, "regex");
			this.pattern = Pattern.compile(regex);
			validateArguments();
		}
		
		protected boolean doProcess(Record record) {

			//The specific Morphline metric
			MorphlineMetrics morphlineMetrics = commandMonitoringHelper.getMorphlineMetrics(record);

			String value = RecordExtensions.getStringValue(record, fieldName);
			boolean match = (value!=null && pattern.matcher(value).matches());
			
			if (dropOnMatch && match) {
				morphlineMetrics.regexMatchFilteredOnFieldMatched++;
				commandMonitoringHelper.addFilteredEventToMonitoring(record,
						CollectionMessages.FILTERED_ON_FIELD_MATCHED, fieldName);
				return true;
			}
			
			if (dropOnMissMatch && !match) {
				morphlineMetrics.regexMatchFilteredOnFieldNotMatched++;
				commandMonitoringHelper.addFilteredEventToMonitoring(record,
						CollectionMessages.FILTERED_ON_FIELD_NOT_MATCHED, fieldName);
				return true;
			}

			morphlineMetrics.regexMatchRecordNotFiltered++;
			return super.doProcess(record);
		}
	}

}
