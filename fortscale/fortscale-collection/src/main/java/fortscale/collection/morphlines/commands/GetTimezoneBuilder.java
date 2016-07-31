package fortscale.collection.morphlines.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import fortscale.utils.logging.Logger;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Command that according to the input source type and server name regexp returns the appropriate timezone..
 */

public final class GetTimezoneBuilder implements CommandBuilder {

	private static MorphlineMetrics morphlineMetrics;

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("GetTimezone");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new GetTimezone(this, config, parent, child, context);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	@Configurable(preConstruction=true)
	public static final class GetTimezone extends AbstractCommand {

		MorphlineCommandMonitoringHelper commandMonitoringHelper = new MorphlineCommandMonitoringHelper();

		private final String sourceType;
		private final String hostnameField;
		private final String timezoneField;
		private TimzoneConfig tzConfig;

		@Value("${morphline.timezone}")
		public String timezones;
		
		private static Logger logger = Logger.getLogger(GetTimezone.class);


		public GetTimezone(CommandBuilder builder, Config config, Command parent,
				Command child, MorphlineContext context) {

				super(builder, config, parent, child, context);
			this.sourceType = getConfigs().getString(config, "sourceType");
			this.hostnameField = getConfigs().getString(config, "hostnameField","");
			this.timezoneField = getConfigs().getString(config, "timezoneOutputField");
			if (timezones != null && timezones.length() >0) {
				tzConfig = getTimeZoneConfig(timezones);
			}
			else {
				tzConfig = null;
			}				
		}

		public GetTimezone(CommandBuilder builder, Config config, Command parent,
				Command child, MorphlineContext context,String timezones)  {
			this(builder,config,parent,child,context);
			this.timezones =timezones;
			this.tzConfig = getTimeZoneConfig(timezones);
		}

		@Override
		protected boolean doProcess(Record record) {

			//The specific Morphline metric
			morphlineMetrics = commandMonitoringHelper.getMorphlineMetrics(record);

			if (tzConfig == null) {
				// ####################################################################################################
				// TEMP: Please refer to jira FV-3191
				// ####################################################################################################
				record.put(this.timezoneField, "UTC");
				if (morphlineMetrics != null) {
					morphlineMetrics.timeZoneNull++;
				}
			} else{
				String hostname = (String)record.getFirstValue(hostnameField);
				record.put(this.timezoneField, tzConfig.getTimeZone(sourceType, hostname));
			}


			// pass record to next command in chain:
			return super.doProcess(record);
		}

		private TimzoneConfig getTimeZoneConfig(String inputJson)
		{
			try {
				ObjectMapper mapper = new ObjectMapper();
				TimzoneConfig tz;

				tz = mapper.readValue(inputJson, TimzoneConfig.class);
				for (TimezoneUnit tzUnit : tz.regexpList) {
					tzUnit.hostPattern = Pattern.compile(tzUnit.host);
				}
				return tz;
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
			return null;
		}
	}


	private static final class TimzoneConfig {
		public List<TimezoneUnit> regexpList = new ArrayList<TimezoneUnit>();
		public String defaultTimezone;

		public String getTimeZone(String type,String host) {


            //in case that the host is null return the default time zone
            if (host != null) {
                for (TimezoneUnit tzUnit : regexpList) {
                    if (tzUnit.type.equals(type)) {
                        Matcher matcher = tzUnit.hostPattern.matcher(host);
                        if (matcher.matches()) {
                            return tzUnit.timezone;
                        }
                    }
                }
            }
			return defaultTimezone;
		}
	}

	private static final class TimezoneUnit {
		public String type;		
		public String host;
		public String timezone;		
		public Pattern hostPattern;
	}
}
