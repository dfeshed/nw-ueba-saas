package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.kitesdk.morphline.base.Notifications;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * EventsJoinerMerge command is used together with a matching EventsJoinerStore to merge 
 * two events during morphline ETL process and output a combined record using 
 * fields extracted from both records.
 * The EventsJoinerMerge command receives an input the list of fields to use as  
 * key for the current record and the list of fields to get the matching record in 
 * the cache. The record will be merged with the fields kept for the record in the 
 * cache and the merge record will be passed on for the rest morphline processing chain.
 */
public class EventsJoinerMergeBuilder implements CommandBuilder {
	
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("EventsJoinerMerge");
	}
	
	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new EventsJoinerMerge(this, config, parent, child, context);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	public static final class EventsJoinerMerge extends AbstractCommand {
		
		private List<String> keys;
		private List<String> mergeFields;
		private EventsJoinerCache cache;
		private boolean dropWhenNoMatch;
		
		public EventsJoinerMerge(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			keys = getConfigs().getStringList(config, "keys");
			mergeFields = getConfigs().getStringList(config, "mergeFields");
			dropWhenNoMatch = getConfigs().getBoolean(config, "dropWhenNoMatch", true);
			String cacheName = getConfigs().getString(config, "cacheName");
			cache = EventsJoinerCache.getInstance(cacheName);
		}
		
		@Override
		protected boolean doProcess(Record inputRecord) {
			
			// get the key fields from the record and look for the record to merge from
			String key = EventsJoinerCache.buildKey(inputRecord, keys);
			Record previousEvent = cache.fetch(key);
			
			if (previousEvent==null && dropWhenNoMatch) {
				// drop record, halt current record execution
				return true;
			}

            if (previousEvent!=null ) {
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
            }
						
			// continue processing in the command chain
			return super.doProcess(inputRecord);
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
			super.doNotify(notification);
		}
		
	}


}
