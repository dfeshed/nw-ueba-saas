package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.kitesdk.morphline.base.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * EventsJoinerCacheDrop command that accepts the record key and cache name 
 * and drops the cached record from the cache
 */
public class EventsJoinerCacheDropBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("EventsJoinerCacheDrop");
	}
	
	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new EventsJoinerCacheDrop(this, config, parent, child, context);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	public static final class EventsJoinerCacheDrop extends AbstractCommand {

		private static final Logger logger = LoggerFactory.getLogger(EventsJoinerCacheDrop.class);
		
		private List<String> keys;
		private EventsJoinerCache cache;
		private String cacheName;
		
		public EventsJoinerCacheDrop(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			keys = getConfigs().getStringList(config, "keys");
			cacheName = getConfigs().getString(config, "cacheName");
			cache = EventsJoinerCache.getInstance(cacheName,null);
		}

		@Override
		protected boolean doProcess(Record inputRecord) {
			// get the key fields from the record and look for the record to merge from
			String key = EventsJoinerCache.buildKey(inputRecord, keys);
			
			// remove previous record by that key from the cache
			boolean removed = cache.remove(key);
			if (!removed) {
				logger.debug("cannot remove record {} from {} cache", key, cacheName);
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
