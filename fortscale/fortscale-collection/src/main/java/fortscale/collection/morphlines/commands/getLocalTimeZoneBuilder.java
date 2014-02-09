package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.TimeZone;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;

import com.typesafe.config.Config;

import fortscale.utils.logging.Logger;

public class getLocalTimeZoneBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("GetLocalTimeZone");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new GetLocalTimeZone(this, config, parent, child, context);
	}

	
	
//		public static LocalTimeZone getInstance() {
//			if (localTzInstance==null) {
//				localTzInstance = new LocalTimeZone();
//			}
//			return localTzInstance;
//		}
		
	
	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	private static final class GetLocalTimeZone extends AbstractCommand {
	
		private final String outputRecordName;
		private static TimeZone timeZone;
		private static final String EMPTY_STRING = "";

		private static final Logger logger = Logger.getLogger(GetLocalTimeZone.class);
		
		public GetLocalTimeZone(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			timeZone = TimeZone.getDefault();
			this.outputRecordName = getConfigs().getString(config, "outputRecordName");

			validateArguments();
		}


		@Override
		protected boolean doProcess(Record inputRecord) {
			try {
				System.out.println(timeZone.getDisplayName());
				inputRecord.put(this.outputRecordName, timeZone.getDisplayName());
				
			} catch (IllegalArgumentException e) {
				inputRecord.put(this.outputRecordName, EMPTY_STRING);
			}

			return super.doProcess(inputRecord);
		}

		
		public static void setLocalTimeZone(TimeZone newTimeZone) {
			timeZone = newTimeZone; 
		}

	}
}
