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
 * EventsJoinerStore command is used together with a matching EventsJoinerMerge to merge 
 * two events during morphline ETL process and output a combined record using 
 * fields extracted from both records.
 * The EventsJoinerStore command receives an input the list of fields to keep as  
 * key for the current record. The record will be dropped from the morphline command 
 * chain on processing and will be kept to be merged in a later
 * EventsJoinerMerge command that match the key fields. 
 */
public class EventsJoinerStoreBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("EventsJoinerStore");
	}
	
	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new EventsJoinerStore(this, config, parent, child, context);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	public static final class EventsJoinerStore extends AbstractCommand {
	
		private List<String> keys;
		private EventsJoinerCache cache;
		
		public EventsJoinerStore(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			keys = getConfigs().getStringList(config, "keys");
			String cacheName = getConfigs().getString(config, "cacheName");
			cache = EventsJoinerCache.getInstance(cacheName);
		}
	
		@Override
		protected boolean doProcess(Record inputRecord) {
			
			// get the key fields from the record and prepare a map input for it
			String key = EventsJoinerCache.buildKey(inputRecord, keys);
			cache.store(key, inputRecord);
			
			// mark command as successful, do not pass the record
			// to chained child command to halt execution
			return true;
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
