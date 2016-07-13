package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import fortscale.collection.monitoring.CollectionMessages;
import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.morphlines.RecordExtensions;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import fortscale.utils.time.TimestampUtils;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.kitesdk.morphline.base.Notifications;
import org.springframework.beans.factory.annotation.Autowired;

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
		private boolean dropFromCache;
		private boolean dropWhenNoMatch;
        private long timeToCacheMiliSec;
        private String timeField;


		MorphlineCommandMonitoringHelper commandMonitoringHelper = new MorphlineCommandMonitoringHelper();

		public EventsJoinerMerge(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			keys = getConfigs().getStringList(config, "keys");
			mergeFields = getConfigs().getStringList(config, "mergeFields");
			dropFromCache = getConfigs().getBoolean(config, "dropFromCache", false);
			dropWhenNoMatch = getConfigs().getBoolean(config, "dropWhenNoMatch", true);
			String cacheName = getConfigs().getString(config, "cacheName");
            timeToCacheMiliSec = getConfigs().getLong(config,"timeToCacheMiliSec",-1);
            timeField = getConfigs().getString(config, "timeField","");
			cache = EventsJoinerCache.getInstance(cacheName,timeField);
		}
		
		@Override
		protected boolean doProcess(Record inputRecord) {
			//The specific Morphline metric
			MorphlineMetrics morphlineMetrics = commandMonitoringHelper.getMorphlineMetrics(inputRecord);

			// get the key fields from the record and look for the record to merge from
			String key = EventsJoinerCache.buildKey(inputRecord, keys);
			Record previousEvent = (dropFromCache)? cache.fetch(key) : cache.peek(key);

			if (previousEvent==null && dropWhenNoMatch) {
				// drop record, halt current record execution
				morphlineMetrics.eventDropped++;
				commandMonitoringHelper.addFilteredEventToMonitoring(inputRecord, CollectionMessages.NO_PREVIOUS_EVENT_AND_DROP_WHEN_NO_MATCH_IS_TRUE);
				return true;
			}

            if (previousEvent!=null ) {
                //in case that we have time to cache - check if it doesn't expire
                if (timeToCacheMiliSec > -1 && !timeField.equals(""))
                {
                    long current_record_utc_date_time = TimestampUtils.convertToMilliSeconds(RecordExtensions.getLongValue(inputRecord, timeField));

                    long prev_utc_date_time = TimestampUtils.convertToMilliSeconds(RecordExtensions.getLongValue(previousEvent,timeField));

                    long delta = current_record_utc_date_time - timeToCacheMiliSec;

                    // in case that the prev event caching was expired
                    if (prev_utc_date_time < delta)
                    {
                        //remove the prev event from cache
                        cache.fetch(key);

                        // continue processing in the command chain
                        return super.doProcess(inputRecord);
                    }
                }

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
