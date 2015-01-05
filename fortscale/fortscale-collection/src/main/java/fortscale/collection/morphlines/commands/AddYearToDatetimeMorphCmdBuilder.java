package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import fortscale.collection.morphlines.RecordExtensions;
import fortscale.services.impl.UsernameNormalizer;
import fortscale.utils.logging.Logger;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

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
	private class AddYearToDatetime extends AbstractCommand {

		private final String dateFormat;
		private final String timeZone;

		public AddYearToDatetime(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);

			dateFormat = getConfigs().getString(config, "dateFormat");
			timeZone = getConfigs().getString(config, "timezone");

			validateArguments();
		}

		@Override
		protected boolean doProcess(Record inputRecord) {
			//Adding year from the system current date to the date_time.
			try {
				Object date_time = inputRecord.getFirstValue("date_time");
				TimeZone outputTimeZone = TimeZone.getTimeZone(timeZone == null ? "UTC" : timeZone);

				if (date_time==null) {
					return false;
				}

				SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
				sdf.setTimeZone(outputTimeZone);
				int year = Calendar.getInstance().get(Calendar.YEAR);



				Date parsedDate = sdf.parse(Integer.toString(year) + " " + date_time.toString());


				Calendar cal = Calendar.getInstance(outputTimeZone);

				Date currentDate = cal.getTime();



				if (parsedDate.compareTo(currentDate)>0) {
					parsedDate.setYear(parsedDate.getYear() - 1);
				}

				inputRecord.replaceValues("date_time", sdf.format(parsedDate));


			} catch (Exception e) {
				logger.error("Error parsing date." + e.getMessage());
				return false;
			}

			return super.doProcess(inputRecord);

		}
	}
}
