package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import fortscale.collection.monitoring.CollectionMessages;
import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.morphlines.RecordExtensions;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.text.SimpleDateFormat;
import java.util.*;

@Configurable()
public class AddYearToDatetimeMorphCmdBuilder implements CommandBuilder {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(AddYearToDatetimeMorphCmdBuilder.class);

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("AddYearToDatetime");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new AddYearToDatetime(this, config, parent, child, context);
	}
	
	// /////////////////////////////////////////////////////////////////////////////
	// Nested class:
	// /////////////////////////////////////////////////////////////////////////////
	@Configurable(preConstruction=true)
	public static class AddYearToDatetime extends AbstractCommand {

		private final String dateFormat;
		private final String timeZone;
		private final SimpleDateFormat sdf;
		private String year = null;
		Calendar cal;

		private MorphlineCommandMonitoringHelper commandMonitoringHelper = new MorphlineCommandMonitoringHelper();

		public AddYearToDatetime(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);

			dateFormat = getConfigs().getString(config, "dateFormat");
			timeZone = getConfigs().getString(config, "timezone");
			sdf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);

			validateArguments();
		}

		@SuppressWarnings("deprecation")
		@Override
		protected boolean doProcess(Record inputRecord) {

			//The specific Morphline metric
			MorphlineMetrics morphlineMetrics = commandMonitoringHelper.getMorphlineMetrics(inputRecord);

			//Adding year from the system current date to the date_time.
			try {
				String date_time = RecordExtensions.getStringValue(inputRecord, "date_time");
				TimeZone outputTimeZone = TimeZone.getTimeZone(RecordExtensions.getStringValue(inputRecord, timeZone, "UTC"));

				if (date_time==null) {
					if (morphlineMetrics != null)
						morphlineMetrics.emptyTimeField++;
					commandMonitoringHelper.addFilteredEventToMonitoring(inputRecord, CollectionMessages.TIME_FIELD_EMPTY);
					return false;
				}

				cal = Calendar.getInstance(outputTimeZone);
				year = Integer.toString(cal.get(Calendar.YEAR));
				sdf.setTimeZone(outputTimeZone);
				Date parsedDate = sdf.parse(year + " " + date_time);


				Date currentDate = cal.getTime();

				if (parsedDate.compareTo(currentDate)>0) {
					// we implicitly assume that the logs we are analyzing are not older than one year
					parsedDate.setYear(parsedDate.getYear() - 1);
				}
				inputRecord.replaceValues("date_time", sdf.format(parsedDate));

			} catch (Exception e) {
				logger.error("Error parsing date." + e.getMessage());
				if (morphlineMetrics != null)
					morphlineMetrics.unvalidTimeField++;
				commandMonitoringHelper.addFilteredEventToMonitoring(inputRecord, CollectionMessages.TIME_FIELD_IS_NOT_VALID);
				return false;
			}

			if (morphlineMetrics != null){
				morphlineMetrics.addYearToDatetimeSuccess++;
			}

			return super.doProcess(inputRecord);

		}
	}
}
