package fortscale.collection.morphlines.commands;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.kitesdk.morphline.base.Notifications;

import com.typesafe.config.Config;

/**
 * EventsJoiner command is used as a command that can do both join and merge for records
 * based on a key to match the records during morphline ETL process and output a combined 
 * record using fields extracted from both records.
 */
public class EventsJoinerBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("EventsJoiner");
	}
	
	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new EventsJoiner(this, config, parent, child, context);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	public static final class EventsJoiner extends AbstractCommand {
		
		private List<String> keys;
		private List<String> mergeFields;
		private EventsJoinerCache cache;
		
		public EventsJoiner(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			keys = getConfigs().getStringList(config, "keys");
			mergeFields = getConfigs().getStringList(config, "mergeFields");
			String cacheName = getConfigs().getString(config, "cacheName");
			cache = EventsJoinerCache.getInstance(cacheName);
		}

		@Override
		protected boolean doProcess(Record inputRecord) {
			
			// get the key fields from the record and look for the record to merge from
			String key = EventsJoinerCache.buildKey(inputRecord, keys);
			Record previousEvent = cache.fetch(key);
			
			if (previousEvent==null) {
				// store the record for later merge
				cache.store(key, inputRecord);
				
				// mark command as successful, do not pass the record
				// to chained child command to halt execution
				return true;
			} else {
				// get the fields to merge from the previous record and put them 
				// into the current record
				for (String field : mergeFields) {
					@SuppressWarnings("rawtypes")
					List values = previousEvent.get(field);
					// add all values to the input record
					inputRecord.removeAll(field);
					for (Object value : values) {
						inputRecord.put(field, value);
					}
				}

				// continue processing in the command chain
				return super.doProcess(inputRecord);
			}
		}
		
		@Override
		protected void doNotify(Record notification) {
			for (Object event : Notifications.getLifecycleEvents(notification)) {
				if (event == Notifications.LifecycleEvent.SHUTDOWN && cache!=null) {
					try {
						cache.close();
					} catch (IOException e) {
						LOG.error("error closing EventsJoinerCache", e);
					}
					cache = null;
				}
			}
		}

		
	}

}
